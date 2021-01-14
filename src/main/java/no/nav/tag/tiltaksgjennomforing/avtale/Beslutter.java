package no.nav.tag.tiltaksgjennomforing.avtale;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggetBeslutter;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.InnloggetBruker;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.veilarbabac.TilgangskontrollService;
import no.nav.tag.tiltaksgjennomforing.exceptions.TilgangskontrollException;
import no.nav.tag.tiltaksgjennomforing.featuretoggles.enhet.AxsysService;
import no.nav.tag.tiltaksgjennomforing.featuretoggles.enhet.NavEnhet;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class Beslutter extends Avtalepart<NavIdent> {

    private TilgangskontrollService tilgangskontrollService;
    private TilskuddPeriodeRepository tilskuddPeriodeRepository;
    private AxsysService axsysService;

    public Beslutter(NavIdent identifikator, TilgangskontrollService tilgangskontrollService, TilskuddPeriodeRepository tilskuddPeriodeRepository, AxsysService axsysService) {
        super(identifikator);
        this.tilgangskontrollService = tilgangskontrollService;
        this.tilskuddPeriodeRepository = tilskuddPeriodeRepository;
        this.axsysService = axsysService;
    }

    public void godkjennTilskuddsperiode(Avtale avtale) {
        sjekkTilgang(avtale);
        avtale.godkjennTilskuddsperiode(getIdentifikator());
    }

    public void avslåTilskuddsperiode(Avtale avtale, EnumSet<Avslagsårsak> avslagsårsaker, String avslagsforklaring) {
        sjekkTilgang(avtale);
        avtale.avslåTilskuddsperiode(getIdentifikator(), avslagsårsaker, avslagsforklaring);
    }

    @Override
    public boolean harTilgang(Avtale avtale) {
        return tilgangskontrollService.harSkrivetilgangTilKandidat(getIdentifikator(), avtale.getDeltakerFnr());
    }

    @Override
    List<Avtale> hentAlleAvtalerMedMuligTilgang(AvtaleRepository avtaleRepository, AvtalePredicate queryParametre) {
        List<String> navEnheter = hentNavEnheter();
        return tilskuddPeriodeRepository.findAllByStatus(queryParametre.hentTilskuddPeriodeStatus())
            .stream()
            .map(TilskuddPeriode::hentAvtale)
            .filter(avtale -> harTiltakstype(queryParametre.getTiltakstype(), avtale.getTiltakstype()))
            .distinct()
            .collect(Collectors.toList());
    }

    private boolean harTiltakstype(Tiltakstype valgtTiltakstype, Tiltakstype tiltakstypeForAvtale) {
        if (valgtTiltakstype == null) {
            return true;
        }
        return tiltakstypeForAvtale.equals(valgtTiltakstype);
    }

    private List<String> hentNavEnheter() {
        return axsysService.hentEnheterNavAnsattHarTilgangTil(getIdentifikator())
                .stream()
                .map(NavEnhet::getVerdi)
                .collect(Collectors.toList());
    }

    @Override
    void godkjennForAvtalepart(Avtale avtale) {
        throw new TilgangskontrollException("Beslutter kan ikke godkjenne avtaler");
    }

    @Override
    public boolean kanEndreAvtale() {
        return false;
    }

    @Override
    public AvtaleStatusDetaljer statusDetaljerForAvtale(Avtale avtale) {
        throw new NotImplementedException();
    }

    @Override
    public boolean erGodkjentAvInnloggetBruker(Avtale avtale) {
        return false;
    }

    @Override
    boolean kanOppheveGodkjenninger(Avtale avtale) {
        return false;
    }

    @Override
    void opphevGodkjenningerSomAvtalepart(Avtale avtale) {
        throw new TilgangskontrollException("Beslutter kan ikke oppheve godkjenninger av avtaler");
    }

    @Override
    protected Avtalerolle rolle() {
        return Avtalerolle.BESLUTTER;
    }

    @Override
    public void låsOppAvtale(Avtale avtale) {
        throw new TilgangskontrollException("Beslutter kan ikke låse opp avtaler");
    }

    @Override
    public InnloggetBruker innloggetBruker() {
        return new InnloggetBeslutter(getIdentifikator());
    }
}
