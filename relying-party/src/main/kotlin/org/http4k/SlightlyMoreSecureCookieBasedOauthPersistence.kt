package org.http4k

import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.http4k.security.AccessToken
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.security.OAuthPersistence
import org.http4k.security.openid.IdToken

class SlightlyMoreSecureCookieBasedOauthPersistence(
    private val delegate: OAuthPersistence = InsecureCookieBasedOAuthPersistence(
        "http4k-oidc"
    )
) : OAuthPersistence by delegate {
    override fun assignToken(
        request: Request,
        redirect: Response,
        accessToken: AccessToken,
        idToken: IdToken?
    ): Response {
        if (idToken == null) return delegate.assignToken(request, redirect, accessToken, idToken)

        //performing validations as described in spec: https://openid.net/specs/openid-connect-core-1_0.html#rfc.section.3.1.3.7

        val jwt = Jwt.lens(Response(Status.OK).body(idToken.value.split(".")[1].base64Decoded()))
        return if (jwt.iss != "https://www.certification.openid.net/test/a/http4k-oidc/")
            delegate.authFailureResponse()
        else
            delegate.assignToken(request, redirect, accessToken, idToken)
    }
}

class Jwt(val iss: String) {
    companion object {
        val lens = Body.auto<Jwt>().toLens()
    }
}