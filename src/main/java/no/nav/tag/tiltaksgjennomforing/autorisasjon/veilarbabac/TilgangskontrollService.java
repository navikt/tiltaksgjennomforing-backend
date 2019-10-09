package no.nav.tag.tiltaksgjennomforing.autorisasjon.veilarbabac;

import static no.nav.tag.tiltaksgjennomforing.featuretoggles.FeatureToggleService.NY_VEILEDERTILGANG;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Fnr;
import no.nav.tag.tiltaksgjennomforing.avtale.Identifikator;
import no.nav.tag.tiltaksgjennomforing.exceptions.TilgangskontrollException;
import no.nav.tag.tiltaksgjennomforing.featuretoggles.FeatureToggleService;

@Service
@RequiredArgsConstructor
public class TilgangskontrollService {

    private final VeilarbabacClient veilarbabacClient;
    private final FeatureToggleService featureToggleService;
    private final TilgangskontrollAvslatt tilgangskontrollAvslatt; 

    public Optional<Boolean> harLesetilgangTilKandidat(Identifikator identifikator, Fnr fnr) {
        return featureToggleService.isEnabled(NY_VEILEDERTILGANG) 
                    ? tilgangskontrollAvslatt.tilgangskontrollAvslatt() 
                            ? Optional.empty() 
                            : Optional.of(hentTilgang(identifikator, fnr, TilgangskontrollAction.read)) 
                    : Optional.empty();
    }

    public Optional<Boolean> harSkrivetilgangTilKandidat(Identifikator identifikator, Fnr fnr) {
        return featureToggleService.isEnabled(NY_VEILEDERTILGANG) 
                    ? tilgangskontrollAvslatt.tilgangskontrollAvslatt() 
                            ? Optional.empty() 
                            : Optional.of(hentTilgang(identifikator, fnr, TilgangskontrollAction.update)) 
                    : Optional.empty();
    }

    public void sjekkLesetilgangTilKandidat(Identifikator identifikator, Fnr fnr) {
        sjekkTilgang(identifikator, fnr, TilgangskontrollAction.read);
    }
        
    public void sjekkSkrivetilgangTilKandidat(Identifikator identifikator, Fnr fnr) {
        sjekkTilgang(identifikator, fnr, TilgangskontrollAction.update);
    }

    private void sjekkTilgang(Identifikator identifikator, Fnr fnr, TilgangskontrollAction action) {
        if (!hentTilgang(identifikator, fnr, action)) {
            throw new TilgangskontrollException("Veileder har ikke følgende tilgang for kandidat: " + action);
        }
    }

    private boolean hentTilgang(Identifikator identifikator, Fnr fnr, TilgangskontrollAction action) {
        return !featureToggleService.isEnabled(NY_VEILEDERTILGANG) 
                || tilgangskontrollAvslatt.tilgangskontrollAvslatt()
                || veilarbabacClient.sjekkTilgang(
                        identifikator,
                        fnr.asString(),
                        action
        );
    }

}