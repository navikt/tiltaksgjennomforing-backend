package no.nav.tag.tiltaksgjennomforing.varsel;

import io.micrometer.core.annotation.Timed;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import no.nav.tag.tiltaksgjennomforing.avtale.Identifikator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface  VarselRepository extends JpaRepository<Varsel, UUID> {
    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    @Override
    List<Varsel> findAll();

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByTidspunktAfter(LocalDateTime tidspunkt);

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByAvtaleIdAndMottaker(UUID avtaleId, Avtalerolle mottaker);


    @Transactional
    @Modifying()
    @Query(value = "UPDATE Varsel v SET v.identifikator = :identifikator WHERE (v.avtaleId = :avtaleId AND v.identifikator = :identifikatorGammelVeileder) OR (v.avtaleId = :avtaleId AND v.identifikator is null)")
    void updateVeilederIdentifikator(@Param("identifikator") Identifikator identifikator, @Param("avtaleId") UUID avtaleId, @Param("identifikatorGammelVeileder") Identifikator identifikatorGammelVeileder);

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByAvtaleIdAndIdentifikatorAndVarslbarStatus(UUID avtaleId, Identifikator identifikator, VarslbarStatus varslbarStatus);

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByIdentifikatorAndVarslbarStatus(Identifikator identifikator, VarslbarStatus varslbarStatus);

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByLestIsFalseAndAndIdentifikatorAndVarslbarStatus(Identifikator identifikator, VarslbarStatus varslbarStatus);

    @Timed(percentiles = { 0.5d, 0.75d, 0.9d, 0.99d, 0.999d })
    List<Varsel> findAllByLestIsFalseAndVarslbarStatusAndMottakerAndAvtaleIdIn(VarslbarStatus varslbarStatus, Avtalerolle mottaker, Set<UUID> avtaleId);
}
