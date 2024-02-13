package domain.repository

import domain.model.Book

interface BookRepository {
    fun findById(id: Int): Book?

    fun findAll(): List<Book>

    fun save(book: Book)

    fun delete(ids: List<Int>)
}