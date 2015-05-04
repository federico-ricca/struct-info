package org.structome.parsing.groovy;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.security.CodeSigner;
import java.security.CodeSource;

import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.structome.parsing.DataProcessor;

public class GroovyProcessor<T> extends DataProcessor<T> {
	private GroovyProcessorCodeVisitor<T> groovyProcessorCodeVisitor;

	@Override
	public T processInput(File _file, T _prototype) throws Exception {
		CompilerConfiguration _configuration = new CompilerConfiguration();
		CodeSigner[] certs = null;
		CodeSource _security = new CodeSource(null, (CodeSigner[]) certs);
		GroovyClassLoader _loader = new GroovyClassLoader();
		GroovyClassLoader _transformLoader = new GroovyClassLoader();

		CompilationUnit _compilationUnit = new CompilationUnit(_configuration, _security, _loader,
				_transformLoader);
		_compilationUnit.addSource(_file);

		groovyProcessorCodeVisitor.setProduct(_prototype);
		
		_compilationUnit.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation() {
			@Override
			public void call(final SourceUnit source, GeneratorContext context, ClassNode classNode)
					throws CompilationFailedException {
				groovyProcessorCodeVisitor.setSourceUnit(source);
				groovyProcessorCodeVisitor.visitClass(classNode);
			}
		}, Phases.CONVERSION);

		_compilationUnit.compile(Phases.CONVERSION);

		return _prototype;
	}

	public void setCodeVisitor(GroovyProcessorCodeVisitor<T> _groovyProcessorCodeVisitor) {
		groovyProcessorCodeVisitor = _groovyProcessorCodeVisitor;
	}
}
