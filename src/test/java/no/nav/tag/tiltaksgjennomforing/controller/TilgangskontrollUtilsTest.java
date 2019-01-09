package no.nav.tag.tiltaksgjennomforing.controller;

import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.context.TokenContext;
import no.nav.tag.tiltaksgjennomforing.domene.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static no.nav.security.oidc.test.support.JwtTokenGenerator.createSignedJWT;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TilgangskontrollUtilsTest {

    @InjectMocks
    private TilgangskontrollUtils tilgangskontroll;

    @Mock
    private OIDCRequestContextHolder contextHolder;

    @Test
    public void skalHenteInnloggetBrukerFraToken() {
        Bruker bruker = new Bruker("99988877766");
        vaerInnloggetSom(bruker);
        assertEquals(bruker, tilgangskontroll.hentInnloggetPerson());
    }

    @Test
    public void skalHenteInnloggetVeilederFraToken() {
        Veileder veileder = new Veileder("Z333333");
        vaerInnloggetSom(veileder);
        assertEquals(veileder, tilgangskontroll.hentInnloggetPerson());
    }

    @Test(expected = TilgangskontrollException.class)
    public void hentInnloggetVeilederSkalFeileHvisInnloggetBrukerIkkeErVeileder() {
        Bruker bruker = new Bruker("99988877766");
        vaerInnloggetSom(bruker);
        tilgangskontroll.hentInnloggetVeileder();
    }

    private void vaerInnloggetSom(Bruker bruker) {
        vaerInnloggetSom(TilgangskontrollUtils.ISSUER_SELVBETJENING, bruker.getFnr().getFnr(), new HashMap<>());
    }

    private void vaerInnloggetSom(Veileder veileder) {
        vaerInnloggetSom(TilgangskontrollUtils.ISSUER_ISSO, "blablabla", Collections.singletonMap("NAVident", veileder.getNavIdent().getId()));
    }

    private void vaerInnloggetSom(String issuer, String subject, Map<String, Object> claims) {
        OIDCValidationContext context = new OIDCValidationContext();
        TokenContext tokenContext = new TokenContext(issuer, "");
        OIDCClaims oidcClaims = new OIDCClaims(createSignedJWT(subject, 0, claims));
        context.addValidatedToken(issuer, tokenContext, oidcClaims);

        when(contextHolder.getOIDCValidationContext()).thenReturn(context);
    }

}