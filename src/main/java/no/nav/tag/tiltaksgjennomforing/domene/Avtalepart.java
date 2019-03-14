package no.nav.tag.tiltaksgjennomforing.domene;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.nav.tag.tiltaksgjennomforing.domene.exceptions.TilgangskontrollException;
import no.nav.tag.tiltaksgjennomforing.domene.exceptions.TiltaksgjennomforingException;

@AllArgsConstructor
@Data
public abstract class Avtalepart<T extends Identifikator> {
    private final T identifikator;
    final Avtale avtale;

    abstract void godkjennForAvtalepart();

    abstract boolean kanEndreAvtale();

    void sjekkOmAvtaleKanGodkjennes() {}

    abstract boolean kanOppheveGodkjenninger();

    public abstract Avtalerolle rolle();

    public void godkjennAvtale() {
        sjekkOmAvtaleKanGodkjennes();
        godkjennForAvtalepart();
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
        avtale.opphevGodkjenninger(rolle());
    }
}
