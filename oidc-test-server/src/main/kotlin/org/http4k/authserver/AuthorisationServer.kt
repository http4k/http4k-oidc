package org.http4k.authserver

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.format.Jackson
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.AccessToken
import org.http4k.security.oauth.server.*
import org.http4k.security.oauth.server.accesstoken.AuthorizationCodeAccessTokenRequest
import java.time.Clock
import java.time.temporal.ChronoUnit.DAYS
import java.util.*

fun AuthorisationServer(): RoutingHttpHandler {
    val server = OAuthServer(
        tokenPath = "/token",
        authRequestTracking = InsecureCookieBasedAuthRequestTracking(),
        clientValidator = InsecureClientValidator(),
        authorizationCodes = InsecureAuthorizationCodes(),
        accessTokens = InsecureAccessTokens(),
        json = Jackson,
        clock = Clock.systemUTC(),
    )

    return routes(
        "as" bind routes(
            server.tokenRoute,
            "/jwks" bind GET to { _: Request -> Response(OK).body("{}") },
            "/userinfo" bind GET to { _: Request -> Response(OK).body("{}") },
            "/authorize" bind GET to server.authenticationStart.then {
                Response(OK)
                    .body("""<html><form method="POST"><button type="submit">Please authenticate</button></form></html>""")
            },
            "/authorize" bind POST to server.authenticationComplete,
            "/" bind { _: Request -> Response(OK).body("Authorisation server") }
        )
    )
}

class InsecureClientValidator : ClientValidator {
    override fun validateClientId(request: Request, clientId: ClientId): Boolean = true

    override fun validateRedirection(request: Request, clientId: ClientId, redirectionUri: Uri): Boolean = true

    override fun validateScopes(request: Request, clientId: ClientId, scopes: List<String>): Boolean = true

    override fun validateCredentials(request: Request, clientId: ClientId, clientSecret: String): Boolean = true
}

class InsecureAuthorizationCodes : AuthorizationCodes {
    private val clock = Clock.systemUTC()
    private val codes = mutableMapOf<AuthorizationCode, AuthorizationCodeDetails>()

    override fun detailsFor(code: AuthorizationCode) =
        codes[code] ?: error("code not stored")

    override fun create(request: Request, authRequest: AuthRequest, response: Response) =
        Success(AuthorizationCode(UUID.randomUUID().toString()).also {
            codes[it] = AuthorizationCodeDetails(
                authRequest.client,
                authRequest.redirectUri!!,
                clock.instant().plus(1, DAYS),
                authRequest.state,
                authRequest.isOIDC()
            )
        })
}

class InsecureAccessTokens : AccessTokens {
    override fun create(clientId: ClientId, tokenRequest: TokenRequest) =
        Failure(UnsupportedGrantType("client_credentials"))

    override fun create(
        clientId: ClientId,
        tokenRequest: AuthorizationCodeAccessTokenRequest,
        authorizationCode: AuthorizationCode
    ) =
        Success(AccessToken(UUID.randomUUID().toString()))
}