package no.nav.tag.tiltaksgjennomforing.avtale;

public enum Tiltakstype {
    ARBEIDSTRENING, MIDLERTIDIG_LONNSTILSKUDD, VARIG_LONNSTILSKUDD;

    // Konstantene må dupliseres på grunn av @DiscriminatorValue, som ikke takler enums eller Tiltakstype.ARBEIDSTRENING.name()
    public static final String ARBEIDSTRENING_VERDI = "ARBEIDSTRENING";
    public static final String MIDLERTIDIG_LONNSTILSKUDD_VERDI = "MIDLERTIDIG_LONNSTILSKUDD";
    public static final String VARIG_LONNSTILSKUDD_VERDI = "VARIG_LONNSTILSKUDD";
}