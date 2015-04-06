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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassDescriptor implements Indexable {
	private String name;
	private Map<String, VarDescriptor> members = new HashMap<String, VarDescriptor>();
	private Map<String, MethodDescriptor> methods = new HashMap<String, MethodDescriptor>();
	private Map<String, ClassMetadata> metadataRegistry = new HashMap<String, ClassMetadata>();
	private ClassDescriptor superClass;
	private boolean resolved;

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return name;
	}

	public void setName(String _name) {
		name = _name;
	}

	public void addMemberDescriptor(VarDescriptor _varDesc) {
		_varDesc.setParentClass(this);
		members.put(_varDesc.getName(), _varDesc);
	}

	public void addMethodDescriptor(MethodDescriptor _methodDesc) {
		_methodDesc.setParentClass(this);
		methods.put(_methodDesc.getName(), _methodDesc);
	}

	public void addMetadata(ClassMetadata _classMetadata) {
		metadataRegistry.put(_classMetadata.getName(), _classMetadata);
	}

	public ClassMetadata getMetadata(String _name) {
		return metadataRegistry.get(_name);
	}

	public VarDescriptor getMember(String _varName) {
		return members.get(_varName);
	}

	public Collection<MethodDescriptor> getAllMethods() {
		return methods.values();
	}

	public Collection<VarDescriptor> getAllMembers() {
		return members.values();
	}

	public String getSimpleName() {
		int index = name.lastIndexOf(".");

		return name.substring(index + 1, name.length());
	}

	public Collection<ClassMetadata> getClassMetadata() {
		return metadataRegistry.values();
	}

	public void setSuperClass(ClassDescriptor _superClass) {
		superClass = _superClass;
	}

	public ClassDescriptor getSuperClass() {
		return superClass;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void resolve() {
		resolved = true;
	}
}
