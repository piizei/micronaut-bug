package bug

import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import jakarta.inject.Singleton
import reactor.core.publisher.Flux

@Controller("/bug")
class BugController(
    private val topClient: TopClient
) {
    @Get(uri="/")
    @Produces(MediaType.APPLICATION_JSON)
    fun index(): Flux<Top>? {
        return topClient.get();
    }

    @Get(uri="/test")
    @Produces(MediaType.APPLICATION_JSON)
    fun test(): String {
        return "[{\"a\":\"avalue\",\"b\":3, \"nested\": [{\"c\":\"cvalue\",\"d\":5}]}]"
    }
}

@Singleton
class TopClient(@param:Client("http://localhost:8080/") private val httpClient: HttpClient) {
    fun get(): Flux<Top>? {
        val req: HttpRequest<*> = HttpRequest.GET<Any>("http://localhost:8080/bug/test")
        return Flux.from(httpClient.retrieve(req, Argument.listOf(Top::class.java)))
            ?.onErrorReturn(emptyList())
            ?.flatMap { it -> Flux.fromIterable(it) }
    }
}

@Introspected
data class Top(
    val a: String,
    val b: Int,
    val nested: Set<Nested>
)

@Introspected
data class Nested(
    val c: String = "",
    val d: Int = 0,
)
