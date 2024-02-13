package application.dto

import domain.model.Book

data class BookDto(
    val id: Int? = null,
    val title: String,
//    val authorId: Int,
    val price: Int,
) {
    fun toBook(): Book {
        return Book(
            id = id,
            title = title,
//            authorId = authorId,
            price = price
        )
    }
}