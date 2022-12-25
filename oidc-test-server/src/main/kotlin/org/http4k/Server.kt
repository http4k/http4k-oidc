package org.http4k

import org.http4k.Config.baseUri
import org.http4k.Config.oauthCredentials
import org.http4k.Config.port
import org.http4k.authserver.AuthorisationServer
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.Port
import org.http4k.core.Credentials
import org.http4k.core.Uri
import org.http4k.core.extend
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.inIntelliJOnly
import org.http4k.lens.port
import org.http4k.lens.secret
import org.http4k.lens.uri
import org.http4k.relyingparty.RelyingParty
import org.http4k.relyingparty.SlightlyMoreSecureCookieBasedOauthPersistence
import org.http4k.routing.routes
import org.http4k.security.OAuthProvider
import org.http4k.server.Undertow
import org.http4k.server.asServer


object Config {
    val port = EnvironmentKey.port().defaulted("PORT", Port(9000))
    val baseUri = EnvironmentKey.uri().defaulted("BASE_URI", Uri.of("http://localhost:9000"))
    val oauthCredentials = EnvironmentKey.secret()
        .map { it.use { secret -> Credentials("http4k-oidc", secret) } }
        .required("CLIENT_SECRET")
}

fun main() {
    val environment = Environment.ENV
    val stack = DebuggingFilters.PrintRequestAndResponse().inIntelliJOnly()

    val client = stack.then(JavaHttpClient())

    val oAuthPersistence = SlightlyMoreSecureCookieBasedOauthPersistence()

    val oauthProvider = OAuthProvider.oidcAuthServer(
        client,
        oauthCredentials(environment),
        baseUri(environment).extend(Uri.of("/oauth/callback")),
        oAuthPersistence
    )

    val allAps = routes(
        RelyingParty(oauthProvider, oAuthPersistence, client),
        AuthorisationServer(),
        Health()
    )

    val printingApp = stack.then(allAps)
    val server = printingApp.asServer(Undertow(port(environment).value)).start()

    println("Server started on port " + server.port())
}
