package org.structured_barebone.data.model

/** A non-success HTTP response, with the parsed problem document when the body contained one. */
class ServerException(
    val status: Int,
    val problem: ProblemDetails? = null,
) : Exception("HTTP $status" + (problem?.code?.let { " ($it)" } ?: ""))
