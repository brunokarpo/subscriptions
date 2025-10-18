package nom.brunokarpo.subscriptions.infra.app

import nom.brunokarpo.subscriptions.infra.api.ApiConfiguration
import nom.brunokarpo.subscriptions.infra.database.DatabaseConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [SubscriptionsApplication::class, DatabaseConfiguration::class, ApiConfiguration::class])
class SubscriptionsApplication

fun main(args: Array<String>) {
    runApplication<SubscriptionsApplication>(*args)
}
