package nom.brunokarpo.subscriptions.domain.customer.subscriptions

import nom.brunokarpo.subscriptions.domain.customer.exceptions.SubscriptionStatusUnknownException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SubscriptionStatusTest {

    @Test
    fun `should create subscription status by name`() {
        val status = SubscriptionStatus.of("REQUESTED")

        assertEquals(SubscriptionStatus.REQUESTED, status)
    }

    @Test
    fun `should create subscription status by name no matter the case`() {
        val status = SubscriptionStatus.of("requested")

        assertEquals(SubscriptionStatus.REQUESTED, status)
    }

    @Test
    fun `should throw subscription status unknown when status does not exists`() {
        val exception = assertThrows<SubscriptionStatusUnknownException> {
            SubscriptionStatus.of("NON_EXISTENT_STATUS")
        }
        assertEquals("Subscription Status unknown: NON_EXISTENT_STATUS", exception.message)
    }
}