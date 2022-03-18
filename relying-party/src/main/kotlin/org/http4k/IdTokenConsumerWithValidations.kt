package org.http4k

import com.nimbusds.jose.JOSEObject
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.PlainObject
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jose.proc.JWSKeySelector
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.security.Nonce
import org.http4k.security.OauthCallbackError.InvalidIdToken
import org.http4k.security.openid.IdToken
import org.http4k.security.openid.IdTokenConsumer
import java.net.URL

class IdTokenConsumerWithValidations : IdTokenConsumer {
    override fun consumeFromAccessTokenResponse(idToken: IdToken): Result<Unit, InvalidIdToken> {
        //performing validations as described in spec: https://openid.net/specs/openid-connect-core-1_0.html#rfc.section.3.1.3.7

        val keySource: JWKSource<SecurityContext> =
            RemoteJWKSet(URL("https://www.certification.openid.net/test/a/http4k-oidc/jwks"))
        val expectedJWSAlg = JWSAlgorithm.RS256

        val keySelector: JWSKeySelector<SecurityContext> =
            JWSVerificationKeySelector(expectedJWSAlg, keySource)

        val claimsVerifier = DefaultJWTClaimsVerifier<SecurityContext>(
            setOf("http4k-oidc"),
            JWTClaimsSet.Builder().issuer("https://www.certification.openid.net/test/a/http4k-oidc/").build(),
            setOf("sub", "iat"),
            setOf()
        )

        val parse: JOSEObject = JOSEObject.parse(idToken.value)

        if (parse is PlainJWT || parse is PlainObject) {
            // if the id_token is unsigned, the relying party should proceed and make a /userinfo request
            return Success(Unit)
        }

        val jwtProcessor = DefaultJWTProcessor<SecurityContext>().apply {
            jwtClaimsSetVerifier = claimsVerifier
            setJWSKeySelector(keySelector)
        }
        return try {
            jwtProcessor.process(idToken.value, null)
            Success(Unit)
        } catch (e: BadJOSEException) {
            Failure(InvalidIdToken(e.message.orEmpty()))
        }

    }

    override fun consumeFromAuthorizationResponse(idToken: IdToken): Result<Unit, InvalidIdToken> = Success(Unit)

    override fun nonceFromIdToken(idToken: IdToken): Nonce? = null

}