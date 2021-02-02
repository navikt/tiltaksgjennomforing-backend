package no.nav.tag.tiltaksgjennomforing.hendelselogg;

import lombok.Data;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import no.nav.tag.tiltaksgjennomforing.varsel.BjelleVarsel;
import no.nav.tag.tiltaksgjennomforing.varsel.VarslbarHendelseType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Hendelselogg {
    @Id
    private UUID id;
    private UUID avtaleId;
    private LocalDateTime tidspunkt;
    @Enumerated(EnumType.STRING)
    private Avtalerolle utførtAv;
    @Enumerated(EnumType.STRING)
    private VarslbarHendelseType hendelse;
    private String beskrivelse;
    @Enumerated(EnumType.STRING)
    private HendelseStatus hendelseStatus;


    private static String hendelseBeskrivelse(Avtale avtale, VarslbarHendelseType hendelse) {
        if (hendelse == VarslbarHendelseType.TILSKUDDSPERIODE_AVSLATT) {
            return BjelleVarsel.genererVarslbarHendelseTekst(avtale, hendelse.getTekst());
        }
        return hendelse.getTekst();
    }

    private static Hendelselogg lagHendelse(Avtale avtale, Avtalerolle utførtAv, VarslbarHendelseType hendelse, HendelseStatus status) {
        Hendelselogg hendelselogg = new Hendelselogg();
        hendelselogg.setId(UUID.randomUUID());
        hendelselogg.setAvtaleId(avtale.getId());
        hendelselogg.setTidspunkt(LocalDateTime.now());
        hendelselogg.setUtførtAv(utførtAv);
        hendelselogg.setHendelse(hendelse);
        hendelselogg.setBeskrivelse(hendelseBeskrivelse(avtale, hendelse));
        hendelselogg.setHendelseStatus(status);
        return hendelselogg;
    }

    public static Hendelselogg nyHendelse(Avtale avtale, Avtalerolle utførtAv, VarslbarHendelseType hendelse) {
      return lagHendelse(avtale, utførtAv, hendelse, HendelseStatus.FELLES);
    }
    public static Hendelselogg nyHendelse(Avtale avtale, Avtalerolle utførtAv, VarslbarHendelseType hendelse, HendelseStatus status) {
        return lagHendelse(avtale, utførtAv, hendelse, status);
    }
}

