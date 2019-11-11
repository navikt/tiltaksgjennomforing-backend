package no.nav.tag.tiltaksgjennomforing.avtale;

import no.nav.tag.tiltaksgjennomforing.TestData;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggetBruker;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggetNavAnsatt;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggetSelvbetjeningBruker;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggingService;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.pilottilgang.TilgangUnderPilotering;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.veilarbabac.TilgangskontrollService;
import no.nav.tag.tiltaksgjennomforing.exceptions.RessursFinnesIkkeException;
import no.nav.tag.tiltaksgjennomforing.exceptions.TilgangskontrollException;
import no.nav.tag.tiltaksgjennomforing.orgenhet.EregService;
import no.nav.tag.tiltaksgjennomforing.orgenhet.Organisasjon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AvtaleControllerTest {

    @InjectMocks
    private AvtaleController avtaleController;

    @Mock
    private AvtaleRepository avtaleRepository;

    @Mock
    private TilgangUnderPilotering tilgangUnderPilotering;

    @Mock
    private TilgangskontrollService tilgangskontrollService;

    @Mock
    private InnloggingService innloggingService;

    @Mock
    private EregService eregService;

    private static List<Avtale> lagListeMedAvtaler(Avtale avtale, int antall) {
        List<Avtale> avtaler = new ArrayList<>();
        for (int i = 0; i <= antall; i++) {
            avtaler.add(avtale);
        }
        return avtaler;
    }

    private static OpprettAvtale lagOpprettAvtale() {
        Fnr deltakerFnr = new Fnr("88888899999");
        BedriftNr bedriftNr = new BedriftNr("12345678");
        return new OpprettAvtale(deltakerFnr, bedriftNr, Tiltakstype.ARBEIDSTRENING);
    }

    @Test
    public void hentSkalReturnereRiktigAvtale() {
        Avtale avtale = TestData.enAvtale();
        vaerInnloggetSom(TestData.innloggetNavAnsatt(TestData.enVeileder(avtale)));
        when(avtaleRepository.findById(avtale.getId())).thenReturn(Optional.of(avtale));
        Avtale hentetAvtale = avtaleController.hent(avtale.getId()).getBody();

        assertThat(hentetAvtale).isEqualTo(avtale);
    }

    @Test(expected = RessursFinnesIkkeException.class)
    public void hentSkalKasteResourceNotFoundExceptionHvisAvtaleIkkeFins() {
        Avtale avtale = TestData.enAvtale();
        vaerInnloggetSom(TestData.innloggetNavAnsatt(TestData.enVeileder(avtale)));
        when(avtaleRepository.findById(avtale.getId())).thenReturn(Optional.empty());
        avtaleController.hent(avtale.getId());
    }

    @Test(expected = TilgangskontrollException.class)
    public void hentSkalKastTilgangskontrollExceptionHvisInnloggetNavAnsattIkkeHarTilgang() {
        Avtale avtale = TestData.enAvtale();
        vaerInnloggetSom(TestData.innloggetNavAnsatt(TestData.enVeileder()));
        when(avtaleRepository.findById(avtale.getId())).thenReturn(Optional.of(avtale));
        avtaleController.hent(avtale.getId());
    }

    @Test
    public void hentAvtalerOpprettetAvVeileder_skal_returnere_avtaler_dersom_veileder_har_tilgang() {
        NavIdent veilederNavIdent = new NavIdent("Z222222");
        Avtale avtaleForVeilederSomSøkesEtter = AvtaleFactory.nyAvtale(lagOpprettAvtale(), veilederNavIdent);
        Avtale avtaleForAnnenVeilder = AvtaleFactory.nyAvtale(lagOpprettAvtale(), new NavIdent("Z111111"));
        InnloggetNavAnsatt innloggetBruker = new InnloggetNavAnsatt(new NavIdent("Z333333"), tilgangskontrollService);
        vaerInnloggetSom(innloggetBruker);
        when(avtaleRepository.findAll()).thenReturn(asList(avtaleForVeilederSomSøkesEtter, avtaleForAnnenVeilder));
        when(tilgangskontrollService.harLesetilgangTilKandidat(eq(innloggetBruker), any(Fnr.class))).thenReturn(Optional.of(true));
        Iterable<Avtale> avtaler = avtaleController.hentAlleAvtalerInnloggetBrukerHarTilgangTil(new AvtalePredicate().setVeilederNavIdent(veilederNavIdent));
        assertThat(avtaler)
                .contains(avtaleForVeilederSomSøkesEtter)
                .doesNotContain(avtaleForAnnenVeilder);
    }

    @Test
    public void hentAvtalerOpprettetAvVeileder_skal_returnere_tom_liste_dersom_veileder_ikke_har_tilgang() {
        NavIdent veilederNavIdent = new NavIdent("Z222222");
        Avtale avtaleForVeilederSomSøkesEtter = AvtaleFactory.nyAvtale(lagOpprettAvtale(), veilederNavIdent);
        InnloggetNavAnsatt innloggetBruker = new InnloggetNavAnsatt(new NavIdent("Z333333"), tilgangskontrollService);
        vaerInnloggetSom(innloggetBruker);
        when(avtaleRepository.findAll()).thenReturn(asList(avtaleForVeilederSomSøkesEtter));
        when(tilgangskontrollService.harLesetilgangTilKandidat(eq(innloggetBruker), any(Fnr.class))).thenReturn(Optional.of(false));
        Iterable<Avtale> avtaler = avtaleController.hentAlleAvtalerInnloggetBrukerHarTilgangTil(new AvtalePredicate().setVeilederNavIdent(veilederNavIdent));
        assertThat(avtaler).doesNotContain(avtaleForVeilederSomSøkesEtter);
    }

    @Test
    public void hentAvtalerOpprettetAvInnloggetVeileder_skal_returnere_avtaler_dersom_veileder_har_tilgang() {
        NavIdent innloggetVeileder = new NavIdent("Z333333");
        Avtale avtaleForInnloggetVeileder = AvtaleFactory.nyAvtale(lagOpprettAvtale(), innloggetVeileder);
        Avtale avtaleForAnnenVeilder = AvtaleFactory.nyAvtale(lagOpprettAvtale(), new NavIdent("Z111111"));
        InnloggetNavAnsatt innloggetBruker = new InnloggetNavAnsatt(innloggetVeileder, tilgangskontrollService);
        vaerInnloggetSom(innloggetBruker);
        when(avtaleRepository.findAll()).thenReturn(asList(avtaleForInnloggetVeileder, avtaleForAnnenVeilder));
        when(tilgangskontrollService.harLesetilgangTilKandidat(eq(innloggetBruker), any(Fnr.class))).thenReturn(Optional.of(true));
        Iterable<Avtale> avtaler = avtaleController.hentAlleAvtalerInnloggetBrukerHarTilgangTil(new AvtalePredicate().setVeilederNavIdent(innloggetVeileder));
        assertThat(avtaler)
                .contains(avtaleForInnloggetVeileder)
                .doesNotContain(avtaleForAnnenVeilder);
    }

    @Test(expected = TilgangskontrollException.class)
    public void hentSkalKastTilgangskontrollExceptionHvisInnloggetSelvbetjeningBrukerIkkeHarTilgang() {
        Avtale avtale = TestData.enAvtale();
        vaerInnloggetSom(new InnloggetSelvbetjeningBruker(new Fnr("55555566666"), emptyList()));
        when(avtaleRepository.findById(avtale.getId())).thenReturn(Optional.of(avtale));
        avtaleController.hent(avtale.getId());
    }

    @Test
    public void opprettAvtaleSkalReturnereCreatedOgOpprettetLokasjon() {
        Avtale avtale = TestData.enAvtale();
        vaerInnloggetSom(TestData.innloggetNavAnsatt(TestData.enVeileder(avtale)));
        when(avtaleRepository.save(any(Avtale.class))).thenReturn(avtale);
        when(eregService.hentVirksomhet(avtale.getBedriftNr())).thenReturn(new Organisasjon(avtale.getBedriftNr(), avtale.getBedriftNavn()));

        ResponseEntity svar = avtaleController.opprettAvtale(new OpprettAvtale(avtale.getDeltakerFnr(), avtale.getBedriftNr(), Tiltakstype.ARBEIDSTRENING));

        assertThat(svar.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(svar.getHeaders().getLocation().getPath()).isEqualTo("/avtaler/" + avtale.getId());
    }

    @Test(expected = RessursFinnesIkkeException.class)
    public void endreAvtaleSkalReturnereNotFoundHvisDenIkkeFins() {
        Avtale avtale = TestData.enAvtale();
        vaerInnloggetSom(TestData.innloggetNavAnsatt(TestData.enVeileder(avtale)));
        when(avtaleRepository.findById(avtale.getId())).thenReturn(Optional.empty());
        avtaleController.endreAvtale(avtale.getId(), avtale.getVersjon(), TestData.ingenEndring());
    }

    @Test
    public void endreAvtaleSkalReturnereOkHvisInnloggetPersonErVeileder() {
        Avtale avtale = TestData.enAvtale();
        vaerInnloggetSom(TestData.innloggetNavAnsatt(TestData.enVeileder(avtale)));
        when(avtaleRepository.findById(avtale.getId())).thenReturn(Optional.of(avtale));
        when(avtaleRepository.save(avtale)).thenReturn(avtale);
        ResponseEntity svar = avtaleController.endreAvtale(avtale.getId(), avtale.getVersjon(), TestData.ingenEndring());
        assertThat(svar.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test(expected = TilgangskontrollException.class)
    public void endreAvtaleSkalReturnereForbiddenHvisInnloggetPersonIkkeHarTilgang() {
        Avtale avtale = TestData.enAvtale();
        vaerInnloggetSom(TestData.enSelvbetjeningBruker());

        when(avtaleRepository.findById(avtale.getId())).thenReturn(Optional.of(avtale));
        avtaleController.endreAvtale(avtale.getId(), avtale.getVersjon(), TestData.ingenEndring());
    }

    @Test
    public void hentAlleAvtalerInnloggetBrukerHarTilgangTilSkalIkkeReturnereAvtalerManIkkeHarTilgangTil() {
        Avtale avtaleMedTilgang = TestData.enAvtale();
        Avtale avtaleUtenTilgang = AvtaleFactory.nyAvtale(new OpprettAvtale(new Fnr("89898989898"), new BedriftNr("111222333"), Tiltakstype.ARBEIDSTRENING), new NavIdent("X643564"));

        InnloggetSelvbetjeningBruker selvbetjeningBruker = TestData.innloggetSelvbetjeningBrukerUtenOrganisasjon(TestData.enDeltaker(avtaleMedTilgang));
        vaerInnloggetSom(selvbetjeningBruker);

        List<Avtale> avtalerBrukerHarTilgangTil = lagListeMedAvtaler(avtaleMedTilgang, 5);
        List<Avtale> alleAvtaler = new ArrayList<>();
        alleAvtaler.addAll(avtalerBrukerHarTilgangTil);
        alleAvtaler.addAll(lagListeMedAvtaler(avtaleUtenTilgang, 4));

        when(avtaleRepository.findAll()).thenReturn(alleAvtaler);

        List<Avtale> hentedeAvtaler = new ArrayList<>();
        for (Avtale avtale : avtaleController.hentAlleAvtalerInnloggetBrukerHarTilgangTil(new AvtalePredicate())) {
            hentedeAvtaler.add(avtale);
        }

        hentedeAvtaler.forEach(avtale -> assertThat(selvbetjeningBruker.harLeseTilgang(avtale)).isTrue());
        assertThat(hentedeAvtaler.size()).isEqualTo(avtalerBrukerHarTilgangTil.size());
    }

    @Test(expected = RessursFinnesIkkeException.class)
    public void hentRolleSkalKasteResourceNotFoundExceptionHvisAvtaleIkkeFins() {
        Avtale avtale = TestData.enAvtale();
        when(avtaleRepository.findById(avtale.getId())).thenReturn(Optional.empty());
        avtaleController.hentRolle(avtale.getId());
    }

    @Test(expected = TilgangskontrollException.class)
    public void hentRolleSkalReturnereForbiddenHvisIkkeTilknyttetAvtale() {
        Avtale avtale = TestData.enAvtale();
        vaerInnloggetSom(TestData.enNavAnsatt());

        when(avtaleRepository.findById(avtale.getId())).thenReturn(Optional.of(avtale));
        avtaleController.hentRolle(avtale.getId());
    }

    @Test
    public void hentRolleSkalReturnereOkMedEnRolleHvisInnloggetBrukerErTilknyttetAvtale() {
        Avtale avtale = TestData.enAvtale();
        InnloggetSelvbetjeningBruker selvbetjeningBruker = TestData.innloggetSelvbetjeningBrukerUtenOrganisasjon(TestData.enDeltaker(avtale));
        vaerInnloggetSom(selvbetjeningBruker);

        when(avtaleRepository.findById(avtale.getId())).thenReturn(Optional.of(avtale));
        ResponseEntity<Avtalerolle> svar = avtaleController.hentRolle(avtale.getId());

        assertThat(svar.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(svar.getBody()).isEqualTo(Avtalerolle.DELTAKER);
    }

    @Test(expected = TilgangskontrollException.class)
    public void opprettAvtale__skal_feile_hvis_veileder_ikke_er_i_pilotering() {
        vaerInnloggetSom(TestData.enNavAnsatt());
        doThrow(TilgangskontrollException.class).when(tilgangUnderPilotering).sjekkTilgang(any());
        avtaleController.opprettAvtale(new OpprettAvtale(new Fnr("11111100000"), new BedriftNr("111222333"), Tiltakstype.ARBEIDSTRENING));
    }

    @Test(expected = TilgangskontrollException.class)
    public void opprettAvtale__skal_feile_hvis_veileder_ikke_har_tilgang_til_bruker() {
        InnloggetNavAnsatt enNavAnsatt = TestData.enNavAnsatt();
        vaerInnloggetSom(enNavAnsatt);
        Fnr deltakerFnr = new Fnr("11111100000");
        doThrow(TilgangskontrollException.class).when(tilgangskontrollService).sjekkSkrivetilgangTilKandidat(enNavAnsatt, deltakerFnr);
        avtaleController.opprettAvtale(new OpprettAvtale(deltakerFnr, new BedriftNr("111222333"), Tiltakstype.ARBEIDSTRENING));
    }

    private void vaerInnloggetSom(InnloggetBruker innloggetBruker) {
        when(innloggingService.hentInnloggetBruker()).thenReturn(innloggetBruker);
        if (innloggetBruker instanceof InnloggetNavAnsatt) {
            when(innloggingService.hentInnloggetNavAnsatt()).thenReturn((InnloggetNavAnsatt) innloggetBruker);
        }
    }

}
