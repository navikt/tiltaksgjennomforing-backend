package no.nav.tag.tiltaksgjennomforing.avtale;


import no.nav.tag.tiltaksgjennomforing.TestData;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.veilarbabac.TilgangskontrollService;
import no.nav.tag.tiltaksgjennomforing.featuretoggles.enhet.AxsysService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class BeslutterTest {

  private TilgangskontrollService tilgangskontrollService = mock(TilgangskontrollService.class);
  private TilskuddPeriodeRepository tilskuddPeriodeRepository = mock(TilskuddPeriodeRepository.class);
  private AvtaleRepository avtaleRepository = mock(AvtaleRepository.class);
  private AxsysService axsysService = mock(AxsysService.class);

  @Test
  public void hentAlleAvtalerMedMuligTilgang_kan_hente_avtale_Med_godkjent_periode_null_i_queryParams_for_setErGodkjkentTilskuddPerioder() {

    // GITT
    Avtale avtale = TestData.enLonnstilskuddAvtaleMedAltUtfylt();
    TilskuddPeriode tilskuddPeriode = new TilskuddPeriode();
    tilskuddPeriode.setBeløp(1200);
    tilskuddPeriode.setAvtaleInnhold(avtale.gjeldendeInnhold());
    avtale.gjeldendeInnhold().setTilskuddPeriode(Lists.list(tilskuddPeriode));

    Beslutter veileder = new Beslutter(new NavIdent("J987654"), tilgangskontrollService, tilskuddPeriodeRepository, axsysService);

    AvtalePredicate avtalePredicate = new AvtalePredicate();
    avtalePredicate.setStatus(null);

    // NÅR
    when(tilskuddPeriodeRepository.findAllByGodkjentTidspunktIsNotNull()).thenReturn(avtale.gjeldendeInnhold().getTilskuddPeriode());
    List<Avtale> avtales = veileder.hentAlleAvtalerMedMuligTilgang(avtaleRepository, avtalePredicate);

    assertThat(avtales).isEmpty();
  }

  @Test
  public void hentAlleAvtalerMedMuligTilgang_kan_hente_avtale_Med_godkjent_periode() {

    // GITT
    Avtale avtale = TestData.enLonnstilskuddAvtaleMedAltUtfylt();
    TilskuddPeriode tilskuddPeriode = new TilskuddPeriode();
    tilskuddPeriode.setBeløp(1200);
    tilskuddPeriode.setAvtaleInnhold(avtale.gjeldendeInnhold());
    avtale.gjeldendeInnhold().setTilskuddPeriode(Lists.list(tilskuddPeriode));

    Beslutter veileder = new Beslutter(new NavIdent("J987654"), tilgangskontrollService, tilskuddPeriodeRepository, axsysService);

    AvtalePredicate avtalePredicate = new AvtalePredicate();
    avtalePredicate.setTilskuddPeriodeStatus(TilskuddPeriodeStatus.GODKJENT);

    // NÅR
    when(tilskuddPeriodeRepository.findAllByGodkjentTidspunktIsNotNull()).thenReturn(avtale.gjeldendeInnhold().getTilskuddPeriode());
    List<Avtale> avtales = veileder.hentAlleAvtalerMedMuligTilgang(avtaleRepository, avtalePredicate);

    assertThat(avtales).isNotEmpty();
  }

  @Test
  public void hentAlleAvtalerMedMuligTilgang_kan_hente_kun_en_avtale_Med_to_ubehandlet_perioder() {

    // GITT
    Avtale avtale = TestData.enLonnstilskuddAvtaleMedAltUtfylt();
    TilskuddPeriode tilskuddPeriode = new TilskuddPeriode();
    tilskuddPeriode.setBeløp(1200);
    tilskuddPeriode.setAvtaleInnhold(avtale.gjeldendeInnhold());

    TilskuddPeriode tilskuddPeriode2 = new TilskuddPeriode();
    tilskuddPeriode2.setBeløp(1250);
    tilskuddPeriode2.setAvtaleInnhold(avtale.gjeldendeInnhold());

    avtale.gjeldendeInnhold().setTilskuddPeriode(Lists.list(tilskuddPeriode, tilskuddPeriode2));

    Beslutter veileder = new Beslutter(new NavIdent("J987654"), tilgangskontrollService, tilskuddPeriodeRepository, axsysService);

    AvtalePredicate avtalePredicate = new AvtalePredicate();
    avtalePredicate.setTilskuddPeriodeStatus(null);

    // NÅR
    when(tilskuddPeriodeRepository.findAllByGodkjentTidspunktIsNull()).thenReturn(avtale.gjeldendeInnhold().getTilskuddPeriode());
    List<Avtale> avtales = veileder.hentAlleAvtalerMedMuligTilgang(avtaleRepository, avtalePredicate);

    assertThat(avtales).isNotEmpty();
    assertThat(avtales.size()).isEqualTo(1);
  }


}