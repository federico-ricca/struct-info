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

import java.util.Collection;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.structome.analysis.core.ClassDescriptor;
import org.structome.analysis.core.ClassMetadata;
import org.structome.analysis.core.DuplicateObjectException;
import org.structome.analysis.core.MethodCallDescriptor;
import org.structome.analysis.core.MethodDescriptor;
import org.structome.analysis.core.ReceiverTypeProxy;
import org.structome.analysis.core.StructureDatabase;
import org.structome.analysis.core.VarDescriptor;

public class GroovyClassCodeVisitor extends ClassCodeVisitorSupport {
	private SourceUnit source;
	private StructureDatabase<ClassDescriptor> database;
	private ClassDescriptor lastProcessedClass;
	private MethodDescriptor lastProcessedMethod;

	public void setSourceUnit(SourceUnit _source) {
		source = _source;
	}
	
	public void setDatabase(StructureDatabase<ClassDescriptor> _db) {
		database = _db;
	}
	
	@Override
	protected SourceUnit getSourceUnit() {
		return source;
	}

	public ClassDescriptor getCurrentClass() {
		return lastProcessedClass;
	}
	
	public MethodDescriptor getCurrentMethod() {
		return lastProcessedMethod;
	}
	
	public StructureDatabase<ClassDescriptor> getDatabase() {
		return database;
	}
	
	@Override
	public void visitClass(ClassNode _node) {
		ClassDescriptor _classDesc = new ClassDescriptor();
		_classDesc.setName(_node.getName());
		_classDesc.resolve();
		lastProcessedClass = _classDesc;
		lastProcessedMethod = null;

		super.visitClass(_node);

		try {
			ClassNode _superClassNode = _node.getSuperClass();
			String _parentClassname = _superClassNode.getNameWithoutPackage();

			if (_superClassNode != null) {
				String _referencedType = lastProcessedClass.getImport(_parentClassname);

				if (_referencedType == null) {
					Collection<ClassDescriptor> _references = database.find(".*\\." + _parentClassname);

					String _fullNameReference = null;

					for (ClassDescriptor _reference : _references) {
						if (_reference.getSimpleName().equals(_parentClassname)) {
							_fullNameReference = _reference.getName();
							break;
						}
					}

					if (_fullNameReference != null) {
						_parentClassname = _fullNameReference;
					} else {
						// TODO Should look for static imports.
					}
				} else {
					_parentClassname = _referencedType;
				}
			}

			for (ClassNode _interfaceNode : _node.getInterfaces()) {
				_interfaceNode.getName();
			}

			database.put(_classDesc);

			ClassDescriptor _superClassDesc = database.get(_parentClassname);

			if (_superClassDesc == null) {
				_superClassDesc = new ClassDescriptor();
				_superClassDesc.setName(_parentClassname);
			}

			_classDesc.setSuperClass(_superClassDesc);

		} catch (DuplicateObjectException e) {
			System.out.println("DuplicateObjectException (" + _classDesc.getSimpleName() + ")" + e.getMessage());
		}
	}

	@Override
	public void visitField(FieldNode _node) {
		super.visitField(_node);

		if (lastProcessedMethod == null) {
			// class member declarations
			VarDescriptor _varDesc = new VarDescriptor();
			_varDesc.setName(_node.getName());
			_varDesc.setType(_node.getType().getText());

			GenericsType[] _generics = _node.getType().getGenericsTypes();
			if (_generics != null && _generics.length > 0) {
				for (GenericsType _genericType : _generics) {
					_varDesc.addGenerics(_genericType.getName());
				}
			}

			lastProcessedClass.addMemberDescriptor(_varDesc);
		}
	}

	@Override
	public void visitVariableExpression(VariableExpression _variableExpression) {
		super.visitVariableExpression(_variableExpression);

		if (lastProcessedMethod != null) {
			String _varName = _variableExpression.getName();

			if (!(_varName.equals("super")) || (_varName.equals("this"))) {
				String _type = _variableExpression.getType().getText();
				VarDescriptor _varDesc = new VarDescriptor();
				_varDesc.setName(_varName);
				_varDesc.setType(_type);

				GenericsType[] _generics = _variableExpression.getType().getGenericsTypes();
				if (_generics != null && _generics.length > 0) {
					for (GenericsType _genericType : _generics) {
						_varDesc.addGenerics(_genericType.getName());
					}
				}

				lastProcessedMethod.addVarDescriptor(_varDesc);
			}
		}
	}

