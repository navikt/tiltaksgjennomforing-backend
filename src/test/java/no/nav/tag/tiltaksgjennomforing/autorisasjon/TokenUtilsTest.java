package no.nav.tag.tiltaksgjennomforing.autorisasjon;

import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.context.TokenContext;
import no.nav.tag.tiltaksgjennomforing.TestData;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.TokenUtils.BrukerOgIssuer;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.TokenUtils.Issuer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static no.nav.security.oidc.test.support.JwtTokenGenerator.createSignedJWT;
import static no.nav.security.oidc.test.support.JwtTokenGenerator.ACR_LEVEL_4;
import static no.nav.tag.tiltaksgjennomforing.autorisasjon.TokenUtils.Issuer.ISSUER_ISSO;
import static no.nav.tag.tiltaksgjennomforing.autorisasjon.TokenUtils.Issuer.ISSUER_SELVBETJENING;
import static no.nav.tag.tiltaksgjennomforing.autorisasjon.TokenUtils.Issuer.ISSUER_SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenUtilsTest {

    @InjectMocks
    private TokenUtils tokenUtils;

    @Mock
    private OIDCRequestContextHolder contextHolder;

    @Test
    public void hentInnloggetBruker__er_selvbetjeningbruker() {
        InnloggetArbeidsgiver selvbetjeningBruker = TestData.enInnloggetArbeidsgiver();
        vaerInnloggetSelvbetjening(selvbetjeningBruker);
        assertThat(tokenUtils.hentBrukerOgIssuer().get()).isEqualTo(new BrukerOgIssuer(ISSUER_SELVBETJENING, selvbetjeningBruker.getIdentifikator().asString()));
    }

    @Test
    public void hentInnloggetBruker__er_selvbetjeningbruker_må_være_nivå_4() {
        InnloggetArbeidsgiver selvbetjeningBruker = TestData.enInnloggetArbeidsgiver();
        vaerInnloggetSelvbetjening(selvbetjeningBruker);
        assertThat(tokenUtils.hentBrukerOgIssuer().get()).isEqualTo(new BrukerOgIssuer(ISSUER_SELVBETJENING, selvbetjeningBruker.getIdentifikator().asString()));
        vaerInnloggetSelvbetjeningNiva3(selvbetjeningBruker);
        assertThat(tokenUtils.hentBrukerOgIssuer().isEmpty()).isTrue();
    }
    
    @Test
    public void hentInnloggetBruker__er_nav_ansatt() {
        InnloggetVeileder navAnsatt = TestData.enInnloggetVeileder();
        vaerInnloggetNavAnsatt(navAnsatt);
        assertThat(tokenUtils.hentBrukerOgIssuer().get()).isEqualTo(new BrukerOgIssuer(ISSUER_ISSO, navAnsatt.getIdentifikator().asString()));
    }

    @Test
    public void hentInnloggetBruker__er_system() {
        vaerInnloggetSystem("systemId");
        assertThat(tokenUtils.hentBrukerOgIssuer().get()).isEqualTo(new BrukerOgIssuer(Issuer.ISSUER_SYSTEM, "systemId"));
    }

    @Test
    public void hentInnloggetBruker__er_uinnlogget() {
        vaerUinnlogget();
        assertThat(tokenUtils.hentBrukerOgIssuer().isEmpty()).isTrue();
    }

    private void vaerUinnlogget() {
        when(contextHolder.getOIDCValidationContext()).thenReturn(new OIDCValidationContext());
    }

    private void vaerInnloggetSystem(String systemId) {
        lagOidcContext(ISSUER_SYSTEM, systemId, new HashMap<>(), null);
    }

    private void vaerInnloggetSelvbetjening(InnloggetArbeidsgiver bruker) {
        lagOidcContext(ISSUER_SELVBETJENING, bruker.getIdentifikator().asString(), new HashMap<>(), ACR_LEVEL_4);
    }

    private void vaerInnloggetSelvbetjeningNiva3(InnloggetArbeidsgiver bruker) {
        lagOidcContext(ISSUER_SELVBETJENING, bruker.getIdentifikator().asString(), new HashMap<>(), "Level3");
    }
    
    private void vaerInnloggetNavAnsatt(InnloggetVeileder innloggetBruker) {
        lagOidcContext(ISSUER_ISSO, "blablabla", Map.of("NAVident", innloggetBruker.getIdentifikator().asString()), null);
    }

    private void lagOidcContext(Issuer issuer, String subject, Map<String, Object> claims, String acrLevel) {
        OIDCValidationContext context = new OIDCValidationContext();
        TokenContext tokenContext = new TokenContext(issuer.issuerName, "");
        OIDCClaims oidcClaims = new OIDCClaims(createSignedJWT(subject, 0, claims, issuer.issuerName, "audience", acrLevel));
        context.addValidatedToken(issuer.issuerName, tokenContext, oidcClaims);

        when(contextHolder.getOIDCValidationContext()).thenReturn(context);
    }
}