package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalepart;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VarselService {
    private final VarselRepository varselRepository;

    public void settTilLest(Avtalepart avtalepart, UUID varselId) {
        varslerForAvtalepart(avtalepart)
                .filter(b -> b.getId().equals(varselId))
                .forEach(b -> {
                    b.settTilLest();
                    varselRepository.save(b);
                });
    }

    public void settVarslerTilLest(Avtalepart avtalepart, List<UUID> varselIder) {
        varslerForAvtalepart(avtalepart)
                .filter(b -> varselIder.contains(b.getId()))
                .forEach(b -> {
                    b.settTilLest();
                    varselRepository.save(b);
                });
    }

    public List<Varsel> varslerForAvtale(Avtalepart avtalepart, UUID avtaleId, Avtalerolle innloggetPart) {
        return varselRepository.findAllByAvtaleIdAndMottaker(avtaleId, innloggetPart);
    }

    public List<Varsel> varslerForOversikt(Avtalepart avtalepart) {
        return varselRepository.findAllByLestIsFalseAndAndIdentifikatorAndVarslbarStatus(
                avtalepart.getIdentifikator(), VarslbarStatus.VARSEL);
    }

    private Stream<Varsel> varslerForAvtalepart(Avtalepart avtalepart) {
        return varselRepository.findAllByTidspunktAfter(LocalDateTime.now().minusDays(1)).stream()
                .filter(bjelleVarsel -> avtalepart.identifikatorer().contains(bjelleVarsel.getIdentifikator()))
                .sorted(Comparator.comparing(Varsel::getTidspunkt).reversed());
    }
}
