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
package org.structome.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.structome.analysis.core.ClassDescriptor;
import org.structome.analysis.core.ClassMetadata;
import org.structome.analysis.core.GroovyProcessor;
import org.structome.analysis.core.MethodCallDescriptor;
import org.structome.analysis.core.MethodDescriptor;
import org.structome.analysis.core.Processor;
import org.structome.analysis.core.SimpleCollectionDatabase;
import org.structome.analysis.core.StaticAnalyser;
import org.structome.analysis.core.StructureDatabase;
import org.structome.analysis.core.VarDescriptor;
import org.structome.util.FileCollector;

public class DataExtractor {
	public static void main(String[] args) throws FileNotFoundException {
		StaticAnalyser _staticAnalyser = new StaticAnalyser();

		StructureDatabase<ClassDescriptor> db = new SimpleCollectionDatabase<ClassDescriptor>();

		Processor<ClassDescriptor> p = new GroovyProcessor();

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

		PrintStream _ps = new PrintStream(new File("database.cstruct"));

		for (ClassDescriptor _classDesc : db.getObjects()) {
			String _name = _classDesc.getName();
			_ps.println("CLASS:" + _name);

			if (_classDesc.getSuperClass() != null) {
				ClassDescriptor _superClassDesc = _classDesc.getSuperClass();
				_ps.println("SUPER:" + _superClassDesc.getName());
			}

			for (VarDescriptor _varDesc : _classDesc.getAllMembers()) {
				_ps.println("FIELD_NAME:" + _varDesc.getName());
				_ps.println("FIELD_TYPE:" + _varDesc.getType());

				for (String _generics : _varDesc.getGenerics()) {
					_ps.println("GENERICS:" + _generics);
				}
			}

			for (MethodDescriptor _methodDesc : _classDesc.getAllMethods()) {
				_ps.println("METHOD:" + _methodDesc.getName());

				for (VarDescriptor _localVarDesc : _methodDesc.getAllVarDescriptors()) {
					_ps.println("LOCAL_VAR_NAME:" + _localVarDesc.getName());
					_ps.println("LOCAL_VAR_TYPE:" + _localVarDesc.getType());

					for (String _generics : _localVarDesc.getGenerics()) {
						_ps.println("GENERICS:" + _generics);
					}
				}

				for (MethodCallDescriptor _methodCallDesc : _methodDesc.getAllMethodCalls()) {
					_ps.println("METHOD_CALL:" + _methodCallDesc.getMethodName());
					_ps.println("RECEIVER_NAME:" + _methodCallDesc.getReceiver());
					_ps.println("RECEIVER_TYPE:" + _methodCallDesc.getReceiverTypeProxy().getTypeName());
				}
				_ps.println("METHOD_END");
			}

			for (ClassMetadata _classMetadata : _classDesc.getClassMetadata()) {
				_ps.println("METADATA:" + _classMetadata.getName());
			}
		}

		_ps.close();

		System.out.println("Done.");
	}
}
