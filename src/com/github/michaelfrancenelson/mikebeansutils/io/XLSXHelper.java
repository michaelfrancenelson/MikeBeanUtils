package com.github.michaelfrancenelson.mikebeansutils.io;

//import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

//import com.github.kunalk16.excel.factory.WorkBookFactory;
//import com.github.kunalk16.excel.model.user.Cell;
//import com.github.kunalk16.excel.model.user.Row;
//import com.github.kunalk16.excel.model.user.Sheet;
//import com.github.kunalk16.excel.model.user.WorkBook;

//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.DataFormatter;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** https://stackoverflow.com/questions/52261887/light-weight-xlsx-reader-in-java
 *
 */
public class XLSXHelper {
	
	public static List<List<String>> _readXLSX(String filename)
	{
		List<List<String>> result = new ArrayList<>();

//		FileInputStream file = null;
//		WorkBook workBook = WorkBookFactory.create(filename);
//		Sheet sheet = workBook.getSheet(0);
//		
//		
//		Collection<Row> rows = sheet.getRows();
//		Collection<Cell> cells = null;
//		
//		int i = 0;
//		for (Row r:rows)
//		{
//			cells = r.getCells();
//			result.add(new ArrayList<String>());
//			for (Cell c:cells)
//			{
//				result.get(i).add(c.getValue());
//			}
//			i++;
//		}
		
//		Workbook workbook = null;
//		try {
//			file = new FileInputStream(new File(filename));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		try {
//			workbook = new XSSFWorkbook(file);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		
//		DataFormatter dFormatter = new DataFormatter();
//		int i = 0;
//		for (Row row : sheet) 
//		{
//			result.add(new ArrayList<String>());
//			for (Cell cell : row) {
//				
//				result.get(i).add(dFormatter.formatCellValue(cell));
//		    }
//		    i++;
//		}
		return result;
	}
}
