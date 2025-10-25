package nom.brunokarpo.subscriptions.infra.database.repositories

import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ProductDatabaseRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : ProductRepository {
    override suspend fun save(aggregate: Product) {
        val sql = """
            INSERT INTO products (id, name, created_at)
            VALUES (:id, :name, now())
        """.trimIndent()
        val params = mapOf(
            "id" to aggregate.id.value(),
            "name" to aggregate.name
        )
        jdbcTemplate.update(sql, params)
    }

    override suspend fun findById(id: ProductId): Product? {
        val sql = """
            SELECT id, name FROM products WHERE id = :id
        """.trimIndent()
        val params = mapOf(
            "id" to id.value()
        )
        return jdbcTemplate.query(sql ,params) { rs, _ ->
            Product.create(
                id = ProductId.from(rs.getObject("id", UUID::class.java)),
                name = rs.getString("name")
            )
        }.firstOrNull()
    }

    override suspend fun findByName(name: String): Product? {
        val sql = """
            SELECT id, name FROM products WHERE lower(name) = :name
        """.trimIndent()
        val params = mapOf(
            "name" to name.lowercase()
        )
        return jdbcTemplate.query(sql ,params) { rs, _ ->
            Product.create(
                id = ProductId.from(rs.getObject("id", UUID::class.java)),
                name = rs.getString("name")
            )
        }.firstOrNull()
    }
}
