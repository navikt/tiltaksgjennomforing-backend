package no.nav.tag.tiltaksgjennomforing.domene;

import lombok.Data;
import no.nav.tag.tiltaksgjennomforing.domene.exceptions.TiltaksgjennomforingException;
import org.springframework.data.annotation.Id;

import java.util.UUID;


@Data
public class GodkjentPaVegneGrunn {
    @Id
    private UUID avtale;
    private boolean ikkeMinId;
    private boolean reservert;
    private boolean digitalKompetanse;

    public void valgtMinstEnGrunn() {
        if (!ikkeMinId && !reservert && !digitalKompetanse) {
            throw new TiltaksgjennomforingException("Minst èn grunn må være valgt");
        }
    }
}

