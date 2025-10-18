package nom.brunokarpo.subscriptions.domain.common

abstract class Identifier<out T>(
    private val id: T,
) {
    fun value(): T = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Identifier<*>

        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = id.toString()
}
