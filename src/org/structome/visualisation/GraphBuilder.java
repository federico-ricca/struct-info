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
package org.structome.visualisation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.structome.analysis.groovy.GroovyJavaDatabase;
import org.structome.analysis.groovy.GroovyJavaDatabaseReader;
import org.structome.analysis.groovy.graph.GrailsClassDependencyGraphBuilder;

public class GraphBuilder {
	public static void main(String[] args) throws IOException {
		GroovyJavaDatabase db = new GroovyJavaDatabaseReader().read(new File(args[0]));

		final PrintStream _completePs = createStream("graphRep_complete.csv");

		GrailsClassDependencyGraphBuilder _builder = new GrailsClassDependencyGraphBuilder(_completePs);

		db.traverse(_builder);

		_completePs.close();

		System.out.println("Objects: " + db.getObjects().size());
		System.out.println("Edges: " + _builder.getEdges().size());
		System.out.println("Done.");

	}

	public static PrintStream createStream(String _filename) throws FileNotFoundException {
		return new PrintStream(new FileOutputStream(new File(_filename)));
	}

}
