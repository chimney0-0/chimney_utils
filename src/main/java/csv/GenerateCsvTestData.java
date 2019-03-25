package csv;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;

public class GenerateCsvTestData {

	public static void main(String[] args) throws IOException {

//		String s = "\r\n";
//		System.out.println(".");

//		new GenerateCsvTestData().generate4(100, 1, 1000, "D:\\data_test\\profile\\");
//        new GenerateCsvTestData().generate3(100, 150, "D:\\data_test\\", true);
//        System.out.println(new GenerateCsvTestData().randomNum());
//        new GenerateCsvTestData().generate2("big", "D:\\data_test\\");
//		new GenerateCsvTestData().generate4(100, "vin","D:\\data_test\\",
//				Arrays.asList("LFPM4ACP1B1A08953", "JF1SH95F9AG107825", "LGJE3EE04DM214889", "LB37624S3GL045050", "LSJA16E39GG120998",
//						"LFPM4ACP1B1A08953", "JF1SH95F9AG107825", "LGJE3EE04DM214889", "LB37624S3GL045050", "AB37624S366665050"));
	}

	public void generate4(Integer repeatTime, String columnName, String rootPath, List<String> values) throws IOException {
		String path = rootPath + "repeat" + values.size() + "x" + repeatTime + ".csv";
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path), false), "utf-8"));
		// 输出表头
		writer.write(columnName);
		writer.newLine();
		// 循环输出数据
		for(int i=0;i<repeatTime;i++) {
			for (String value : values) {
				writer.write(value);
				writer.newLine();
			}
			writer.flush();
		}

		writer.flush();
		writer.close();
		System.out.println(values.size()*repeatTime + "行数据写入完成");
	}


	// 测试独立值和topK算法的文件
	public void generate3(Integer rowNum, Integer columnNum, String rootPath, boolean same) throws IOException {
		String path = rootPath + "test" + rowNum + "x" + columnNum + same + ".csv";
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path), false), "utf-8"));
		//输出表头
		for (int i = 0; i < columnNum; i++) {
			writer.write("_c" + i);
			if (i < columnNum - 1) writer.write(",");
		}
		if (same) {
			Long total = (long) (rowNum * 10000);
			for (Long count = 1L; count <= total; count++) {
				writer.newLine();
				if (count % 10000 == 0) {
					writer.flush();
					System.out.println(count + "行数据写入完成");
				}
				//每行输出相同的数据
				for (int i = 0; i < columnNum; i++) {
					writer.write(String.valueOf(count.intValue() % 1000)); // 设定独立值个数
					if (i < columnNum - 1) writer.write(",");
				}
			}
		} else {
			Long total = (long) (rowNum * 10000);
			for (Long count = 1L; count <= total; count++) {
				writer.newLine();
				if (count % 10000 == 0) {
					writer.flush();
					System.out.println(count + "行数据写入完成");
				}
				//每行输出不同的数据
				for (int i = 0; i < columnNum; i++) {
					writer.write(count + "-" + columnNum);
					if (i < columnNum - 1) writer.write(",");
				}
			}
		}
		writer.flush();
		writer.close();
		System.out.println(rowNum + "w行数据写入完成");
	}


	// 测试去重的文件
	public void generate2(String name, String rootPath) throws IOException {
		String path = rootPath + name + ".csv";
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path), false), "utf-8"));
		//输出表头
		writer.write("c1,c2,c3,c4,c5");
		writer.newLine();
		//输出数据
		for (int i = 0; i < 1000; i++) {
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
		String path = rootPath + "profile_test" + rowNumW + "wX" + columnNum + ".csv";
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path), false), "utf-8"));
		//输出表头
		for (int i = 0; i < columnNum; i++) {
			writer.write("c" + i);
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
					if (columnNum > 1) {
						writer.write(",");
					}
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
		return String.valueOf((int) (Math.random() * 100000000));
	}

	public String randomStr() {
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		//由Random生成随机数
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		//长度为几就循环几次
		for (int i = 0; i < 8; ++i) {
			//产生0-61的数字
			int number = random.nextInt(str.length());
			//将产生的数字通过length次承载到sb中
			sb.append(str.charAt(number));
		}
		//将承载的字符转换成字符串
		return sb.toString();
	}

}