	@Override
	public void visitProperty(PropertyNode _node) {
		super.visitProperty(_node);

		if (_node.getField().getInitialExpression() instanceof ClosureExpression) {
			ClosureExpression _closure = (ClosureExpression) _node.getField().getInitialExpression();

			MethodDescriptor _methodDesc = new MethodDescriptor();
			_methodDesc.setName(_node.getName());

			if (_closure.getParameters() != null) {
				for (Parameter _param : _closure.getParameters()) {
					VarDescriptor _varDesc = new VarDescriptor();
					_varDesc.setName(_param.getName());
					_varDesc.setType(_param.getType().getName());
					_methodDesc.addVarDescriptor(_varDesc);
				}
			}

			lastProcessedClass.addMethodDescriptor(_methodDesc);

			lastProcessedMethod = _methodDesc;
		} else {
			VarDescriptor _fieldDecl = new VarDescriptor();
			_fieldDecl.setName(_node.getName());
			_fieldDecl.setType(_node.getType().getText());
			lastProcessedClass.addMemberDescriptor(_fieldDecl);
		}
	}

	@Override
	public void visitMethod(MethodNode node) {
		super.visitMethod(node);

		MethodDescriptor _methodDesc = new MethodDescriptor();
		_methodDesc.setName(node.getName());

		for (Parameter _param : node.getParameters()) {
			VarDescriptor _varDesc = new VarDescriptor();
			_varDesc.setName(_param.getName());
			_varDesc.setType(_param.getType().getName());

			GenericsType[] _generics = _param.getType().getGenericsTypes();
			if (_generics != null && _generics.length > 0) {
				for (GenericsType _genericType : _generics) {
					_varDesc.addGenerics(_genericType.getName());
				}
			}

			_methodDesc.addVarDescriptor(_varDesc);
		}
		lastProcessedClass.addMethodDescriptor(_methodDesc);

		lastProcessedMethod = _methodDesc;
	}

	private void resolveMethodCallExpression(MethodCallExpression _methodCallExpression) {
		ASTNode receiverNode = _methodCallExpression.getReceiver();

		String _varName = "unknown";
		ReceiverTypeProxy _receiverTypeProxy = new ReceiverTypeProxy();
		String _methodName = _methodCallExpression.getMethod().getText();

		if (receiverNode instanceof VariableExpression) {
			VariableExpression _varExp = (VariableExpression) receiverNode;
			_varName = _varExp.getName();

			if (_varName.equals("this")) {
				_receiverTypeProxy.setTypeName(lastProcessedClass.getName());
			} else if (_varName.equals("super")) {
			} else if (_varExp.getAccessedVariable() != null) {
				_receiverTypeProxy.setTypeName(_varExp.getType().getText());
			} else {
				VarDescriptor _declaredVar = lastProcessedMethod.getVarDescriptor(_varName);
				if (_declaredVar != null) {
					_receiverTypeProxy.setTypeName(_declaredVar.getType());
				} else {
					VarDescriptor _varDesc = lastProcessedClass.getMember(_varName);

					if (_varDesc != null) {
						_receiverTypeProxy.setTypeName(_varDesc.getType());
					} else {
						System.out.println("Could not resolve variable " + _varName);
					}
				}
			}
		} else if (receiverNode instanceof ClassExpression) {
			ClassExpression _classExpr = (ClassExpression) receiverNode;

			System.out.println("ClassExpr >> " + _classExpr);
		} else {
			if (receiverNode instanceof MethodCallExpression) {
				this.resolveMethodCallExpression((MethodCallExpression) receiverNode);
			} else {
				System.out.println("unknown >> " + receiverNode);
			}
		}

		MethodCallDescriptor _methodCall = new MethodCallDescriptor();

		_methodCall.setReceiver(_varName);
		_methodCall.setReceiverType(_receiverTypeProxy);
		_methodCall.setMethodName(_methodName);

		lastProcessedMethod.addMethodCallDescriptor(_methodCall);
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression _methodCallExpression) {
		// lastProcessedMethod can be null if the source file
		// is a configuration script: .i.e no class declared inside groovy file
		if (lastProcessedMethod != null) {
			this.resolveMethodCallExpression(_methodCallExpression);
		}
	}

	@Override
	public void visitAnnotations(AnnotatedNode _node) {
		super.visitAnnotations(_node);

		if (_node instanceof ClassNode) {
			for (AnnotationNode _annotationNode : _node.getAnnotations()) {
				ClassMetadata _classMetaData = new ClassMetadata();
				String _metadataKey = _annotationNode.getClassNode().getName();
				_classMetaData.setName(_metadataKey);
				
				for (String _key : _annotationNode.getMembers().keySet()) {
					Expression _expr = _annotationNode.getMember(_key);

					_classMetaData.addValue(_key, _expr.toString());
				}

				lastProcessedClass.addMetadata(_metadataKey, _classMetaData);
			}
		}
	}

	@Override
	public void visitImports(ModuleNode _node) {
		super.visitImports(_node);

		List<ImportNode> _imports = _node.getImports();

		for (ImportNode _import : _imports) {
			String _referencedType = _import.getClassName();
			String _name = _import.getType().getNameWithoutPackage();
			if (_name.equals("*")) {
				_name = _referencedType;
			}
			lastProcessedClass.addImport(_name, _referencedType);
		}
	}

	public void reset() {
		lastProcessedClass = null;
		lastProcessedMethod = null;
	}

}
