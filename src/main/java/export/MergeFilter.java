package export;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.apache.log4j.Logger;
import utils.database.JfinalConfig;

import java.util.*;

public class MergeFilter {

    private Logger logger = Logger.getLogger(MergeFilter.class);


    public static void main(String[] args) {
        /*参数设置*/
        //数据库连接
        String dbHost = "localhost";
        String dbName = "export";
        String dbUser = "root";
        String dbPass = "123456";

        //表列表
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String targetTable = "2002_total_mul";
        MergeFilter filter = new MergeFilter(dbHost, dbName, dbUser, dbPass);

        //for (String month : months) {
        Arrays.asList(months).parallelStream().forEach(month -> {
            String originTable = "2002-" + month;
            filter.merge(originTable, targetTable);
            //}
        });

        String sql = "create table `2002_result_mul` " +
                "(select `pname`, `贸易类型_C`, SUM(`quantity`), SUM(`value`), any_value(`paddr`), any_value(`tel`), any_value(`zip`) from `2002_total` group by `pname`, `贸易类型_C`)";
        Db.update(sql);

        //filter.mergeSql("2002-01", "2002_total_sql");


    }

    public MergeFilter(String dbHost, String dbName, String dbUser, String dbPass) {
        try {
            JfinalConfig.init("jdbc:mysql://" + dbHost + ":3306/" + dbName + "?characterEncoding=utf-8&useSSL=false&serverTimezone=GMT", dbUser, dbPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void merge(String originTable, String targetTable) {

        int size = 5000;
        int start = 0;
        while (true) {
            List<Record> recordList = Db.find("select `进出口_C`,`quantity`,`value`,`pname`,`paddr`,`tel`,`zip`,`贸易类型_C` from `" + originTable + "` limit " + start + "," + size);
            if (recordList.size() == 0) break;
            logger.info(originTable + "开始处理: " + start + "-" + (size + start));
            List<Record> records = Collections.synchronizedList(new ArrayList<>());
            recordList.parallelStream().forEach((Record f) -> {
                //判断过滤条件
                String pname = f.getStr("pname");
                if (pname.trim().equals("")) return;
                String export = f.getStr("进出口_C");
                if (!export.equals("出口")) return;
                String type = f.getStr("贸易类型_C");
                if (!(type.equals("一般贸易") || type.equals("来料加工装配贸易") || type.equals("进料加工贸易"))) return;
                //插入目标表
                records.add(f);
            });
            Db.batchSave(targetTable, records, 5000);
            logger.info(originTable + "处理完成: " + start + "-" + (size + start));
            start += size;
        }
    }

    public void mergeSql(String originTable, String targetTable) {
        logger.info("开始处理" + originTable + "至" + targetTable);
        String sql = "insert into `" + targetTable + "` ( `进出口_C`,`quantity`,`value`,`pname`,`paddr`,`tel`,`zip`,`贸易类型_C`) " +
                "(select `进出口_C`,`quantity`,`value`,`pname`,`paddr`,`tel`,`zip`,`贸易类型_C` from `" + originTable + "` " +
                "where `进出口_C` = '出口' and `贸易类型_C` in ('一般贸易','来料加工装配贸易','进料加工贸易'))";
        Db.update(sql);
        logger.info("完成处理" + originTable + "至" + targetTable);
    }


}
