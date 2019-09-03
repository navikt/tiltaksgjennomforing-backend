package no.nav.tag.tiltaksgjennomforing.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import no.nav.security.oidc.api.Protected;
import no.nav.tag.tiltaksgjennomforing.domene.autorisasjon.InnloggetBruker;
import no.nav.tag.tiltaksgjennomforing.domene.varsel.BjelleVarsel;
import no.nav.tag.tiltaksgjennomforing.domene.varsel.BjelleVarselService;
import no.nav.tag.tiltaksgjennomforing.integrasjon.InnloggingService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(value = "lest", required = false) Boolean lest) {
        InnloggetBruker bruker = innloggingService.hentInnloggetBruker();
        return bjelleVarselService.varslerForInnloggetBruker(bruker, avtaleId, lest);
    }

    @PostMapping("{varselId}/sett-til-lest")
    @Transactional
    public ResponseEntity settTilLest(@PathVariable("varselId") UUID varselId) {
        InnloggetBruker bruker = innloggingService.hentInnloggetBruker();
        bjelleVarselService.settTilLest(bruker, varselId);
        return ResponseEntity.ok().build();
    }
}