package nom.brunokarpo.subscriptions.application.product.exceptions

class ProductUniqueNameException(
	name: String
) : Exception("Product with name '$name' already exists!")