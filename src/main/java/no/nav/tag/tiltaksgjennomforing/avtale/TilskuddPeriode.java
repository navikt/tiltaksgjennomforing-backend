package no.nav.tag.tiltaksgjennomforing.avtale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import no.nav.tag.tiltaksgjennomforing.exceptions.Feilkode;
import no.nav.tag.tiltaksgjennomforing.exceptions.FeilkodeException;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TilskuddPeriode {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "avtale_innhold")
    @JsonIgnore
    @ToString.Exclude
    private AvtaleInnhold avtaleInnhold;

    @NonNull
    private Integer beløp;
    @NonNull
    private LocalDate startDato;
    @NonNull
    private LocalDate sluttDato;

    @Convert(converter = NavIdentConverter.class)
    private NavIdent godkjentAvNavIdent;

    private LocalDateTime godkjentTidspunkt;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Avslagsårsak> avslagsårsaker = EnumSet.noneOf(Avslagsårsak.class);

    private String avslagsforklaring;
    @Convert(converter = NavIdentConverter.class)
    private NavIdent avslåttAvNavIdent;
    private LocalDateTime avslåttTidspunkt;

    @Enumerated(EnumType.STRING)
    private TilskuddPeriodeStatus status = TilskuddPeriodeStatus.UBEHANDLET;

    public TilskuddPeriode(TilskuddPeriode periode) {
        id = UUID.randomUUID();
        beløp = periode.beløp;
        startDato = periode.startDato;
        sluttDato = periode.sluttDato;
        status = periode.status;
    }

    public Avtale hentAvtale() {
        return getAvtaleInnhold().getAvtale();
    }

    private void sjekkOmKanBehandles() {
        if (status != TilskuddPeriodeStatus.UBEHANDLET) {
            throw new FeilkodeException(Feilkode.TILSKUDDSPERIODE_ER_ALLEREDE_BEHANDLET);
        }
        if (!getAvtaleInnhold().getAvtale().erGodkjentAvVeileder()) {
            throw new FeilkodeException(Feilkode.TILSKUDDSPERIODE_KAN_KUN_BEHANDLES_VED_INNGAATT_AVTALE);
        }
        if (startDato.isBefore(LocalDate.now().minusWeeks(2))) {
            throw new FeilkodeException(Feilkode.TILSKUDDSPERIODE_BEHANDLE_FOR_TIDLIG);
        }
    }

    public void godkjenn(NavIdent beslutter) {
        sjekkOmKanBehandles();

        setGodkjentTidspunkt(LocalDateTime.now());
        setGodkjentAvNavIdent(beslutter);
        setStatus(TilskuddPeriodeStatus.GODKJENT);
    }

    public void avslå(NavIdent beslutter, EnumSet<Avslagsårsak> avslagsårsaker, String avslagsforklaring) {
        sjekkOmKanBehandles();
        if (avslagsforklaring.isBlank()) {
            throw new FeilkodeException(Feilkode.TILSKUDDSPERIODE_AVSLAGSFORKLARING_PAAKREVD);
        }
        if (avslagsårsaker.isEmpty()) {
            throw new FeilkodeException(Feilkode.TILSKUDDSPERIODE_INGEN_AVSLAGSAARSAKER);
        }

        setAvslåttTidspunkt(LocalDateTime.now());
        setAvslåttAvNavIdent(beslutter);
        this.avslagsårsaker.addAll(avslagsårsaker);
        setAvslagsforklaring(avslagsforklaring);
        setStatus(TilskuddPeriodeStatus.AVSLÅTT);
    }
}
