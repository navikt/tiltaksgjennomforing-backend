package no.nav.tag.tiltaksgjennomforing;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Avtale {

    @Id
    private Integer id;
    private LocalDateTime opprettetTidspunkt;

    private String deltakerFodselsnr;
    private String deltakerFornavn;
    private String deltakerEtternavn;
    private String deltakerAdresse;
    private String deltakerPostnummer;
    private String deltakerPoststed;

    private String bedriftNavn;
    private String bedriftAdresse;
    private String bedriftPostnummer;
    private String bedriftPoststed;

    private String arbeidsgiverFodselsnr;
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

    public static AvtaleBuilder builder() {
        return new AvtaleBuilder() {
            public Avtale build() {
                if (super.deltakerFodselsnr == null)
                    throw new IllegalArgumentException();
                return super.build();
            }
        };
    }

    public static Avtale nyAvtale(String deltakerFodselsnr) {
        return Avtale.builder()
                .deltakerFodselsnr(deltakerFodselsnr)
                .opprettetTidspunkt(LocalDateTime.now())
                .maal(new ArrayList<>())
                .oppgaver(new ArrayList<>())
                .deltakerFornavn("")
                .deltakerEtternavn("")
                .deltakerAdresse("")
                .deltakerPostnummer("")
                .deltakerPoststed("")
                .bedriftNavn("")
                .bedriftAdresse("")
                .bedriftPostnummer("")
                .bedriftPoststed("")
                .arbeidsgiverFodselsnr("")
                .arbeidsgiverFornavn("")
                .arbeidsgiverEtternavn("")
                .arbeidsgiverEpost("")
                .arbeidsgiverTlf("")
                .veilederFornavn("")
                .veilederEtternavn("")
                .veilederEpost("")
                .veilederTlf("")
                .oppfolging("")
                .tilrettelegging("")
                .startDatoTidspunkt(LocalDateTime.now())
                .arbeidstreningLengde(1)
                .arbeidstreningStillingprosent(0)
                .bekreftetAvBruker(false)
                .bekreftetAvArbeidsgiver(false)
                .bekreftetAvVeileder(false)
                .build();
    }
}
