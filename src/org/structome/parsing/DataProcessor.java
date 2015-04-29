package org.structome.parsing;

import java.io.File;

public abstract class DataProcessor<T> {
	private ProductFactory<T> productFactory;

	public abstract T processInput(File _file, T _prototype) throws Exception;

	public T process(File _file) throws Exception{
		T _prototype = productFactory.newProduct();

		T _product = this.processInput(_file, _prototype);

		return _product;
	}

	public void setProductFactory(ProductFactory<T> _productFactory) {
		productFactory = _productFactory;
	}

}
