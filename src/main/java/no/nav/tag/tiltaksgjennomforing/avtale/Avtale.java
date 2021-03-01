package no.nav.tag.tiltaksgjennomforing.avtale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import lombok.experimental.FieldNameConstants;
import no.nav.tag.tiltaksgjennomforing.avtale.events.*;
import no.nav.tag.tiltaksgjennomforing.exceptions.*;
import no.nav.tag.tiltaksgjennomforing.persondata.Navn;
import no.nav.tag.tiltaksgjennomforing.persondata.NavnFormaterer;
import no.nav.tag.tiltaksgjennomforing.utils.TelefonnummerValidator;
import no.nav.tag.tiltaksgjennomforing.utils.Utils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SortNatural;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Set.copyOf;
import static no.nav.tag.tiltaksgjennomforing.utils.Utils.sjekkAtIkkeNull;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@NoArgsConstructor
@FieldNameConstants
public class Avtale extends AbstractAggregateRoot<Avtale> {

    @Convert(converter = FnrConverter.class)
    private Fnr deltakerFnr;
    @Convert(converter = BedriftNrConverter.class)
    private BedriftNr bedriftNr;
    @Convert(converter = NavIdentConverter.class)
    private NavIdent veilederNavIdent;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private Tiltakstype tiltakstype;

