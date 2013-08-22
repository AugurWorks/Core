package com.augurworks.decisiontree.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.augurworks.decisiontree.Row;
import com.augurworks.decisiontree.RowGroup;
import com.augurworks.decisiontree.impl.CopyableDouble;
import com.augurworks.decisiontree.impl.RowGroupImpl;
import com.augurworks.decisiontree.impl.RowImpl;

public class VballTrainingFlowTest {
	private static String FILENAME = "/home/saf/Documents/sample.csv";
	private static RowGroup<WeatherData, CopyableDouble, VBallPlay> rows = parseData(FILENAME); 
	
	@Test
	public void testReadFile() {
		assertEquals(rows.size(), 14);
		Row<WeatherData, CopyableDouble, VBallPlay> row = rows.getRow(0);
		assertEquals(row.get(WeatherData.HUMIDITY), new CopyableDouble(5));
		assertEquals(row.getResult(), VBallPlay.NO);
	}
	
	@Test
	public void testEntropyColumn() {
		// original entropy
		assertTrue(Math.abs(rows.getOriginalEntropy() - 0.940) < 0.001);
		
		System.out.println(rows.getEntropy(WeatherData.HINT, new CopyableDouble(0)));
	}
	
	private static RowGroup<WeatherData, CopyableDouble, VBallPlay> parseData(String filename) {
		File f = new File(filename);
		if (!f.exists()) {
			throw new IllegalArgumentException("File " + filename
					+ " does not exist");
		}
		return readCsv(f);
	}

	private static RowGroup<WeatherData, CopyableDouble, VBallPlay> readCsv(File f) {
		if (!f.exists()) {
			throw new IllegalArgumentException("File " + f + " does not exist");
		}
		if (!f.canRead()) {
			throw new IllegalArgumentException("File " + f + " cannot be read");
		}
		RowGroup<WeatherData, CopyableDouble, VBallPlay> rows = 
				new RowGroupImpl<WeatherData, CopyableDouble, VBallPlay>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));

			// Get the title line
			String line = br.readLine();
			if (line == null) {
				return rows;
			}
			String[] titles = line.split(",");
			List<WeatherData> titleNames = new ArrayList<WeatherData>();
			for (int i = 0; i < titles.length - 1; i++) {
				String title = titles[i].trim();
				WeatherData name = WeatherData.fromString(title);
				if (titleNames.contains(name)) {
					throw new IllegalArgumentException(name
							+ " is duplicated in the input file");
				}
				titleNames.add(name);
			}
			
			// Data rows
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data.length != titleNames.size() + 1) {
					throw new IllegalArgumentException("Line " + line + " does not have the correct number of columns");
				}
				Row<WeatherData, CopyableDouble, VBallPlay> row = 
						new RowImpl<WeatherData, CopyableDouble, VBallPlay>();
				for (int i = 0; i < data.length - 1; i++) {
					WeatherData name = titleNames.get(i);
					row.put(name, CopyableDouble.valueOf(data[i]));
				}
				// result should be last
				row.setResult(VBallPlay.fromString(data[data.length - 1]));
				rows.addRow(row);
			}
			
			return rows;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return rows;
	}
}
