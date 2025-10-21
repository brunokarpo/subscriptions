package nom.brunokarpo.subscriptions.infra.database.jpa

import nom.brunokarpo.subscriptions.infra.database.jpa.entities.SubscriptionId
import nom.brunokarpo.subscriptions.infra.database.jpa.entities.SubscriptionMapper
import org.springframework.data.repository.CrudRepository

interface SubscriptionJpaRepository: CrudRepository<SubscriptionMapper, SubscriptionId>