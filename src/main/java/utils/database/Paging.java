package utils.database;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import utils.file.SqlRead;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chimney
 * 分页模板
 */
public class Paging {

	public void paging(long start, long maxId, int size){

		String tableName = "judicial_documents__collect";
		String sql = SqlRead.sql("sql/lawsuit", "summarize.sql").replace("$tableName", tableName);

		while (true) {

			if (start >= maxId) {
				break;
			}

			long end = (start + size) <= maxId ? (start + size) : maxId;

			List<Record> recordList = Db.find(sql, start, end);

//			log.debug(tableName + "id查询范围: " + start + "-" + end + "，取得" + recordList.size() + "条记录");

			// 去重（注意：案号为空的不会被认为是重复数据）
			List<Record> addList = new ArrayList<>();
			List<String> queryList = new ArrayList<>();
			for(Record record : recordList){
				String caseNo = record.getStr("case_name");
				if(caseNo != null){
					caseNo = caseNo.replace("(","（").replace(")","）");
					record.set("case_name", caseNo);
					queryList.add(caseNo);
				}
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < queryList.size() - 1; i++) {
				sb.append("?,");
			}
			sb.append("?");

			String querySql = "select case_name from judicial_documents_all where case_name in (" + sb.toString() + ")";
//			Record result = Db.use("catalogue").findFirst(querySql, queryList.toArray());
//			String caseNoStr = (result == null || result.get("case_no") == null)? "," : result.getStr("case_no");
//			List<String> resultList = Arrays.asList(caseNoStr.split(","));

			List<String> resultList = new ArrayList<>();
			List<Record> result = Db.use("catalogue").find(querySql, queryList.toArray());
			for(Record record : result){
				resultList.add(record.getStr("case_name"));
			}

			for(Record record : recordList) {
				String caseNo = record.getStr("case_name");
				if(caseNo == null || !resultList.contains(caseNo)){
					addList.add(record);
				}
			}

			if(addList.isEmpty()){
//				log.debug("去重后无新记录");
			}else {
				Db.batchSave("judicial_documents_all", addList, 5000);
//				log.debug("去重后" + addList.size() + "条记录，写入表judicial_documents_all");
			}
			start = start + size;

		}


	}

}