    private LocalDateTime opprettetTidspunkt;
    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @OneToMany(mappedBy = "avtale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy(AvtaleInnhold.Fields.versjon)
    private List<AvtaleInnhold> versjoner = new ArrayList<>();

    private Instant sistEndret;
    private boolean avbrutt;
    private boolean slettemerket;
    private LocalDate avbruttDato;
    private String avbruttGrunn;
    private boolean opprettetAvArbeidsgiver;
    @JsonIgnore
    private String enhetGeografisk;
    @JsonIgnore
    private String enhetOppfolging;

    @OneToMany(mappedBy = "avtale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @SortNatural
    private SortedSet<TilskuddPeriode> tilskuddPeriode = new TreeSet<>();

    private Avtale(OpprettAvtale opprettAvtale) {
        this.id = UUID.randomUUID();
        this.opprettetTidspunkt = LocalDateTime.now();
        this.deltakerFnr = sjekkAtIkkeNull(opprettAvtale.getDeltakerFnr(), "Deltakers fnr må være satt.");
        this.bedriftNr = sjekkAtIkkeNull(opprettAvtale.getBedriftNr(), "Arbeidsgivers bedriftnr må være satt.");
        this.tiltakstype = opprettAvtale.getTiltakstype();
        this.sistEndret = Instant.now();
        var innhold = AvtaleInnhold.nyttTomtInnhold();
        innhold.setAvtale(this);
        this.versjoner.add(innhold);
    }

    public static Avtale veilederOppretterAvtale(OpprettAvtale opprettAvtale, NavIdent navIdent) {
        Avtale avtale = new Avtale(opprettAvtale);
        avtale.veilederNavIdent = sjekkAtIkkeNull(navIdent, "Veileders NAV-ident må være satt.");
        avtale.registerEvent(new AvtaleOpprettetAvVeileder(avtale, navIdent));
        return avtale;
    }

    public static Avtale arbeidsgiverOppretterAvtale(OpprettAvtale opprettAvtale) {
        Avtale avtale = new Avtale(opprettAvtale);
        avtale.opprettetAvArbeidsgiver = true;
        avtale.registerEvent(new AvtaleOpprettetAvArbeidsgiver(avtale));
        return avtale;
    }

    public void endreAvtale(Instant sistEndret, EndreAvtale nyAvtale, Avtalerolle utfortAv) {
        sjekkOmAvtalenKanEndres();
        sjekkSistEndret(sistEndret);
        gjeldendeInnhold().endreAvtale(nyAvtale);
        nyeTilskuddsperioder();
        sistEndretNå();
        registerEvent(new AvtaleEndret(this, utfortAv));
    }

    public void delMedAvtalepart(Avtalerolle avtalerolle) {
        String tlf = telefonnummerTilAvtalepart(avtalerolle);
        if (!TelefonnummerValidator.erGyldigMobilnummer(tlf)) {
            throw new FeilkodeException(Feilkode.UGYLDIG_TLF);
        }
        registerEvent(new AvtaleDeltMedAvtalepart(this, avtalerolle));
    }

    private String telefonnummerTilAvtalepart(Avtalerolle avtalerolle) {
        switch (avtalerolle) {
            case DELTAKER:
                return getDeltakerTlf();
            case ARBEIDSGIVER:
                return getArbeidsgiverTlf();
            case VEILEDER:
                return getVeilederTlf();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Delegate(excludes = MetoderSomIkkeSkalDelegeresFraAvtaleInnhold.class)
    public AvtaleInnhold gjeldendeInnhold() {
        return versjoner.get(versjoner.size() - 1);
    }

    @JsonProperty
    public boolean erLaast() {
        return erGodkjentAvVeileder() && erGodkjentAvArbeidsgiver() && erGodkjentAvDeltaker();
    }

    @JsonProperty
    public boolean erGodkjentAvDeltaker() {
        return this.getGodkjentAvDeltaker() != null;
    }

    @JsonProperty
    public boolean erGodkjentAvArbeidsgiver() {
        return this.getGodkjentAvArbeidsgiver() != null;
    }

    @JsonProperty
    public boolean erGodkjentAvVeileder() {
        return this.getGodkjentAvVeileder() != null;
    }

    private void sjekkOmAvtalenKanEndres() {
        if (erGodkjentAvDeltaker() || erGodkjentAvArbeidsgiver() || erGodkjentAvVeileder()) {
            throw new TilgangskontrollException("Godkjenninger må oppheves før avtalen kan endres.");
        }
    }

    void opphevGodkjenningerSomArbeidsgiver() {
        boolean varGodkjentAvDeltaker = erGodkjentAvDeltaker();
        opphevGodkjenninger();
        registerEvent(new GodkjenningerOpphevetAvArbeidsgiver(this, new GamleVerdier(varGodkjentAvDeltaker, false)));
    }

    void opphevGodkjenningerSomVeileder() {
        boolean varGodkjentAvDeltaker = erGodkjentAvDeltaker();
        boolean varGodkjentAvArbeidsgiver = erGodkjentAvArbeidsgiver();
        opphevGodkjenninger();
        registerEvent(new GodkjenningerOpphevetAvVeileder(this, new GamleVerdier(varGodkjentAvDeltaker, varGodkjentAvArbeidsgiver)));
    }

    private void opphevGodkjenninger() {
        setGodkjentAvDeltaker(null);
        setGodkjentAvArbeidsgiver(null);
        setGodkjentAvVeileder(null);
        setGodkjentPaVegneAv(false);
        setGodkjentPaVegneGrunn(null);
        setGodkjentAvNavIdent(null);
        sistEndretNå();
    }

    private void sistEndretNå() {
        this.sistEndret = Instant.now();
    }

    void sjekkSistEndret(Instant sistEndret) {
        if (sistEndret == null || sistEndret.isBefore(this.sistEndret)) {
            throw new SamtidigeEndringerException();
        }
    }

    void godkjennForArbeidsgiver(Identifikator utfortAv) {
        sjekkOmAltErUtfylt();
        this.setGodkjentAvArbeidsgiver(LocalDateTime.now());
        sistEndretNå();
        registerEvent(new GodkjentAvArbeidsgiver(this, utfortAv));
    }

    void godkjennForVeileder(Identifikator utfortAv) {
        sjekkOmAltErUtfylt();
        if (erUfordelt()) {
            throw new AvtaleErIkkeFordeltException();
        }
        if (!erGodkjentAvArbeidsgiver() || !erGodkjentAvDeltaker()) {
            throw new VeilederSkalGodkjenneSistException();
        }

        this.setGodkjentAvVeileder(LocalDateTime.now());
        this.setGodkjentAvNavIdent(new NavIdent(utfortAv.asString()));
        sistEndretNå();
        registerEvent(new GodkjentAvVeileder(this, utfortAv));
    }

    void godkjennForVeilederOgDeltaker(Identifikator utfortAv, GodkjentPaVegneGrunn paVegneAvGrunn) {
        sjekkOmAltErUtfylt();
        if (erGodkjentAvDeltaker()) {
            throw new DeltakerHarGodkjentException();
        }
        if (!erGodkjentAvArbeidsgiver()) {
            throw new ArbeidsgiverSkalGodkjenneFørVeilederException();
        }
        paVegneAvGrunn.valgtMinstEnGrunn();
        this.setGodkjentAvVeileder(LocalDateTime.now());
        this.setGodkjentAvDeltaker(LocalDateTime.now());
        this.setGodkjentPaVegneAv(true);
        this.setGodkjentPaVegneGrunn(paVegneAvGrunn);
        this.setGodkjentAvNavIdent(new NavIdent(utfortAv.asString()));
        sistEndretNå();
        registerEvent(new GodkjentPaVegneAv(this, utfortAv));
    }

    void godkjennForDeltaker(Identifikator utfortAv) {
        sjekkOmAltErUtfylt();
        this.setGodkjentAvDeltaker(LocalDateTime.now());
        sistEndretNå();
        registerEvent(new GodkjentAvDeltaker(this, utfortAv));
    }

    void sjekkOmAltErUtfylt() {
        if (!erAltUtfylt()) {
            throw new AltMåVæreFyltUtException();
        }
    }

    @JsonProperty
    public String status() {
        return statusSomEnum().getStatusVerdi();
    }

    public Status statusSomEnum() {
        if (isAvbrutt()) {
            return Status.AVBRUTT;
        } else if (erGodkjentAvVeileder() && (this.getSluttDato().isBefore(LocalDate.now()))) {
            return Status.AVSLUTTET;
        } else if (erGodkjentAvVeileder() && (this.getStartDato().isBefore(LocalDate.now().plusDays(1)))) {
            return Status.GJENNOMFØRES;
        } else if (erGodkjentAvVeileder()) {
            return Status.KLAR_FOR_OPPSTART;
        } else if (erAltUtfylt()) {
            return Status.MANGLER_GODKJENNING;
        } else {
            return Status.PÅBEGYNT;
        }
    }

    @JsonProperty
    public boolean kanAvbrytes() {
        return !isAvbrutt();
    }

    @JsonProperty
    public boolean kanGjenopprettes() {
        return isAvbrutt();
    }

    public void avbryt(Veileder veileder, AvbruttInfo avbruttInfo) {
        if (this.kanAvbrytes()) {
            this.setAvbrutt(true);
            this.setAvbruttDato(avbruttInfo.getAvbruttDato());
            this.setAvbruttGrunn(avbruttInfo.getAvbruttGrunn());
            if (this.erUfordelt()) {
                this.setVeilederNavIdent(veileder.getIdentifikator());
            }
            sistEndretNå();
            registerEvent(new AvbruttAvVeileder(this, veileder.getIdentifikator()));
        }
    }

    public void overtaAvtale(NavIdent nyNavIdent) {
        NavIdent gammelNavIdent = this.getVeilederNavIdent();
        this.setVeilederNavIdent(nyNavIdent);
        sistEndretNå();
        if (gammelNavIdent == null) {
            this.registerEvent(new AvtaleOpprettetAvArbeidsgiverErFordelt(this));
        } else {
            registerEvent(new AvtaleNyVeileder(this, gammelNavIdent));
        }
    }

    public void gjenopprett(Veileder veileder) {
        if (this.kanGjenopprettes()) {
            this.setAvbrutt(false);
            this.setAvbruttDato(null);
            this.setAvbruttGrunn(null);
            sistEndretNå();
            registerEvent(new AvtaleGjenopprettet(this, veileder.getIdentifikator()));
        }
    }

    boolean erAltUtfylt() {
        return gjeldendeInnhold().erAltUtfylt();
    }

    public void leggTilBedriftNavn(String bedriftNavn) {
        this.setBedriftNavn(bedriftNavn);
    }

    public void leggTilDeltakerNavn(Navn navn) {
        NavnFormaterer formaterer = new NavnFormaterer(navn);
        this.setDeltakerFornavn(formaterer.getFornavn());
        this.setDeltakerEtternavn(formaterer.getEtternavn());
    }

    @JsonProperty
    public boolean kanLåsesOpp() {
        return erGodkjentAvVeileder();
    }

    public void sjekkOmKanLåsesOpp() {
        if (!kanLåsesOpp()) {
            throw new FeilkodeException(Feilkode.KAN_IKKE_LAASES_OPP);
        }
    }

    public void låsOppAvtale() {
        sjekkOmKanLåsesOpp();
        versjoner.add(this.gjeldendeInnhold().nyVersjon());
        sistEndretNå();
        registerEvent(new AvtaleLåstOpp(this));
    }

    @JsonProperty
    public boolean erUfordelt() {
        return this.getVeilederNavIdent() == null;
    }

    public void godkjennTilskuddsperiode(NavIdent beslutter) {
        if (!erGodkjentAvVeileder()) {
            throw new FeilkodeException(Feilkode.TILSKUDDSPERIODE_KAN_KUN_BEHANDLES_VED_INNGAATT_AVTALE);
        }
        TilskuddPeriode gjeldendePeriode = gjeldendeTilskuddsperiode();
        gjeldendePeriode.godkjenn(beslutter);
        registerEvent(new TilskuddsperiodeGodkjent(this, gjeldendePeriode, beslutter));
    }

    public void avslåTilskuddsperiode(NavIdent beslutter, EnumSet<Avslagsårsak> avslagsårsaker, String avslagsforklaring) {
        if (!erGodkjentAvVeileder()) {
            throw new FeilkodeException(Feilkode.TILSKUDDSPERIODE_KAN_KUN_BEHANDLES_VED_INNGAATT_AVTALE);
        }
        TilskuddPeriode gjeldendePeriode = gjeldendeTilskuddsperiode();
        gjeldendePeriode.avslå(beslutter, avslagsårsaker, avslagsforklaring);
        registerEvent(new TilskuddsperiodeAvslått(this, beslutter));
    }


    protected TilskuddPeriodeStatus getGjeldendeTilskuddsperiodestatus() {
        TilskuddPeriode tilskuddPeriode = gjeldendeTilskuddsperiode();
        if (tilskuddPeriode == null) {
            return null;
        }
        return tilskuddPeriode.getStatus();
    }

    public TilskuddPeriode tilskuddsperiode(int index) {
        return tilskuddPeriode.toArray(new TilskuddPeriode[0])[index];
    }

    @JsonProperty
    public TilskuddPeriode gjeldendeTilskuddsperiode() {
        if (tilskuddPeriode.isEmpty()) {
            return null;
        }

        List<TilskuddPeriode> tilskuddsperioderSortert = tilskuddPeriode.stream()
                .sorted(Comparator.comparing(TilskuddPeriode::getStartDato))
                .collect(Collectors.toList());

        // Finner første avslått
        Optional<TilskuddPeriode> førsteAvslått = tilskuddsperioderSortert.stream().filter(tilskuddPeriode -> tilskuddPeriode.getStatus() == TilskuddPeriodeStatus.AVSLÅTT).findFirst();
        if (førsteAvslått.isPresent()) {
            return førsteAvslått.get();
        }

        // Finn første som kan behandles
        Optional<TilskuddPeriode> førsteSomKanBehandles = tilskuddsperioderSortert.stream().filter(TilskuddPeriode::kanBehandles).findFirst();
        if (førsteSomKanBehandles.isPresent()) {
            return førsteSomKanBehandles.get();
        }

        // Finn siste godkjent
        Optional<TilskuddPeriode> sisteGodkjent = Lists
                .reverse(tilskuddsperioderSortert).stream().filter(tilskuddPeriode -> tilskuddPeriode.getStatus() == TilskuddPeriodeStatus.GODKJENT)
                .findFirst();
        if (sisteGodkjent.isPresent()) {
            return sisteGodkjent.get();
        }

        return tilskuddsperioderSortert.get(0);
    }

    public void slettemerk(NavIdent utførtAv) {
        this.setSlettemerket(true);
        registerEvent(new AvtaleSlettemerket(this, utførtAv));
    }

    void forlengTilskuddsperioder(LocalDate gammelSluttDato, LocalDate nySluttDato) {
        TilskuddPeriode sisteTilskuddsperiode = tilskuddPeriode.last();
        if (sisteTilskuddsperiode.getStatus() == TilskuddPeriodeStatus.UBEHANDLET) {
            // Kan utvide siste tilskuddsperiode hvis den er ubehandlet
            tilskuddPeriode.addAll(beregnTilskuddsperioder(sisteTilskuddsperiode.getStartDato(), nySluttDato));
            tilskuddPeriode.remove(sisteTilskuddsperiode);
        } else {
            // Regner ut nye perioder fra gammel avtaleslutt til ny avtaleslutt
            tilskuddPeriode.addAll(beregnTilskuddsperioder(gammelSluttDato.plusDays(1), nySluttDato));
        }
    }

    public void forkortTilskuddsperioder(LocalDate nySluttDato) {
        for (TilskuddPeriode tilskuddsperiode : Set.copyOf(tilskuddPeriode)) {
            TilskuddPeriodeStatus status = tilskuddsperiode.getStatus();
            if (tilskuddsperiode.getStartDato().isAfter(nySluttDato)) {
                if (status == TilskuddPeriodeStatus.UBEHANDLET) {
                    tilskuddPeriode.remove(tilskuddsperiode);
                } else {
                    tilskuddsperiode.setStatus(TilskuddPeriodeStatus.ANNULLERT);
                }
            } else if (tilskuddsperiode.getSluttDato().isAfter(nySluttDato)) {
                if (status == TilskuddPeriodeStatus.UBEHANDLET) {
                    tilskuddsperiode.setSluttDato(nySluttDato);
                    tilskuddsperiode.setBeløp(beregnTilskuddsbeløp(tilskuddsperiode.getStartDato(), tilskuddsperiode.getSluttDato()));
                } else if (status == TilskuddPeriodeStatus.GODKJENT) {
                    TilskuddPeriode ny = new TilskuddPeriode(tilskuddsperiode);
                    ny.setSluttDato(nySluttDato);
                    ny.setBeløp(beregnTilskuddsbeløp(tilskuddsperiode.getStartDato(), tilskuddsperiode.getSluttDato()));
                    tilskuddsperiode.setStatus(TilskuddPeriodeStatus.ANNULLERT);
                    tilskuddPeriode.add(ny);
                }
            }
        }
    }

    void endreBeløpITilskuddsperioder() {
        for (TilskuddPeriode tilskuddsperiode : Set.copyOf(tilskuddPeriode)) {
            if (tilskuddsperiode.getStatus() == TilskuddPeriodeStatus.UBEHANDLET) {
                tilskuddsperiode.setBeløp(beregnTilskuddsbeløp(tilskuddsperiode.getStartDato(), tilskuddsperiode.getSluttDato()));
            } else if (tilskuddsperiode.getStatus() == TilskuddPeriodeStatus.GODKJENT) {
                TilskuddPeriode ny = new TilskuddPeriode(tilskuddsperiode);
                ny.setBeløp(beregnTilskuddsbeløp(tilskuddsperiode.getStartDato(), tilskuddsperiode.getSluttDato()));
//                boolean beløpetHarØkt = ny.getBeløp() > tilskuddsperiode.getBeløp();
//                ny.setStatus(beløpetHarØkt ? TilskuddPeriodeStatus.UBEHANDLET : TilskuddPeriodeStatus.GODKJENT);
                ny.setStatus(TilskuddPeriodeStatus.UBEHANDLET);
                tilskuddsperiode.setStatus(TilskuddPeriodeStatus.ANNULLERT);
                tilskuddPeriode.add(ny);
            }
        }
    }

    private Integer beregnTilskuddsbeløp(LocalDate startDato, LocalDate sluttDato) {
        return RegnUtTilskuddsperioderForAvtale.beløpForPeriode(startDato, sluttDato, getDatoForRedusertProsent(), getSumLonnstilskudd(), getSumLønnstilskuddRedusert());
    }


    private List<TilskuddPeriode> beregnTilskuddsperioder(LocalDate startDato, LocalDate sluttDato) {
        return RegnUtTilskuddsperioderForAvtale.beregnTilskuddsperioderForAvtale(getSumLonnstilskudd(), startDato, sluttDato, getLonnstilskuddProsent(), getDatoForRedusertProsent(), getSumLønnstilskuddRedusert());
    }

    private void nyeTilskuddsperioder() {
        tilskuddPeriode.clear();
        if (Utils.erIkkeTomme(getStartDato(), getSluttDato(), getSumLonnstilskudd())) {
            tilskuddPeriode.addAll(beregnTilskuddsperioder(getStartDato(), getSluttDato()));
        }
    }

    public void forlengAvtale(LocalDate nySluttDato) {
        forlengTilskuddsperioder(this.getSluttDato(), nySluttDato);
        versjoner.add(gjeldendeInnhold().nyGodkjentVersjon());
        gjeldendeInnhold().setSluttDato(nySluttDato);
        registerEvent(new AvtaleForlenget(this));
    }

    public void endreTilskuddsberegning(EndreAvtale tilskuddsberegning) {
        versjoner.add(gjeldendeInnhold().nyGodkjentVersjon());
        gjeldendeInnhold().endreAvtale(tilskuddsberegning);
        endreBeløpITilskuddsperioder();
        registerEvent(new TilskuddsberegningEndret(this, null));
    }

    public void forkortAvtale(LocalDate nySluttDato) {
        if (nySluttDato.isAfter(getSluttDato()) || nySluttDato.isBefore(getStartDato())) {
            // Ikke forkortelse, kast feil
        }
        forkortTilskuddsperioder(nySluttDato);
        versjoner.add(gjeldendeInnhold().nyGodkjentVersjon());
        gjeldendeInnhold().setSluttDato(nySluttDato);
        registerEvent(new AvtaleForlenget(this));
    }

    private interface MetoderSomIkkeSkalDelegeresFraAvtaleInnhold {
        UUID getId();

        void setId(UUID id);

        Avtale getAvtale();
    }
}
