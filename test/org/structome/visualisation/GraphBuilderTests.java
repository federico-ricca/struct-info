/*************************************************************************** 
   Copyright 2015 Federico Ricca

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ***************************************************************************/
package org.structome.visualisation;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.structome.analysis.groovy.GroovyJavaDatabase;
import org.structome.analysis.groovy.GroovyJavaDatabaseReader;
import org.structome.analysis.groovy.graph.GrailsClassDependencyGraphBuilder;

public class GraphBuilderTests {
	@ClassRule
	public static TemporaryFolder folder = new TemporaryFolder();

	@BeforeClass
	public static void setUp() throws IOException {
		folder.create();
	}

	@Test
	public void buildGraphSimpleClassDependency() throws IOException {
		File _testDB = folder.newFile();

		PrintStream _ps = new PrintStream(_testDB);

		_ps.println("CLASS:org.test.pkg.ClassA");
		_ps.println("FIELD_NAME:fieldA");
		_ps.println("FIELD_TYPE:List");
		_ps.println("FIELD_NAME:fieldB");
		_ps.println("FIELD_TYPE:java.lang.Object");
		_ps.println("FIELD_NAME:fieldC");
		_ps.println("FIELD_TYPE:String");
		_ps.println("FIELD_NAME:fieldD");
		_ps.println("FIELD_TYPE:ClassB");
		_ps.println("CLASS:org.test.pkg.ClassB");
		_ps.println("FIELD_NAME:fieldA");
		_ps.println("FIELD_TYPE:List");

		_ps.close();

		GroovyJavaDatabase db = new GroovyJavaDatabaseReader().read(_testDB);

		ByteArrayOutputStream _out = new ByteArrayOutputStream();

		_ps = new PrintStream(_out);
		GrailsClassDependencyGraphBuilder _builder = new GrailsClassDependencyGraphBuilder(_ps);

		db.traverse(_builder);

		BufferedReader _reader = new BufferedReader(new StringReader(_out.toString()));

		String _line = _reader.readLine();

		assertEquals("ClassA,ClassB", _line);
	}

	@Test
	public void buildGraphTransitiveClassDependency() throws IOException {
		File _testDB = folder.newFile();

		PrintStream _ps = new PrintStream(_testDB);

		_ps.println("CLASS:org.test.pkg.ClassA");
		_ps.println("FIELD_NAME:fieldA");
		_ps.println("FIELD_TYPE:List");
		_ps.println("FIELD_NAME:fieldB");
		_ps.println("FIELD_TYPE:java.lang.Object");
		_ps.println("FIELD_NAME:fieldC");
		_ps.println("FIELD_TYPE:String");
		_ps.println("FIELD_NAME:fieldD");
		_ps.println("FIELD_TYPE:ClassB");
		_ps.println("CLASS:org.test.pkg.ClassB");
		_ps.println("FIELD_NAME:fieldA");
		_ps.println("FIELD_TYPE:ClassC");
		_ps.println("CLASS:org.test.pkg.ClassC");
		_ps.println("FIELD_NAME:fieldA");
		_ps.println("FIELD_TYPE:List");

		_ps.close();

		GroovyJavaDatabase db = new GroovyJavaDatabaseReader().read(_testDB);

		ByteArrayOutputStream _out = new ByteArrayOutputStream();

		_ps = new PrintStream(_out);
		GrailsClassDependencyGraphBuilder _builder = new GrailsClassDependencyGraphBuilder(_ps);

		db.traverse(_builder);

		BufferedReader _reader = new BufferedReader(new StringReader(_out.toString()));

		String _line = _reader.readLine();
		assertEquals("ClassB,ClassC", _line);

		_line = _reader.readLine();
		assertEquals("ClassA,ClassB", _line);
	}

	@Test
	public void buildGraphUseGenerics() throws IOException {
		File _testDB = folder.newFile();

		PrintStream _ps = new PrintStream(_testDB);

		_ps.println("CLASS:org.test.pkg.ClassA");
		_ps.println("FIELD_NAME:fieldA");
		_ps.println("FIELD_TYPE:List");
		_ps.println("FIELD_NAME:fieldB");
		_ps.println("FIELD_TYPE:java.lang.Object");
		_ps.println("FIELD_NAME:fieldC");
		_ps.println("FIELD_TYPE:String");
		_ps.println("FIELD_NAME:fieldD");
		_ps.println("FIELD_TYPE:ClassB");
		_ps.println("GENERICS:ClassZ");
		_ps.println("GENERICS:ClassW");
		_ps.println("CLASS:org.test.pkg.ClassB");
		_ps.println("FIELD_NAME:fieldA");
		_ps.println("FIELD_TYPE:List");

		_ps.close();

		GroovyJavaDatabase db = new GroovyJavaDatabaseReader().read(_testDB);

		ByteArrayOutputStream _out = new ByteArrayOutputStream();

		_ps = new PrintStream(_out);
		GrailsClassDependencyGraphBuilder _builder = new GrailsClassDependencyGraphBuilder(_ps);

		db.traverse(_builder);

		BufferedReader _reader = new BufferedReader(new StringReader(_out.toString()));

		String _line = _reader.readLine();

		assertEquals("ClassA,ClassB", _line);

		_line = _reader.readLine();
		assertEquals("ClassA,ClassZ", _line);

		_line = _reader.readLine();
		assertEquals("ClassA,ClassW", _line);
	}
}
