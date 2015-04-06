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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleCollectionDatabase<T extends Indexable> implements StructureDatabase<T> {
	private Map<String, T> data = new HashMap<String, T>();
	
	@Override
	public Collection<T> getObjects() {
		return data.values();
	}

	@Override
	public void put(T _object) throws DuplicateObjectException {
		if (data.containsKey(_object.getId())) {
			throw new DuplicateObjectException(_object.getId());
		}
		data.put(_object.getId(), _object);
	}

	@Override
	public T get(String _id) {
		return data.get(_id);
	}

	@Override
	public Collection<T> find(String _query) {
		_query = _query.replace(";","\\;");
		_query = _query.replace("[","\\[");
		_query = _query.replace("]","\\]");
		
		ArrayList<T> _result = new ArrayList<T>();
		
		for (String _key : data.keySet()) {
			if (_key.matches(_query)) {
				_result.add(data.get(_key));
			}
		}
		
		return _result;
	}
}
