package no.nav.tag.tiltaksgjennomforing.varsel.oppgave;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.events.AvtaleOpprettetAvArbeidsgiver;
import no.nav.tag.tiltaksgjennomforing.persondata.PersondataService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LagGosysVarselLytter {
    private final OppgaveVarselService oppgaveVarselService;
    private final PersondataService persondataService;

    private void varsleGosys (Avtale avtale) {
        final String aktørid = persondataService.hentAktørId(avtale.getDeltakerFnr());
        oppgaveVarselService.opprettOppgave(aktørid, avtale.getTiltakstype(), avtale.getId());
    }

    @EventListener
    public void opprettGosysVarsel(AvtaleOpprettetAvArbeidsgiver event) {
        varsleGosys(event.getAvtale());
    }
}
