package no.nav.tag.tiltaksgjennomforing.domene;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class Veileder extends Avtalepart<NavIdent> {
    public Veileder(NavIdent identifikator) {
        super(identifikator);
    }

    @Override
    public void endreGodkjenning(Avtale avtale, boolean godkjenning) {
        avtale.endreVeiledersGodkjennelse(godkjenning);
    }

    @Override
    public boolean kanEndreAvtale() {
        return true;
    }

    @Override
    public Rolle rolle() {
        return Rolle.VEILEDER;
    }
}
