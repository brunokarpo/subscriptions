package nom.brunokarpo.subscriptions.application.customer.exceptions

import nom.brunokarpo.subscriptions.application.usecases.ApplicationException

class ProductNotExistsException(
	productName: String
): ApplicationException("Product with name '$productName' does not exists!")