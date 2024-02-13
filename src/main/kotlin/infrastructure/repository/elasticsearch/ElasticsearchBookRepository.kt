package infrastructure.repository.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch.core.BulkResponse
import co.elastic.clients.elasticsearch.core.GetRequest
import co.elastic.clients.elasticsearch.core.GetResponse
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.SearchResponse
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation
import domain.model.Book
import domain.repository.BookRepository

class ElasticsearchBookRepository(
    private val elasticsearchClient: ElasticsearchClient
): BookRepository {
    private val indexName = "books"

    override fun findAll(): List<Book> {
        try {
            // TODO searchがドキュメント通りに書いても動かないので、後日修正してコメントイン
//            val searchResponse: SearchResponse<Book> = elasticsearchClient.search(
//                SearchRequest.Builder().apply {
//                    it.index(indexName)
//                    it.query {
//                        it.matchAll() // 正しいmatchAllクエリの構築方法
//                    }
//                }.build(),
//                Book::class.java // 検索結果をBookクラスにマッピング
//            )

            // 検索結果からBookのリストを抽出して返す
//            return searchResponse.hits().hits().mapNotNull { it.source() }
            return emptyList()
        } catch (e: Exception) {
            println("An error occurred while fetching all books: ${e.message}")
            throw IllegalStateException("Failed to fetch all books", e)
        }
    }

    override fun findById(id: Int): Book? {
        try {
            val response: GetResponse<Book> = elasticsearchClient.get(GetRequest.Builder()
                .index(indexName)
                .id(id.toString())
                .build(), Book::class.java
            )

            return if (response.found()) {
                response.source() // 直接Bookオブジェクトを返す
            } else {
                null
            }
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun save(book: Book) {
        try {
            if (book.id != null) {
                // IDが指定されている場合は、そのIDを使用してドキュメントを登録
                elasticsearchClient.index {
                    it.index(indexName).id(book.id.toString()).document(book)
                }
            } else {
                // IDが指定されていない場合は、IDの指定を省略
                elasticsearchClient.index {
                    it.index(indexName).document(book)
                }
            }
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }

    }

    override fun delete(ids: List<Int>) {
        try {
            val operations = ids.map { id ->
                BulkOperation.of { op ->
                    op.delete { del -> del.index(indexName).id(id.toString()) }
                }
            }

            // バルクリクエストを構築して実行
            val bulkResponse: BulkResponse = elasticsearchClient.bulk {
                it.operations(operations)
            }

            // バルク操作の結果をチェック
            if (bulkResponse.errors()) {
                // エラーがある場合は、詳細をログに記録するなどの処理を行う
                println("Errors occurred during bulk delete operation.")
            }
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }
}