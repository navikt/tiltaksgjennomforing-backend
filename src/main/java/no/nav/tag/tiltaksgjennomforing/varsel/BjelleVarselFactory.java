package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;

public class BjelleVarselFactory {
    private final Avtale avtale;
    private final VarslbarHendelse hendelse;

    public BjelleVarselFactory(Avtale avtale, VarslbarHendelse hendelse) {
        this.avtale = avtale;
        this.hendelse = hendelse;
    }

    public BjelleVarsel deltaker(VarslbarStatus niva) {
        return BjelleVarsel.nyttVarsel(avtale.getDeltakerFnr(), hendelse, niva, avtale);
    }

    public BjelleVarsel arbeidsgiver(VarslbarStatus niva) {
        return BjelleVarsel.nyttVarsel(avtale.getBedriftNr(), hendelse, niva, avtale);
    }

    public BjelleVarsel veileder(VarslbarStatus niva) {
        return BjelleVarsel.nyttVarsel(avtale.getVeilederNavIdent(), hendelse, niva, avtale);
    }
}
