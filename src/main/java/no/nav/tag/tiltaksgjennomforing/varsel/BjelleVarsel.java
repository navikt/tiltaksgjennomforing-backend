package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Identifikator;
import no.nav.tag.tiltaksgjennomforing.avtale.IdentifikatorConverter;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class BjelleVarsel extends AbstractAggregateRoot {
    @Id
    private UUID id;
    private boolean lest;
    @Convert(converter = IdentifikatorConverter.class)
    private Identifikator identifikator;
    private String varslingstekst;
    private UUID varslbarHendelse;
    private UUID avtaleId;
    private LocalDateTime tidspunkt;

    public static BjelleVarsel nyttVarsel(Identifikator identifikator, VarslbarHendelse varslbarHendelse) {
        BjelleVarsel varsel = new BjelleVarsel();
        varsel.id = UUID.randomUUID();
        varsel.tidspunkt = LocalDateTime.now();
        varsel.identifikator = identifikator;
        varsel.varslbarHendelse = varslbarHendelse.getId();
        varsel.varslingstekst = varslbarHendelse.getVarslbarHendelseType().getTekst();
        varsel.avtaleId = varslbarHendelse.getAvtaleId();
        return varsel;
    }

    public void settTilLest() {
        lest = true;
    }
}
