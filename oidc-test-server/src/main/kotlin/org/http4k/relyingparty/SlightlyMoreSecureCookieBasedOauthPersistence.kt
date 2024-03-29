package org.http4k.relyingparty

import com.nimbusds.jose.JOSEObject
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.PlainObject
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jose.proc.JWSKeySelector
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.PlainJWT
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.security.*
import org.http4k.security.openid.IdToken
import java.net.URL


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

        val keySource: JWKSource<SecurityContext> =
            RemoteJWKSet(URL("https://www.certification.openid.net/test/a/http4k-oidc/jwks"))
        val expectedJWSAlg = JWSAlgorithm.RS256

        val keySelector: JWSKeySelector<SecurityContext> =
            JWSVerificationKeySelector(expectedJWSAlg, keySource)


        val parse: JOSEObject = JOSEObject.parse(idToken.value)

        if(parse is PlainJWT || parse is PlainObject){
            // if the id_token is unsigned, the relying party should proceed and make a /userinfo request
            return delegate.assignToken(request, redirect, accessToken, idToken)
        }

        val jwtProcessor = DefaultJWTProcessor<SecurityContext>().apply {
            setJWSKeySelector(keySelector)
        }
        return try {
            val jwt = jwtProcessor.process(idToken.value, null)

            val nonceSent = retrieveNonce(request)
            val nonceReceived = jwt.getClaim("nonce")?.let { Nonce(it.toString()) }

            println("sent nonce=${nonceSent?.value}")
            println("sent backs=${nonceReceived?.value}")

            if (nonceReceived != nonceSent) {
                delegate.authFailureResponse(OAuthCallbackError.InvalidNonce(nonceSent?.value, nonceReceived?.value)).body("invalid nonce")
            }else{
                delegate.assignToken(request, redirect, accessToken, idToken)
            }

        } catch (e: BadJOSEException) {
            delegate.authFailureResponse(OAuthCallbackError.InvalidIdToken(e.message.orEmpty())).body(e.message.orEmpty())
        }
    }
}