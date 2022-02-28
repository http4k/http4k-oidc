package org.http4k

import org.http4k.Config.baseUri
import org.http4k.Config.oauthCredentials
import org.http4k.Config.port
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.Port
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ClientFilters
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.inIntelliJOnly
import org.http4k.lens.port
import org.http4k.lens.secret
import org.http4k.lens.uri
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.InsecureCookieBasedOAuthPersistence
import org.http4k.security.OAuthProvider
import org.http4k.server.Undertow
import org.http4k.server.asServer


fun app(
    oauthProvider: OAuthProvider,
    oAuthPersistence: InsecureCookieBasedOAuthPersistence,
    client: HttpHandler
) = routes(
    "/health" bind GET to { Response(OK) },
    "/oauth" bind routes(
        "/" bind GET to oauthProvider.authFilter.then {
            val authenticatedClient =
                ClientFilters.BearerAuth(oAuthPersistence.retrieveToken(it)?.value ?: error("not authenticated"))
                    .then(client)

            val userInfo = authenticatedClient(
                Request(
                    GET,
                    oauthProvider.providerConfig.apiBase.extend(Uri.of("/test/a/http4k-oidc/userinfo"))
                )
            ).bodyString()

            Response(OK).body(userInfo)
        },
        "/callback" bind GET to oauthProvider.callback
    )
)

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

    val oAuthPersistence = InsecureCookieBasedOAuthPersistence("http4k-oidc")

    val oauthProvider = OAuthProvider.oidcAuthServer(
        client,
        oauthCredentials(environment),
        baseUri(environment).extend(Uri.of("/oauth/callback")),
        oAuthPersistence
    )

    val printingApp = stack.then(app(oauthProvider, oAuthPersistence, client))
    val server = printingApp.asServer(Undertow(port(environment).value)).start()

    println("Server started on " + server.port())
}
