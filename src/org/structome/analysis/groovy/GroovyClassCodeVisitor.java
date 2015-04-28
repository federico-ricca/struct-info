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
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;
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
	private ClassDescriptor currentClass;
	private MethodDescriptor currentMethod;
	private int blockScope = 0;

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
		return currentClass;
	}

	public MethodDescriptor getCurrentMethod() {
		return currentMethod;
	}

	public StructureDatabase<ClassDescriptor> getDatabase() {
		return database;
	}

	@Override
	public void visitClass(ClassNode _node) {
		ClassDescriptor _classDesc = new ClassDescriptor();
		_classDesc.setName(_node.getName());
		_classDesc.resolve();
		currentClass = _classDesc;
		currentMethod = null;

		super.visitClass(_node);

		try {
			ClassNode _superClassNode = _node.getSuperClass();
			String _parentClassname = _superClassNode.getNameWithoutPackage();

			if (_superClassNode != null) {
				String _referencedType = currentClass.getImport(_parentClassname);

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
			System.out.println("DuplicateObjectException (" + _classDesc.getSimpleName() + ")"
					+ e.getMessage());
		}
	}

	@Override
	public void visitField(FieldNode _node) {
		super.visitField(_node);

		if (currentMethod == null) {
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

			currentClass.addMemberDescriptor(_varDesc);
		}
	}

	@Override
	public void visitVariableExpression(VariableExpression _variableExpression) {
		super.visitVariableExpression(_variableExpression);

		if ((currentMethod != null) && blockScope == 1) {
			String _varName = _variableExpression.getName();

			if (currentMethod.getVarDescriptor(_varName) == null) {
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

					currentMethod.addVarDescriptor(_varDesc);
				}
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

			currentClass.addMethodDescriptor(_methodDesc);

			currentMethod = _methodDesc;
		} else {
			VarDescriptor _fieldDecl = new VarDescriptor();
			_fieldDecl.setName(_node.getName());
			_fieldDecl.setType(_node.getType().getText());
			currentClass.addMemberDescriptor(_fieldDecl);
		}
	}

	@Override
	public void visitMethod(MethodNode node) {
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
		currentClass.addMethodDescriptor(_methodDesc);

		currentMethod = _methodDesc;

		super.visitMethod(node);
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
				_receiverTypeProxy.setTypeName(currentClass.getName());
			} else if (_varName.equals("super")) {
			} else if (_varExp.getAccessedVariable() != null) {
				_receiverTypeProxy.setTypeName(_varExp.getType().getText());
			} else {
				VarDescriptor _declaredVar = currentMethod.getVarDescriptor(_varName);
				if (_declaredVar != null) {
					_receiverTypeProxy.setTypeName(_declaredVar.getType());
				} else {
					VarDescriptor _varDesc = currentClass.getMember(_varName);

					if (_varDesc != null) {
						_receiverTypeProxy.setTypeName(_varDesc.getType());
					} else {
						System.out.println("Could not resolve variable " + _varName);
					}
				}
			}
		} else if (receiverNode instanceof ClassExpression) {
			ClassExpression _classExpr = (ClassExpression) receiverNode;
		} else {
			if (receiverNode instanceof MethodCallExpression) {
				this.resolveMethodCallExpression((MethodCallExpression) receiverNode);
			}
		}

		MethodCallDescriptor _methodCall = new MethodCallDescriptor();

		_methodCall.setReceiver(_varName);
		_methodCall.setReceiverType(_receiverTypeProxy);
		_methodCall.setMethodName(_methodName);

		currentMethod.addMethodCallDescriptor(_methodCall);
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression _methodCallExpression) {
		// currentMethod can be null if the source file
		// is a configuration script: .i.e no class declared inside groovy file
		if (currentMethod != null) {
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

				currentClass.addMetadata(_metadataKey, _classMetaData);
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
			currentClass.addImport(_name, _referencedType);
		}
	}

	public void reset() {
		currentClass = null;
		currentMethod = null;
	}

	@Override
	protected void visitObjectInitializerStatements(ClassNode node) {
		super.visitObjectInitializerStatements(node);
	}

	@Override
	public void visitPackage(PackageNode node) {
		super.visitPackage(node);
	}

	@Override
	protected void visitClassCodeContainer(Statement code) {
		super.visitClassCodeContainer(code);
	}

	@Override
	public void visitDeclarationExpression(DeclarationExpression expression) {
		super.visitDeclarationExpression(expression);
	}

	@Override
	protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
		super.visitConstructorOrMethod(node, isConstructor);
	}

	@Override
	public void visitConstructor(ConstructorNode node) {
		super.visitConstructor(node);
	}

	@Override
	protected void visitStatement(Statement statement) {
		super.visitStatement(statement);
	}

	@Override
	public void visitAssertStatement(AssertStatement statement) {
		super.visitAssertStatement(statement);
	}

	@Override
	public void visitBlockStatement(BlockStatement block) {
		blockScope++;
		super.visitBlockStatement(block);
		blockScope--;
	}

	@Override
	public void visitBreakStatement(BreakStatement statement) {
		super.visitBreakStatement(statement);
	}

	@Override
	public void visitCaseStatement(CaseStatement statement) {
		super.visitCaseStatement(statement);
	}

	@Override
	public void visitCatchStatement(CatchStatement statement) {
		super.visitCatchStatement(statement);
	}

	@Override
	public void visitContinueStatement(ContinueStatement statement) {
		super.visitContinueStatement(statement);
	}

	@Override
	public void visitDoWhileLoop(DoWhileStatement loop) {
		super.visitDoWhileLoop(loop);
	}

	@Override
	public void visitExpressionStatement(ExpressionStatement statement) {
		super.visitExpressionStatement(statement);
	}

	@Override
	public void visitForLoop(ForStatement forLoop) {
		super.visitForLoop(forLoop);
	}

	@Override
	public void visitIfElse(IfStatement ifElse) {
		super.visitIfElse(ifElse);
	}

	@Override
	public void visitReturnStatement(ReturnStatement statement) {
		super.visitReturnStatement(statement);
	}

	@Override
	public void visitSwitch(SwitchStatement statement) {
		super.visitSwitch(statement);
	}

	@Override
	public void visitSynchronizedStatement(SynchronizedStatement statement) {
		super.visitSynchronizedStatement(statement);
	}

	@Override
	public void visitThrowStatement(ThrowStatement statement) {
		super.visitThrowStatement(statement);
	}

	@Override
	public void visitTryCatchFinally(TryCatchStatement statement) {
		super.visitTryCatchFinally(statement);
	}

	@Override
	public void visitWhileLoop(WhileStatement loop) {
		super.visitWhileLoop(loop);
	}

	@Override
	protected void visitEmptyStatement(EmptyStatement statement) {
		super.visitEmptyStatement(statement);
	}

	@Override
	public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
		super.visitStaticMethodCallExpression(call);
	}

	@Override
	public void visitConstructorCallExpression(ConstructorCallExpression call) {
		super.visitConstructorCallExpression(call);
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		super.visitBinaryExpression(expression);
	}

	@Override
	public void visitTernaryExpression(TernaryExpression expression) {
		super.visitTernaryExpression(expression);
	}

	@Override
	public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
		super.visitShortTernaryExpression(expression);
	}

	@Override
	public void visitPostfixExpression(PostfixExpression expression) {
		super.visitPostfixExpression(expression);
	}

	@Override
	public void visitPrefixExpression(PrefixExpression expression) {
		super.visitPrefixExpression(expression);
	}

	@Override
	public void visitBooleanExpression(BooleanExpression expression) {
		super.visitBooleanExpression(expression);
	}

	@Override
	public void visitNotExpression(NotExpression expression) {
		super.visitNotExpression(expression);
	}

	@Override
	public void visitClosureExpression(ClosureExpression expression) {
		super.visitClosureExpression(expression);
	}

	@Override
	public void visitTupleExpression(TupleExpression expression) {
		super.visitTupleExpression(expression);
	}

	@Override
	public void visitListExpression(ListExpression expression) {
		super.visitListExpression(expression);
	}

	@Override
	public void visitArrayExpression(ArrayExpression expression) {
		super.visitArrayExpression(expression);
	}

	@Override
	public void visitMapExpression(MapExpression expression) {
		super.visitMapExpression(expression);
	}

	@Override
	public void visitMapEntryExpression(MapEntryExpression expression) {
		super.visitMapEntryExpression(expression);
	}

	@Override
	public void visitRangeExpression(RangeExpression expression) {
		super.visitRangeExpression(expression);
	}

	@Override
	public void visitSpreadExpression(SpreadExpression expression) {
		super.visitSpreadExpression(expression);
	}

	@Override
	public void visitSpreadMapExpression(SpreadMapExpression expression) {
		super.visitSpreadMapExpression(expression);
	}

	@Override
	public void visitMethodPointerExpression(MethodPointerExpression expression) {
		super.visitMethodPointerExpression(expression);
	}

	@Override
	public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
		super.visitUnaryMinusExpression(expression);
	}

	@Override
	public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
		super.visitUnaryPlusExpression(expression);
	}

	@Override
	public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
		super.visitBitwiseNegationExpression(expression);
	}

	@Override
	public void visitCastExpression(CastExpression expression) {
		super.visitCastExpression(expression);
	}

	@Override
	public void visitConstantExpression(ConstantExpression expression) {
		super.visitConstantExpression(expression);
	}

	@Override
	public void visitClassExpression(ClassExpression expression) {
		super.visitClassExpression(expression);
	}

	@Override
	public void visitPropertyExpression(PropertyExpression expression) {
		super.visitPropertyExpression(expression);
	}

	@Override
	public void visitAttributeExpression(AttributeExpression expression) {
		super.visitAttributeExpression(expression);
	}

	@Override
	public void visitFieldExpression(FieldExpression expression) {
		super.visitFieldExpression(expression);
	}

	@Override
	public void visitGStringExpression(GStringExpression expression) {
		super.visitGStringExpression(expression);
	}

	@Override
	protected void visitListOfExpressions(List<? extends Expression> list) {
		super.visitListOfExpressions(list);
	}

	@Override
	public void visitArgumentlistExpression(ArgumentListExpression ale) {
		super.visitArgumentlistExpression(ale);
	}

	@Override
	public void visitClosureListExpression(ClosureListExpression cle) {
		super.visitClosureListExpression(cle);
	}

	@Override
	public void visitBytecodeExpression(BytecodeExpression cle) {
		super.visitBytecodeExpression(cle);
	}

}
