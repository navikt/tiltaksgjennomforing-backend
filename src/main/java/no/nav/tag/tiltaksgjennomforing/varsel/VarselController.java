package no.nav.tag.tiltaksgjennomforing.varsel;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import no.nav.security.oidc.api.Protected;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggingService;
import no.nav.tag.tiltaksgjennomforing.avtale.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Protected
@RestController
@RequestMapping("/varsler")
@Timed
@RequiredArgsConstructor
public class VarselController {
    private final InnloggingService innloggingService;
    private final VarselService varselService;
    private final AvtaleRepository avtaleRepository;

    @GetMapping
    public Iterable<Varsel> hentVarsler(
        @CookieValue("innlogget-part") Avtalerolle innloggetPart) {
        Avtalepart avtalepart = innloggingService.hentAvtalepart(innloggetPart);
        return varselService.varslerForOversikt(avtalepart.getIdentifikator());
    }

    @GetMapping("/logg-varsler")
    public List<Varsel> hentLoggVarsler(
            @RequestParam(value = "avtaleId") UUID avtaleId, @CookieValue("innlogget-part") Avtalerolle innloggetPart) {
        Avtalepart avtalepart = innloggingService.hentAvtalepart(innloggetPart);
        return varselService.varslerForAvtale(avtaleId, avtalepart.getIdentifikator());
    }

    @PostMapping("{varselId}/sett-til-lest")
    @Transactional
    public ResponseEntity<?> settTilLest(@PathVariable("varselId") UUID varselId, @CookieValue("innlogget-part") Avtalerolle innloggetPart) {
        Avtalepart avtalepart = innloggingService.hentAvtalepart(innloggetPart);
        varselService.settTilLest(avtalepart, varselId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sett-alle-til-lest")
    @Transactional
    public ResponseEntity<?> settVarslerTilLest(@RequestBody List<UUID> varselIder, @CookieValue("innlogget-part") Avtalerolle innloggetPart) {
        Avtalepart avtalepart = innloggingService.hentAvtalepart(innloggetPart);
        varselService.settVarslerTilLest(avtalepart, varselIder);
        return ResponseEntity.ok().build();
    }
}
