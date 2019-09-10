package no.nav.tag.tiltaksgjennomforing.integrasjon.veilarbabac;

import no.nav.tag.tiltaksgjennomforing.domene.NavIdent;
import no.nav.tag.tiltaksgjennomforing.domene.autorisasjon.InnloggetNavAnsatt;
import no.nav.tag.tiltaksgjennomforing.domene.exceptions.TilgangskontrollException;
import no.nav.tag.tiltaksgjennomforing.integrasjon.sts.STSClient;
import no.nav.tag.tiltaksgjennomforing.integrasjon.sts.STSToken;
import no.nav.tag.tiltaksgjennomforing.integrasjon.veilarbabac.TilgangskontrollAction;
import no.nav.tag.tiltaksgjennomforing.integrasjon.veilarbabac.VeilarbabacClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static no.nav.tag.tiltaksgjennomforing.integrasjon.veilarbabac.VeilarbabacClient.DENY_RESPONSE;
import static no.nav.tag.tiltaksgjennomforing.integrasjon.veilarbabac.VeilarbabacClient.PERMIT_RESPONSE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VeilarbabacClientTest {

    private static final String FNR = "11111111111";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private STSClient stsClient;

    private VeilarbabacClient veilarbabacClient;

    @Before
    public void setUp() {
        mockReturverdiFraVeilarbabac("permit");
        when(stsClient.hentSTSToken()).thenReturn(etStsToken());
        veilarbabacClient = new VeilarbabacClient(
                restTemplate,
                stsClient,
                "https://test.no"
        );
    }

    private static STSToken etStsToken() {
        return new STSToken("-", "-", 100);
    }

    @Test
    public void harSkrivetilgangTilKandidat__skal_returnere_false_hvis_deny() {
        mockReturverdiFraVeilarbabac(DENY_RESPONSE);
        assertThat(veilarbabacClient.sjekkTilgang(enVeileder(), "1000000000001", TilgangskontrollAction.update)).isFalse();
    }

    private void mockReturverdiFraVeilarbabac(String response) {
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(response));
    }

    private static InnloggetNavAnsatt enVeileder() {
        return new InnloggetNavAnsatt(new NavIdent("X123456"), null);
    }

    @Test
    public void harSkrivetilgangTilKandidat__skal_returnere_true_hvis_permit() {
        mockReturverdiFraVeilarbabac(PERMIT_RESPONSE);
        assertThat(veilarbabacClient.sjekkTilgang(enVeileder(), FNR, TilgangskontrollAction.update)).isTrue();
    }

    @Test(expected=TilgangskontrollException.class)
    public void harSkrivetilgangTilKandidat__skal_kaste_exception_hvis_ikke_allow_eller_deny() {
        mockReturverdiFraVeilarbabac("blabla");
        veilarbabacClient.sjekkTilgang(enVeileder(), FNR, TilgangskontrollAction.update);
    }

    @Test
    public void harSkrivetilgangTilKandidat__skal_gjøre_kall_med_riktige_parametre() {
        STSToken stsToken = etStsToken();

        InnloggetNavAnsatt veileder = enVeileder();

        when(stsClient.hentSTSToken()).thenReturn(stsToken);

        veilarbabacClient.sjekkTilgang(enVeileder(), FNR, TilgangskontrollAction.update);

        HttpHeaders headers = new HttpHeaders();
        headers.set("subject", veileder.getIdentifikator().asString());
        headers.set("subjectType", "InternBruker");
        headers.set("Authorization", "Bearer " + stsToken.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        verify(restTemplate).exchange(
                eq("https://test.no/person?fnr=" + FNR + "&action=update"),
                eq(HttpMethod.GET),
                eq(new HttpEntity(headers)),
                eq(String.class)
        );
    }

}
