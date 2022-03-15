package org.http4k

import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.security.*

fun OAuthProvider.Companion.oidcAuthServer(
    client: HttpHandler,
    credentials: Credentials,
    callbackUri: Uri,
    oAuthPersistence: OAuthPersistence,
    scopes: List<String> = listOf("profile", "email", "phone", "address", "openid")
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
        { it },
        CrossSiteRequestForgeryToken.SECURE_CSRF,
        accessTokenFetcherAuthenticator = BasicAuthAccessTokenFetcherAuthenticator(providerConfig),
    )
}

