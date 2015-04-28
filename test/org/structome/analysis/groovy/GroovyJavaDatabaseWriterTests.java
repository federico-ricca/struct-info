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
package org.structome.analysis.groovy;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.structome.analysis.core.ClassDescriptor;
import org.structome.analysis.core.Processor;

public class GroovyJavaDatabaseWriterTests {
	static String[] sourceCode = { 
		"class TestClass {", 
		"	public List<ClassB> myList;",
		"	public void testMethod(Set<ClassC> _set) {", 
		"		Map<ClassD, ClassE> _map = [:];", 
		"	}", 
		"}" 
		};
	
	static String[] compiledCode = {
		"CLASS:TestClass",
		"SUPER:Object",
		"FIELD_NAME:myList",
		"FIELD_TYPE:List",
		"GENERICS:ClassB",
		"METHOD:testMethod",
		"LOCAL_VAR_NAME:_map",
		"LOCAL_VAR_TYPE:Map",
		"GENERICS:ClassD",
		"GENERICS:ClassE",
		"LOCAL_VAR_NAME:_set",
		"LOCAL_VAR_TYPE:Set",
		"GENERICS:ClassC",
		"END_METHOD"
	};
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testWriteDatabase() throws Exception {
		GroovyJavaDatabase db = new GroovyJavaDatabase();

		Processor<ClassDescriptor> p = new GroovyProcessor(new GroovyClassCodeVisitor());

		p.setDatabase(db);

		folder.create();
		File _f = folder.newFile();

		PrintStream _ps = new PrintStream(_f);

		for (String s : sourceCode) {
			_ps.println(s);
		}

		_ps.close();

		p.visit(_f);

		FileOutputStream _fos = new FileOutputStream(_f);

		GroovyJavaDatabaseWriter _writer = new GroovyJavaDatabaseWriter(_fos);
		
		db.traverse(_writer);
		
		_fos.close();
		
		BufferedReader _reader = new BufferedReader(new FileReader(_f));
		
		String _line = _reader.readLine();
		int i=0;
		
		while (_line != null) {
			assertEquals(compiledCode[i], _line);
			
			_line = _reader.readLine();
			i++;
		}
		
		assertEquals(compiledCode.length, i);
		
		_reader.close();
	}
}
