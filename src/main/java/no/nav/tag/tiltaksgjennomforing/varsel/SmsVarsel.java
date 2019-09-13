package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Identifikator;
import no.nav.tag.tiltaksgjennomforing.varsel.events.SmsVarselOpprettet;
import no.nav.tag.tiltaksgjennomforing.varsel.events.SmsVarselResultatMottatt;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SmsVarsel extends AbstractAggregateRoot {
    @Id
    private UUID id;
    private SmsVarselStatus status;
    private String telefonnummer;
    private Identifikator identifikator;
    private String meldingstekst;
    private UUID varslbarHendelse;

    public static SmsVarsel nyttVarsel(String telefonnummer,
                                       Identifikator identifikator,
                                       String meldingstekst,
                                       UUID varslbarHendelseId) {
        SmsVarsel varsel = new SmsVarsel();
        varsel.status = SmsVarselStatus.USENDT;
        varsel.telefonnummer = telefonnummer;
        varsel.identifikator = identifikator;
        varsel.meldingstekst = meldingstekst;
        varsel.varslbarHendelse = varslbarHendelseId;
        varsel.registerEvent(new SmsVarselOpprettet(varsel));
        return varsel;
    }

    public void endreStatus(SmsVarselStatus status) {
        this.setStatus(status);
        registerEvent(new SmsVarselResultatMottatt(this));
    }

    public void settId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}