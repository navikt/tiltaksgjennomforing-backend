package no.nav.tag.tiltaksgjennomforing.avtale;

public interface AvtaleGodkjenningStartegy {
    boolean harDeltakerFyltUtAlt();
    boolean harVeilederFyltUtAlt();
    boolean harArbeidsgiverFyltUtAlt();
    boolean erAltUtfylt();
}
