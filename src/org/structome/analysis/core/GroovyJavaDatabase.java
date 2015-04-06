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


public class GroovyJavaDatabase extends SimpleCollectionDatabase<ClassDescriptor> {

	public void traverse(GroovyJavaDatabaseVisitor _visitor) {
		for (ClassDescriptor _classDesc : this.getObjects()) {
			try {
				if (_visitor.visitClassDescriptor(_classDesc, this)) {
					for (VarDescriptor _varDecl : _classDesc.getAllMembers()) {
						try {
							_visitor.visitVarDescriptor(_varDecl, this);
						} catch (Exception _e) {
							System.out.println("WARNING: could not process node " + _classDesc + " on "
									+ _classDesc + "; " + _e.getMessage());
						}
					}

					for (MethodDescriptor _methodDesc : _classDesc.getAllMethods()) {
						try {
							if (_visitor.visitMethodDescriptor(_methodDesc, this)) {
								for (VarDescriptor _methodVarDesc : _methodDesc.getAllVarDescriptors()) {
									try {
										_visitor.visitMethodVarDescriptor(_methodVarDesc, this);
									} catch (Exception _e) {
										System.out.println("WARNING: could not process node "
												+ _methodVarDesc + " on " + _methodDesc + " (class="
												+ _classDesc + "); " + _e.getMessage());
									}

								}

								for (MethodCallDescriptor _methodCallDesc : _methodDesc.getAllMethodCalls()) {
									try {
										_visitor.visitMethodCallDescriptor(_methodCallDesc, this);
									} catch (Exception _e) {
										System.out.println("WARNING: could not process node "
												+ _methodCallDesc + " on " + _methodDesc + " (class="
												+ _classDesc + "); " + _e.getMessage());
									}
								}
							}
						} catch (Exception _e) {
							System.out.println("WARNING: could not process node " + _methodDesc + " on "
									+ _classDesc + "; " + _e.getMessage());
						}

					}
				}
			} catch (Exception _e) {
				System.out.println("WARNING: could not process node " + _classDesc + "; " + _e.getMessage());
			}
		}
	}

}
