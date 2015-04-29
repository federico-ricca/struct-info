package org.structome.parsing.groovy;

import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.control.SourceUnit;

public class GroovyProcessorCodeVisitor<T> extends ClassCodeVisitorSupport {
	private SourceUnit sourceUnit;
	private T product;

	@Override
	protected SourceUnit getSourceUnit() {
		return sourceUnit;
	}

	public void setSourceUnit(SourceUnit _sourceUnit) {
		sourceUnit = _sourceUnit;
	}
	
	public void setProduct(T _product) {
		product = _product;
	}
	
	public T getProduct() {
		return product;
	}
}
