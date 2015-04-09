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
package org.structome.analysis.groovy.graph;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.structome.analysis.core.ClassDescriptor;
import org.structome.analysis.core.MethodCallDescriptor;
import org.structome.analysis.core.MethodDescriptor;
import org.structome.analysis.core.VarDescriptor;
import org.structome.analysis.groovy.GroovyJavaDatabase;
import org.structome.analysis.groovy.GroovyJavaDatabaseVisitor;

public class GrailsClassDependencyGraphBuilder implements GroovyJavaDatabaseVisitor {
	private Set<String> edgeSet = new HashSet<String>();
	private PrintStream graphPrintStream;

	public GrailsClassDependencyGraphBuilder(PrintStream _ps) {
		graphPrintStream = _ps;
	}

	@Override
	public void visitVarDescriptor(VarDescriptor _varDecl, GroovyJavaDatabase _db) {
		String query = ".*\\." + _varDecl.getType();
		final String _source = _varDecl.getParentClass().getSimpleName();

		for (ClassDescriptor _class : _db.find(query)) {
			final String _target = _class.getSimpleName();

			if (!_source.equals(_target)) {
				String _edge = _source + "," + _target;

				if (!edgeSet.contains(_edge)) {
					edgeSet.add(_edge);
					graphPrintStream.println(_edge);
				}
			}
		}

		for (String _generics : _varDecl.getGenerics()) {
			final String _target = _generics;

			if (!_source.equals(_target)) {
				final String _edge = _source + "," + _target;

				if (!edgeSet.contains(_edge)) {
					edgeSet.add(_edge);
					graphPrintStream.println(_edge);
				}
			}
		}

		String _scopeClass = _varDecl.getParentClass().getSimpleName();

		if (_scopeClass.endsWith("Service") || _scopeClass.endsWith("Controller")
				|| _scopeClass.endsWith("Job")) {
			String _member = _varDecl.getName();

			if (_member.endsWith("Service")) {
				String _temp1 = _member.substring(0, 1);
				String _temp2 = _member.substring(1);
				_member = _temp1.toUpperCase() + _temp2;

				query = ".*" + _member;
				for (ClassDescriptor _class : _db.find(query)) {
					final String _target = _class.getSimpleName();
					String _edge = _source + "," + _target;

					edgeSet.add(_edge);
					graphPrintStream.println(_edge);
				}
			}
		}
	}

	@Override
	public void visitMethodVarDescriptor(VarDescriptor _methodVarDesc, GroovyJavaDatabase _db) {
		String query = ".*\\." + _methodVarDesc.getType();

		for (ClassDescriptor _class : _db.find(query)) {
			final String _source = _methodVarDesc.getParentClass().getSimpleName();
			final String _target = _class.getSimpleName();

			if (!_source.equals(_target)) {
				final String _edge = _source + "," + _target;

				if (!edgeSet.contains(_edge)) {
					edgeSet.add(_edge);
					graphPrintStream.println(_edge);
				}
			}
		}
	}

	@Override
	public boolean visitMethodDescriptor(MethodDescriptor _methodDesc, GroovyJavaDatabase _db) {
		return true;
	}

	@Override
	public void visitMethodCallDescriptor(MethodCallDescriptor _methodCallDesc, GroovyJavaDatabase _db) {
		// System.out.println("method call: " + _methodCallDesc);
	}

	@Override
	public boolean visitClassDescriptor(ClassDescriptor _classDesc, GroovyJavaDatabase _db) {
		if (_classDesc.getName().contains("Tests")) {
			return false;
		}

		return true;
	}

	public Collection<String> getEdges() {
		return edgeSet;
	}

	@Override
	public void visitEndOfMethodDescriptor(MethodDescriptor _methodDesc, GroovyJavaDatabase _db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitEndOfClassDescriptor(ClassDescriptor _classDesc, GroovyJavaDatabase _db) {
		// TODO Auto-generated method stub

	}
}
