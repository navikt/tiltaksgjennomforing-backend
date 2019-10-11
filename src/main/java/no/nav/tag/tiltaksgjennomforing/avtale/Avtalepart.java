package no.nav.tag.tiltaksgjennomforing.avtale;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.nav.tag.tiltaksgjennomforing.exceptions.TilgangskontrollException;
import no.nav.tag.tiltaksgjennomforing.exceptions.TiltaksgjennomforingException;

import java.util.UUID;

@AllArgsConstructor
@Data
public abstract class Avtalepart<T extends Identifikator> {
    private final T identifikator;
    final Avtale avtale;

    abstract void godkjennForAvtalepart();

    abstract boolean kanEndreAvtale();

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

    public void fylleUtAvtaleGodkjentVersjonVerdier(Integer versjon, Avtale sisteAvtaleVersjon, UUID baseAvtaleId) {
        if (!kanEndreAvtale()) {
            throw new TilgangskontrollException("Kan ikke fylle ut informasjon for ny versjon av avtale.");
        }
        avtale.fylleUtAvtaleGodkjentVersjonVerdier( sisteAvtaleVersjon,rolle());
    }

    public void opphevGodkjenninger() {
        if (!kanOppheveGodkjenninger()) {
            throw new TiltaksgjennomforingException("Kan ikke oppheve godkjenninger i avtalen.");
        }
        opphevGodkjenningerSomAvtalepart();
    }

}
