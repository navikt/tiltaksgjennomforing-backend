package no.nav.tag.tiltaksgjennomforing.avtale;

public class LonnstilskuddGodkjenningStrategy implements AvtaleGodkjenningStartegy {
    @Override
    public boolean harDeltakerFyltUtAlt() {
        return false;
    }

    @Override
    public boolean harVeilederFyltUtAlt() {
        return false;
    }

    @Override
    public boolean harArbeidsgiverFyltUtAlt() {
        return false;
    }

    @Override
    public boolean erAltUtfylt() {
        return false;
    }
}
