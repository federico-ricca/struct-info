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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GroovyJavaDatabaseReader {

	public GroovyJavaDatabase read(File _file) throws IOException {
		GroovyJavaDatabase db = new GroovyJavaDatabase();

		BufferedReader _reader = new BufferedReader(new FileReader(_file));

		String _line = _reader.readLine();
		int _lineNumber = 0;

		ClassDescriptor _currentClassDesc = null;
		VarDescriptor _currentVarDesc = null;
		MethodDescriptor _currentMethodDesc = null;
		MethodCallDescriptor _currentMethodCallDesc = null;

		while (_line != null) {
			if (_line.startsWith("CLASS:")) {
				_line = _line.substring("CLASS:".length());
				_currentClassDesc = new ClassDescriptor();
				_currentClassDesc.setName(_line);

				try {
					db.put(_currentClassDesc);
				} catch (DuplicateObjectException e) {
					throw new IOException("Duplicated class definition: " + _currentClassDesc + "; line: "
							+ _lineNumber);
				}
			} else if (_line.startsWith("FIELD_NAME:")) {
				_line = _line.substring("FIELD_NAME:".length());
				_currentVarDesc = new VarDescriptor();
				_currentVarDesc.setName(_line);
			} else if (_line.startsWith("FIELD_TYPE:")) {
				_line = _line.substring("FIELD_TYPE:".length());
				_currentVarDesc.setType(_line);
				_currentClassDesc.addMemberDescriptor(_currentVarDesc);
			} else if (_line.startsWith("GENERICS:")) {
				_line = _line.substring("GENERICS:".length());
				_currentVarDesc.addGenerics(_line);
			} else if (_line.startsWith("METHOD:")) {

				_line = _line.substring("METHOD:".length());
				_currentMethodDesc = new MethodDescriptor();
				_currentMethodDesc.setName(_line);
			} else if (_line.startsWith("LOCAL_VAR_NAME")) {
				_line = _line.substring("LOCAL_VAR_NAME:".length());
				_currentVarDesc = new VarDescriptor();
				_currentVarDesc.setName(_line);
			} else if (_line.startsWith("LOCAL_VAR_TYPE:")) {
				_line = _line.substring("LOCAL_VAR_TYPE:".length());
				_currentVarDesc.setType(_line);
				_currentMethodDesc.addVarDescriptor(_currentVarDesc);
			} else if (_line.startsWith("METHOD_CALL:")) {
				_line = _line.substring("METHOD_CALL:".length());
				_currentMethodCallDesc = new MethodCallDescriptor();
				_currentMethodCallDesc.setMethodName(_line);
			} else if (_line.startsWith("RECEIVER_NAME:")) {
				_line = _line.substring("RECEIVER_NAME:".length());
				_currentMethodCallDesc.setReceiver(_line);
			} else if (_line.startsWith("RECEIVER_TYPE:")) {
				_line = _line.substring("RECEIVER_TYPE:".length());
				ReceiverTypeProxy _receiverTypeProxy = new ReceiverTypeProxy();
				_receiverTypeProxy.setTypeName(_line);
				_currentMethodCallDesc.setReceiverType(_receiverTypeProxy);
				_currentMethodDesc.addMethodCallDescriptor(_currentMethodCallDesc);
			} else if (_line.startsWith("METHOD_END")) {
				_currentClassDesc.addMethodDescriptor(_currentMethodDesc);
			} else if (_line.startsWith("METADATA:")) {
				_line = _line.substring("METADATA:".length());
			}

			_line = _reader.readLine();
		}

		_reader.close();

		return db;
	}
}
