package org.structome.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.groovy.ast.ClassNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.structome.parsing.groovy.GroovyProcessor;
import org.structome.parsing.groovy.GroovyProcessorCodeVisitor;

public class GroovyProcessorTests {
	static String[] sourceCode = {
		"package some.package;",
		"import some.other.package.BaseClass;",			
		"class TestClassA extends BaseClass {",		
		"	public List<ClassB> myList;",		
		"	public void testMethod(Set<ClassC> _set) {",		
		"		Map<ClassD, ClassE> _map = [:];",		
		"		SomeClass.someStaticMethod(0);", 		
		"		if (_set instanceof AnotherClass) {",		
		"			BaseClass p = (AnotherClass) _set;", 
		"		}", 
		"	}", 
		"}" };

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testClassDeclaration() throws IOException {
		// Create temporary folder and file for source code
		folder.create();
		File _f = folder.newFile();

		PrintStream _ps = new PrintStream(_f);
		for (String s : sourceCode) {
			_ps.println(s);
		}
		_ps.close();

		// Create a Groovy processor that will return a set of classnames
		GroovyProcessor<Set<String>> _groovyProcessor = new GroovyProcessor<Set<String>>();

		_groovyProcessor.setProductFactory(new ProductFactory<Set<String>>() {
			@Override
			public Set<String> newProduct() {
				return new HashSet<String>();
			}
		});

		// Create and set a code visitor for collecting class names
		_groovyProcessor.setCodeVisitor(new GroovyProcessorCodeVisitor<Set<String>>() {

			@Override
			public void visitClass(ClassNode node) {
				super.visitClass(node);
				
				Set<String> _prod = this.getProduct();
				
				_prod.add(node.getNameWithoutPackage());
				
				_prod.add(node.getSuperClass().getNameWithoutPackage());
			}

		});

		Set<String> _product;
		try {
			_product = _groovyProcessor.process(_f);

			assertEquals(2, _product.size());
			assertTrue(_product.contains("TestClassA"));
			assertTrue(_product.contains("BaseClass"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
