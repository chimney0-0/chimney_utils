package idv.common.web;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownPDF {
	
	/**
	 * 根据文件URL,获取文件名,下载文件
	 * @param inUrl  文件URL
	 * @param filePath 保存的文件夹路径
	 */
	public static void downPdf(String inUrl, String filePath){
		//checkPath(filePath);
		checkPathRelativePath(filePath);
		InputStream is = null;
		HttpURLConnection urlcon = null;
		try {
			
			URL url = new URL(inUrl);
			urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("GET");
			urlcon.setConnectTimeout(5000);
			urlcon.setReadTimeout(10 * 1000);
			urlcon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
			urlcon.connect();
//			String contentDisposition = urlcon.getHeaderField("Content-Disposition");
			if(urlcon.getResponseCode() == 200){
				is = urlcon.getInputStream();
				byte[] b = new byte[1024*1024];
				int len;
				OutputStream bos = new FileOutputStream(new File(filePath));
				while ((len = is.read(b)) != -1) {
					bos.write(b, 0, len);
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
	}
	
	public static byte[] getPdf(String inUrl, String filePath){
		checkPath(filePath);
		
		InputStream is = null;
		HttpURLConnection urlcon = null;
		ByteArrayOutputStream output = null;
		try {
			
			URL url = new URL(inUrl);
			urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("GET");
			urlcon.setConnectTimeout(120000);
			urlcon.setReadTimeout(300 * 1000);
			urlcon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
//			urlcon.setRequestProperty("Cookie", "JSESSIONID=C910DF226AEBD805ADD9034479DA1491; yfx_c_g_u_id_10003155=_ck18042016321418753945756203371; yfx_f_l_v_t_10003155=f_t_1524213134872__r_t_1529645700800__v_t_1529654693875__r_c_2");
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
	 * 检查路径是否存在，不存在则创建
	 * @param path
	 */
	public static void checkPath(String path){
		File f = new File(path);
		if (f.isFile()){
			File dir = f.getParentFile();
			if(!dir.exists()){
				dir.mkdirs();
			}
		} 
	}
	
	/**
	 * （相对路径）检查路径是否存在，不存在则创建
	 * @param path
	 */
	public static void checkPathRelativePath(String path){
		File file = new File(path); 
		File fileParent = file.getParentFile(); 
		if(!fileParent.exists()){ 
		 fileParent.mkdirs(); 
		} 
//		try {
//			file.createNewFile();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
