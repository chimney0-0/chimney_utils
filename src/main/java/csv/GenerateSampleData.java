package csv;

import com.google.common.base.Joiner;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GenerateSampleData {

//	static Random random = new Random();

	public static void main(String[] args) {
//		String rootPath = args[0];
//		Integer end = Integer.valueOf(args[2]);
//		Integer start = Integer.valueOf(args[1]);
		String rootPath = "D:\\data_test\\profile\\";
		Integer start = 2;
		Integer end = 10;
		GenerateSampleData generateSampleData = new GenerateSampleData();
		List<Integer> nums = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			nums.add(i);
		}


//		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 4,
//				10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
//////		nums
//////				.parallelStream()
//////				.forEach(i -> generateSampleData.generateOne(rootPath + "part_" + i, 100 * 10000));
////
//		nums.forEach(i -> threadPoolExecutor.execute(() ->
////				generateSampleData.generateOne(rootPath + "part_" + i, 100 * 10000))
//				generateSampleData.generateVariousColumns(200, 50, rootPath, i))
//		);
//		threadPoolExecutor.shutdown();

		generateSampleData.generateVariousColumns(300,20, rootPath, 1);

	}

	public void generateVariousColumns(Integer rowNumW, Integer columnNum, String rootPath, Integer partNum) {
		try {
			String path = rootPath + "profile_test" + rowNumW + "wX" + columnNum + "_part" + partNum + ".csv";
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path), false), "utf-8"));
			//逐行输出数据，每行的第一个数据为计数

			//输出表头 fixme
			for (int i = 0; i < columnNum; i++) {
				writer.write("c" + i);
				if (i < columnNum - 1) writer.write(",");
			}
			writer.newLine();

			Long start = rowNumW * 10000 * (partNum - 1) + 1L;
			Long end = (long) (rowNumW * 10000 * partNum);
			for (Long count = start; count <= end; count++) {
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
						writer.write(randomStr(20));
					} else if (i <= columnNum / 2) {
						writer.write(randomNum());
						writer.write(",");
					} else {
						writer.write(randomDualNullStr(16));
						writer.write(",");
					}
				}
				writer.newLine();
			}
			writer.flush();
			writer.close();
			System.out.println(rowNumW + "w行数据写入完成");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateOne(String path, Integer rowCount) {
		if (new File(path).exists()) {
			return;
		}

		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path), false), "utf-8"));

			for (int i = 0; i < rowCount; i++) {
				List<String> data = new ArrayList<>();

				// 19列
				//id
				data.add(randomNum());
				//lp
				data.add(randomStr(60));
				for (int j = 0; j < 6; j++) {
					data.add(randomNum());
				}
				// pack_code
				data.add(randomDualStr(50));
				//is_disabled
				data.add(randomNum());
				for (int j = 0; j < 2; j++) {
					data.add(randomDate());
				}
				// package_type
				data.add(randomDualNullStr(60));
				//item_id
				data.add(randomNum());
				for (int j = 0; j < 4; j++) {
					data.add(randomDualNullStr(70));
				}
				data.add(randomNum());


				String dataStr = Joiner.on(",").join(data);
				writer.write(dataStr);
				writer.newLine();

				if (i % 10000 == 0) {
					writer.flush();
				}
			}

			writer.close();
			System.out.println(new Date() + " " + path + "数据生成");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private String randomDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
		long begin = 1512334030191L;
		long end = 1537335566191L;
		Random random = new Random();
		long date = begin + (long) (random.nextDouble() * (end - begin));
//		long date = begin + (long) (Math.random() * (end - begin));
		return sdf.format(new Date(date));
	}

	private String randomNum() {
		Long begin = 100000000L;
		Long end = 99999999999999L;
		Random random = new Random();
		Long number = begin + (long) (random.nextDouble() * (end - begin));
//		Long number = begin + (long) (Math.random() * (end - begin));
		return number.toString();
	}

	private String randomStr(Integer length) {
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		//由Random生成随机数
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		//长度为几就循环几次
		for (int i = 0; i < length; ++i) {
			//产生0-61的数字
//			int number = (int) (Math.random() * str.length());
			int number = random.nextInt(str.length());
			//将产生的数字通过length次承载到sb中
			sb.append(str.charAt(number));
		}
		//将承载的字符转换成字符串
		return sb.toString();
	}

	private String randomDualStr(Integer length) {
		length = (int) (length * 1.8);
		return randomStr(length);
	}

	private String randomDualNullStr(Integer length) {
		int rd = Math.random() > 0.9995 ? 1 : 0;
		if (rd == 1) {
			return "";
		} else {
			return randomDualStr(length);
		}
	}

}
