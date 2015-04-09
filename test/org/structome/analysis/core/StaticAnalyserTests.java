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
package org.structome.analysis.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.structome.analysis.groovy.GroovyClassCodeVisitor;
import org.structome.analysis.groovy.GroovyProcessor;
import org.structome.util.FileCollector;

public class StaticAnalyserTests {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void dummyAnalyseJavaFile() throws Exception {
		final Indexable e = new Indexable() {
			public String getId() {
				return "1";
			}
		};

		StructureDatabase<Indexable> db = new SimpleCollectionDatabase<Indexable>();

		StaticAnalyser _staticAnalyser = new StaticAnalyser();

		FileCollector _fileCollector = new FileCollector(new File("src"));

		Processor<Indexable> _processor = new Processor<Indexable>() {
			private StructureDatabase<Indexable> tempDB;

			@Override
			public void setDatabase(StructureDatabase<Indexable> _db) {
				tempDB = _db;
			}

			@Override
			public void visit(File _f) throws DuplicateObjectException {
				tempDB.put(e);
			}

		};

		_processor.setDatabase(db);

		_staticAnalyser.addProcessor(".*\\.java", _processor);

		for (File _f : _fileCollector.collect("StaticAnalyser.java")) {
			_staticAnalyser.performAnalysisOn(_f);
		}

		assertEquals(1, db.getObjects().size());
	}

	@Test
	public void analyseJavaFileUsingGroovyParser() throws Exception {
		StaticAnalyser _staticAnalyser = new StaticAnalyser();

		FileCollector _fileCollector = new FileCollector(new File("src"));

		StructureDatabase<ClassDescriptor> db = new SimpleCollectionDatabase<ClassDescriptor>();

		Processor<ClassDescriptor> p = new GroovyProcessor(new GroovyClassCodeVisitor());

		_staticAnalyser.addProcessor(".*\\.java", p);

		p.setDatabase(db);

		for (File _f : _fileCollector.collect("StaticAnalyser.java")) {
			_staticAnalyser.performAnalysisOn(_f);
		}

		List<ClassDescriptor> _list = new ArrayList<ClassDescriptor>(db.find(".*\\.StaticAnalyser"));

		assertEquals(1, _list.size());

		ClassDescriptor _classDesc = _list.get(0);
	}

	@Test
	public void analyseJavaGenericsExpression() throws Exception {
		StructureDatabase<ClassDescriptor> db = new SimpleCollectionDatabase<ClassDescriptor>();

		Processor<ClassDescriptor> p = new GroovyProcessor(new GroovyClassCodeVisitor());

		p.setDatabase(db);

		folder.create();
		File _f = folder.newFile();

		PrintStream _ps = new PrintStream(_f);

		_ps.println("class TestClass {");
		_ps.println("	public List<ClassB> myList;");
		_ps.println("	public void testMethod(Set<ClassC> _set) {");
		_ps.println("		Map<ClassD, ClassE> _map = [:];");
		_ps.println("	}");
		_ps.println("}");

		_ps.close();

		p.visit(_f);

		ClassDescriptor _classDesc = db.get("TestClass");

		assertEquals(1, db.getObjects().size());
		VarDescriptor _varDesc = _classDesc.getMember("myList");
		assertNotNull(_varDesc);
		assertEquals(1, _varDesc.getGenerics().size());
		assertEquals("ClassB", _varDesc.getGenerics().get(0));

		List<MethodDescriptor> _methods = new ArrayList<MethodDescriptor>(_classDesc.getAllMethods());
		MethodDescriptor _method = _methods.get(0);

		VarDescriptor _param = _method.getVarDescriptor("_set");
		assertEquals(1, _param.getGenerics().size());
		assertEquals("ClassC", _param.getGenerics().get(0));
	}

