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

import java.io.OutputStream;
import java.io.PrintStream;

import org.structome.analysis.core.ClassDescriptor;
import org.structome.analysis.core.ClassMetadata;
import org.structome.analysis.core.MethodCallDescriptor;
import org.structome.analysis.core.MethodDescriptor;
import org.structome.analysis.core.VarDescriptor;

public class GroovyJavaDatabaseWriter implements GroovyJavaDatabaseVisitor {
	private PrintStream printStream;

	public GroovyJavaDatabaseWriter(OutputStream _outputStream) {
		printStream = new PrintStream(_outputStream);
	}

	@Override
	public boolean visitClassDescriptor(ClassDescriptor _classDesc, GroovyJavaDatabase _db) {
		String _name = _classDesc.getName();
		printStream.println("CLASS:" + _name);

		if (_classDesc.getSuperClass() != null) {
			ClassDescriptor _superClassDesc = _classDesc.getSuperClass();
			printStream.println("SUPER:" + _superClassDesc.getName());
		}

		if (_classDesc.getImports() != null) {
			for (String _import : _classDesc.getImports()) {
				printStream.println("IMPORT:" + _import);
			}
		}

		for (ClassMetadata _classMetadata : _classDesc.getClassMetadata()) {
			printStream.println("METADATA:" + _classMetadata.getName());
		}
		
		return true;
	}

	@Override
	public void visitVarDescriptor(VarDescriptor _varDesc, GroovyJavaDatabase _db) {
		printStream.println("FIELD_NAME:" + _varDesc.getName());
		printStream.println("FIELD_TYPE:" + _varDesc.getType());

		for (String _generics : _varDesc.getGenerics()) {
			printStream.println("GENERICS:" + _generics);
		}
	}

	@Override
	public boolean visitMethodDescriptor(MethodDescriptor _methodDesc, GroovyJavaDatabase _db) {
		printStream.println("METHOD:" + _methodDesc.getName());

		return true;
	}

	@Override
	public void visitMethodVarDescriptor(VarDescriptor _methodVarDesc, GroovyJavaDatabase _db) {
		printStream.println("LOCAL_VAR_NAME:" + _methodVarDesc.getName());
		printStream.println("LOCAL_VAR_TYPE:" + _methodVarDesc.getType());

		for (String _generics : _methodVarDesc.getGenerics()) {
			printStream.println("GENERICS:" + _generics);
		}
	}

	@Override
	public void visitMethodCallDescriptor(MethodCallDescriptor _methodCallDesc, GroovyJavaDatabase _db) {
		printStream.println("METHOD_CALL:" + _methodCallDesc.getMethodName());
		printStream.println("RECEIVER_NAME:" + _methodCallDesc.getReceiver());
		printStream.println("RECEIVER_TYPE:" + _methodCallDesc.getReceiverTypeProxy().getTypeName());
	}

	@Override
	public void visitEndOfMethodDescriptor(MethodDescriptor _methodDesc, GroovyJavaDatabase _db) {
		printStream.println("END_METHOD");
	}

	@Override
	public void visitEndOfClassDescriptor(ClassDescriptor _classDesc, GroovyJavaDatabase _db) {
		// TODO Auto-generated method stub
		
	}
}
