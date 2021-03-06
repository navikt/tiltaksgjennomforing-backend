package no.nav.tag.tiltaksgjennomforing.avtale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import no.nav.tag.tiltaksgjennomforing.exceptions.Feilkode;
import no.nav.tag.tiltaksgjennomforing.exceptions.FeilkodeException;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jetbrains.annotations.NotNull;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
public class TilskuddPeriode implements Comparable<TilskuddPeriode> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "avtale_id")
    @JsonIgnore
    @ToString.Exclude
    private Avtale avtale;

    @NonNull
    private Integer beløp;
    @NonNull
    private LocalDate startDato;
    @NonNull
    private LocalDate sluttDato;

    @Convert(converter = NavIdentConverter.class)
    private NavIdent godkjentAvNavIdent;

    private LocalDateTime godkjentTidspunkt;

    private String enhet;

    @NonNull
    private Integer lonnstilskuddProsent;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Avslagsårsak> avslagsårsaker = EnumSet.noneOf(Avslagsårsak.class);

    private String avslagsforklaring;
    @Convert(converter = NavIdentConverter.class)
    private NavIdent avslåttAvNavIdent;
    private LocalDateTime avslåttTidspunkt;
    private Integer løpenummer;

    @Enumerated(EnumType.STRING)
    private TilskuddPeriodeStatus status = TilskuddPeriodeStatus.UBEHANDLET;

    private boolean aktiv = true;

    public TilskuddPeriode deaktiverOgLagNyUbehandlet() {
        this.aktiv = false;
        TilskuddPeriode kopi = new TilskuddPeriode();
        kopi.id = UUID.randomUUID();
        kopi.løpenummer = this.løpenummer;
        kopi.beløp = this.beløp;
        kopi.lonnstilskuddProsent = this.lonnstilskuddProsent;
        kopi.startDato = this.startDato;
        kopi.sluttDato = this.sluttDato;
        kopi.avtale = this.avtale;
        kopi.aktiv = true;
        kopi.status = TilskuddPeriodeStatus.UBEHANDLET;
        return kopi;
    }

    private void sjekkOmKanBehandles() {
        if (status != TilskuddPeriodeStatus.UBEHANDLET) {
            throw new FeilkodeException(Feilkode.TILSKUDDSPERIODE_ER_ALLEREDE_BEHANDLET);
        }
        if (LocalDate.now().isBefore(kanBesluttesFom())) {
            throw new FeilkodeException(Feilkode.TILSKUDDSPERIODE_BEHANDLE_FOR_TIDLIG);
        }
    }

    @JsonProperty
    private LocalDate kanBesluttesFom() {
        if (løpenummer == 1) {
            return LocalDate.MIN;
        }
        return startDato.minusMonths(1);
    }

    void godkjenn(NavIdent beslutter, String enhet) {
        sjekkOmKanBehandles();

        setGodkjentTidspunkt(LocalDateTime.now());
        setGodkjentAvNavIdent(beslutter);
        setEnhet(enhet);
        setStatus(TilskuddPeriodeStatus.GODKJENT);
    }

    void avslå(NavIdent beslutter, EnumSet<Avslagsårsak> avslagsårsaker, String avslagsforklaring) {
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

    public boolean kanBehandles() {
        try {
            sjekkOmKanBehandles();
            return true;
        } catch (FeilkodeException e) {
            return false;
        }
    }

    @Override
    public int compareTo(@NotNull TilskuddPeriode o) {
        return new CompareToBuilder()
                .append(this.getStartDato(), o.getStartDato())
                .append(this.isAktiv(), o.isAktiv())
                .append(this.getStatus(), o.getStatus())
                .append(this.getId(), o.getId())
                .toComparison();
    }
}
