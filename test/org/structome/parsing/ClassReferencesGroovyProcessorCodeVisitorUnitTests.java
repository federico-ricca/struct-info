package org.structome.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.structome.parsing.groovy.ClassReferencesGroovyProcessorCodeVisitor;
import org.structome.parsing.groovy.GroovyProcessor;

public class ClassReferencesGroovyProcessorCodeVisitorUnitTests {
	static String[] sourceCode = { 
		"package some.package;", 
		"import some.other.package.BaseClass;",
		"import some.other.package.UnusedClass;",
		"class TestClassA extends BaseClass {",
		"	public List<ClassB> myList;",
		"	public ReturnValue testMethod(Set<ClassC> _set) {",
		"		Map<ClassD, ClassE> _map = [:];",
		"		SomeClass.someStaticMethod(0);",
		"		if (_set instanceof AnotherClass) {",
		"			BaseClass p = (SomeOtherClass) _set;", 
		"		}",
		"		return null;",
		"	}",
		"}" };

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testClassReferences() throws IOException {
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

		// Create and set a code visitor for collecting main class name and
		// superclass
		_groovyProcessor.setCodeVisitor(new ClassReferencesGroovyProcessorCodeVisitor());

		Set<String> _product;
		try {
			_product = _groovyProcessor.process(_f);

			assertTrue(_product.contains("TestClassA"));
			assertTrue(_product.contains("BaseClass"));
			assertTrue(_product.contains("UnusedClass"));
			assertTrue(_product.contains("List"));
			assertTrue(_product.contains("ClassB"));
			assertTrue(_product.contains("ClassC"));
			assertTrue(_product.contains("ClassD"));
			assertTrue(_product.contains("ClassE"));
			assertTrue(_product.contains("SomeClass"));
			assertTrue(_product.contains("AnotherClass"));
			assertTrue(_product.contains("SomeOtherClass"));
			assertTrue(_product.contains("ReturnValue"));
			assertEquals(15, _product.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
