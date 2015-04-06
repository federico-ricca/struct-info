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

import java.util.Vector;

public class VarDescriptor {
	private ClassDescriptor parentClass;
	private MethodDescriptor parentMethod;
	private String name;
	private String type;
	private Vector<String> generics = new Vector<String>();

	public void setName(String _name) {
		name = _name;
	}

	public void setType(String _type) {
		type = _type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return name + ":" + type;
	}

	public void setParentClass(ClassDescriptor _parent) {
		parentClass = _parent;
	}

	public ClassDescriptor getParentClass() {
		return parentClass;
	}

	public void setParentMethod(MethodDescriptor _parent) {
		parentMethod = _parent;
	}

	public MethodDescriptor getParentMethod() {
		return parentMethod;
	}

	public void addGenerics(String _genericType) {
		generics.add(_genericType);
	}

	public Vector<String> getGenerics() {
		return generics;
	}
}
