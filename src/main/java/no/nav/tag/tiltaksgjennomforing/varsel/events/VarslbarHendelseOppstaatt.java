package no.nav.tag.tiltaksgjennomforing.varsel.events;

import lombok.Value;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import no.nav.tag.tiltaksgjennomforing.avtale.events.GamleVerdier;
import no.nav.tag.tiltaksgjennomforing.varsel.VarslbarHendelse;

@Value
public class VarslbarHendelseOppstaatt {
    private final Avtale avtale;
    private final VarslbarHendelse varslbarHendelse;
    private final GamleVerdier gamleVerdier;
    private final Avtalerolle utførtAv;
}
