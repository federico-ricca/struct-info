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

import java.util.HashMap;
import java.util.Map;

public class ClassMetadata {
    private String metadataName;
    private Map<String, String> metadataValues = new HashMap<String, String>();

    public String getName() {
        return metadataName;
    }

    public void setName(String _name) {
        metadataName = _name;
    }

    public void addValue(String _key, String _value) {
        metadataValues.put(_key, _value);
    }

    public String getValue(String _key) {
        return metadataValues.get(_key);
    }
}
