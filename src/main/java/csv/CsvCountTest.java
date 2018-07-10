package csv;

import utils.file.FileUtil;

import java.io.*;

public class CsvCountTest {

    public static void main(String[] args) throws FileNotFoundException {
        String path = "D:\\data_test\\test1000x10v1.csv";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
    }

}
