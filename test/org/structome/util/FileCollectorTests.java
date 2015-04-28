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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileCollectorTests {
	public static final int MAX_FILES_JAVA = 10;
	public static final int MAX_FILES_GROOVY = 10;
	public static final int MAX_FILES_SCALA = 10;
	
	@ClassRule
	public static TemporaryFolder folder = new TemporaryFolder();
	
	@BeforeClass
	public static void setUp() throws IOException {
		String _preffix="SampleSource-";
		String _postfix=".java";
		
		for (int index=0; index < MAX_FILES_JAVA; index++) {
			folder.newFile(_preffix+index+_postfix);
		}
		
		folder.newFolder("SUBFOLDER-A");
		folder.newFolder("SUBFOLDER-B");
		folder.newFolder("SUBFOLDER-B/SUBSUB-B");
		
		_postfix=".groovy";
		for (int index=0; index < MAX_FILES_GROOVY; index++) {
			folder.newFile("SUBFOLDER-A/"+_preffix+index+_postfix);
		}
		
		_postfix=".groovy";
		for (int index=0; index < MAX_FILES_GROOVY; index++) {
			folder.newFile("SUBFOLDER-B/SUBSUB-B/"+_preffix+index+_postfix);
		}
		
		_postfix=".scala";
		for (int index=0; index < MAX_FILES_SCALA; index++) {
			folder.newFile("SUBFOLDER-B/"+_preffix+index+_postfix);
		}
	}
	
	@Test
	public void testCollectWithoutFilters() {
		FileCollector _fileCollector = new FileCollector(folder.getRoot());
		_fileCollector.excludeFolders(".*/SUBSUB-B");

		assertTrue(_fileCollector.collect().size() == MAX_FILES_JAVA+MAX_FILES_GROOVY+MAX_FILES_SCALA);
	}

	@Test
	public void testCollectTwiceWithoutFilters() {
		FileCollector _fileCollector = new FileCollector(folder.getRoot());
		_fileCollector.excludeFolders(".*/SUBSUB-B");

		assertTrue(_fileCollector.collect().size() == MAX_FILES_JAVA+MAX_FILES_GROOVY+MAX_FILES_SCALA);
	}
	
	@Test 
	public void testCollectionWithExcludeFolderFilters() {
		FileCollector _fileCollector = new FileCollector(folder.getRoot());

		_fileCollector.excludeFolders("(SUBFOLDER-A|.*/SUBSUB-B)");
		
		assertTrue(_fileCollector.collect().size() == MAX_FILES_JAVA+MAX_FILES_GROOVY);		
	}

	@Test 
	public void testCollectionWithRegex() {
		FileCollector _fileCollector = new FileCollector(folder.getRoot());

		_fileCollector.resetExcludeFolders();
		_fileCollector.excludeFolders(".*/SUBSUB-B");

		assertTrue(_fileCollector.collect(".*\\.(java|groovy)").size() == MAX_FILES_JAVA+MAX_FILES_GROOVY);		
	}
}
