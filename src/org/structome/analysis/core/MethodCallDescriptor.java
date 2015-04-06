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

public class MethodCallDescriptor {
	private String receiver;
	private ReceiverTypeProxy receiverTypeProxy;
	private String methodName;

	public String toString() {
		return receiver + "::" + receiverTypeProxy + "." + methodName;
	}

	public void setReceiver(String _receiver) {
		receiver = _receiver;
	}

	public void setReceiverType(ReceiverTypeProxy _receiverTypeProxy) {
		receiverTypeProxy = _receiverTypeProxy;
	}

	public void setMethodName(String _methodName) {
		methodName = _methodName;
	}

	public String getReceiver() {
		return receiver;
	}

	public ReceiverTypeProxy getReceiverTypeProxy() {
		return receiverTypeProxy;
	}

	public String getMethodName() {
		return methodName;
	}
}
