package no.nav.tag.tiltaksgjennomforing.avtale.events;

import lombok.Value;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.TilskuddPeriode;

@Value
public class TilskuddsperiodeForkortet {
    Avtale avtale;
    TilskuddPeriode tilskuddsperiode;
}
