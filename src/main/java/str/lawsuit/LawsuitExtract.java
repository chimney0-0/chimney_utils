package str.lawsuit;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
//import com.seassoon.company.quality.report.RunReport;
////import com.seassoon.etl.config.JfinalConfig;
//import com.seassoon.etl.util.SqlRead;
//import com.seassoon.log.core.Logger;
//import com.seassoon.log.core.SeassoonLogFactory;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.database.JfinalConfig;
import utils.file.SqlRead;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chimney
 */
public class LawsuitExtract implements Serializable {

//	private Logger log = SeassoonLogFactory.getLogger(LawsuitExtract.class);

	public static void main(String[] args) {
//		JfinalConfig.init();

//		new LawsuitExtract().start("judicial_documents", Long.valueOf(args[0]),10000);
		new LawsuitExtract().test("judicial_documents__collect");
//		new LawsuitExtract().complement(Long.valueOf(args[0]), Long.valueOf(args[1]));
	}

	public void test(String tableName) {
		// 分别统计三个字段的填充
		int plaintiffCount = 0;
		int defendantCount = 0;
		int caseCount = 0;
		String sql = SqlRead.sql("sql/company", "lawsuit.sql");
		sql = sql.replace("$tableName", tableName);
		List<Record> datas = Db.use("main").find(sql, 500, 1000);
		for (Record record : datas) {
			ResultEntity resultEntity = extract(record);
			if (resultEntity.isEmpty()) {
//				log.debug(record.get("id") + "/" + record.get("detail_id") + " 未提取到信息");
			} else {
//				log.debug(record.get("id") + "/" + record.get("detail_id") + ":" + resultEntity.toString());
				if (resultEntity.getPlaintiff() != null) {
					plaintiffCount++;
				}
				if (resultEntity.getDefendant() != null) {
					defendantCount++;
				}
				if (resultEntity.getLastCaseNo() != null) {
					caseCount++;
				}
			}
		}
//		log.info(datas.size() + "条记录: " + "提取原告信息" + plaintiffCount + "条, 提取被告信息" + defendantCount + "条, 提取上一审案号信息" + caseCount + "条");
	}

	public void complement(long start, long end) {
		long t1 = System.currentTimeMillis();
		List<Record> data = Db.use("main").find(SqlRead.sql("sql/company", "lawsuit_complement.sql"), start, end);
//		log.info("读取到" + data.size() + "条数据");
		List<Record> addList = Collections.synchronizedList(new ArrayList<>());
		long t2 = System.currentTimeMillis();

		final int parallelism = 8;

		ForkJoinPool forkJoinPool = null;

		try {
			forkJoinPool = new ForkJoinPool(parallelism);
			forkJoinPool.submit(() -> {

						data.parallelStream().forEach(record -> {
							ResultEntity resultEntity = extract(record);
							if (resultEntity.isEmpty()) {
							} else {
								if (resultEntity.getPlaintiff() != null) {
									//						plaintiffCount++;
									record.set("plaintiff", resultEntity.getPlaintiff());
								}
								if (resultEntity.getDefendant() != null) {
									//						defendantCount++;
									record.set("defendant", resultEntity.getDefendant());
								}
								if (resultEntity.getLastCaseNo() != null) {
									//						caseCount++;
									record.set("the_number_of_cases_in_the_first_instance", resultEntity.getLastCaseNo());
								}
								// 更新时间
								record.set("gmt_modified", new Date());

							}
							// 更新记录
							addList.add(record.remove("content").remove("detail_id"));
						});

					}
			).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			if (forkJoinPool != null) {
				forkJoinPool.shutdown(); //always remember to shutdown the pool
			}
		}

		long t3 = System.currentTimeMillis();
		try {
			Db.use("main").batchSave("judicial_documents_origin", addList, 10000);

			long t4 = System.currentTimeMillis();
//			log.info("批量插入成功{}条，查询用时{}毫秒，处理用时{}毫秒，插入用时{}毫秒", addList.size(), (t2 - t1), (t3 - t2), (t4 - t3));
		} catch (Exception e) {
//			log.error("批量插入错误", e);
//			log.info("批量插入失败");
		}

	}

