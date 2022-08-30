package org.http4k.tools

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import java.io.File
import java.io.FileWriter


fun main() {
    val basePath = "oidc-test-server/src/main/resources"

    val rsaJWK = RSAKeyGenerator(2048)
        .keyID("http4k-oidc-rsa")
        .generate()

    val jwks = JWKSet(rsaJWK).toPublicJWKSet()
    File("$basePath/jwks.json").writeText(jwks.toString())

    JcaPEMWriter(FileWriter(File("$basePath/http4k-oidc-rsa.pem"))).use {
        it.writeObject(rsaJWK.toRSAPrivateKey())
    }
}