package org.http4k.authserver

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.http4k.security.Nonce
import java.util.*


val jwks by lazy { JWKSet.load(object {}::class.java.getResourceAsStream("/jwks.json")).toPublicJWKSet().toString() }

fun idToken(nonce: Nonce): String {
    val key = String(
        object {}::class.java.getResourceAsStream("/http4k-oidc-rsa.pem")?.readAllBytes()
            ?: error("could not load private key")
    )
    val jwtKey = RSAKey.parseFromPEMEncodedObjects(key).toRSAKey()
    val signer: JWSSigner = RSASSASigner(jwtKey)
    val claimsSet = JWTClaimsSet.Builder()
        .subject("alice")
        .issuer("https://http4k-oidc.herokuapp.com")
        .claim("nonce", nonce.value)
        .expirationTime(Date(Date().time + 60 * 1000))
        .build()
    val signedJWT = SignedJWT(
        JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwtKey.keyID).build(),
        claimsSet
    )

    signedJWT.sign(signer)
    return signedJWT.serialize()
}

fun main() {
    println(idToken(Nonce("bob")))
}