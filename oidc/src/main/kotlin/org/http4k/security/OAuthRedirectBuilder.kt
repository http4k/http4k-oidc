package org.http4k.security

import org.http4k.core.Uri
import org.http4k.core.query
import org.http4k.security.oauth.server.AuthRequest
import org.http4k.security.openid.RequestJwts

typealias RedirectionUriBuilder = (Uri, AuthRequest) -> Uri

val defaultUriBuilder: RedirectionUriBuilder = { uri: Uri, authRequest: AuthRequest ->
    val oauthUri = uri.query("client_id", authRequest.client.value)
        .query("response_type", authRequest.responseType.queryParameterValue)
        .query("scope", authRequest.scopes.joinToString(" "))
        .query("redirect_uri", authRequest.redirectUri.toString())
        .query("state", authRequest.state?.value)
    if (authRequest.nonce != null) {
        oauthUri.query("nonce", authRequest.nonce.value)
    } else {
        oauthUri
    }
}

fun uriBuilderWithRequestJwt(requestJwts: RequestJwts) = { uri: Uri, authRequest: AuthRequest ->
    defaultUriBuilder(uri, authRequest)
        .query("request", requestJwts.create(authRequest).value)
}
