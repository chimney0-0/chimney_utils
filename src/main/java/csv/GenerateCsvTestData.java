package csv;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;

public class GenerateCsvTestData {

    public static void main(String[] args) throws IOException {
//        new GenerateCsvTestData().generate(10, 10, "E:\\data_test\\");
//        System.out.println(new GenerateCsvTestData().randomNum());
        new GenerateCsvTestData().generate2("big", "E:\\data_test\\");
    }

    public void generate2(String name, String rootPath) throws IOException {
        String path = rootPath + name + ".csv";
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path), false), "utf-8"));
        //输出表头
        writer.write("c1,c2,c3,c4,c5");
        writer.newLine();
        //输出数据
        for(int i = 0 ; i <1000; i++){
            writer.write("1,1,c,1,100\n" +
                    "1,1,c,1,22\n" +
                    "1,1,bb,2,13\n" +
                    "1,1,aaa,3,100\n" +
                    "1,2,c,1,33\n" +
                    "1,2,bb,2,999\n" +
                    "1,2,aaa,3,100\n" +
                    "1,3,c,1,44\n" +
                    "1,3,bb,2,22\n" +
                    "1,3,aaa,3,13\n" +
                    "1,3,d,4,100\n");
        }
        writer.flush();
        writer.close();
    }

    public void generate(Integer rowNumW, Integer columnNum, String rootPath) throws IOException {
        String path = rootPath + "test" + rowNumW + "x" + columnNum + ".csv";
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path), false), "utf-8"));
        //输出表头
        for (int i = 0; i < columnNum; i++) {
            writer.write("_c" + i);
            if (i < columnNum - 1) writer.write(",");
        }
        //逐行输出数据，每行的第一个数据为计数
        Long total = (long) (rowNumW * 10000);
        for (Long count = 1L; count <= total; count++) {
            writer.newLine();
            if (count % 10000 == 0) {
                writer.flush();
                System.out.println(count + "行数据写入完成");
            }
            //每行输出数据
            for (int i = 0; i < columnNum; i++) {
                if (i == 0) {
                    writer.write(count.toString());
                    writer.write(",");
                } else if (i == columnNum - 1) {
                    writer.write(randomStr());
                } else if (i <= columnNum / 2) {
                    writer.write(randomNum());
                    writer.write(",");
                } else {
                    writer.write(randomStr());
                    writer.write(",");
                }
            }
        }
        writer.flush();
        writer.close();
        System.out.println(rowNumW + "w行数据写入完成");
    }


    public String randomNum() {
//        DecimalFormat df = new DecimalFormat("#.0");
//        return df.format(Math.random() * 100);
        return String.valueOf((int) (Math.random() * 100));
    }

    public String randomStr() {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        //由Random生成随机数
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        //长度为几就循环几次
        for (int i = 0; i < 2; ++i) {
            //产生0-61的数字
            int number = random.nextInt(str.length());
            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
    }

}
