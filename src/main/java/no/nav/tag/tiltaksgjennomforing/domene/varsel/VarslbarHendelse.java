package no.nav.tag.tiltaksgjennomforing.domene.varsel;

import lombok.Data;
import no.nav.tag.tiltaksgjennomforing.domene.Avtale;
import no.nav.tag.tiltaksgjennomforing.domene.events.VarslbarHendelseOppstaatt;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class VarslbarHendelse extends AbstractAggregateRoot {
    @Id
    private UUID id;
    private LocalDateTime tidspunkt;
    private UUID avtaleId;
    private VarslbarHendelseType varslbarHendelseType;
    @Column(keyColumn = "id")
    private List<SmsVarsel> smsVarsler = new ArrayList<>();

    public static VarslbarHendelse nyHendelse(Avtale avtale, VarslbarHendelseType varslbarHendelseType) {
        VarslbarHendelse varslbarHendelse = new VarslbarHendelse();
        varslbarHendelse.avtaleId = avtale.getId();
        varslbarHendelse.varslbarHendelseType = varslbarHendelseType;
        varslbarHendelse.smsVarsler.addAll(varslbarHendelse.lagSmsVarsler(avtale));
        varslbarHendelse.registerEvent(new VarslbarHendelseOppstaatt(avtale, varslbarHendelse));
        return varslbarHendelse;
    }

    private List<SmsVarsel> lagSmsVarsler(Avtale avtale) {
        SmsVarselFactory factory = new SmsVarselFactory(avtale);
        switch (varslbarHendelseType) {
            case GODKJENT_AV_DELTAKER:
            case GODKJENT_AV_ARBEIDSGIVER:
                return Arrays.asList(factory.veileder());
            case GODKJENT_AV_VEILEDER:
                return Arrays.asList(factory.deltaker(), factory.arbeidsgiver());
            case GODKJENT_PAA_VEGNE_AV:
                return Arrays.asList(factory.arbeidsgiver());
            default:
                return Collections.emptyList();
        }
    }

    public void settStatusPaaSmsVarsel(UUID smsVarselId, SmsVarselStatus status) {
        for (SmsVarsel smsVarsel : smsVarsler) {
            if (smsVarsel.getId().equals(smsVarselId)) {
                smsVarsel.setStatus(status);
            }
        }
    }

    public void settIdOgOpprettetTidspunkt() {
        this.id = UUID.randomUUID();
        this.tidspunkt = LocalDateTime.now();
        this.smsVarsler.forEach(SmsVarsel::settId);
    }
}