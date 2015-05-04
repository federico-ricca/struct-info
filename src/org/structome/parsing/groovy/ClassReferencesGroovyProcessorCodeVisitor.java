package org.structome.parsing.groovy;

import java.util.Set;

import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;

public class ClassReferencesGroovyProcessorCodeVisitor extends GroovyProcessorCodeVisitor<Set<String>> {

	@Override
	public void visitClass(ClassNode node) {
		this.getProduct().add(node.getNameWithoutPackage());

		if (node.getGenericsTypes() != null) {
			for (GenericsType _generics : node.getGenericsTypes()) {
				this.getProduct().add(_generics.getType().getNameWithoutPackage());
			}
		}

		ClassNode _superType = node.getSuperClass();

		this.getProduct().add(_superType.getNameWithoutPackage());

		if (_superType.getGenericsTypes() != null) {
			for (GenericsType _generics : _superType.getGenericsTypes()) {
				this.getProduct().add(_generics.getType().getNameWithoutPackage());
			}
		}

		super.visitClass(node);
	}

	@Override
	public void visitImports(ModuleNode node) {
		for (ImportNode importNode : node.getImports()) {
			this.getProduct().add(importNode.getType().getNameWithoutPackage());
		}
		super.visitImports(node);
	}

	@Override
	public void visitAnnotations(AnnotatedNode node) {
		for (AnnotationNode annotationNode : node.getAnnotations()) {
			this.getProduct().add(annotationNode.getText());
		}
		super.visitAnnotations(node);
	}

	@Override
	public void visitDeclarationExpression(DeclarationExpression expression) {
		ClassNode _type = expression.getType();

		this.getProduct().add(_type.getNameWithoutPackage());

		if (_type.getGenericsTypes() != null) {
			for (GenericsType _generics : _type.getGenericsTypes()) {
				this.getProduct().add(_generics.getType().getNameWithoutPackage());
			}
		}

		super.visitDeclarationExpression(expression);
	}

	@Override
	public void visitConstructor(ConstructorNode node) {
		// TODO Auto-generated method stub
		super.visitConstructor(node);
	}

	@Override
	public void visitMethod(MethodNode node) {
		for (Parameter _parameter : node.getParameters()) {
			ClassNode _type = _parameter.getType();

			this.getProduct().add(_type.getNameWithoutPackage());

			if (_type.getGenericsTypes() != null) {
				for (GenericsType _generics : _type.getGenericsTypes()) {
					this.getProduct().add(_generics.getType().getNameWithoutPackage());
				}
			}
		}

		ClassNode _type = node.getReturnType();
		this.getProduct().add(_type.getNameWithoutPackage());

		if (_type.getGenericsTypes() != null) {
			for (GenericsType _generics : _type.getGenericsTypes()) {
				this.getProduct().add(_generics.getType().getNameWithoutPackage());
			}
		}

		super.visitMethod(node);
	}

	@Override
	public void visitField(FieldNode node) {
		ClassNode _type = node.getType();
		this.getProduct().add(_type.getNameWithoutPackage());

		if (_type.getGenericsTypes() != null) {
			for (GenericsType _generics : _type.getGenericsTypes()) {
				this.getProduct().add(_generics.getType().getNameWithoutPackage());
			}
		}

		super.visitField(node);
	}

	@Override
	public void visitProperty(PropertyNode node) {
		// TODO Auto-generated method stub
		super.visitProperty(node);
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		if (call.getObjectExpression() instanceof VariableExpression) {
			// receiver is a variable / class
			VariableExpression _varExp = (VariableExpression) call.getObjectExpression();
			
			if (_varExp.getAccessedVariable() == null) {
				// no variable as receiver => static call
				this.getProduct().add(_varExp.getText());
			}
		}

		super.visitMethodCallExpression(call);
	}

	@Override
	public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
		super.visitStaticMethodCallExpression(call);
	}

	@Override
	public void visitCastExpression(CastExpression expression) {
		ClassNode _type = expression.getType();
		this.getProduct().add(_type.getNameWithoutPackage());

		if (_type.getGenericsTypes() != null) {
			for (GenericsType _generics : _type.getGenericsTypes()) {
				this.getProduct().add(_generics.getType().getNameWithoutPackage());
			}
		}

		super.visitCastExpression(expression);
	}

	@Override
	public void visitAttributeExpression(AttributeExpression expression) {
		// TODO Auto-generated method stub
		super.visitAttributeExpression(expression);
	}

	@Override
	public void visitFieldExpression(FieldExpression expression) {
		super.visitFieldExpression(expression);
	}

	@Override
	public void visitArgumentlistExpression(ArgumentListExpression ale) {
		super.visitArgumentlistExpression(ale);
	}

	@Override
	public void visitVariableExpression(VariableExpression expression) {
		ClassNode _type = expression.getType();
		this.getProduct().add(_type.getNameWithoutPackage());

		if (_type.getGenericsTypes() != null) {
			for (GenericsType _generics : _type.getGenericsTypes()) {
				this.getProduct().add(_generics.getType().getNameWithoutPackage());
			}
		}

		super.visitVariableExpression(expression);
	}

	@Override
	public void visitClassExpression(ClassExpression expression) {
		this.getProduct().add(expression.getType().getNameWithoutPackage());
		super.visitClassExpression(expression);
	}

}
