package application.usecase

import application.dto.BookDto
import domain.repository.BookRepository
import infrastructure.repository.elasticsearch.ElasticsearchBookRepository

class BookRegistrationUseCase(
    private val bookRepository: BookRepository
) {
    fun registerBook(bookDto: BookDto) {
        try {
            bookRepository.save(bookDto.toBook())
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }
}