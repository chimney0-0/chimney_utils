package idv.common.file;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileRead {


    public static String readFileToString(File file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String str = null;
            while (((str = br.readLine()) != null)) {
                sb.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString().trim();
    }

    public static String getDocx(String path){
        try{
            POIXMLTextExtractor extractor;
            OPCPackage opcPackage = POIXMLDocument.openPackage(path);
            extractor = new XWPFWordExtractor(opcPackage);
            return extractor.getText();
        }catch (Exception e) {
            System.out.println("读取docx文件出错：" + e.getMessage());
        }
        return null;
    }

}
