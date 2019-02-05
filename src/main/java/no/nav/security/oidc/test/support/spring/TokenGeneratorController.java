package no.nav.security.oidc.test.support.spring;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.jwt.SignedJWT;
import no.nav.security.oidc.api.Unprotected;
import no.nav.security.oidc.test.support.JwkGenerator;
import no.nav.security.oidc.test.support.JwtTokenGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/local")
public class TokenGeneratorController {

    @Unprotected
    @GetMapping
    public TokenEndpoint[] endpoints(HttpServletRequest request) {
        String base = request.getRequestURL().toString();
        return new TokenEndpoint[]{new TokenEndpoint("Get JWT as serialized string", base + "/jwt", "subject"),
                new TokenEndpoint("Get JWT as SignedJWT object with claims", base + "/claims", "subject"),
                new TokenEndpoint("Add JWT as a cookie, (optional) redirect to secured uri", base + "/cookie", "subject", "redirect", "cookiename"),
                new TokenEndpoint("Get JWKS used to sign token", base + "/jwks"),
                new TokenEndpoint("Get JWKS used to sign token as JWKSet object", base + "/jwkset"),
                new TokenEndpoint("Get token issuer metadata (ref oidc .well-known)", base + "/metadata")};
    }

    @Unprotected
    @GetMapping("/jwt")
    public String issueToken(@RequestParam(value = "subject", defaultValue = "12345678910") String subject) {
        return JwtTokenGenerator.createSignedJWT(subject, "iss-localhost", "aud-localhost").serialize();
    }

    @Unprotected
    @GetMapping("/claims")
    public SignedJWT jwtClaims(@RequestParam(value = "subject", defaultValue = "12345678910") String subject) {
        return JwtTokenGenerator.createSignedJWT(subject, "iss-localhost", "aud-localhost");
    }

    @Unprotected
    @GetMapping("/selvbetjening-cookie")
    public Cookie addSelvbetjeningCookie(@RequestParam(value = "subject", defaultValue = "01234567890") String subject,
                                         @RequestParam(value = "cookiename", defaultValue = "selvbetjening-idtoken") String cookieName,
                                         @RequestParam(value = "redirect", required = false) String redirect,
                                         @RequestParam(value = "expiry", required = false) String expiry,
                                         HttpServletRequest request, HttpServletResponse response) throws IOException {
        return bakeCookie(subject, cookieName, redirect, expiry, request, response, new HashMap<>(), "selvbetjening", "aud-selvbetjening");
    }

    @Unprotected
    @GetMapping("/isso-cookie")
    public Cookie addNavCookie(@RequestParam(value = "subject", defaultValue = "01234567890") String subject,
                               @RequestParam(value = "NAV-ident", defaultValue = "X123456") String navIdent,
                               @RequestParam(value = "cookiename", defaultValue = "isso-idtoken") String cookieName,
                               @RequestParam(value = "redirect", required = false) String redirect,
                               @RequestParam(value = "expiry", required = false) String expiry,
                               HttpServletRequest request,
                               HttpServletResponse response
    ) throws IOException {
        return bakeCookie(subject, cookieName, redirect, expiry, request, response, Collections.singletonMap("NAVident", navIdent), "isso", "aud-isso");
    }

    private Cookie bakeCookie(
            String subject,
            String cookieName,
            String redirect,
            String expiry,
            HttpServletRequest request,
            HttpServletResponse response,
            Map<String, Object> claims,
            String issuer,
            String audience
    ) throws IOException {
        long expiryTime = expiry != null ? Long.parseLong(expiry) : JwtTokenGenerator.EXPIRY;
        SignedJWT token = JwtTokenGenerator.createSignedJWT(subject, expiryTime, claims, issuer, audience);
        Cookie cookie = new Cookie(cookieName, token.serialize());
        cookie.setDomain(request.getServerName());
        cookie.setPath("/");
        response.addCookie(cookie);
        if (redirect != null) {
            response.sendRedirect(redirect);
            return null;
        }
        return cookie;
    }

    @Unprotected
    @GetMapping("/jwks")
    public String jwks() throws IOException {
        return IOUtils.readInputStreamToString(getClass().getResourceAsStream(JwkGenerator.DEFAULT_JWKSET_FILE),
                Charset.defaultCharset());
    }

    @Unprotected
    @GetMapping("/jwkset")
    public JWKSet jwkSet() {
        return JwkGenerator.getJWKSet();
    }

    @Unprotected
    @GetMapping("/metadata-isso")
    public String metadataIsso() throws IOException {
        return IOUtils.readInputStreamToString(getClass().getResourceAsStream("/metadata-isso.json"),
                Charset.defaultCharset());
    }

    @Unprotected
    @GetMapping("/metadata-selvbetjening")
    public String metadataSelvbetjening() throws IOException {
        return IOUtils.readInputStreamToString(getClass().getResourceAsStream("/metadata-selvbetjening.json"),
                Charset.defaultCharset());
    }

    class TokenEndpoint {
        String desc;
        String uri;
        String[] params;

        public TokenEndpoint(String desc, String uri, String... params) {
            this.desc = desc;
            this.uri = uri;
            this.params = params;

        }

        public String getDesc() {
            return desc;
        }

        public String getUri() {
            return uri;
        }

        public String[] getParams() {
            return params;
        }
    }
}
