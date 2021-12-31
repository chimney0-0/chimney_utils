package idv.common.poi;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelAnalysisUtil {
	
	
	
	
	
	public static List<List<List<String>>> getTableForExcel(String path){
		File file = new File(path);
		String fileName=file.getName();    
		String fileTyle=fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()); 
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
		List<List<List<String>>> result=new ArrayList<>();
		if("xlsx".equals(fileTyle)) {
			try {
				XSSFWorkbook xssfWorkbook = new XSSFWorkbook(bis);
				int sheetNum = xssfWorkbook.getNumberOfSheets();
				
				for(int i = 0; i < sheetNum; i++) {
					 XSSFSheet hssfSheet = xssfWorkbook.getSheetAt(i);
					 List<List<String>> sheet=new ArrayList<>();
					 for (int rowNum =0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						 XSSFRow hssfRow = hssfSheet.getRow(rowNum);
						 if(hssfRow != null) {
							 List<String> row = new ArrayList<>();
							 for(int cellNum = 0;cellNum < hssfRow.getLastCellNum();cellNum++) {
								 XSSFCell cell = hssfRow.getCell(cellNum);
								 if(cell == null) {
									 continue;
								 }
								 String cell_str = getValueXlsx(cell);
								 if(cell_str !=null && !cell_str.equals("")) {
									 row.add(cell_str);
								 }
							 }
							 sheet.add(row);
						 }
					 }
					 result.add(sheet);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}else if("xls".equals(fileTyle)) {
			try {
				HSSFWorkbook xssfWorkbook = new HSSFWorkbook(bis);
				int sheetNum = xssfWorkbook.getNumberOfSheets();
				for(int i = 0; i < sheetNum; i++) {
					HSSFSheet hssfSheet = xssfWorkbook.getSheetAt(i);
					List<List<String>> sheet=new ArrayList<>();
					for (int rowNum =0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
						HSSFRow hssfRow = hssfSheet.getRow(rowNum);
						if(hssfRow != null) {
							 List<String> row = new ArrayList<>();
							 for(int cellNum = 0;cellNum < hssfRow.getLastCellNum();cellNum++) {
								 HSSFCell cell = hssfRow.getCell(cellNum);
								 if(cell == null) {
									 continue;
								 }
								 String cell_str = getValueXls(cell);
								 if(cell_str !=null && !cell_str.equals("")) {
									 row.add(cell_str);
								 }
							 }
							 sheet.add(row);
						 }
					}
					result.add(sheet);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return result;
	}

	/**
	 * 读取xlsx(2007版以后)
	 * @param path 文件绝对路径
	 * @return
	 */
	public static List<Map<String, Object>> readXlsx(String path) {
		try {
			File file = new File(path);
			BufferedInputStream  bis = new BufferedInputStream(new FileInputStream(file));
			//HSSFWorkbook hssfWorkbook = new HSSFWorkbook(bis);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(bis);
			// Read the Sheet
			int sheetNum = xssfWorkbook.getNumberOfSheets();
			int chufaSheet = 0;
			for(int i = 0; i < sheetNum; i++) {
				String sheetName = xssfWorkbook.getSheetAt(i).getSheetName();
				if(sheetName.contains("处罚")) {
					chufaSheet = i;
				}
			}
		    XSSFSheet hssfSheet = xssfWorkbook.getSheetAt(chufaSheet);
		    List<Map<String, Object>> list = new ArrayList<>();
		    int tilteNum = 0;
		    for (int rowNum = 0; rowNum <= 6; rowNum++) {
		    	XSSFRow hssfRow = hssfSheet.getRow(rowNum);
		        if(hssfRow != null) {
//		        	System.out.println(hssfRow.getLastCellNum());
		        	List<String> titleList = new ArrayList<>();
		        	
		        	for(int cellNum = 0;cellNum < hssfRow.getLastCellNum();cellNum++) {
		        		XSSFCell cell = hssfRow.getCell(cellNum);
		        		if(cell == null) {
		        			continue;
		        		}
		        		String cell_str = getValueXlsx(cell);
		        		if(cell_str !=null && !cell_str.equals("")) {
		        			titleList.add(cell_str);
		        		}
		        	}
		        	if(titleList.size() > 4) {
		        		tilteNum = rowNum;
		        		break;
		        	}
		        }
		    }
//		    System.out.println(tilteNum);
		    List<Map<String,Object>> listAll = new ArrayList<>();
		    XSSFRow hssfTitle = hssfSheet.getRow(tilteNum);
		    for (int rowNum = tilteNum + 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
		    	XSSFRow hssfRow = hssfSheet.getRow(rowNum);
		    	if(hssfRow != null) {
		    		Map<String,Object> map = new HashMap<>();
		    		int p = 0;
		    		XSSFCell cell0 = hssfRow.getCell(0);
		    		if(cell0 == null) {
		    			p = 1;
		    		}
		    		for(int cellNum = p;cellNum < hssfRow.getLastCellNum();cellNum++) {
		    			XSSFCell cell = hssfRow.getCell(cellNum);
		        		XSSFCell title = hssfTitle.getCell(cellNum);
		        		if(title == null||cell==null) {
		        			continue;
		        		}
		        		String title_str = getValueXlsx(title);
		        		title_str = title_str.replaceAll("\r", "");
		        		title_str = title_str.replaceAll("\n", "");
		        		title_str = title_str.replaceAll("\\s", "");
		        		title_str = title_str.replaceAll(" ", "");
		        		String cell_str = null;
		        		if(cell != null) {
		        			cell_str = getValueXlsx(cell);
		        		}
		        		if(!title_str.equals("")) {
		        			map.put(title_str, cell_str);
		        		}
		        		map.put("save_path", path);
		    		}
		    		List<String> valueList = new ArrayList<>();
		    		int mapSize = map.size();
		    		int w = 0;
		    		for(Map.Entry<String,Object> entry : map.entrySet()) {
		    			String value = null;
		    			if(entry.getValue() != null) {
		    				value = entry.getValue().toString();
		    			}
		    			if(value == null || value.equals("")) {
		    				w++;
		    			}
		    		}
		    		if(mapSize > 5) {
		    			//防止插入空数据
		    			if(w < mapSize-3) {
		    				listAll.add(map);
		    			}
		    		}
		    	}
		    }
		    return listAll;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 读取xls(2007版以前)
	 * @param path 文件绝对路径
	 * @return
	 */
	public static List<Map<String, Object>> readXls(String path) {
		try {
			File file = new File(path);
			BufferedInputStream  bis = new BufferedInputStream(new FileInputStream(file));
			//HSSFWorkbook hssfWorkbook = new HSSFWorkbook(bis);
			HSSFWorkbook xssfWorkbook = new HSSFWorkbook(bis);
			// Read the Sheet
			int sheetNum = xssfWorkbook.getNumberOfSheets();
			int chufaSheet = 0;
			for(int i = 0; i < sheetNum; i++) {
				String sheetName = xssfWorkbook.getSheetAt(i).getSheetName();
				if(sheetName.contains("处罚")|| sheetName.contains("附2") || sheetName.equals("body")) {
					chufaSheet = i;
				}
			}
			HSSFSheet hssfSheet = xssfWorkbook.getSheetAt(chufaSheet);
		    List<Map<String, Object>> listAll = new ArrayList<>();
		    int tilteNum = 0;
		    for (int rowNum = 0; rowNum <= 6; rowNum++) {
		    	HSSFRow hssfRow = hssfSheet.getRow(rowNum);
		        if(hssfRow != null) {
		        	List<String> titleList = new ArrayList<>();
		        	for(int cellNum = 0;cellNum < hssfRow.getLastCellNum();cellNum++) {
		        		HSSFCell cell = hssfRow.getCell(cellNum);
		        		if(cell == null) {
		        			continue;
		        		}
		        		String cell_str = getValueXls(cell);
		        		if(cell_str !=null && !cell_str.equals("")) {
		        			titleList.add(cell_str);
		        		}
		        	}
		        	if(titleList.size() > 4) {
		        		tilteNum = rowNum;
		        		break;
		        	}
		        }
		    }
//		    System.out.println(tilteNum);
		    HSSFRow hssfTitle = hssfSheet.getRow(tilteNum);
		    for (int rowNum = tilteNum + 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
		    	HSSFRow hssfRow = hssfSheet.getRow(rowNum);
		    	if(hssfRow != null) {
		    		Map<String,Object> map = new HashMap<>();
		    		int p = 0;
		    		HSSFCell cell0 = hssfRow.getCell(0);
		    		if(cell0 == null) {
		    			p = 1;
		    		}
		    		for(int cellNum = p;cellNum < hssfRow.getLastCellNum();cellNum++) {
		    			HSSFCell cell = hssfRow.getCell(cellNum);
		        		HSSFCell title = hssfTitle.getCell(cellNum);
		        		if(title == null||cell==null) {
		        			continue;
		        		}
		        		String title_str = getValueXls(title);
		        		title_str = title_str.replaceAll("\r", "");
		        		title_str = title_str.replaceAll("\n", "");
		        		title_str = title_str.replaceAll("\\s", "");
		        		title_str = title_str.replaceAll(" ", "");
		        		String cell_str = null;
		        		if(cell != null) {
		        			cell_str = getValueXls(cell);
		        		}

					    if (title_str == null || cell_str == null || cell_str.equals(title_str)) {
						    continue;
					    }

		        		if(!title_str.equals("")) {
		        			map.put(title_str, cell_str);
		        		}
//		        		map.put("save_path", path);
		    		}
		    		List<String> valueList = new ArrayList<>();
		    		int mapSize = map.size();
		    		int w = 0;
		    		for(Map.Entry<String,Object> entry : map.entrySet()) {
		    			String value = null;
		    			if(entry.getValue() != null) {
		    				value = entry.getValue().toString();
		    			}
		    			if(value == null || value.equals("")) {
		    				w++;
		    			}
		    		}
		    		if(mapSize > 5) {
		    			//防止插入空数据
		    			if(w < mapSize-3) {
		    				
		    				listAll.add(map);
		    			}
		    		}
		    	}
		    	
		    }
		    return listAll;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
			System.err.println(path);
		}
		return null;
	}
	
	/**
	 * 获取xlsx文件单元格里的值(2007版以后)
	 * @param xssfCell
	 * @return
	 */
	@SuppressWarnings("static-access")
    private static String getValueXlsx(XSSFCell xssfCell) {
        try {
			if(xssfCell.getCellType() == xssfCell.CELL_TYPE_BOOLEAN) {
			    return String.valueOf(xssfCell.getBooleanCellValue());
			} else if (xssfCell.getCellType() == xssfCell.CELL_TYPE_NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(xssfCell)) {
			    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			    	return sdf.format(HSSFDateUtil.getJavaDate(xssfCell.getNumericCellValue()));
			    }else {
					DecimalFormat df = new DecimalFormat("0");
			    	return df.format(xssfCell.getNumericCellValue());
			    }
			} else {
			    return String.valueOf(xssfCell.getStringCellValue());
			}
		} catch (Exception e) {
//			System.out.println("位置："+xssfCell.getAddress().getColumn()+":"+xssfCell.getAddress().getRow());
			return null;
		}
    }
	
	/**
	 * 获取xls文件单元格里的值(2007版以前)
	 * @param hssfCell
	 * @return
	 */
	@SuppressWarnings("static-access")
    private static String getValueXls(HSSFCell hssfCell) {
		try {
			if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
			    return String.valueOf(hssfCell.getBooleanCellValue());
			} else if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				if (DateUtil.isCellDateFormatted(hssfCell)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			        return sdf.format(HSSFDateUtil.getJavaDate(hssfCell.getNumericCellValue()));
			    }else {
					hssfCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			    	return String.valueOf(hssfCell.getStringCellValue());
			    }
			} else{
			    return String.valueOf(hssfCell.getStringCellValue());
			}
		} catch (Exception e) {
//			System.out.println("位置："+hssfCell.getAddress().getColumn()+":"+hssfCell.getAddress().getRow());
			return null;
		}
    }
}
