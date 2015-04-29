package org.structome.parsing;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DataProcessorTests {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testGenericProcessor() throws IOException {
		final String TEST_DATA = "this is a text";
		final String EXPECTED_DATA = "THIS IS A TEXT";

		folder.create();
		File _f = folder.newFile();

		PrintStream _ps = new PrintStream(_f);
		_ps.println(TEST_DATA);
		_ps.close();

		DataProcessor<String> _processor = new DataProcessor<String>() {

			@Override
			public String processInput(File _file, String _prototype) throws IOException {
				BufferedReader _fr = new BufferedReader(new FileReader(_file));

				return _fr.readLine().toUpperCase();
			}

		};

		_processor.setProductFactory(new ProductFactory<String>() {
			public String newProduct() {
				return "";
			}
		});

		String _product;
		try {
			_product = _processor.process(_f);

			assertEquals(EXPECTED_DATA, _product);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
