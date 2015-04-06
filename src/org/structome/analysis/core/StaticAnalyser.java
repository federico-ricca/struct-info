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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StaticAnalyser {
	private Map<String, Processor<?>> processors = new HashMap<String, Processor<?>>();

	public void performAnalysisOn(File _f) throws Exception {
		for (String _s : processors.keySet()) {
			if (_f.getName().matches(_s)) {
				processors.get(_s).visit(_f);
			}
		}
	}

	public void addProcessor(String _regex, Processor<?> _processor) {
		processors.put(_regex, _processor);
	}
}
