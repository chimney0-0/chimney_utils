package export;

import com.jfinal.plugin.activerecord.Db;
import org.apache.log4j.Logger;
import utils.database.JfinalConfig;

public class GroupMerge {
    private Logger logger = Logger.getLogger(GroupMerge.class);

    public GroupMerge(String dbHost, String dbName, String dbUser, String dbPass) {
        try {
            JfinalConfig.init("jdbc:mysql://" + dbHost + ":3306/" + dbName + "?characterEncoding=utf-8&useSSL=false&serverTimezone=GMT", dbUser, dbPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runResult(String originTable, String targetTable,
                          String exportColumn, String quantityColumn, String valueColumn, String nameColumn, String addrColumn, String telColumn, String zipColumn, String typeColumn) {

        //添加索引
        StringBuffer sbIndex = new StringBuffer();
        sbIndex.append("alter table `");
        sbIndex.append(originTable);
        sbIndex.append("` add index `export` (`");
        sbIndex.append(exportColumn);
        sbIndex.append("`), add index `type` (`");
        sbIndex.append(typeColumn);
        sbIndex.append("`),add index `pname` (`");
        sbIndex.append(nameColumn);
        sbIndex.append("`), add index `record` (`");
        sbIndex.append(nameColumn);
        sbIndex.append("` , `");
        sbIndex.append(typeColumn);
        sbIndex.append("`)");
        logger.info(sbIndex.toString());
        try {
            Db.update(sbIndex.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info(originTable + "添加索引完成");

        //创建目标表
        StringBuilder sbCreate = new StringBuilder();
        sbCreate.append("create table if not exists `");
        sbCreate.append(targetTable);
        sbCreate.append("`");
        sbCreate.append("(\n" +
                "  `source` varchar(20) DEFAULT NULL,\n" +
                "  `经营单位` varchar(200) DEFAULT NULL,\n" +
                "  `贸易方式` varchar(240) DEFAULT NULL,\n" +
                "  `SUM(数量)` int(20) DEFAULT NULL,\n" +
                "  `SUM(金额)` double DEFAULT NULL, \n" +
                "  `单位地址` varchar(255) DEFAULT NULL,\n" +
                "  `电话` varchar(40) DEFAULT NULL,\n" +
                "  `邮编` varchar(12) DEFAULT NULL\n" +
                ")");
        Db.update(sbCreate.toString());

        //插入目标表
        StringBuilder sbInsert = new StringBuilder();
        sbInsert.append("insert into `");
        sbInsert.append(targetTable);
        sbInsert.append("` ( select '");
        sbInsert.append(originTable);
        sbInsert.append("', `");
        sbInsert.append(nameColumn);
        sbInsert.append("`, `");
        sbInsert.append(typeColumn);
        sbInsert.append("`, SUM(`");
        sbInsert.append(quantityColumn);
        sbInsert.append("`), SUM(`");
        sbInsert.append(valueColumn);
        sbInsert.append("`), any_value(`");
        sbInsert.append(addrColumn);
        sbInsert.append("`), any_value(`");
        sbInsert.append(telColumn);
        sbInsert.append("`), any_value(`");
        sbInsert.append(zipColumn);
        sbInsert.append("`) from `");
        sbInsert.append(originTable);
        sbInsert.append("`");
        sbInsert.append(" where `");
        sbInsert.append(exportColumn);
        sbInsert.append("` = '出口' and `");
        sbInsert.append(typeColumn);
        sbInsert.append("` in ('一般贸易','来料加工装配贸易','进料加工贸易') group by `");
        sbInsert.append(nameColumn);
        sbInsert.append("`, `");
        sbInsert.append(typeColumn);
        sbInsert.append("`)");
        logger.info(sbInsert.toString());
        Db.update(sbInsert.toString());
        logger.info(originTable + "统计数据插入" + targetTable + "完成");


    }


    public void runResultOnline(String originTable, String targetTable,
                          String exportColumn, String quantityColumn, String valueColumn, String nameColumn, String addrColumn, String telColumn, String zipColumn, String typeColumn) {

        //添加索引
        StringBuffer sbIndex = new StringBuffer();
        sbIndex.append("alter table `");
        sbIndex.append(originTable);
        sbIndex.append("` add index `export` (`");
        sbIndex.append(exportColumn);
        sbIndex.append("`), add index `type` (`");
        sbIndex.append(typeColumn);
        sbIndex.append("`),add index `pname` (`");
        sbIndex.append(nameColumn);
        sbIndex.append("`), add index `record` (`");
        sbIndex.append(nameColumn);
        sbIndex.append("` , `");
        sbIndex.append(typeColumn);
        sbIndex.append("`)");
        logger.info(sbIndex.toString());
        try {
            Db.update(sbIndex.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info(originTable + "添加索引完成");

        //创建目标表
//        StringBuilder sbCreate = new StringBuilder();
//        sbCreate.append("create table if not exists `");
//        sbCreate.append(targetTable);
//        sbCreate.append("`");
//        sbCreate.append("(\n" +
//                "  `source` varchar(20) DEFAULT NULL,\n" +
//                "  `company` varchar(200) DEFAULT NULL,\n" +
//                "  `shipment` varchar(240) DEFAULT NULL,\n" +
//                "  `SUM(quantity)` decimal(32,0) DEFAULT NULL,\n" +
//                "  `SUM(value)` decimal(32,0) DEFAULT NULL, \n" +
//                "  `addr` varchar(255) DEFAULT NULL,\n" +
//                "  `tel` varchar(40) DEFAULT NULL,\n" +
//                "  `zip` varchar(12) DEFAULT NULL\n" +
//                ")");
//        Db.update(sbCreate.toString());

        //插入目标表
        StringBuilder sbInsert = new StringBuilder();
        sbInsert.append("insert into `");
        sbInsert.append(targetTable);
        sbInsert.append("` ( select '");
        sbInsert.append(originTable);
        sbInsert.append("', `");
        sbInsert.append(nameColumn);
        sbInsert.append("`, `");
        sbInsert.append(typeColumn);
        sbInsert.append("`, SUM(`");
        sbInsert.append(quantityColumn);
        sbInsert.append("`), SUM(`");
        sbInsert.append(valueColumn);
        sbInsert.append("`), `");
        sbInsert.append(addrColumn);
        sbInsert.append("`,`");
        sbInsert.append(telColumn);
        sbInsert.append("`, `");
        sbInsert.append(zipColumn);
        sbInsert.append("` from `");
        sbInsert.append(originTable);
        sbInsert.append("`");
        sbInsert.append(" where `");
        sbInsert.append(exportColumn);
        sbInsert.append("` = '出口' and `");
        sbInsert.append(typeColumn);
        sbInsert.append("` in ('一般贸易','来料加工装配贸易','进料加工贸易') group by `");
        sbInsert.append(nameColumn);
        sbInsert.append("`, `");
        sbInsert.append(typeColumn);
        sbInsert.append("`)");
        logger.info(sbInsert.toString());
        Db.update(sbInsert.toString());
        logger.info(originTable + "统计数据插入" + targetTable + "完成");


    }

    public void runSql(String sql){
        Db.update(sql);
        logger.info("执行完成："+sql);
    }

}
