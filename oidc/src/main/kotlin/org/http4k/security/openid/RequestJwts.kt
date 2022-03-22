package org.http4k.security.openid

import org.http4k.security.oauth.server.AuthRequest

fun interface RequestJwts {
    fun create(authRequest: AuthRequest): RequestJwtContainer
}

data class RequestJwtContainer(val value: String)
