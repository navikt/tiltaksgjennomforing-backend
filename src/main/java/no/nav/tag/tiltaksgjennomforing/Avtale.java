package no.nav.tag.tiltaksgjennomforing;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Avtale {

    @Id
    private String id;
    private LocalDateTime opprettetTidspunkt = LocalDateTime.now();

    private String deltakerFornavn;
    private String deltakerEtternavn;
    private String deltakerAdresse;
    private String deltakerPostnummer;
    private String deltakerPoststed;

    private String bedriftNavn;
    private String bedriftAdresse;
    private String bedriftPostnummer;
    private String bedriftPoststed;

    private String arbeidsgiverFornavn;
    private String arbeidsgiverEtternavn;
    private String arbeidsgiverEpost;
    private String arbeidsgiverTlf;

    private String veilederFornavn;
    private String veilederEtternavn;
    private String veilederEpost;
    private String veilederTlf;

    private String oppfolging;
    private String tilrettelegging;

    private LocalDateTime startDatoTidspunkt;
    private Integer arbeidstreningLengde;
    private Integer arbeidstreningStillingprosent;

    @Column(keyColumn = "id")
    private List<Maal> maal;
    @Column(keyColumn = "id")
    private List<Oppgave> oppgaver;

    private boolean bekreftetAvBruker;
    private boolean bekreftetAvArbeidsgiver;
    private boolean bekreftetAvVeileder;
}
