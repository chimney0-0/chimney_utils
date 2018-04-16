package utils.file;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 文件操作类
 * @author jiandaiqiang
 *
 */
public class FileUtil {
	
	public static void WriteFile(String filePath, InputStream is){
		BufferedWriter bw = null;
		BufferedReader br = null;
		
		checkPath(filePath);
		
		try {
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath), true),"UTF-8"));
			String info = null;
			while((info = br.readLine()) != null){
				bw.write(info + "\r\n");
			}
			
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void WriteFile(String filePath, List<String> writeInfo){
		BufferedWriter bw = null;
		checkPath(filePath);
		
		try {
			bw = new BufferedWriter(new FileWriter(filePath, true));
			for(String info : writeInfo){
				bw.write(info + "\r\n");
			}
			bw.flush();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}finally{
			if(bw != null){
				try {
					bw.close();
				} catch (IOException ignored) {
					
				}
			}
		}
	}
	
	public static void WriteFile(String filePath, String writeInfo, boolean append){
		BufferedWriter bw = null;
		checkPath(filePath);
		
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath), append),"UTF-8"));
			bw.write(writeInfo + "\r\n");
			bw.flush();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}finally{
			if(bw != null){
				try {
					bw.close();
				} catch (IOException ignored) {
					
				}
			}
		}
	}	
	
	public static void WriteMapFile(String filepath, Map<Integer, Integer> infos){
		BufferedWriter bw = null;
		
		checkPath(filepath);
		
		try {
			File file = new File(filepath);
			bw = new BufferedWriter(new FileWriter(file));
			StringBuilder sb = new StringBuilder();
			for(Entry<Integer, Integer> entry : infos.entrySet()){
				sb.append(entry.getKey()).append("|").append(entry.getValue());
			}
			bw.write(sb.toString() + "\r\n");
			bw.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 检查路径是否存在，不存在则创建
	 * @param path 路径
	 */
	private static void checkPath(String path){
		File folder = new File(path.substring(0,path.lastIndexOf("/")));
		if(!folder.exists()){
			try{
			folder.mkdirs();
			}catch (Exception e) {
				System.out.println("检查文件路径失败！" + e.getMessage());
			}
		}
	}
	
	/**
	 * 读取指定类型文件
	 * @param folderPath 文件夹路径
	 * @param fileType 指定类型
	 * @return
	 */
	public static List<File> readFileByType(String folderPath, final String fileType){
		String[] files = new File(folderPath).list(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(fileType);
			}
		});
		
		List<File> fileList = new ArrayList<>();
		for(String file : files != null ? files : new String[0]){
			String filePath = folderPath + File.separator + file;
			fileList.add(new File(filePath));
		}
		return fileList;
	}
	
	private static List<File> getDirFiles(File dir){
		List<File> files = new ArrayList<File>();
		File[] dirs = dir.listFiles();
		for(File file : dirs){
			if(file.isDirectory()){
				files.addAll(getDirFiles(file));
			}
			else{
				files.add(file);
			}
		}
		return files;
	}
	
	public static void readFile(File file, List<String> allInfo){
		BufferedReader br = null;
		try {
//			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			br = new BufferedReader(new InputStreamReader(FileUtil.class.getClassLoader().getResourceAsStream(file.getPath())));

			String str;
			while(((str = br.readLine()) != null)){
				if(!str.equals("") && !str.startsWith("#")){
					allInfo.add(str);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException ignored) {
				}
			}
		}
	}
	public static String readFile(File file){
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String str;
			while(((str = br.readLine()) != null)){
				sb.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException ignored) {
				}
			}
		}
		return sb.toString().trim();
	}
	
	/**
	 * 将PDF下载到指定路径
	 * @param pdfUrl url
	 * @param writeFile 路径
	 */
	public static void getPdfInNet(String pdfUrl, String writeFile){
		File folder = new File(writeFile.substring(0, writeFile.lastIndexOf("\\")));
		if(!folder.exists()){
			folder.mkdirs();
		}
		OutputStream bos = null;
		try{
			URL u = new URL(pdfUrl);
			InputStream i = u.openStream();
			byte[] b = new byte[1024*1024];
			int len;
			bos = new FileOutputStream(new File(writeFile));
			while ((len = i.read(b)) != -1) {
				bos.write(b, 0, len);
			}
			bos.flush();
			bos.close();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static byte[] getPdf(String inUrl, String filePath){
		File folder = new File(filePath.substring(0,filePath.lastIndexOf("\\")));
		if(!folder.exists()){
			folder.mkdirs();
		}
		
		InputStream is;
		HttpURLConnection urlcon = null;
		ByteArrayOutputStream output = null;
		try {
			
			URL url = new URL(inUrl);
			urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("GET");
			urlcon.setConnectTimeout(60* 1000);
			urlcon.setReadTimeout(60 * 1000);
			urlcon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
			urlcon.connect();
//			String contentDisposition = urlcon.getHeaderField("Content-Disposition");
			if(urlcon.getResponseCode() == 200){
				is = urlcon.getInputStream();
				
				output = new ByteArrayOutputStream();
				
				byte[] b = new byte[1024*1024];
				int len;
				OutputStream bos = new FileOutputStream(new File(filePath));
				while ((len = is.read(b)) != -1) {
					bos.write(b, 0, len);
					output.write(b, 0, len);
				}
				bos.flush();
				bos.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(urlcon != null){
				urlcon.disconnect();
			}
		}
		if(output == null){
			//有的图片本身无法下载
			return new byte[]{};
		}
		return output.toByteArray();
	}
	/**
	 * 获取配置文件（绝对路径）
	 * @param proPath 绝对路径
	 * @return 配置
	 */
	public static Properties getProAbsolute(String proPath){
		Properties pro = new Properties();
		try {
			pro.load(new BufferedReader(new InputStreamReader(new FileInputStream(proPath), "UTF-8")));
		} catch (IOException e) {
			System.out.println("Properties load error!" + e.getMessage());
		}
		return pro;
	}
	/**
	 * 获取配置文件（相对路径）
	 * @param path 相对路径
	 * @return 配置
	 */
	public static Properties getProRelative(String path){
		Properties pro = new Properties();
		try {
			pro.load(new InputStreamReader(FileUtil.class.getClassLoader().getResourceAsStream(path),"utf-8"));
		} catch (Exception e) {
			System.out.println("Properties load error：" + e.getMessage());
			e.printStackTrace();
		}
		return pro;
	}

	public static Properties getOrderedProRelative(String path){
	    Properties pro = new OrderedProperties();
        try {
            pro.load(new InputStreamReader(FileUtil.class.getClassLoader().getResourceAsStream(path),"utf-8"));
        } catch (Exception e) {
            System.out.println("Properties load error：" + e.getMessage());
            e.printStackTrace();
        }
        return pro;
    }

}
