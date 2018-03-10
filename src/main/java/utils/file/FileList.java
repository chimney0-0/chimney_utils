package utils.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取路径下的全部文件
 */
public class FileList {
    /**
     * 传入路径，返回该路径下的所有文件的路径
     *
     * @param directoryPath
     * @return
     */
    public static String[] getFilePathList(String directoryPath) {
        File[] files = new File(directoryPath).listFiles();
        String[] filesPath = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filesPath[i] = files[i].getAbsolutePath();
        }
        return filesPath;
    }

    /**
     * 获取给定路径下所有文件的路径，包括其子级的文件
     *
     * @param directoryPath
     * @return
     */
    public static String[] getAllFilePath(String directoryPath) {
        List<File> fileList = new ArrayList<File>();
        File[] files = new File(directoryPath).listFiles();
        for (int i = 0; i < files.length; i++) {
            //如果该子文件为目录，
            addSubFile(files[i], fileList);
        }
        List<String> filePathList = new ArrayList<String>();
        for (File file : fileList) {
            filePathList.add(file.getAbsolutePath());
        }
        return filePathList.toArray(new String[filePathList.size()]);
    }


    private static void addSubFile(File file, List<File> fileList) {

        if (file.isDirectory()) {//如果该路径为目录
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                addSubFile(files[i], fileList);
            }

        } else if (file.isFile()) {//如果该路径为文件
            fileList.add(file);
        } else {
            System.out.println("未找到文件：" + file.getAbsolutePath());
        }
    }

}
