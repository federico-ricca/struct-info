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
package org.structome.extraction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.structome.analysis.core.ClassDescriptor;
import org.structome.analysis.core.Processor;
import org.structome.analysis.core.StaticAnalyser;
import org.structome.analysis.groovy.GroovyClassCodeVisitor;
import org.structome.analysis.groovy.GroovyJavaDatabase;
import org.structome.analysis.groovy.GroovyJavaDatabaseWriter;
import org.structome.analysis.groovy.GroovyProcessor;
import org.structome.util.FileCollector;

public class DataExtractor {
	public static void main(String[] args) throws IOException {
		StaticAnalyser _staticAnalyser = new StaticAnalyser();

		GroovyJavaDatabase db = new GroovyJavaDatabase();

		Processor<ClassDescriptor> p = new GroovyProcessor(new GroovyClassCodeVisitor());

		_staticAnalyser.addProcessor(".*\\.(java|groovy)", p);

		p.setDatabase(db);

		FileCollector _fileCollector = new FileCollector(new File(args[0]));

		for (File _f : _fileCollector.collect()) {
			try {
				System.out.println("Analysing file " + _f.getName());
				_staticAnalyser.performAnalysisOn(_f);
			} catch (Exception e) {

			}
		}

		System.out.println("Objects: " + db.getObjects().size());

		FileOutputStream _fos = new FileOutputStream("database.cstruct");

		GroovyJavaDatabaseWriter _writer = new GroovyJavaDatabaseWriter(_fos);
		
		db.traverse(_writer);
		
		_fos.close();

		System.out.println("Done.");
	}
}