	@Test
	public void analysePackageDependency() throws Exception {
		StructureDatabase<ClassDescriptor> db = new SimpleCollectionDatabase<ClassDescriptor>();

		Processor<ClassDescriptor> p = new GroovyProcessor(new GroovyClassCodeVisitor());

		p.setDatabase(db);

		folder.create();
		File _f1 = folder.newFile();
		File _f2 = folder.newFile();

		PrintStream _ps = new PrintStream(_f1);
		_ps.println("package org.test;");
		_ps.println("class TestClassB {");
		_ps.println("}");
		_ps.close();

		_ps = new PrintStream(_f2);
		_ps.println("package org.parentpackage;");
		_ps.println("class TestClassA {");
		_ps.println("}");
		_ps.close();

		p.visit(_f1);
		p.visit(_f2);

		ClassDescriptor _classDesc = db.get("org.test.TestClassB");

		assertNotNull(_classDesc);
		assertEquals("TestClassB", _classDesc.getSimpleName());

		_classDesc = db.get("org.parentpackage.TestClassA");

		assertNotNull(_classDesc);
		assertEquals("TestClassA", _classDesc.getSimpleName());
	}

	@Test
	public void analyseParentClassDifferentPackage() throws Exception {
		StructureDatabase<ClassDescriptor> db = new SimpleCollectionDatabase<ClassDescriptor>();

		Processor<ClassDescriptor> p = new GroovyProcessor(new GroovyClassCodeVisitor());

		p.setDatabase(db);

		folder.create();
		File _f1 = folder.newFile();
		File _f2 = folder.newFile();

		PrintStream _ps = new PrintStream(_f1);
		_ps.println("package org.test;");
		_ps.println("import org.parentpackage.TestClassA;");
		_ps.println("class TestClassB extends TestClassA {");
		_ps.println("}");
		_ps.close();

		_ps = new PrintStream(_f2);
		_ps.println("package org.parentpackage;");
		_ps.println("class TestClassA extends ClassFromA3rdPartyPackage {");
		_ps.println("}");
		_ps.close();

		p.visit(_f1);
		p.visit(_f2);

		ClassDescriptor _classDesc = db.get("org.test.TestClassB");

		assertEquals(2, db.getObjects().size());

		assertNotNull(_classDesc);
		assertNotNull(_classDesc.getSuperClass());
		assertTrue(_classDesc.isResolved());
		assertEquals("TestClassA", _classDesc.getSuperClass().getSimpleName());

		_classDesc = db.get("org.parentpackage.TestClassA");
		assertTrue(_classDesc.isResolved());
		assertEquals("ClassFromA3rdPartyPackage", _classDesc.getSuperClass().getName());
		assertFalse(_classDesc.getSuperClass().isResolved());
	}

	@Test
	public void analyseParentClassSamePackage() throws Exception {
		StructureDatabase<ClassDescriptor> db = new SimpleCollectionDatabase<ClassDescriptor>();

		Processor<ClassDescriptor> p = new GroovyProcessor(new GroovyClassCodeVisitor());

		p.setDatabase(db);

		folder.create();
		File _f1 = folder.newFile();

		PrintStream _ps = new PrintStream(_f1);
		_ps.println("package org.test;");
		_ps.println("class TestClassB extends TestClassA {");
		_ps.println("}");
		_ps.close();

		p.visit(_f1);

		ClassDescriptor _classDesc = db.get("org.test.TestClassB");

		assertEquals(1, db.getObjects().size());

		assertNotNull(_classDesc);
		assertNotNull(_classDesc.getSuperClass());
		assertFalse("TestClassA", _classDesc.getSuperClass().isResolved());
		assertEquals("TestClassA", _classDesc.getSuperClass().getName());
	}
	
	@Test
	public void analyseParentClassThirdPartyPackage() throws Exception {
		StructureDatabase<ClassDescriptor> db = new SimpleCollectionDatabase<ClassDescriptor>();

		Processor<ClassDescriptor> p = new GroovyProcessor(new GroovyClassCodeVisitor());

		p.setDatabase(db);

		folder.create();
		File _f1 = folder.newFile();

		PrintStream _ps = new PrintStream(_f1);
		_ps.println("package org.test;");
		_ps.println("import some.third.party.ClassFromA3rdPartyPackage;");
		_ps.println("class TestClassA extends ClassFromA3rdPartyPackage {");
		_ps.println("}");
		_ps.close();

		p.visit(_f1);

		assertEquals(1, db.getObjects().size());

		ClassDescriptor _classDesc = db.get("org.test.TestClassA");

		assertNotNull(_classDesc);
		assertNotNull(_classDesc.getSuperClass());
		assertEquals("some.third.party.ClassFromA3rdPartyPackage", _classDesc.getSuperClass().getName());
	}
}
