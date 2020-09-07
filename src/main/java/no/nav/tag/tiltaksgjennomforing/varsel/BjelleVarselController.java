package no.nav.tag.tiltaksgjennomforing.varsel;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import no.nav.security.oidc.api.Protected;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggetBruker;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggingService;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Protected
@RestController
@RequestMapping("/varsler")
@Timed
@RequiredArgsConstructor
public class BjelleVarselController {
    private final InnloggingService innloggingService;
    private final BjelleVarselService bjelleVarselService;

    @GetMapping
    public Iterable<BjelleVarsel> hentVarsler(
            @RequestParam(value = "avtaleId", required = false) UUID avtaleId,
            @RequestParam(value = "lest", required = false) Boolean lest, @CookieValue("innlogget-part") Optional<Avtalerolle> innloggetPart) {
        InnloggetBruker<?> bruker = innloggingService.hentInnloggetBruker(innloggetPart.get());
        return bjelleVarselService.varslerForInnloggetBruker(bruker, avtaleId, lest);
    }

    @PostMapping("{varselId}/sett-til-lest")
    @Transactional
    public ResponseEntity<?> settTilLest(@PathVariable("varselId") UUID varselId, Optional<Avtalerolle> innloggetPart) {
        InnloggetBruker<?> bruker = innloggingService.hentInnloggetBruker(innloggetPart.get());
        bjelleVarselService.settTilLest(bruker, varselId);
        return ResponseEntity.ok().build();
    }
}
