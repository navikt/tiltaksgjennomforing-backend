package no.nav.tag.tiltaksgjennomforing.avtale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndreAvtale {

    private String deltakerFornavn;
    private String deltakerEtternavn;
    private String deltakerTlf;
    private String bedriftNavn;
    private String arbeidsgiverFornavn;
    private String arbeidsgiverEtternavn;
    private String arbeidsgiverTlf;
    private String veilederFornavn;
    private String veilederEtternavn;
    private String veilederTlf;

    private String oppfolging;
    private String tilrettelegging;

    private LocalDate startDato;
    private LocalDate sluttDato;
    private Integer stillingprosent;

    // Arbeidstreningsfelter
    private List<Maal> maal = new ArrayList<>();
    private List<Oppgave> oppgaver = new ArrayList<>();
    private String arbeidsoppgaver;

    // Lønnstilskuddsfelter
    private String arbeidsgiverKontonummer;
    private String stillingtype;
    private String arbeidsoppgaver;
    private Integer lonnstilskuddProsent;
    private Integer manedslonn;
    private BigDecimal feriepengesats;
    private BigDecimal arbeidsgiveravgift;

    // Mentorfelter
    private String mentorFornavn;
    private String mentorEtternavn;
    private String mentorOppgaver;
    private Integer mentorAntallTimer;
    private Integer mentorTimelonn;
}
