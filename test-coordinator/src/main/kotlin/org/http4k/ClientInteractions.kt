package org.http4k

import org.http4k.client.JavaHttpClient
import org.http4k.core.*
import org.http4k.filter.ClientFilters
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.inIntelliJOnly

class ClientInteractions {
    private val client = Filter.NoOp.then(ClientFilters.FollowRedirects()).then(ClientFilters.Cookies())
        .then(DebuggingFilters.PrintRequestAndResponse().inIntelliJOnly()).then(JavaHttpClient())

    fun performBasicOauth() {
        client(Request(Method.GET, "https://http4k-oidc.herokuapp.com/oauth"))
    }
}