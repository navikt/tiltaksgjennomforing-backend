package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.avtale.*;
import no.nav.tag.tiltaksgjennomforing.avtale.events.GamleVerdier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static no.nav.tag.tiltaksgjennomforing.varsel.VarslbarHendelseType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

public class LagVarselFraVarslbarHendelseTest {
    private static Avtale avtale;
    private static Identifikator deltaker;
    private static Identifikator arbeidsgiver;
    private static Identifikator veileder;
    private static TilskuddPeriode gjeldendeperiode;

    @BeforeAll
    static void setUp() {
        avtale = TestData.enLonnstilskuddAvtaleGodkjentAvVeileder();
        deltaker = avtale.getDeltakerFnr();
        arbeidsgiver = avtale.getBedriftNr();
        veileder = avtale.getVeilederNavIdent();
        gjeldendeperiode = avtale.gjeldendeTilskuddsperiode();
        avtale.avslåTilskuddsperiode(TestData.enNavIdent(), EnumSet.of(Avslagsårsak.FEIL_I_REGELFORSTÅELSE), "registrert feil i fakta");
    }

    @DisplayName("Skal varsle riktig mottakere når hendelse oppstår")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provider")
    void testLagVarsler(VarslbarHendelseType hendelse, GamleVerdier gamleVerdier, List<Identifikator> skalVarsles, List<Identifikator> skalFåLogg) {

        List<Varsel> varsler = LagVarselFraVarslbarHendelse.lagBjelleVarsler(avtale, VarslbarHendelse.nyHendelse(avtale, hendelse, Avtalerolle.VEILEDER), gamleVerdier, Avtalerolle.VEILEDER);
        assertThat(varsler).filteredOn(varsel -> varsel.isBjelle()).extracting(Varsel::getIdentifikator).containsAll(skalVarsles);
        assertThat(varsler).filteredOn(varsel -> !varsel.isBjelle()).extracting(Varsel::getIdentifikator).containsAll(skalFåLogg);
        if (!varsler.isEmpty()) {
             assertThat(varsler).extracting(Varsel::getHendelseType).containsOnly(hendelse);
        }
    }

    private static Stream<Arguments> provider() {
        return Stream.of(
                of(TILSKUDDSPERIODE_GODKJENT, new GamleVerdier(), List.of(veileder), List.of()),
                of(TILSKUDDSPERIODE_AVSLATT, new GamleVerdier(), List.of(veileder), List.of()),
                of(OPPRETTET, new GamleVerdier(), List.of(deltaker, arbeidsgiver), List.of(veileder)),
                of(ENDRET, new GamleVerdier(), List.of(), List.of(deltaker, arbeidsgiver, veileder)),
                of(GODKJENT_AV_DELTAKER, new GamleVerdier(), List.of(veileder), List.of(deltaker, arbeidsgiver)),
                of(GODKJENT_AV_ARBEIDSGIVER, new GamleVerdier(), List.of(veileder), List.of(deltaker, arbeidsgiver)),
                of(GODKJENT_AV_VEILEDER, new GamleVerdier(), List.of(deltaker, arbeidsgiver), List.of(veileder)),
                of(GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER, new GamleVerdier(true, false), List.of(deltaker, veileder), List.of(arbeidsgiver)),
                of(GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER, new GamleVerdier(false, false), List.of(veileder), List.of(deltaker, arbeidsgiver)),
                of(GODKJENNINGER_OPPHEVET_AV_VEILEDER, new GamleVerdier(true, false), List.of(deltaker), List.of(arbeidsgiver, veileder)),
                of(GODKJENNINGER_OPPHEVET_AV_VEILEDER, new GamleVerdier(false, true), List.of(arbeidsgiver), List.of(deltaker, veileder)),
                of(GODKJENNINGER_OPPHEVET_AV_VEILEDER, new GamleVerdier(true, true), List.of(deltaker, arbeidsgiver), List.of(veileder)),
                of(DELT_MED_ARBEIDSGIVER, new GamleVerdier(), List.of(), List.of()),
                of(DELT_MED_DELTAKER, new GamleVerdier(), List.of(), List.of())
        );
    }
}
