package no.nav.tag.tiltaksgjennomforing.avtale.events;

import lombok.Value;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;

@Value
public class AvtaleEndret {
    Avtale avtale;
    Avtalerolle utfortAv;
}
