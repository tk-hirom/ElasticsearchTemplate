import application.dto.BookDto
import application.usecase.BookRegistrationUseCase
import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import domain.repository.BookRepository
import infrastructure.repository.elasticsearch.ElasticsearchBookRepository
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
        }
    }
    val restClient = RestClient.builder(
        HttpHost("localhost", 9200) // Elasticsearchのホストとポートを指定
    ).build()
    val transport = RestClientTransport(restClient, JacksonJsonpMapper())
    val bookRepository: BookRepository = ElasticsearchBookRepository(ElasticsearchClient(transport))
    val bookRegistrationUseCase = BookRegistrationUseCase(bookRepository)

    routing {
        post("/books/register") {
            val bookRegistrationRequest = call.receive<BookRegistrationRequest>()

            bookRegistrationUseCase.registerBook(
                BookDto(
                    id = null,
                    title = bookRegistrationRequest.title,
                    price = bookRegistrationRequest.price
                )
            )

            call.respond(HttpStatusCode.Created)
        }
    }
}

data class BookRegistrationRequest(
    val title: String,
    val price: Int
)
