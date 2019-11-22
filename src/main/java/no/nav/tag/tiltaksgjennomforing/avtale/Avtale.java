package no.nav.tag.tiltaksgjennomforing.avtale;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import lombok.experimental.FieldNameConstants;
import no.nav.tag.tiltaksgjennomforing.avtale.events.*;
import no.nav.tag.tiltaksgjennomforing.exceptions.TilgangskontrollException;
import no.nav.tag.tiltaksgjennomforing.exceptions.TiltaksgjennomforingException;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Enumerated(value = EnumType.STRING)
    @Column(updatable = false, insertable = false)
    private Tiltakstype tiltakstype;

    private LocalDateTime opprettetTidspunkt;
    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @OneToMany(mappedBy = "avtale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AvtaleInnhold> versjoner = new ArrayList<>();

    private Instant sistEndret;
    private boolean avbrutt;

    public Avtale(Fnr deltakerFnr, BedriftNr bedriftNr, NavIdent veilederNavIdent, Tiltakstype tiltakstype) {
        this.id = UUID.randomUUID();
        this.opprettetTidspunkt = LocalDateTime.now();
        this.deltakerFnr = sjekkAtIkkeNull(deltakerFnr, "Deltakers fnr må være satt.");
        this.bedriftNr = sjekkAtIkkeNull(bedriftNr, "Arbeidsgivers bedriftnr må være satt.");
        this.veilederNavIdent = sjekkAtIkkeNull(veilederNavIdent, "Veileders NAV-ident må være satt.");
        this.tiltakstype = tiltakstype;
        this.sistEndret = Instant.now();
        var innhold = AvtaleInnhold.nyttTomtInnhold();
        innhold.setAvtale(this);
        this.versjoner.add(innhold);
        registerEvent(new AvtaleOpprettet(this, veilederNavIdent));
    }

    public void endreAvtale(Instant sistEndret, EndreAvtale nyAvtale, Avtalerolle utfortAv) {
        sjekkOmAvtalenKanEndres();
        sjekkSistEndret(sistEndret);

        gjeldendeInnhold().endreAvtale(nyAvtale);
        settSistEndret();

        settSistEndret();

        registerEvent(new AvtaleEndret(this, utfortAv));
    }

    private interface MetoderSomIkkeSkalDelegeresFraAvtaleInnhold {
        UUID getId();

        void setId(UUID id);

        Avtale getAvtale();
    }

    @Delegate(excludes = MetoderSomIkkeSkalDelegeresFraAvtaleInnhold.class)
    private AvtaleInnhold gjeldendeInnhold() {
        /* TODO: Bruk en tryggere måte å hente gjeldende versjon. Alternativer:
            - Lagre en gjeldende versjon på Avtale, og gjøre oppslag i versjoner-lista
            - Sortere versjoner etter versjonsnummer, kan dermed plukke siste element
        */
        return versjoner.get(versjoner.size() - 1);
    }

    @JsonProperty
    public boolean erLaast() {
        return erGodkjentAvVeileder() && erGodkjentAvArbeidsgiver() && erGodkjentAvDeltaker();
    }

    public boolean erGodkjentAvDeltaker() {
        return this.getGodkjentAvDeltaker() != null;
    }

    public boolean erGodkjentAvArbeidsgiver() {
        return this.getGodkjentAvArbeidsgiver() != null;
    }

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
    }

    private void settSistEndret() {
        this.sistEndret = Instant.now();
    }

    void sjekkSistEndret(Instant sistEndret) {
        if (sistEndret == null || sistEndret.isBefore(this.sistEndret)) {
            throw new SamtidigeEndringerException("Du må oppdatere siden før du kan lagre eller godkjenne. Det er gjort endringer i avtalen som du ikke har sett.");
        }
    }

    void godkjennForArbeidsgiver(Identifikator utfortAv) {
        sjekkOmKanGodkjennes();
        this.setGodkjentAvArbeidsgiver(LocalDateTime.now());
        registerEvent(new GodkjentAvArbeidsgiver(this, utfortAv));
    }

    void godkjennForVeileder(Identifikator utfortAv) {
        sjekkOmKanGodkjennes();
        this.setGodkjentAvVeileder(LocalDateTime.now());
        registerEvent(new GodkjentAvVeileder(this, utfortAv));
    }

    void godkjennForVeilederOgDeltaker(Identifikator utfortAv, GodkjentPaVegneGrunn paVegneAvGrunn) {
        sjekkOmKanGodkjennes();
        this.setGodkjentAvVeileder(LocalDateTime.now());
        this.setGodkjentAvDeltaker(LocalDateTime.now());
        this.setGodkjentPaVegneAv(true);
        this.setGodkjentPaVegneGrunn(paVegneAvGrunn);
        registerEvent(new GodkjentPaVegneAv(this, utfortAv));
    }

    void godkjennForDeltaker(Identifikator utfortAv) {
        sjekkOmKanGodkjennes();
        this.setGodkjentAvDeltaker(LocalDateTime.now());
        registerEvent(new GodkjentAvDeltaker(this, utfortAv));
    }

    void sjekkOmKanGodkjennes() {
        if (!heleAvtalenErFyltUt()) {
            throw new TiltaksgjennomforingException("Alt må være utfylt før avtalen kan godkjennes.");
        }
    }

    @JsonProperty
    public String status() {
        if (isAvbrutt()) {
            return "Avbrutt";
        } else if (erGodkjentAvVeileder() && (this.getSluttDato().isBefore(LocalDate.now()))) {
            return "Avsluttet";
        } else if (erGodkjentAvVeileder() && (this.getStartDato().isBefore(LocalDate.now().plusDays(1)))) {
            return "Gjennomføres";
        } else if (erGodkjentAvVeileder()) {
            return "Klar for oppstart";
        } else if (heleAvtalenErFyltUt()) {
            return "Mangler godkjenning";
        } else {
            return "Påbegynt";
        }
    }

    @JsonProperty
    public boolean kanAvbrytes() {
        // Nå regner vi at veileder kan avbryte avtalen hvis veileder ikke har godkjent(kan også være at han kan
        // avbryte kun de avtalene som ikke er godkjente av deltaker og AG),
        return !erGodkjentAvVeileder() && !isAvbrutt();
    }

    public void avbryt(Veileder veileder) {
        if (this.kanAvbrytes()) {
            this.setAvbrutt(true);
            registerEvent(new AvbruttAvVeileder(this, veileder.getIdentifikator()));
        }
    }

    boolean heleAvtalenErFyltUt() {
        return gjeldendeInnhold().heleAvtalenErFyltUt();
    }

    public void leggTilBedriftNavn(String bedriftNavn) {
        this.setBedriftNavn(bedriftNavn);
    }

    @JsonProperty
    public boolean kanLåsesOpp() {
        return erGodkjentAvVeileder();
    }

    public void sjekkOmKanLåsesOpp() {
        if (!kanLåsesOpp()) {
            throw new TiltaksgjennomforingException("Avtalen kan ikke låses opp");
        }
    }

    public void låsOppAvtale() {
        sjekkOmKanLåsesOpp();
        versjoner.add(this.gjeldendeInnhold().nyVersjon());
        registerEvent(new AvtaleLåstOpp(this));
    }

    public boolean skalJournalfores() {
        return tiltakstype == Tiltakstype.ARBEIDSTRENING
                && erGodkjentAvVeileder()
                && this.getJournalpostId() == null;
    }
}
