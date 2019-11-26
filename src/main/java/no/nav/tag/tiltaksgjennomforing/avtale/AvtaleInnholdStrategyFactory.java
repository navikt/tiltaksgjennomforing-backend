package no.nav.tag.tiltaksgjennomforing.avtale;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AvtaleInnholdStrategyFactory {
    public AvtaleInnholdStrategy create(AvtaleInnhold avtaleInnhold, Tiltakstype tiltakstype) {
        switch (tiltakstype) {
            case ARBEIDSTRENING:
                return new ArbeidstreningStrategy(avtaleInnhold);
            case MIDLERTIDIG_LONNSTILSKUDD:
                return new MidlertidigLonnstilskuddStrategy(avtaleInnhold);
            case VARIG_LONNSTILSKUDD:
                return new VarigLonnstilskuddStrategy(avtaleInnhold);
        }
        throw new IllegalStateException();
    }
}