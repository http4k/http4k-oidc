package org.http4k

import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.filter.ClientFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.OAuthPersistence
import org.http4k.security.OAuthProvider

fun RelyingParty(
    oauthProvider: OAuthProvider,
    oAuthPersistence: OAuthPersistence,
    client: HttpHandler
) = routes(
    "/oauth" bind routes(
        "/" bind GET to oauthProvider.authFilter.then {
            val token = oAuthPersistence.retrieveToken(it)?.value ?: error("not authenticated")
            val authenticatedClient = ClientFilters.BearerAuth(token).then(client)
            val userInfoEndpoint = oauthProvider.providerConfig.apiBase.extend(Uri.of("/test/a/http4k-oidc/userinfo"))

            val userInfo = authenticatedClient(Request(GET, userInfoEndpoint)).bodyString()

            Response(Status.OK).body(userInfo)
        },
        "/callback" bind GET to oauthProvider.callback
    )
)