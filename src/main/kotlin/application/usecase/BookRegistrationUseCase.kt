package application.usecase

import application.dto.BookDto
import infrastructure.repository.elasticsearch.ElasticsearchBookRepository

class BookRegistrationUseCase(
    private val elasticsearchBookRepository: ElasticsearchBookRepository
) {
    fun registerBook(bookDto: BookDto) {
        try {
            elasticsearchBookRepository.save(bookDto.toBook())
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }
}