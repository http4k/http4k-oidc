package org.http4k

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes

fun AuthorisationServer() = routes(
    "as" bind routes(
        "/" bind { _: Request -> Response(Status.OK).body("Authorisation server") }
    )
)