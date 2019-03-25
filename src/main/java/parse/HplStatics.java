package parse;

import com.hplsql.ComplexColInfo;
import com.hplsql.HplLineageParser;
import com.hplsql.SimpleColInfo;
import com.hplsql.TableLineageInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HplStatics {

	public static void main(String[] args) {

//		ExcelXLSXOutputer outputer;
//		List<String[]> dataList = new ArrayList<>();

		// 结果输出到excel文件
		String path = "D:\\项目资料\\上汽\\数据\\sql_result.xls";
		String[] header = {"sh文件名", "语句序号", "sql语句", "解析结果", "是否检查", "解析是否正确"};
		try {
//			outputer = new ExcelXLSXOutputer(path, header);


			// 读取sh文件
			String root = "D:\\项目资料\\上汽\\数据\\脚本只保留sql的 2";
			File[] files = new File(root).listFiles();
			for (File file : files) {
				if(!file.getName().endsWith(".sh") && !file.getName().endsWith(".sql")){
					continue;
				}
				String content = readFileToString(file);
				String[] sqls = content.split(";");

				Integer index = 0;
				for (String sql : sqls) {
					if (sql.trim().length() > 3) {
						if(sql.trim().toLowerCase().startsWith("use") || sql.trim().toLowerCase().startsWith("set") || sql.trim().toLowerCase().startsWith("alter table") || sql.trim().startsWith("add jar")){
							continue;
						}

						// 语句序号
						index++;
						// 解析结果
						StringBuilder sb = new StringBuilder();
						try {
							List<TableLineageInfo> result = HplLineageParser.parseSql(sql);
							sb.append("解析结果如下.........................").append("\n");

							for (int i = 0; i < result.size(); i++) {
								TableLineageInfo lineageInfo = result.get(i);
								if (lineageInfo.getTableName() == null || lineageInfo.getTableName().length() == 0) {
									sb.append("找不到依赖关系!\n");
									continue;
								}
								sb.append("表依赖关系为:\n");
								if (lineageInfo.getTableName() != null && lineageInfo.getTableName().length() > 0) {
									sb.append(lineageInfo.getTableName()).append("\t<- ");  //打印目标表名
								}
								Set<String> fromTables = lineageInfo.getFromTables();
								Iterator<String> itTables = fromTables.iterator();
								while (itTables.hasNext()) {
									sb.append(itTables.next());
									if (itTables.hasNext()) {
										sb.append(",");
									}
								}
								sb.append("\n字段依赖关系为:\n");
								Set<String> setColumn = lineageInfo.getMapColumns().keySet(); //目标表的字段列表
								Iterator<String> it = setColumn.iterator();
								ComplexColInfo inputCols;
								while (it.hasNext()) {
									String strCol = it.next();
									sb.append(strCol + "\t<- "); //打印目标字段
									inputCols = lineageInfo.getMapColumns().get(strCol);
									Iterator<String> itSubCols = inputCols.getSubColFullNames().iterator();
									while (itSubCols.hasNext()) // 目标字段的输入字段列表
									{
										String strSubCol = itSubCols.next();
										SimpleColInfo colInfo = inputCols.getSubColInfos().get(strSubCol);
										sb.append(colInfo.getTable() + "." + colInfo.getColName()); //打印输入表名和字段
										if (itSubCols.hasNext()) {
											sb.append(",");
										}
									}
									sb.append("\n");
								}
								sb.append("\n");
							}

						} catch (Exception e) {
							String[] data = {file.getName(), index.toString(), sql, "报错: "+e.getMessage(), "n", ""};
//							outputer.process(data);
						}
						// "sh文件名", "语句序号", "sql语句", "解析结果", "是否检查", "解析是否正确"
						String[] data = {file.getName(), index.toString(), sql, sb.toString(), "n", ""};
//						outputer.process(data);
					}
				}
			}
//			outputer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static String readFileToString(File file) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String str = null;
			while (((str = br.readLine()) != null)) {
				sb.append(str);
				sb.append("\n");
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

}
