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
import java.io.FileFilter;
import java.util.Collection;
import java.util.Vector;

public class FileCollector {
	private File root;
	private String folderExclusionFilter;
	
	public FileCollector(File _root) {
		root = _root;
	}

	public void beginCollect(Vector<File> _collected, File _rootFolder, final String _regex) {
		File[] files = _rootFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (file.isDirectory() || file.getName().matches(_regex));
			}
		});

		for (File f : files) {
			if (!f.isDirectory()) {
				_collected.add(f);
			} else {
				String _folder = f.getAbsolutePath().substring(root.getAbsolutePath().length()+1);
                if (folderExclusionFilter == null || !_folder.matches(folderExclusionFilter)) {
					this.beginCollect(_collected, f, _regex);
				}
			}
		}
	}

	public Collection<File> collect() {
		Vector<File> collected = new Vector<File>();
		
		this.beginCollect(collected, root, ".*");
		
		return collected;
	}

	public Collection<File> collect(String _regex) {
		Vector<File> collected = new Vector<File>();
		
		this.beginCollect(collected, root, _regex);
		
		return collected;
	}

	public void excludeFolders(String _folderRegex) {
		folderExclusionFilter = _folderRegex;
	}

	public void resetExcludeFolders() {
		folderExclusionFilter = null;
	}
}
