package com.github.michaelfrancenelson.mikebeansutils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** https://stackoverflow.com/questions/52261887/light-weight-xlsx-reader-in-java
 *
 */
public class XLSXHelper {
	
	public static List<List<String>> readXLSX(String filename)
	{
		List<List<String>> result = new ArrayList<>();

		FileInputStream file = null;
		Workbook workbook = null;
		try {
			file = new FileInputStream(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Sheet sheet = workbook.getSheetAt(0);
		
		DataFormatter dFormatter = new DataFormatter();
		int i = 0;
		for (Row row : sheet) 
		{
			result.add(new ArrayList<String>());
			for (Cell cell : row) {
				
				result.get(i).add(dFormatter.formatCellValue(cell));
		    }
		    i++;
		}
		return result;
	}
}
