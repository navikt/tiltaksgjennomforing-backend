package no.nav.tag.tiltaksgjennomforing.domene.varsel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.domene.Identifikator;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SmsVarsel {
    @Id
    private UUID id;
    private SmsVarselStatus status;
    private String telefonnummer;
    private Identifikator identifikator;
    private String meldingstekst;

    public static SmsVarsel nyttVarsel(String telefonnummer,
                                       Identifikator identifikator,
                                       String meldingstekst) {
        SmsVarsel varsel = new SmsVarsel();
        varsel.status = SmsVarselStatus.USENDT;
        varsel.telefonnummer = telefonnummer;
        varsel.identifikator = identifikator;
        varsel.meldingstekst = meldingstekst;
        return varsel;
    }

    public void settId() {
        this.id = UUID.randomUUID();
    }
}
