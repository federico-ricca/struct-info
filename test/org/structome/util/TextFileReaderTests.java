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
package org.structome.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.structome.util.TextFileReader.LineVisitor;

public class TextFileReaderTests {
	public static final String[] testLines = { "line 1", "line 2", "line 3" };

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Test
	public void testVisitor() throws IOException {
		File tempFile = testFolder.newFile("file.txt");

		PrintStream _ps = new PrintStream(tempFile);

		for (String _line : testLines) {
			_ps.println(_line);
		}

		_ps.close();

		TextFileReader _reader = new TextFileReader(tempFile.getAbsolutePath());

		_reader.readWith(new LineVisitor() {

			@Override
			public void visit(String _line, int _lineNumber) {
				Assert.assertEquals(testLines[_lineNumber], _line);
			}
		});
	}
}
