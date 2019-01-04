package no.nav.tag.tiltaksgjennomforing.domene;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Veileder extends Person {
    private NavIdent navIdent;
}
