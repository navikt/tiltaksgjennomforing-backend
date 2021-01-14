package no.nav.tag.tiltaksgjennomforing.autorisasjon;

import lombok.RequiredArgsConstructor;
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.AltinnReportee;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.TokenUtils.BrukerOgIssuer;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.TokenUtils.Issuer;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.altinntilgangsstyring.AltinnTilgangsstyringService;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.veilarbabac.TilgangskontrollService;
import no.nav.tag.tiltaksgjennomforing.avtale.*;
import no.nav.tag.tiltaksgjennomforing.enhet.Norg2Client;
import no.nav.tag.tiltaksgjennomforing.exceptions.TilgangskontrollException;
import no.nav.tag.tiltaksgjennomforing.featuretoggles.enhet.AxsysService;
import no.nav.tag.tiltaksgjennomforing.persondata.PersondataService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InnloggingService {

    private final SystembrukerProperties systembrukerProperties;
    private final BeslutterAdGruppeProperties beslutterAdGruppeProperties;
    private final TokenUtils tokenUtils;
    private final AltinnTilgangsstyringService altinnTilgangsstyringService;
    private final TilgangskontrollService tilgangskontrollService;
    private final PersondataService persondataService;
    private final Norg2Client norg2Client;
    private final TilskuddPeriodeRepository tilskuddPeriodeRepository;
    private final AxsysService axsysService;

    public Avtalepart hentAvtalepart(Avtalerolle avtalerolle) {
        BrukerOgIssuer brukerOgIssuer = tokenUtils.hentBrukerOgIssuer().orElseThrow(() -> new TilgangskontrollException("Bruker er ikke innlogget."));
        Issuer issuer = brukerOgIssuer.getIssuer();

        if (issuer == Issuer.ISSUER_SELVBETJENING && avtalerolle == Avtalerolle.DELTAKER) {
            return new Deltaker(new Fnr(brukerOgIssuer.getBrukerIdent()));
        }

        else if (issuer == Issuer.ISSUER_SELVBETJENING && avtalerolle == Avtalerolle.ARBEIDSGIVER) {
            Set<AltinnReportee> altinnOrganisasjoner = altinnTilgangsstyringService
                    .hentAltinnOrganisasjoner(new Fnr(brukerOgIssuer.getBrukerIdent()));
            Map<BedriftNr, Collection<Tiltakstype>> tilganger = altinnTilgangsstyringService.hentTilganger(new Fnr(brukerOgIssuer.getBrukerIdent()));
            return new Arbeidsgiver(new Fnr(brukerOgIssuer.getBrukerIdent()), altinnOrganisasjoner, tilganger, persondataService, norg2Client);
        }

        else if (issuer == Issuer.ISSUER_ISSO && avtalerolle == Avtalerolle.VEILEDER) {
            return new Veileder(new NavIdent(brukerOgIssuer.getBrukerIdent()), tilgangskontrollService, persondataService, norg2Client);
        }

        else if (issuer == Issuer.ISSUER_ISSO && avtalerolle == Avtalerolle.BESLUTTER && tokenUtils.harAdGruppe(beslutterAdGruppeProperties.getId())) {
            return new Beslutter(new NavIdent(brukerOgIssuer.getBrukerIdent()), tilgangskontrollService, tilskuddPeriodeRepository, axsysService);
        }

        else {
            throw new TilgangskontrollException("Ugyldig kombinasjon av issuer og rolle.");
        }
    }

    public Veileder hentVeileder() {
        return (Veileder) hentAvtalepart(Avtalerolle.VEILEDER);
    }

    public Arbeidsgiver hentArbeidsgiver() {
        return (Arbeidsgiver) hentAvtalepart(Avtalerolle.ARBEIDSGIVER);
    }

    public InnloggetBruker hentInnloggetBruker(Avtalerolle avtalerolle) {
        return hentAvtalepart(avtalerolle).innloggetBruker();
    }

    public InnloggetVeileder hentInnloggetVeileder() {
        try {
            return (InnloggetVeileder) hentInnloggetBruker(Avtalerolle.VEILEDER);
        } catch (ClassCastException e) {
            throw new TilgangskontrollException("Innlogget bruker er ikke veileder.");
        }
    }

    public void validerSystembruker() {
        tokenUtils.hentBrukerOgIssuer()
                .filter(t -> (Issuer.ISSUER_SYSTEM == t.getIssuer() && systembrukerProperties.getId().equals(t.getBrukerIdent())))
                .orElseThrow(() -> new TilgangskontrollException("Systemet har ikke tilgang til tjenesten"));
    }

    public Beslutter hentBeslutter() {
        return (Beslutter) hentAvtalepart(Avtalerolle.BESLUTTER);
    }
}
