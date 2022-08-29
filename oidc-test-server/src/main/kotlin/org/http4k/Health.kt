package org.http4k

import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind

fun Health() = "/health" bind Method.GET to { Response(Status.OK) }