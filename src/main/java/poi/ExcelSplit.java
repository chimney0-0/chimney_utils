package poi;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import utils.file.FileList;

import java.io.*;
import java.util.Iterator;

public class ExcelSplit {
    String rootPath = "D:\\temptask\\origin";

    String targetRootPath = "D:\\temptask\\split";

    public static void main(String[] args) {

    }

    public void startsplit() {

        String[] subPaths = FileList.getFilePathList(rootPath);

        for (String subPath : subPaths) {
            processFile(subPath);
        }

    }


    private void processFile(String path) {
        if (path.endsWith(".xls")) {
            // xls文件

        } else if (path.endsWith(".xlsx")) {
            // xlsx 文件

        } else {
            // 文件夹
            String[] subPaths = FileList.getFilePathList(path);
            for (String subPath : subPaths) {
                processFile(subPath);
            }
        }
    }

    private void splitXls(String path) throws IOException {
        String fileName = path.substring(path.lastIndexOf("\\"), path.lastIndexOf("."));
        InputStream inputStream = new FileInputStream(new File(path));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        int sheetNum = hssfWorkbook.getNumberOfSheets();
        for(int i = 0; i < sheetNum ; i ++){
            // 每个Sheet
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetNum);
            String sheetName = hssfSheet.getSheetName();
            String targetPath = path.substring(0, path.lastIndexOf("\\"))+fileName+"_"+sheetName+".csv";
            Iterator<Row> rowIterator = hssfSheet.rowIterator();
            while (rowIterator.hasNext()){
                Row row = rowIterator.next();
                row.cellIterator();
            }
        }

    }

}
