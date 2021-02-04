package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalepart;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BjelleVarselService {
    private final BjelleVarselRepository bjelleVarselRepository;

    public List<BjelleVarsel> varslerForAvtalepart(Avtalepart avtalepart, UUID avtaleId, Boolean lest) {
        Stream<BjelleVarsel> stream = bjelleVarslerForAvtalepart(avtalepart);
        if (avtaleId != null) {
            stream = stream.filter(b -> b.getAvtaleId().equals(avtaleId));
        }
        if (lest != null) {
            stream = stream.filter(b -> b.isLest() == lest);
        }
        return stream.collect(Collectors.toList());
    }

    public void settTilLest(Avtalepart avtalepart, UUID varselId) {
        bjelleVarslerForAvtalepart(avtalepart)
                .filter(b -> b.getId().equals(varselId))
                .forEach(b -> {
                    b.settTilLest();
                    bjelleVarselRepository.save(b);
                });
    }

    public void settVarslerTilLest(Avtalepart avtalepart, List<UUID> varselIder) {
        bjelleVarslerForAvtalepart(avtalepart)
                .filter(b -> varselIder.contains(b.getId()))
                .forEach(b -> {
                    b.settTilLest();
                    bjelleVarselRepository.save(b);
                });
    }

    public List<BjelleVarsel> loggVarslerAvtaleParten(Avtalepart avtalepart, UUID avtaleId) {
        Stream<BjelleVarsel> stream = hentLoggVarslerForAvtalepart(avtalepart, avtaleId);
        return stream.collect(Collectors.toList());
    }

    public List<BjelleVarsel> varslerForAvtaleparten(Avtalepart avtalepart, UUID avtaleId, Boolean lest) {
        Stream<BjelleVarsel> stream;
        if (avtaleId != null) {
            stream = bjelleVarslerForAvtaleOgAvtalepart(avtalepart, avtaleId, VarslbarStatus.VARSEL);
        }else
            stream = bjelleVarslerForAvtaleparten(avtalepart);

        if (lest != null) {
            stream = stream.filter(varsel -> varsel.isLest() == lest);
        }
        return stream.collect(Collectors.toList());
    }

    private Stream<BjelleVarsel> hentLoggVarslerForAvtalepart(Avtalepart avtalepart, UUID avtaleId) {
        return bjelleVarselRepository.findAllByAvtaleIdAndIdentifikator(
                avtaleId, avtalepart.getIdentifikator()).stream();
    }

    private Stream<BjelleVarsel> bjelleVarslerForAvtaleOgAvtalepart(Avtalepart avtalepart, UUID avtaleId, VarslbarStatus varslbarStatus) {
        return bjelleVarselRepository.findAllByAvtaleIdAndIdentifikatorAndVarslbarStatus(
                avtaleId, avtalepart.getIdentifikator(), varslbarStatus).stream();
    }

    private Stream<BjelleVarsel> bjelleVarslerForAvtaleparten(Avtalepart avtalepart) {
        return bjelleVarselRepository.findAllByIdentifikatorAndVarslbarStatus(
                avtalepart.getIdentifikator(), VarslbarStatus.VARSEL).stream();
    }

    private Stream<BjelleVarsel> bjelleVarslerForAvtalepart(Avtalepart avtalepart) {
        return bjelleVarselRepository.findAllByTidspunktAfter(LocalDateTime.now().minusDays(1)).stream()
                .filter(bjelleVarsel -> avtalepart.identifikatorer().contains(bjelleVarsel.getIdentifikator()))
                .sorted(Comparator.comparing(BjelleVarsel::getTidspunkt).reversed());
    }
}
