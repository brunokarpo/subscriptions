package nom.brunokarpo.subscriptions.application.customer.exceptions

class ProductNotExistsException(
	productName: String
): Exception("Product with name '$productName' does not exists!")