	public void start(String tableName, Long startId, int size) {
		String targetTable = tableName + "_origin";

		String sql = SqlRead.sql("sql/company", "lawsuit.sql");

		Properties provinceProperties = new Properties();
		try {
			InputStreamReader input = new InputStreamReader(LawsuitExtract.class.getClassLoader().getResourceAsStream("quality/code_province.properties"), "utf-8");
			provinceProperties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}


		Long start = startId;
		while (true) {
			long t1 = System.currentTimeMillis();
//			log.info("读取数据..." + tableName + ": id>" + start + " and id<=" + (start + size));
			int notEmptyCount = 0;

			// 分别统计三个字段的填充
			int plaintiffCount = 0;
			int defendantCount = 0;
			int caseCount = 0;

			List<Record> datas = Db.use("main").find(sql.replace("$tableName", tableName), start, start + size);

			long t2 = System.currentTimeMillis();

			if (datas.isEmpty()) {
//				log.info("未读取到新数据，处理结束");
				break;
			}

			List<Record> addList = Collections.synchronizedList(new ArrayList<>());
			datas.parallelStream().forEach(record -> {

				// 替换省份缩写
				String province = record.getStr("province");
				if (province != null && provinceProperties.containsKey(province)) {
					record.set("province", provinceProperties.getProperty(province)).set("gmt_modified", new Date());
				}

				ResultEntity resultEntity = extract(record);
				if (resultEntity.isEmpty()) {
//					log.debug(record.get("id")+"/"+record.get("detail_id")+" 未提取到信息");
				} else {
//					log.debug(record.get("id")+"/"+record.get("detail_id")+":"+resultEntity.toString());
//					notEmptyCount++;
					if (resultEntity.getPlaintiff() != null) {
//						plaintiffCount++;
						record.set("plaintiff", resultEntity.getPlaintiff());
					}
					if (resultEntity.getDefendant() != null) {
//						defendantCount++;
						record.set("defendant", resultEntity.getDefendant());
					}
					if (resultEntity.getLastCaseNo() != null) {
//						caseCount++;
						record.set("the_number_of_cases_in_the_first_instance", resultEntity.getLastCaseNo());
					}
					// 更新时间
					record.set("gmt_modified", new Date());

				}
				// 更新记录
				addList.add(record.remove("content").remove("detail_id"));
			});

			// 下一页
			start = start + size;
//			log.info(datas.size()+"条记录, "+notEmptyCount+"条提取出信息 ---- "+"提取原告信息"+plaintiffCount+"条, 提取被告信息"+defendantCount+"条, 提取上一审案号信息"+caseCount+"条");
//			log.info(datas.size() + "条记录信息提取完成..");

			long t3 = System.currentTimeMillis();

			try {
				Db.use("main").batchSave(targetTable, addList, size);

				long t4 = System.currentTimeMillis();
//				log.info("批量插入成功{}条，查询用时{}毫秒，处理用时{}毫秒，插入用时{}毫秒", addList.size(), (t2 - t1), (t3 - t2), (t4 - t3));
			} catch (Exception e) {
//				log.error("批量插入错误", e);
//				log.info("批量插入失败");
			}


//			try {
//				Thread.sleep(10 * 1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}

		}

	}

	public ResultEntity extract(String content, String procedure) {
		Record record = new Record();
		record.set("content", content);
		record.set("trial_procedure", procedure);
		return extract(record);
	}

