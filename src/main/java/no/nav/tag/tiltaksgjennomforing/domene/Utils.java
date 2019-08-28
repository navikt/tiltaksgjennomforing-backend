package no.nav.tag.tiltaksgjennomforing.domene;

import lombok.experimental.UtilityClass;
import no.nav.tag.tiltaksgjennomforing.domene.exceptions.TiltaksgjennomforingException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@UtilityClass
public class Utils {
    public static <T> T sjekkAtIkkeNull(T in, String feilmelding) {
        if (in == null) {
            throw new TiltaksgjennomforingException(feilmelding);
        }
        return in;
    }

    public static boolean erIkkeTomme(Object... objekter) {
        for (Object objekt : objekter) {
            if (objekt instanceof String && ((String) objekt).isEmpty()) {
                return false;
            }
            if (objekt == null) {
                return false;
            }
        }
        return true;
    }

    public static URI lagUri(String path) {
        return UriComponentsBuilder
                .fromPath(path)
                .build()
                .toUri();
    }

    public static void sjekkAtTekstIkkeOverskrider1000Tegn(String tekst, String feilmelding) {
        if ((tekst != null) && tekst.length() > 1000) {
            throw new TiltaksgjennomforingException(feilmelding);
        }
    }
}
