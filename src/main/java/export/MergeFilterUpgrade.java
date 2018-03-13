package export;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.apache.log4j.Logger;
import utils.database.JfinalConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MergeFilterUpgrade {

    private Logger logger = Logger.getLogger(MergeFilterUpgrade.class);


    public static void main(String[] args) {
        /*参数设置*/
        //数据库连接
        String dbHost = "localhost";
        String dbName = "export";
        String dbUser = "root";
        String dbPass = "123456";

        //表列表
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String targetTable = "2003_total";
        MergeFilterUpgrade filter = new MergeFilterUpgrade(dbHost, dbName, dbUser, dbPass);

        //for (String month : months) {
        Arrays.asList(months).parallelStream().forEach(month -> {
            String originTable = "data2003" + month;
            filter.merge(originTable, targetTable);
            //}
        });
    }

    public MergeFilterUpgrade(String dbHost, String dbName, String dbUser, String dbPass) {
        try {
            JfinalConfig.init("jdbc:mysql://" + dbHost + ":3306/" + dbName + "?characterEncoding=utf-8&useSSL=false&serverTimezone=GMT", dbUser, dbPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void merge(String originTable, String targetTable) {
        //先给表加上主键
        StringBuilder sbKey = new StringBuilder();
        sbKey.append("alter table `");
        sbKey.append(originTable);
        sbKey.append("` add `id` int(11) auto_increment PRIMARY key");
        Db.update(sbKey.toString());
        logger.info(originTable+"添加主键完成");

        int size = 5000;
        //int start = 0;
        int lastId = 0;

        String exportColumn = "进口或出口";
        String quantityColumn = "数量";
        String valueColumn = "金额";
        String nameColumn = "经营单位";
        String addrColumn = "单位地址";
        String telColumn = "电话";
        String zipColumn = "邮编";
        String typeColumn = "贸易方式";

        while (true) {

            //sql改为拼接
            StringBuilder sb = new StringBuilder();
            sb.append("select `id` ,");
            sb.append("`");
            sb.append(exportColumn);
            sb.append("` , `");
            sb.append(quantityColumn);
            sb.append("` , `");
            sb.append(valueColumn);
            sb.append("` , `");
            sb.append(nameColumn);
            sb.append("` , `");
            sb.append(addrColumn);
            sb.append("` , `");
            sb.append(telColumn);
            sb.append("` , `");
            sb.append(zipColumn);
            sb.append("` , `");
            sb.append(typeColumn);
            sb.append("` from `");
            sb.append(originTable);
            sb.append("` where `id` > ");
            sb.append(lastId);
            sb.append(" limit ");
            sb.append(size);
            logger.info(sb.toString());

            List<Record> recordList = Db.find(sb.toString());
            if (recordList.size() == 0) break;
            lastId = recordList.get(recordList.size()-1).getInt("id"); //重新获取最大的id
            logger.info(originTable + "开始处理: " + (lastId+1) + "-" + (size + lastId));
            List<Record> records = Collections.synchronizedList(new ArrayList<>());
            recordList.parallelStream().forEach((Record f) -> {
                //判断过滤条件
                String pname = f.getStr(nameColumn);
                if (pname.trim().equals("")) return;
                String export = f.getStr(exportColumn);
                if (!export.equals("出口")) return;
                String type = f.getStr(typeColumn);
                if (!(type.equals("一般贸易") || type.equals("来料加工装配贸易") || type.equals("进料加工贸易"))) return;
                //插入目标表
                records.add(f);
            });
            Db.batchSave(targetTable, records, 5000);
            //logger.info(originTable + "处理完成: " + start + "-" + (size + start));
            //start += size;
        }
    }

}
