import file.*;

import java.util.Arrays;


public class ReadFileListTest {

    public static void main(String[] args){
        String filefolder = "/Users/chimney/文档";
        System.out.println(Arrays.toString(FileList.getAllFilePath(filefolder)));
    }

}
