package no.nav.tag.tiltaksgjennomforing.varsel;

import io.micrometer.core.annotation.Timed;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import no.nav.tag.tiltaksgjennomforing.avtale.Identifikator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface VarselRepository extends JpaRepository<Varsel, UUID> {
    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    @Override
    List<Varsel> findAll();

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByTidspunktAfter(LocalDateTime tidspunkt);

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByAvtaleIdAndMottaker(UUID avtaleId, Avtalerolle mottaker);

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByAvtaleIdAndIdentifikatorAndVarslbarStatus(UUID avtaleId, Identifikator identifikator, VarslbarStatus varslbarStatus);

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByIdentifikatorAndVarslbarStatus(Identifikator identifikator, VarslbarStatus varslbarStatus);

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByLestIsFalseAndAndIdentifikatorAndVarslbarStatus(Identifikator identifikator, VarslbarStatus varslbarStatus);
}
