import application.dto.BookDto
import application.usecase.BookRegistrationUseCase
import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import infrastructure.repository.elasticsearch.ElasticsearchBookRepository
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient

fun main(args: Array<String>) : Unit = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
        }
    }
    routing {
        post("/books/register") {
            val bookRegistrationRequest = call.receive<BookRegistrationRequest>()

            val restClient = RestClient.builder(
                HttpHost("localhost", 9200) // Elasticsearchのホストとポートを指定
            ).build()

            val transport = RestClientTransport(restClient, JacksonJsonpMapper())
            val bookRegistrationUseCase = BookRegistrationUseCase(ElasticsearchBookRepository(ElasticsearchClient(transport)))
            bookRegistrationUseCase.registerBook(
                BookDto(
                    id = null,
                    title = bookRegistrationRequest.title,
//                    authorId = bookRegistrationRequest.authorId.toInt(),
                    price = bookRegistrationRequest.price
                )
            )
            call.respond(HttpStatusCode.Created)
        }

        get("/") {
            val restClient = RestClient.builder(
                HttpHost("localhost", 9200) // Elasticsearchのホストとポートを指定
            ).build()

            val transport = RestClientTransport(restClient, JacksonJsonpMapper())
            val elasticsearchBookRepository = ElasticsearchBookRepository(ElasticsearchClient(transport))
            val books = elasticsearchBookRepository.findAll()
            call.respond(books)

        }
    }
}

data class BookRegistrationRequest(
    val title: String,
//    val authorId: String,
    val publicationDate: String,
    val price: Int
)