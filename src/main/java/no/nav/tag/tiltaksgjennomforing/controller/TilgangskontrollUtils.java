package no.nav.tag.tiltaksgjennomforing.controller;

import com.nimbusds.jwt.JWTClaimsSet;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.tag.tiltaksgjennomforing.domene.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TilgangskontrollUtils {
    private final OIDCRequestContextHolder contextHolder;
    public final static String ISSUER_ISSO = "isso";
    public final static String ISSUER_SELVBETJENING = "selvbetjening";

    @Autowired
    public TilgangskontrollUtils(OIDCRequestContextHolder contextHolder) {
        this.contextHolder = contextHolder;
    }

    public Person hentInnloggetPerson() {
        if (innloggetPersonErVeileder()) {
            return hentInnloggetVeileder();
        } else if (innloggetPersonErBruker()) {
            return hentInnloggetBruker();
        } else {
            throw new TilgangskontrollException("Bruker er ikke innlogget.");
        }
    }

    private Bruker hentInnloggetBruker() {
        String fnr = hentClaim(ISSUER_SELVBETJENING, "sub")
                .orElseThrow(() -> new TilgangskontrollException("Finner ikke fodselsnummer til bruker."));
        return new Bruker(fnr);
    }

    public Veileder hentInnloggetVeileder() {
        String navIdent = hentClaim(ISSUER_ISSO, "NAVident")
                .orElseThrow(() -> new TilgangskontrollException("Innlogget bruker er ikke veileder."));
        return new Veileder(navIdent);
    }

    private Optional<String> hentClaim(String issuer, String claim) {
        Optional<JWTClaimsSet> claimSet = hentClaimSet(issuer);
        return claimSet.map(jwtClaimsSet -> String.valueOf(jwtClaimsSet.getClaim(claim)));
    }

    private Optional<JWTClaimsSet> hentClaimSet(String issuer) {
        OIDCClaims claims = contextHolder
                .getOIDCValidationContext()
                .getClaims(issuer);

        if (claims == null) {
            return Optional.empty();
        } else {
            return Optional.of(claims.getClaimSet());
        }

    }

    private boolean innloggetPersonErVeileder() {
        return hentClaimSet(ISSUER_ISSO)
                .map(jwtClaimsSet -> jwtClaimsSet.getClaims().containsKey("NAVident"))
                .orElse(false);
    }

    private boolean innloggetPersonErBruker() {
        Optional<Fnr> fnr = hentClaim(ISSUER_SELVBETJENING, "sub").map(fnrString -> new Fnr(fnrString));
        return fnr.isPresent();
    }
}
