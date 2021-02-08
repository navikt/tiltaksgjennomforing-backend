package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class Varsel extends AbstractAggregateRoot<Varsel> {
    @Id
    private UUID id;
    private boolean lest;
    @Convert(converter = IdentifikatorConverter.class)
    private Identifikator identifikator;
    private String tekst;
    @Enumerated(EnumType.STRING)
    private VarslbarHendelseType hendelseType;
    private boolean bjelle;

    private UUID avtaleId;
    private LocalDateTime tidspunkt;
    @Enumerated(EnumType.STRING)
    private Avtalerolle mottaker;
    @Enumerated(EnumType.STRING)
    private Avtalerolle utførtAv;

    public static String genererVarslbarHendelseTekst(Avtale avtale, String VarslbarHendelseTekst) {
        TilskuddPeriode gjeldendePeriode = avtale.gjeldendeTilskuddsperiode();
        String avslagÅrsaker = gjeldendePeriode.getAvslagsårsaker().stream()
                .map(type -> type.getTekst().toLowerCase()).collect(Collectors.joining(", "));
        return VarslbarHendelseTekst
                .concat(gjeldendePeriode.getAvslåttAvNavIdent().asString())
                .concat(". Årsak til retur: ")
                .concat(avslagÅrsaker)
                .concat(". Forklaring: ")
                .concat(gjeldendePeriode.getAvslagsforklaring());
    }

    private static String getVarslbarHendelseTekst(VarslbarHendelse varslbarHendelse, Avtale avtale) {
        if (varslbarHendelse.getVarslbarHendelseType() == VarslbarHendelseType.TILSKUDDSPERIODE_AVSLATT) {
            return genererVarslbarHendelseTekst(avtale, varslbarHendelse.getVarslbarHendelseType().getTekst());
        }
        return varslbarHendelse.getVarslbarHendelseType().getTekst();
    }


    public static Varsel nyttVarsel(Identifikator identifikator, VarslbarHendelse varslbarHendelse, VarslbarStatus varslbarStatus, Avtale avtale, Avtalerolle mottaker, Avtalerolle utførtAv) {
        Varsel varsel = new Varsel();
        varsel.id = UUID.randomUUID();
        varsel.tidspunkt = LocalDateTime.now();
        varsel.identifikator = identifikator;
        varsel.tekst = getVarslbarHendelseTekst(varslbarHendelse, avtale);
        varsel.hendelseType = varslbarHendelse.getVarslbarHendelseType();
        varsel.avtaleId = varslbarHendelse.getAvtaleId();
        varsel.bjelle = varslbarStatus == VarslbarStatus.VARSEL;
        varsel.mottaker = mottaker;
        varsel.utførtAv = utførtAv;
        return varsel;
    }

    public void settTilLest() {
        lest = true;
    }
}
