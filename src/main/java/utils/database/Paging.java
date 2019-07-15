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

			// todo

			start = start + size;

		}


	}


	public static void paging(){

		long start = 0;
		long maxId = 10000;
		int size = 2000;

		String sql = "";

		while (true) {

			if (start >= maxId) {
				break;
			}

			long end = (start + size) <= maxId ? (start + size) : maxId;

			List<Record> recordList = Db.find(sql, start, end);

			// todo

			start = start + size;

		}
	}

}
