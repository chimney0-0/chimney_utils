package idv.common.poi;

import com.google.common.base.Joiner;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import idv.common.file.FileList;

import java.io.*;
import java.util.Iterator;

public class ExcelSplit {
    String rootPath = "D:\\data_test\\1";

    String targetRootPath = "D:\\data_test\\1csv";

    public static void main(String[] args) throws IOException {
//        new ExcelSplit().splitXls("D:\\temptask\\origin\\2008-2018检测数据\\0-整理数据\\检测数据-2010-2011.xls");
//        new ExcelSplit().splitXlsx("D:\\temptask\\origin\\2008-2018检测数据\\0-整理数据\\检测数据-2016-2017.xlsx");
//        new ExcelSplit().processFile("D:\\temptask\\origin\\2008-2018检测数据");
        new ExcelSplit().startsplit();
    }

    public void startsplit() throws IOException {

        String[] subPaths = FileList.getFilePathList(rootPath);

        for (String subPath : subPaths) {
            processFile(subPath);
        }

    }


    private void processFile(String path) throws IOException {
        if (path.endsWith(".xls")) {
            // xls文件
            splitXls(path);
        } else if (path.endsWith(".xlsx")) {
            // xlsx 文件
            splitXlsx(path);
        } else {
            // 文件夹
            String targetPath = path.replace(rootPath, targetRootPath);
            String[] subPaths = FileList.getFilePathList(path);
            if (subPaths.length > 0) new File(targetPath).mkdirs();
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
        System.err.println(sheetNum);
        for (int i = 0; i < sheetNum; i++) {
            // 每个Sheet
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(i);
            String sheetName = hssfSheet.getSheetName();
//            String prePath = path.substring(0, path.lastIndexOf("\\")).replace(rootPath, targetRootPath);
            String targetPath = path.substring(0, path.lastIndexOf("\\")).replace(rootPath, targetRootPath) + fileName + "_" + sheetName + ".csv";
            System.err.println(targetPath);
//            new File(prePath).mkdirs();

            Iterator<Row> rowIterator = hssfSheet.rowIterator();
            // 记录表头列数
            Row firstRow = null;
            int startIndex = -1;
            while (firstRow == null) {
                startIndex++;
                firstRow = hssfSheet.getRow(startIndex);
                if (startIndex > 1000) break;
            }
            if (startIndex > 1000) {
                System.err.println("空表");
                continue;
            }
            int columnNum = hssfSheet.getRow(startIndex).getLastCellNum();


            System.err.println(columnNum);
            if (columnNum == -1) continue;
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPath))));
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
//                Short columnNum = row.getLastCellNum(); // 注意是下标+1
//                System.err.println(columnNum);
                String[] data = new String[columnNum];
                for (int j = 0; j < columnNum; j++) {
                    data[j] = row.getCell(j) == null ? "" : row.getCell(j).toString();
                }
                bw.write(convertData(data));
            }
            bw.flush();
            bw.close();
        }
    }

    private void splitXlsx(String path) throws IOException {
        String fileName = path.substring(path.lastIndexOf("\\"), path.lastIndexOf("."));
        InputStream inputStream = new FileInputStream(new File(path));
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        int sheetNum = xssfWorkbook.getNumberOfSheets();
        System.err.println(sheetNum);
        for (int i = 0; i < sheetNum; i++) {
            // 每个Sheet
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(i);
            String sheetName = xssfSheet.getSheetName();
//            String prePath = path.substring(0, path.lastIndexOf("\\")).replace(rootPath, targetRootPath);
            String targetPath = path.substring(0, path.lastIndexOf("\\")).replace(rootPath, targetRootPath) + fileName + "_" + sheetName + ".csv";
            System.err.println(targetPath);
//            new File(prePath).mkdirs();

            // 记录表头列数
            Row firstRow = null;
            int startIndex = -1;
            while (firstRow == null) {
                startIndex++;
                firstRow = xssfSheet.getRow(startIndex);
                if (startIndex > 1000) break;
            }
            if (startIndex > 1000) {
                System.err.println("空表");
                continue;
            }
            int columnNum = xssfSheet.getRow(startIndex).getLastCellNum();
//            // 处理表头空字段
//            while (columnNum > 1 &&
//                    (xssfSheet.getRow(0).getCell(columnNum-1) == null ||
//                            xssfSheet.getRow(0).getCell(columnNum-1).toString().length() == 0)){
//                columnNum --;
//            }
            System.err.println(columnNum);
            if (columnNum == -1) continue;
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPath))));
            Iterator<Row> rowIterator = xssfSheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
//                Short columnNum = row.getLastCellNum(); // 注意是下标+1
//                System.err.println(columnNum);
                String[] data = new String[columnNum];
                for (int j = 0; j < columnNum; j++) {
                    data[j] = row.getCell(j) == null ? "" : row.getCell(j).toString();
                }
                bw.write(convertData(data));
            }
            bw.flush();
            bw.close();
        }
    }

    public String convertData(String[] nextLine) {
        String separator = ",";
        for (int i = 0; i < nextLine.length; i++) {
            if (nextLine[i] != null) {
                //nextLine[i].replaceAll("\s", replacement)
                nextLine[i] = nextLine[i].replaceAll("\"\"", "\\\"").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
            }
        }
        return "\"" + Joiner.on("\"" + separator + "\"").useForNull("").join(nextLine) + "\"\n";
    }

}
