package no.nav.tag.tiltaksgjennomforing.avtale;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.nav.tag.tiltaksgjennomforing.exceptions.TilgangskontrollException;
import no.nav.tag.tiltaksgjennomforing.exceptions.TiltaksgjennomforingException;

@AllArgsConstructor
@Data
public abstract class Avtalepart<T extends Identifikator> {
    private final T identifikator;
    final Avtale avtale;
    static String tekstHeaderAvtaleErIkkkeFyltUt = "Avtalen har ikke fylt ut enda";
    static String tekstHeaderAvtaleErGodkjentAvInnloggetBruker = "Du har godkjent avtalen.";
    static String tekstAvtaleErGodkjentAvAllePartner = "Alle partner har godkjent avtalen og den er klar for oppstart nå.";
    static String ekstraTekstAvtaleErGodkjentAvAllePartner = " Her er det ekstra tekst om alle partner har godkjent avtalen ....";
    static String tekstHeaderAvtaleVenterPaaDinGodkjenning = "Du må godkjenne avtalen ";
    static String tekstAvtaleVenterPaaDinGodkjenning = "Hele avtalen er nå fylt ut og klar for godkjenning av deg. Les hele avtalen først. Hvis du er uenig i innholdet, eller har spørsmål til avtalen, bør du kontakte din veileder via Aktivitetsplanen før du godkjenner.";
    static String ekstraTekstAvtaleVenterPaaDinGodkjenning = "Vennligst godkjenn avtalen så fort som mulig";
    static String tekstAvtaleVenterPaaVeilederGodkjenning = "Veileder må godkjenne avtalen";
    static String ekstraTekstAvtaleVenterPaaVeilederGodkjenning = "Avtalen kan ikke tas i bruk før at veileder har godkjent avtalen, be veildere for å godkjenne avtalen.";
    static String tekstAvtaleVenterPaaAndrepartnerGodkjenning = "Andre partner må godkjenne avtalen";
    static String ekstraTekstAvtaleVenterPaaAndrePartnerGodkjenning = "Avtalen kan ikke tas i bruk før at alle har godkjent avtalen, be andre partner for å godkjenne avtalen.";

    abstract void godkjennForAvtalepart();

    abstract boolean kanEndreAvtale();

    public abstract AvtaleStatusDetaljer statusDetaljerForAvtale();

    public abstract boolean erGodkjentAvInnloggetBruker();

    void sjekkOmAvtaleKanGodkjennes() {
    }

    abstract boolean kanOppheveGodkjenninger();

    public abstract Avtalerolle rolle();

    abstract void godkjennForVeilederOgDeltaker(GodkjentPaVegneGrunn paVegneAvGrunn);

    abstract void opphevGodkjenningerSomAvtalepart();

    public void godkjennAvtale(Integer versjon) {
        avtale.sjekkVersjon(versjon);
        sjekkOmAvtaleKanGodkjennes();
        godkjennForAvtalepart();
    }


    public void godkjennPaVegneAvDeltaker(GodkjentPaVegneGrunn paVegneAvGrunn, Integer versjon) {
        avtale.sjekkVersjon(versjon);
        godkjennForVeilederOgDeltaker(paVegneAvGrunn);
    }

    public void endreAvtale(Integer versjon, EndreAvtale endreAvtale) {
        if (!kanEndreAvtale()) {
            throw new TilgangskontrollException("Kan ikke endre avtale.");
        }
        avtale.endreAvtale(versjon, endreAvtale, rolle());
    }

    public void opphevGodkjenninger() {
        if (!kanOppheveGodkjenninger()) {
            throw new TiltaksgjennomforingException("Kan ikke oppheve godkjenninger i avtalen.");
        }
        opphevGodkjenningerSomAvtalepart();
    }

}
