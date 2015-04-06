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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodDescriptor {
	private ClassDescriptor parentClass;
	private String name;
	private List<MethodCallDescriptor> methodCalls = new ArrayList<MethodCallDescriptor>();
	private Map<String, VarDescriptor> declaredVariables = new HashMap<String, VarDescriptor>();

	public void addVarDescriptor(VarDescriptor _varDesc) {
		_varDesc.setParentMethod(this);
		_varDesc.setParentClass(this.getParentClass());
		declaredVariables.put(_varDesc.getName(), _varDesc);
	}

	public void setParentClass(ClassDescriptor _parentClass) {
		parentClass = _parentClass;
		
		// retrigger setParentClass on local vars (in case of deferred addition of this method to parent class)
		for (VarDescriptor _varDesc : declaredVariables.values()) {
			_varDesc.setParentClass(parentClass);
		}
	}

	public ClassDescriptor getParentClass() {
		return parentClass;
	}

	public void setName(String _name) {
		name = _name;
	}

	public String getName() {
		return name;
	}

	public void addMethodCallDescriptor(MethodCallDescriptor _methodCall) {
		methodCalls.add(_methodCall);
	}

	public VarDescriptor getVarDescriptor(String _varName) {
		return declaredVariables.get(_varName);
	}

	public List<MethodCallDescriptor> getAllMethodCalls() {
		return methodCalls;
	}

	public String toString() {
		return name;
	}

	public Collection<VarDescriptor> getAllVarDescriptors() {
		return declaredVariables.values();
	}
}
