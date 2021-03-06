package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;

public class BjelleVarselFactory {
    private final Avtale avtale;
    private final VarslbarHendelse hendelse;

    public BjelleVarselFactory(Avtale avtale, VarslbarHendelse hendelse) {
        this.avtale = avtale;
        this.hendelse = hendelse;
    }

    public BjelleVarsel deltaker() {
        return BjelleVarsel.nyttVarsel(avtale.getDeltakerFnr(), hendelse, avtale);
    }

    public BjelleVarsel arbeidsgiver() {
        return BjelleVarsel.nyttVarsel(avtale.getBedriftNr(), hendelse, avtale);
    }

    public BjelleVarsel veileder() {
        return BjelleVarsel.nyttVarsel(avtale.getVeilederNavIdent(), hendelse, avtale);
    }
}
