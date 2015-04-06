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


public interface GroovyJavaDatabaseVisitor {

	public boolean visitClassDescriptor(ClassDescriptor _classDesc, GroovyJavaDatabase _db);

	public void visitVarDescriptor(VarDescriptor _varDecl, GroovyJavaDatabase _db);

	public boolean visitMethodDescriptor(MethodDescriptor _methodDesc, GroovyJavaDatabase _db);

	public void visitMethodVarDescriptor(VarDescriptor _methodVarDesc, GroovyJavaDatabase _db);

	public void visitMethodCallDescriptor(MethodCallDescriptor _methodCallDesc, GroovyJavaDatabase _db);

}
