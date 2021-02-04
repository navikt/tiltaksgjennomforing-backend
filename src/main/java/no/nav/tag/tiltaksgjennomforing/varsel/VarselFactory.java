package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;

public class VarselFactory {
    private final Avtale avtale;
    private final VarslbarHendelse hendelse;

    public VarselFactory(Avtale avtale, VarslbarHendelse hendelse) {
        this.avtale = avtale;
        this.hendelse = hendelse;
    }

    public Varsel deltaker(VarslbarStatus niva) {
        return Varsel.nyttVarsel(avtale.getDeltakerFnr(), hendelse, niva, avtale);
    }

    public Varsel arbeidsgiver(VarslbarStatus niva) {
        return Varsel.nyttVarsel(avtale.getBedriftNr(), hendelse, niva, avtale);
    }

    public Varsel veileder(VarslbarStatus niva) {
        return Varsel.nyttVarsel(avtale.getVeilederNavIdent(), hendelse, niva, avtale);
    }
}