	public ResultEntity extract(Record record) {
		ResultEntity resultEntity = new ResultEntity();

		// 需要用到的信息
		// 文书内容
		String content = record.getStr("content");
		if (content == null) {
			return new ResultEntity();
		}
		// 流程: 一审/二审/再审/其他
		String procedure = record.getStr("trial_procedure");
		// 类型: 执行裁定书/执行xx/xx判决书/xx裁定书
//		String type = record.getStr("instrument_type");

		// 开头还可能有 法院 判决书名称 案号
		if (content.contains("<div") || content.contains("<a")) {
			Document document = Jsoup.parse(content);
			content = new RemoveHtml().getPlainText(document);
		}

		// 去掉重复的换行
		content = content.replaceAll("[\r\n]{2,}", "\n").replace((char) 12288, (char) 32).replace("－","-")
				.replace("０", "0").replace("１", "1").replace("２", "2").replace("３","3").replace("４","4")
				.replace("５","5").replace("６","6").replace("７","7").replace("８","8").replace("９","9");

		String subContent = content;

		// "原告"+人名+标点符号
		// "原告"+公司名+标点符号
		// "申请执行人"（执行裁定书）
		List<Pattern> patternList1 = Arrays.asList(
				// 原告 位于开头
				Pattern.compile("(?<=^原告[：\\s]?|[\\s。]原告[：\\s]?|[\\s。]n原告[：\\s]?|^n原告[：\\s]?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=^原告[：\\s]?|[\\s。]原告[：\\s]?|[\\s。]n原告[：\\s]?|^n原告[：\\s]?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=[，。\\s])"),
				// 原告 位于换行或句号后
//				Pattern.compile("(?<=[\\s。]原告：?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=[\\s。]原告：?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站))(?=[，。\\s])"),
				// 错误格式 n原告
//				Pattern.compile("(?<=[\\s。]n原告：?|^n原告：?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=[\\s。]n原告：?|^n原告：?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站))(?=[，。\\s])"),
				// "原告人" 原告...（反诉被告）
				Pattern.compile("(?<=^原告人[：\\s]?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=^原告人[：\\s]?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=[，。\\s])|(?<=[\\s。]原告人[：\\s]?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=[\\s。]原告人[：\\s]?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])"),
				Pattern.compile("(?<=^原告[：\\s]?|[\\s。]原告[：\\s]?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=（反诉被告[）)][，。\\s])|(?<=^原告[：\\s]?|[\\s。]原告[：\\s]?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=（反诉被告[）)][，。\\s])"),
				// 原告（反诉被告） 上诉人（原审被告、反诉原告） 上诉人（原审被告） 上诉人（原审原告） 上诉人（原审原告，反诉被告） 上诉人（一审被告）
				Pattern.compile("(?<=(原告[（(]反诉被告[）)][：\\s]?)|(^上诉人[：\\s]?)|([\\s。]上诉人[：\\s]?)|(^上诉人[（(]原审被告[、，]反诉原告[）)][：\\s]?)|([\\s。]上诉人[（(]原审被告[、，]反诉原告[）)][：\\s]?)|(^上诉人[（(]原审原告[、，]反诉被告[）)][：\\s]?)|([\\s。]上诉人[（(]原审原告[、，]反诉被告[）)][：\\s]?)|(^上诉人[（(]原审被告[）)][：\\s]?)|([\\s。]上诉人[（(]原审被告[）)][：\\s]?)|(^上诉人[（(]原审原告[）)][：\\s]?)|([\\s。]上诉人[（(]原审原告[）)][：\\s]?)|(^上诉人[（(]一审被告[）)][：\\s]?)|([\\s。]上诉人[（(]一审被告[）)][：\\s]?)|(^上诉人[（(]一审原告[）)][：\\s]?)|([\\s。]上诉人[（(]一审原告[）)][：\\s]?)|([\\s。]上诉人[（(]原审被告人[）)][：\\s]?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(原告[（(]反诉被告[）)][：\\s]?)|(^上诉人[：\\s]?)|([\\s。]上诉人[：\\s]?)|(^上诉人[（(]原审被告[、，]反诉原告[）)][：\\s]?)|([\\s。]上诉人[（(]原审被告[、，]反诉原告[）)][：\\s]?)|(^上诉人[（(]原审原告[、，]反诉被告[）)][：\\s]?)|([\\s。]上诉人[（(]原审原告[、，]反诉被告[）)][：\\s]?)|(^上诉人[（(]原审被告[）)][：\\s]?)|([\\s。]上诉人[（(]原审被告[）)][：\\s]?)|(^上诉人[（(]原审原告[）)][：\\s]?)|([\\s。]上诉人[（(]原审原告[）)][：\\s]?)|(^上诉人[（(]一审被告[）)][：\\s]?)|([\\s。]上诉人[（(]一审被告[）)][：\\s]?)|(^上诉人[（(]一审原告[）)][：\\s]?)|([\\s。]上诉人[（(]一审原告[）)][：\\s]?)|([\\s。]上诉人[（(]原审被告人[）)][：\\s]?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])"),
//				Pattern.compile("(?<=(^上诉人（一审被告）：?)|([\\s。]上诉人（一审被告）：?)|(^上诉人（一审原告）：?)|([\\s。]上诉人（一审原告）：?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(^上诉人（一审被告）：?)|([\\s。]上诉人（一审被告）：?)|(^上诉人（一审原告）：?)|([\\s。]上诉人（一审原告）：?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站)）)?[，。\\s])"),
				// 再审申请人（一审被告、二审上诉人） 再审申请人（一审原告、二审上诉人） 再审申请人（一审原告、反诉被告，二审上诉人）
				Pattern.compile("(?<=(再审申请人[（(]一审被告[、，]二审上诉人[）)][：\\s]?)|(再审申请人[（(]一审原告[、，]二审上诉人[）)][：\\s]?)|(再审申请人[（(]一审被告[、，]二审被上诉人[）)][：\\s]?)|(再审申请人[（(]一审原告[、，]二审被上诉人[）)][：\\s]?)|(再审申请人[（(]一审原告[、，]反诉被告[、，]二审上诉人[）)][：\\s]?)|(再审申请人[（(]一审被告[、，]反诉原告[、，]二审上诉人[）)][：\\s]?)|([\\s。]申请再审人[：\\s]?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(再审申请人[（(]一审被告[、，]二审上诉人[）)][：\\s]?)|(再审申请人[（(]一审原告[、，]二审上诉人[）)][：\\s]?)|(再审申请人[（(]一审被告[、，]二审被上诉人[）)][：\\s]?)|(再审申请人[（(]一审原告[、，]二审被上诉人[）)][：\\s]?)|(再审申请人[（(]一审原告[、，]反诉被告[、，]二审上诉人[）)][：\\s]?)|(再审申请人[（(]一审被告[、，]反诉原告[、，]二审上诉人[）)][：\\s]?)|([\\s。]申请再审人[：\\s]?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])"),
				// 申请执行人
				Pattern.compile("(?<=(^申请人?执行人[：\\s]?)|(^申请人[：\\s]?)|([\\s。]申请人?执行人[：\\s]?)|([\\s。]申请人[：\\s]?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(^申请人?执行人[：\\s]?)|(^申请人[：\\s]?)|([\\s。]申请人?执行人[：\\s]?)|([\\s。]申请人[：\\s]?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])"),
				// 公诉机关
				Pattern.compile("(?<=(^公诉机关[：\\s]?)|([\\s。]公诉机关[：\\s]?)|(n公诉机关[：\\s]?))([\\u4e00-\\u9fa5X]{2,15}?检察院)(?=[，。\\\\s])"),
				// 附带民事诉讼原告人
				Pattern.compile("(?<=(^(原审)?附带民事诉讼原告人[：\\s]?)|([\\s。](原审)?附带民事诉讼原告人[：\\s]?)|(^上诉人[（(]原审附带民事诉讼原告人[）)][：\\s]?)|([\\s。]上诉人[（(]原审附带民事诉讼原告人[）)][：\\s]?)|([\\s。]上诉人[（(]原审附带民事诉讼原告人暨原审附带民事诉讼被告人[）)][：\\s]?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(^(原审)?附带民事诉讼原告人[：\\s]?)|([\\s。](原审)?附带民事诉讼原告人[：\\s]?)|(^上诉人[（(]原审附带民事诉讼原告人[）)][：\\s]?)|([\\s。]上诉人[（(]原审附带民事诉讼原告人[）)][：\\s]?)（原审附带民事诉讼原告人暨原审附带民事诉讼被告人[）)])([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])"),
				// 申请人（原仲裁被申请人） 申请人（仲裁被申请人）
				Pattern.compile("(?<=(^申请人[（(]原仲裁被申请人[）)][：\\s]?)|([\\s。]申请人[（(]原仲裁被申请人[）)][：\\s]?)|(^申请人[（(]仲裁被申请人[）)][：\\s]?)|([\\s。]申请人[（(]仲裁被申请人[）)][：\\s]?)|(^申请人[（(]原仲裁申请人[）)][：\\s]?)|([\\s。]申请人[（(]原仲裁申请人[）)][：\\s]?)|(^申请人[（(]仲裁申请人[）)][：\\s]?)|([\\s。]申请人[（(]仲裁申请人[）)][：\\s]?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(^申请人[（(]原仲裁被申请人[）)][：\\s]?)|([\\s。]申请人[（(]原仲裁被申请人[）)][：\\s]?)|(^申请人[（(]仲裁被申请人[）)][：\\s]?)|([\\s。]申请人[（(]仲裁被申请人[）)][：\\s]?)|(^申请人[（(]原仲裁申请人[）)][：\\s]?)|([\\s。]申请人[（(]原仲裁申请人[）)][：\\s]?)|(^申请人[（(]仲裁申请人[）)][：\\s]?)|([\\s。]申请人[（(]仲裁申请人[）)][：\\s]?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])")
		);
		resultEntity.setPlaintiff(pattern(subContent, patternList1));

		// "被告"+人名+标点符号
		// "被告"+公司名+标点符号
		// "被执行人"（执行裁定书）
		List<Pattern> patternList2 = Arrays.asList(
				// 被告 位于开头
				Pattern.compile("(?<=^被告[：\\s]?|[\\s。]被告[：\\s]?|[\\s。]n被告[：\\s]?|^n被告[：\\s]?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=^被告[：\\s]?|[\\s。]被告[：\\s]?|[\\s。]n被告[：\\s]?|^n被告[：\\s]?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])"),
				// 被告 位于换行或句号后
//				Pattern.compile("(?<=[\\s。]被告：?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=[\\s。]被告：?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站)）)?[，。\\s])"),
				// 错误格式 n被告
//				Pattern.compile("(?<=([\\s。]n被告：?)|(^n被告：?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=([\\s。]n被告：?)|(^n被告：?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站)）)?[，。\\s])"),
				// "被告人" 被告...（反诉原告）
				Pattern.compile("(?<=^被告人[：\\s]?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=^被告人[：\\s]?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）()\\.]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=[，。\\s])|(?<=[\\s。]被告人[：\\s]?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=[\\s。]被告人[：\\s]?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])"),
				Pattern.compile("(?<=^被告[：\\s]?|[\\s。]被告[：\\s]?)([\\u4e00-\\u9fa5X×x]{2,4}?)(?=（反诉原告[）)][，。\\s])|(?<=^被告[：\\s]?|[\\s。]被告[：\\s]?)([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=（反诉原告[）)][，。\\s])"),
				// 被告（原审原告） 被上诉人（原审原告、反诉被告） 被上诉人（原审原告） 被上诉人（原审被告） 被上诉人（原审被告，反诉原告） 被上诉人（一审原告）
				Pattern.compile("(?<=(被告[（(]原审原告[）)][：\\s]?)|(被告[（(]反诉原告[）)][：\\s]?)|(^被上诉人[：\\s]?)|([\\s。]被上诉人[：\\s]?)|(被上诉人[（(]原审原告[、，]反诉被告[）)][：\\s]?)|(被上诉人[（(]原审被告[、，]反诉原告[）)][：\\s]?)|(被上诉人[（(]原审原告[）)][：\\s]?)|(被上诉人[（(]原审被告[）)][：\\s]?)|(被上诉人[（(]一审原告[）)][：\\s]?)|(被上诉人[（(]一审被告[）)][：\\s]?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(被告[（(]原审原告[）)][：\\s]?)|(被告[（(]反诉原告[）)][：\\s]?)|(^被上诉人[：\\s]?)|([\\s。]被上诉人[：\\s]?)|(被上诉人[（(]原审原告[、，]反诉被告[）)][：\\s]?)|(被上诉人[（(]原审被告[、，]反诉原告[）)][：\\s]?)|(被上诉人[（(]原审原告[）)][：\\s]?)|(被上诉人[（(]原审被告[）)][：\\s]?)|(被上诉人[（(]一审原告[）)][：\\s]?)|(被上诉人[（(]一审被告[）)][：\\s]?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])"),
//				Pattern.compile("(?<=(被上诉人（一审原告）：?)|(被上诉人（一审被告）：?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(被上诉人（一审原告）：?)|(被上诉人（一审被告）：?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）()\\.]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站))(?=[，。\\s])"),
				// 被申请人（一审原告、二审被上诉人）被申请人（一审被告、二审上诉人） 被申请人（一审被告、二审被上诉人） 被申请人（一审被告、反诉原告，二审上诉人）
				Pattern.compile("(?<=(被申请人[（(]一审原告[、，]二审上诉人[）)][：\\s]?)|(被申请人[（(]一审被告[、，]二审上诉人[）)][：\\s]?)|(被申请人[（(]一审原告[、，]二审被上诉人[）)][：\\s]?)|(被申请人[（(]一审被告[、，]二审被上诉人[）)][：\\s]?)|(被申请人[（(]一审被告[、，]反诉原告：[、，]二审上诉人?)|(被申请人[（(]一审原告[、，]反诉被告：[、，]二审上诉人?)|(被申请人[（(]一审被告[、，]反诉原告：[、，]二审被上诉人?)|(被申请人[（(]一审原告[、，]反诉被告：[、，]二审被上诉人?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(被申请人[（(]一审原告[、，]二审上诉人[）)][：\\s]?)|(被申请人[（(]一审被告[、，]二审上诉人[）)][：\\s]?)|(被申请人[（(]一审原告[、，]二审被上诉人[）)][：\\s]?)|(被申请人[（(]一审被告[、，]二审被上诉人[）)][：\\s]?)|(被申请人[（(]一审被告[、，]反诉原告：[、，]二审上诉人?)|(被申请人[（(]一审原告[、，]反诉被告：[、，]二审上诉人?)|(被申请人[（(]一审被告[、，]反诉原告：[、，]二审被上诉人?)|(被申请人[（(]一审原告[、，]反诉被告：[、，]二审被上诉人?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=[，。\\s])"),
				// 被执行人 被申请人 允许公司后带括号和别名
				Pattern.compile("(?<=(^被执行人[：\\s]?)|(^被申请人[：\\s]?)|([\\s。]被执行人[：\\s]?)|([\\s。]被申请人[：\\s]?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(^被执行人[：\\s]?)|(^被申请人[：\\s]?)|([\\s。]被执行人[：\\s]?)|([\\s。]被申请人[：\\s]?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])"),
//				Pattern.compile("(?<=([\\s。]被执行人：?)|([\\s。]被申请人：?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=([\\s。]被执行人：?)|([\\s。]被申请人：?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站))(?=[，。\\s])|(?<=([\\s。]被执行人：?)|([\\s。]被申请人：?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站)）)?[，。\\s])"),
				// 附带民事诉讼被告人 原审附带民事诉讼被告人
				Pattern.compile("(?<=(^(原审)?附带民事诉讼被告人[：\\s]?)|([\\s。](原审)?附带民事诉讼被告人[：\\s]?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(^(原审)?附带民事诉讼被告人[：\\s]?)|([\\s。](原审)?附带民事诉讼被告人[：\\s]?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）()\\.]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])"),
				// 被申请人（原仲裁申请人） 被申请人（仲裁申请人）
				Pattern.compile("(?<=(^被申请人[（(]原仲裁被申请人[）)][：\\s]?)|([\\s。]被申请人[（(]原仲裁被申请人[）)][：\\s]?)|(^被申请人[（(]仲裁被申请人[）)][：\\s]?)|([\\s。]被申请人[（(]仲裁被申请人[）)][：\\s]?)|(^被申请人[（(]原仲裁申请人[）)][：\\s]?)|([\\s。]被申请人[（(]原仲裁申请人[）)][：\\s]?)|(^被申请人[（(]仲裁申请人[）)][：\\s]?)|([\\s。]被申请人[（(]仲裁申请人[）)][：\\s]?))([\\u4e00-\\u9fa5X×x]{2,4}?)(?=(（系[\\S]{2,12}）)?[，。\\s])|(?<=(^被申请人[（(]原仲裁被申请人[）)][：\\s]?)|([\\s。]被申请人[（(]原仲裁被申请人[）)][：\\s]?)|(^被申请人[（(]仲裁被申请人[）)][：\\s]?)|([\\s。]被申请人[（(]仲裁被申请人[）)][：\\s]?)|(^被申请人[（(]原仲裁申请人[）)][：\\s]?)|([\\s。]被申请人[（(]原仲裁申请人[）)][：\\s]?)|(^被申请人[（(]仲裁申请人[）)][：\\s]?)|([\\s。]被申请人[（(]仲裁申请人[）)][：\\s]?))([\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(公司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|事务所|学校|部|站|组|管理所|中学))(?=(（[\\u4e00-\\u9fa5][\\u4e00-\\u9fa5a-zA-Z（）().]{1,24}(司|企业|政府|中心|队|厅|局|支行|分行|厂|会|社|矿|院|单位|店|所|学校|部|站|组|中学)）)?[，。\\s])")
		);
		resultEntity.setDefendant(pattern(subContent, patternList2));

		if (procedure != null && (procedure.contains("二审") || procedure.contains("再审"))) {
			Pattern pattern = Pattern.compile("(?<=不服[\\u4e00-\\u9fa5]{0,15}(法院|本院|该院)?(（[\\u4e00-\\u9fa5]{3,15}）)?)[（(\\[][12][90][0-9]{2}[）)\\]][\\u4e00-\\u9fa5（）]{1,6}[初终再]审?字第?[0-9-]{1,5}号|(?<=(法院|本院|该院)[\\S]{0,24}作出的?)[（(\\[][12][90][0-9]{2}[）)\\]][\\u4e00-\\u9fa5]{1,10}[初终再]审?字第?[0-9-]{1,5}号(?=([\\S]{0,4}判决)|[\\S]{0,4}裁定|[\\S]{0,4}调解)|(?<=法院于[\\S]{0,16}受理)（[12][90][0-9]{2}）[\\u4e00-\\u9fa5]{1,10}[初终再]审?字第?[0-9-]{1,5}号(?=[\\S]{0,30}一案)|(?<=委员会[\\S]{0,24}作出的)[\\u4e00-\\u9fa5]{1,10}仲案字[（(\\[][12][90][0-9]{2}[）)\\]]第[0-9-]{1,5}号(?=([\\S]{0,4}仲裁))|(?<=于[\\S]{2,20}作出)[（(\\[][12][90][0-9]{2}[）)\\]][\\u4e00-\\u9fa5]{1,10}[初终]审?字第?[0-9-]{1,5}号(?=([\\S]{0,8}判决)|[\\S]{0,4}裁定|[\\S]{0,4}调解)");
			Matcher matcher = pattern.matcher(content);
			if (matcher.find()) {
				resultEntity.setLastCaseNo(matcher.group());
			} else {
//				log.debug(record.get("id") + "/" + record.get("detail_id") + " 未找到上一审案号！");
			}
		}

		return resultEntity;
	}

