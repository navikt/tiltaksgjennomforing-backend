package no.nav.tag.tiltaksgjennomforing.autorisasjon;

import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.NavIdent;
import no.nav.tag.tiltaksgjennomforing.avtale.OpprettAvtale;
import no.nav.tag.tiltaksgjennomforing.avtale.Veileder;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.veilarbabac.TilgangskontrollService;

public class InnloggetNavAnsatt extends InnloggetBruker<NavIdent> {

    private final TilgangskontrollService tilgangskontrollService;

    public InnloggetNavAnsatt(NavIdent identifikator, TilgangskontrollService tilgangskontrollService) {
        super(identifikator);
        this.tilgangskontrollService = tilgangskontrollService;
    }

    public Avtale opprettAvtale(OpprettAvtale opprettAvtale) {
        return Avtale.nyAvtale(opprettAvtale, getIdentifikator());
    }

    @Override
    public Veileder avtalepart(Avtale avtale) {
        return new Veileder(getIdentifikator(), avtale);
    }

    @Override
    public boolean harLeseTilgang(Avtale avtale)  {
        return tilgangskontrollService.harLesetilgangTilKandidat(getIdentifikator(), avtale.getDeltakerFnr()).orElseGet(() -> harOpprettetAvtale(avtale));
    }

    private boolean harOpprettetAvtale(Avtale avtale) {
        return avtale.getVeilederNavIdent().equals(getIdentifikator());
    }

    @Override
    public boolean harSkriveTilgang(Avtale avtale) {
        return tilgangskontrollService.harSkrivetilgangTilKandidat(getIdentifikator(), avtale.getDeltakerFnr()).orElseGet(() -> harOpprettetAvtale(avtale));
    }
}
