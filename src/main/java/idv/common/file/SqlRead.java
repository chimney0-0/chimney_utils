package idv.common.file;

import idv.app.database.JfinalConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SqlRead {
	public SqlRead() {
	}

	public static String sql(String path, String fileName) {
		if (path == null) {
			path = "";
		}

		String url = path.charAt(path.length() - 1) == '/' ? path + fileName : path + "/" + fileName;
		StringBuilder sb = new StringBuilder();

		try {
			Throwable var4 = null;
			Object var5 = null;

			try {
				InputStreamReader input = new InputStreamReader(JfinalConfig.class.getClassLoader().getResourceAsStream(url), "utf-8");

				try {
					BufferedReader in = new BufferedReader(input);
					String line = "";

					while((line = in.readLine()) != null) {
						if (line.indexOf("#") <= -1) {
							sb.append(" " + line + " ");
						}
					}
				} finally {
					if (input != null) {
						input.close();
					}

				}
			} catch (Throwable var16) {
				if (var4 == null) {
					var4 = var16;
				} else if (var4 != var16) {
					var4.addSuppressed(var16);
				}

//				throw var4;
			}
		} catch (Exception var17) {
			var17.printStackTrace();
			throw new RuntimeException("读取sql文件错误！");
		}

		return sb.toString();
	}
}