	private String pattern(String content, List<Pattern> patternList) {
		List<String> resultList = new ArrayList<>();
		int boundary;
		for (Pattern pattern : patternList) {
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				String found = matcher.group();
				// 查看是否包含已有的 如果是则跳出该正则
				boolean repeat = false;

				String[] strings = {"辩称", "答辩", "诉称", "认为", "违约", "不服", "无奈", "起诉称", "不认可", "仍不服", "无异议", "经仲裁", "诉至", "选择起诉", "遂诉至", "对此不服", "撤诉后", "起诉后", "诉来本院", "现诉至", "即诉至", "诉请中", "上诉", "未举证", "未答辩", "未到庭",
						"的", "是", "被", "系由", "系该", "系再", "名称原", "已将", "报警", "作为", "因此", "支付该款", "表示", "另表", "应当", "不得已", "要求", "自知", "无关", "负担", "承担", "下达", "长期", "而非", "一方", "多次", "属于",
						"质证认为", "住院期间", "受伤后", "出院后", "因工受伤", "之起诉", "未作", "审核后", "支付后", "之行为", "之请求", "主张", "所举证据", "打我", "赔偿", "造成", "构成", "擅自", "自负", "签字", "执行", "保存", "否认", "提出", "没有", "予以",
						"多次", "催款", "余下损失", "治疗期间", "催讨", "催收", "索款", "其余损失", "被撞", "供暖后", "伤后", "发现后", "生病", "倒地", "离开", "反悔", "被咬", "下岗后", "处工作", "拖欠", "提起",
						"及车上人员", "于受伤当日", "之女", "之父", "之子", "之母", "以夫妻", "以与被告", "共同", "亦系"};

				boolean exclude = false;

				for (String str : strings) {
					if (found.startsWith(str)) {
						exclude = true;
						break;
					}
				}

				if (found.length() <= 4 && (found.endsWith("后") || found.endsWith("不服") || found.endsWith("证明") || found.endsWith("期间") || found.endsWith("诉称") || found.endsWith("辩称"))) {
					exclude = true;
				}

				if (exclude) {
					boundary = matcher.start();
					content = content.substring(0, boundary);
					break;
				}

				if (found.length() <= 4 && found.startsWith("人")) {
					continue;
				}

				for (String result : resultList) {
					if (found.startsWith(result)) {
						repeat = true;
						break;
					}
				}
				if (repeat) {
					break;
				}

				resultList.add(matcher.group());
			}
		}
		if (resultList.isEmpty()) {
			return null;
		}
		return StringUtils.join(resultList, ";");
	}

}
