package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;

public class VarselFactory {
    private final Avtale avtale;
    private final VarslbarHendelse hendelse;
    private final Avtalerolle utførtAv;

    public VarselFactory(Avtale avtale, VarslbarHendelse hendelse, Avtalerolle utførtAv) {
        this.avtale = avtale;
        this.hendelse = hendelse;
        this.utførtAv = utførtAv;
    }

    public Varsel deltaker(boolean bjelle) {
        return Varsel.nyttVarsel(avtale.getDeltakerFnr(), hendelse, bjelle, avtale, Avtalerolle.DELTAKER, utførtAv);
    }

    public Varsel arbeidsgiver(boolean bjelle) {
        return Varsel.nyttVarsel(avtale.getBedriftNr(), hendelse, bjelle, avtale, Avtalerolle.ARBEIDSGIVER, utførtAv);
    }

    public Varsel veileder(boolean bjelle) {
        return Varsel.nyttVarsel(avtale.getVeilederNavIdent(), hendelse, bjelle, avtale, Avtalerolle.VEILEDER, utførtAv);
    }
}
