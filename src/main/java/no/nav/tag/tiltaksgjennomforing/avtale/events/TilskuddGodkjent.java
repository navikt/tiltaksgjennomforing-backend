package no.nav.tag.tiltaksgjennomforing.avtale.events;

import lombok.Value;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.Identifikator;

@Value
public class TilskuddGodkjent implements AvtaleGodkjent {

    Avtale avtale;
    Identifikator utfortAv;
}
