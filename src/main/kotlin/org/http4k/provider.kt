package org.http4k

import org.http4k.core.*
import org.http4k.security.*

fun OAuthProvider.Companion.oidcAuthServer(
    client: HttpHandler,
    credentials: Credentials,
    callbackUri: Uri,
    oAuthPersistence: OAuthPersistence,
    scopes: List<String> = listOf("openid")
): OAuthProvider {
    val providerConfig = OAuthProviderConfig(
        Uri.of("https://www.certification.openid.net"),
        "/test/a/http4k-oidc/authorize",
        "/test/a/http4k-oidc/token",
        credentials
    )
    return OAuthProvider(
        providerConfig,
        client,
        callbackUri,
        scopes,
        oAuthPersistence,
        { it.query("nonce", CrossSiteRequestForgeryToken.SECURE_CSRF().value) },
        CrossSiteRequestForgeryToken.SECURE_CSRF,
        accessTokenFetcherAuthenticator = BasicAuthAccessTokenFetcherAuthenticator(providerConfig)
    )
}


class BasicAuthAccessTokenFetcherAuthenticator(private val providerConfig: OAuthProviderConfig) :
    AccessTokenFetcherAuthenticator {
    override fun authenticate(request: Request) =
        request.header("Authorization", "Basic ${providerConfig.credentials.base64Encoded()}")

    private fun Credentials.base64Encoded(): String = "$user:$password".base64Encode()
}

