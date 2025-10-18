package nom.brunokarpo.subscriptions.application.product.exceptions

import nom.brunokarpo.subscriptions.application.usecases.ApplicationException

class ProductUniqueNameException(
    name: String,
) : ApplicationException("Product with name '$name' already exists!")
