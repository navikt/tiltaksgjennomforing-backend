package no.nav.tag.tiltaksgjennomforing.avtale;

import no.nav.tag.tiltaksgjennomforing.exceptions.TiltaksgjennomforingException;

public class Veileder extends Avtalepart<NavIdent> {

    public Veileder(NavIdent identifikator, Avtale avtale) {
        super(identifikator, avtale);
    }

    @Override
    public void godkjennForAvtalepart() {
        avtale.godkjennForVeileder(getIdentifikator());
    }

    public void avbrytAvtale(Integer versjon) {
        avtale.sjekkVersjon(versjon);
        avtale.avbryt(this);
    }

    @Override
    public boolean kanEndreAvtale() {
        return true;
    }


    @Override
    public void sjekkOmAvtaleKanGodkjennes() {
        if (!avtale.erGodkjentAvArbeidsgiver() || !avtale.erGodkjentAvDeltaker()) {
            throw new TiltaksgjennomforingException("Veileder må godkjenne avtalen etter deltaker og arbeidsgiver.");
        }
    }

    @Override
    boolean kanOppheveGodkjenninger() {
        return true;
    }

    @Override
    public Avtalerolle rolle() {
        return Avtalerolle.VEILEDER;
    }

    @Override
    public void godkjennForVeilederOgDeltaker(GodkjentPaVegneGrunn paVegneAvGrunn) {
        if (avtale.erGodkjentAvDeltaker()) {
            throw new TiltaksgjennomforingException("Deltaker har allerde godkjent avtalen");
        }
        if (!avtale.erGodkjentAvArbeidsgiver()) {
            throw new TiltaksgjennomforingException("Arbeidsgiver må godkjenne avtalen før veileder kan godkjenne");
        }
        paVegneAvGrunn.valgtMinstEnGrunn();
        avtale.godkjennForVeilederOgDeltaker(getIdentifikator(), paVegneAvGrunn);
    }

    @Override
    void opphevGodkjenningerSomAvtalepart() {
        avtale.opphevGodkjenningerSomVeileder();
    }
}