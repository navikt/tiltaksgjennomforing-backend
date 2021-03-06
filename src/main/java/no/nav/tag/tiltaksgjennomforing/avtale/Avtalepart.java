package no.nav.tag.tiltaksgjennomforing.avtale;

import static no.nav.tag.tiltaksgjennomforing.persondata.PersondataService.hentGeoLokasjonFraPdlRespons;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggetBruker;
import no.nav.tag.tiltaksgjennomforing.enhet.Norg2Client;
import no.nav.tag.tiltaksgjennomforing.exceptions.KanIkkeEndreException;
import no.nav.tag.tiltaksgjennomforing.exceptions.KanIkkeOppheveException;
import no.nav.tag.tiltaksgjennomforing.exceptions.RessursFinnesIkkeException;
import no.nav.tag.tiltaksgjennomforing.exceptions.TilgangskontrollException;
import no.nav.tag.tiltaksgjennomforing.hendelselogg.Hendelselogg;
import no.nav.tag.tiltaksgjennomforing.hendelselogg.HendelseloggRepository;
import no.nav.tag.tiltaksgjennomforing.persondata.PdlRespons;

@AllArgsConstructor
@Slf4j
@Data
public abstract class Avtalepart<T extends Identifikator> {
    private final T identifikator;
    static String tekstHeaderAvtalePaabegynt = "Du må fylle ut avtalen";
    static String tekstHeaderVentAndreGodkjenning = "Vent til de andre har godkjent";
    static String tekstHeaderAvtaleErGodkjentAvAllePartner = "Avtalen er ferdig utfylt og godkjent";
    static String tekstAvtaleErGodkjentAvAllePartner = "Tiltaket starter ";
    static String tekstHeaderAvtaleVenterPaaDinGodkjenning = "Du må godkjenne ";
    static String tekstAvtaleVenterPaaAndrepartnerGodkjenning = "Andre partner må godkjenne avtalen";
    static String ekstraTekstAvtaleVenterPaaAndrePartnerGodkjenning = "Avtalen kan ikke tas i bruk før de andre har godkjent avtalen.";
    static String tekstHeaderAvtaleGjennomfores = "Tiltaket gjennomføres";
    static String tekstHeaderAvtaleErAvsluttet = "Tiltaket er avsluttet";
    static String tekstHeaderAvtaleAvbrutt = "Tiltaket er avbrutt";
    static String tekstAvtaleAvbrutt = "Veilederen har bestemt at tiltaket og avtalen skal avbrytes.";
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy");

    public boolean harTilgang(Avtale avtale) {
        if (avtale.isSlettemerket()) {
            return false;
        }
        return harTilgangTilAvtale(avtale);
    }

    abstract boolean harTilgangTilAvtale(Avtale avtale);

    abstract List<Avtale> hentAlleAvtalerMedMuligTilgang(AvtaleRepository avtaleRepository, AvtalePredicate queryParametre);

    public List<Avtale> hentAlleAvtalerMedLesetilgang(AvtaleRepository avtaleRepository, AvtalePredicate queryParametre) {
        return hentAlleAvtalerMedMuligTilgang(avtaleRepository, queryParametre).stream()
                .filter(queryParametre)
                .filter(this::harTilgang)
                .collect(Collectors.toList());
    }

    public Avtale hentAvtale(AvtaleRepository avtaleRepository, UUID avtaleId) {
        Avtale avtale = avtaleRepository.findById(avtaleId)
                .orElseThrow(RessursFinnesIkkeException::new);
        sjekkTilgang(avtale);
        return avtale;
    }

    abstract void godkjennForAvtalepart(Avtale avtale);

    abstract boolean kanEndreAvtale();

    public abstract AvtaleStatusDetaljer statusDetaljerForAvtale(Avtale avtale);

    public abstract boolean erGodkjentAvInnloggetBruker(Avtale avtale);

    abstract boolean kanOppheveGodkjenninger(Avtale avtale);

    abstract void opphevGodkjenningerSomAvtalepart(Avtale avtale);

    public void godkjennAvtale(Instant sistEndret, Avtale avtale) {
        sjekkTilgang(avtale);
        avtale.sjekkSistEndret(sistEndret);
        godkjennForAvtalepart(avtale);
    }

    public void sjekkTilgang(Avtale avtale) {
        if (!harTilgang(avtale)) {
            throw new TilgangskontrollException("Ikke tilgang til avtale");
        }
    }

    public void endreAvtale(Instant sistEndret, EndreAvtale endreAvtale, Avtale avtale, EnumSet<Tiltakstype> tiltakstyperMedTilskuddsperioder) {
        sjekkTilgang(avtale);
        if (!kanEndreAvtale()) {
            throw new KanIkkeEndreException();
        }
        avvisDatoerTilbakeITid(avtale, endreAvtale.getStartDato(), endreAvtale.getSluttDato());
        avtale.endreAvtale(sistEndret, endreAvtale, rolle(), tiltakstyperMedTilskuddsperioder);
    }

    protected void avvisDatoerTilbakeITid(Avtale avtale, LocalDate startDato, LocalDate sluttDato) {
    }

    protected abstract Avtalerolle rolle();

    public void opphevGodkjenninger(Avtale avtale) {
        if (!kanOppheveGodkjenninger(avtale)) {
            throw new KanIkkeOppheveException();
        }
        if (!avtale.erGodkjentAvVeileder() && !avtale.erGodkjentAvArbeidsgiver() && !avtale.erGodkjentAvDeltaker()) {
            throw new KanIkkeOppheveException();
        }
        opphevGodkjenningerSomAvtalepart(avtale);
    }

    public abstract void låsOppAvtale(Avtale avtale);

    public abstract InnloggetBruker innloggetBruker();

    public Collection<? extends Identifikator> identifikatorer() {
        return List.of(getIdentifikator());
    }

    public List<Hendelselogg> hentHendelselogg(UUID avtaleId, AvtaleRepository avtaleRepository, HendelseloggRepository hendelseloggRepository) {
        Avtale avtale = hentAvtale(avtaleRepository, avtaleId);
        return hendelseloggRepository.findAllByAvtaleId(avtale.getId());
    }

    protected void leggTilGeografiskEnhet(Avtale avtale, PdlRespons pdlRespons, Norg2Client norg2Client) {
        String enhet = hentGeoLokasjonFraPdlRespons(pdlRespons)
                .map(geoLokasjon -> norg2Client.hentGeografiskEnhet(geoLokasjon))
                .orElse(null);
        avtale.setEnhetGeografisk(enhet);
    }
}
