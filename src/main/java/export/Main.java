package export;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        //2001年
        //数据库连接
        String dbHost = "10.50.5.14";
        String dbName = "db_hk_test";
        String dbUser = "user_hk";
        String dbPass = "itkhiyjedwsdcfjhkh345";

        //表列表
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        //注意12月的要手动修改字段名
        String targetTable = "2001_result_bymonth";

        //字段
        String exportColumn = "exp_or_imp";
        String quantityColumn = "quantity";
        String valueColumn = "value";
        String nameColumn = "company";
        String addrColumn = "paddr";
        String telColumn = "tel";
        String zipColumn = "zip";
        String typeColumn = "shipment";

        GroupMerge merge = new GroupMerge(dbHost,dbName,dbUser,dbPass);
        Arrays.asList(months).parallelStream().forEach(month -> {
            String origin = "2001-"+month;
            merge.runResultOnline(origin, targetTable, exportColumn, quantityColumn, valueColumn, nameColumn, addrColumn, telColumn, zipColumn, typeColumn);
        });

        StringBuilder sb = new StringBuilder();
        sb.append("create table `2001_result` (select `");
        sb.append(nameColumn);
        sb.append("`, `");
        sb.append(typeColumn);
        sb.append("`, SUM(`SUM(");
        sb.append(quantityColumn);
        sb.append(")`),SUM(`SUM(");
        sb.append(valueColumn);
        sb.append(")`),`");
        sb.append(addrColumn);
        sb.append("`,`");
        sb.append(telColumn);
        sb.append("`,`");
        sb.append(zipColumn);
        sb.append("` from `");
        sb.append(targetTable);
        sb.append("` group by `");
        sb.append(nameColumn);
        sb.append("`,`");
        sb.append(typeColumn);
        sb.append("` )");

        System.out.println(sb.toString());
        merge.runSql(sb.toString());

        //2002年
        /*参数设置*/
        //数据库连接
        /*String dbHost = "localhost";
        String dbName = "export";
        String dbUser = "root";
        String dbPass = "123456";

        //表列表
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String targetTable = "2003_result_bymonth";

        //字段
        String exportColumn = "进口或出口";
        String quantityColumn = "数量";
        String valueColumn = "金额";
        String nameColumn = "经营单位";
        String addrColumn = "单位地址";
        String telColumn = "电话";
        String zipColumn = "邮编";
        String typeColumn = "贸易方式";

        GroupMerge merge = new GroupMerge(dbHost,dbName,dbUser,dbPass);
        Arrays.asList(months).parallelStream().forEach(month -> {
            String origin = "data2003"+month;
            merge.runResult(origin, targetTable, exportColumn, quantityColumn, valueColumn, nameColumn, addrColumn, telColumn, zipColumn, typeColumn);
        });*/

        //2005年
        //数据库连接
//        String dbHost = "10.50.5.14";
//        String dbName = "db_hk_test";
//        String dbUser = "user_hk";
//        String dbPass = "itkhiyjedwsdcfjhkh345";
//
//        //表列表
//        //String[] months = { "10", "11"};
//        String[] months = { "12"};
//        //String[] months = {"01", "02", "03", "04", "05", "06"};
//        //String[] months = {"07", "08", "09"};
//
//        //注意12月的要手动修改字段名
//        String targetTable = "2005_result_bymonth";
//
//        //字段
//        String exportColumn = "exp_or_imp";
//        String quantityColumn = "quantity";
//        String valueColumn = "value";
//        String nameColumn = "company";
//        String addrColumn = "paddr";
//        String telColumn = "tel";
//        String zipColumn = "zip";
//        String typeColumn = "shipment";
//
//        GroupMerge merge = new GroupMerge(dbHost,dbName,dbUser,dbPass);
//        Arrays.asList(months).parallelStream().forEach(month -> {
//            String origin = "hg2005_"+month;
//            merge.runResultOnline(origin, targetTable, exportColumn, quantityColumn, valueColumn, nameColumn, addrColumn, telColumn, zipColumn, typeColumn);
//        });


        //2003年
//        String dbHost = "10.50.5.14";
//        String dbName = "db_hk_test";
//        String dbUser = "user_hk";
//        String dbPass = "itkhiyjedwsdcfjhkh345";
//
//        //表列表
//        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
//
//        String targetTable = "2003_result_bymonth";
//
//        //字段
//        String exportColumn = "进口或出口";
//        String quantityColumn = "数量";
//        String valueColumn = "金额";
//        String nameColumn = "经营单位";
//        String addrColumn = "单位地址";
//        String telColumn = "电话";
//        String zipColumn = "邮编";
//        String typeColumn = "贸易方式";
//
//        GroupMerge merge = new GroupMerge(dbHost,dbName,dbUser,dbPass);
//        Arrays.asList(months).parallelStream().forEach(month -> {
//            String origin = "data2003"+month;
//            merge.runResultOnline(origin, targetTable, exportColumn, quantityColumn, valueColumn, nameColumn, addrColumn, telColumn, zipColumn, typeColumn);
//        });
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("create table `2003_result` (select `");
//        sb.append(nameColumn);
//        sb.append("`, `");
//        sb.append(typeColumn);
//        sb.append("`, SUM(`SUM(");
//        sb.append(quantityColumn);
//        sb.append(")`),SUM(`SUM(");
//        sb.append(valueColumn);
//        sb.append(")`),`");
//        sb.append(addrColumn);
//        sb.append("`,`");
//        sb.append(telColumn);
//        sb.append("`,`");
//        sb.append(zipColumn);
//        sb.append("` from `");
//        sb.append(targetTable);
//        sb.append("` group by `");
//        sb.append(nameColumn);
//        sb.append("`,`");
//        sb.append(typeColumn);
//        sb.append("` )");
//
//        System.out.println(sb.toString());
//        merge.runSql(sb.toString());


        //2004年
//        String dbHost = "10.50.5.14";
//        String dbName = "db_hk_test";
//        String dbUser = "user_hk";
//        String dbPass = "itkhiyjedwsdcfjhkh345";
//
//        //表列表
//        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
//
//        String targetTable = "2004_result_bymonth";
//
//        //字段
//        String exportColumn = "进口或出口";
//        String quantityColumn = "数量";
//        String valueColumn = "金额";
//        String nameColumn = "经营单位";
//        String addrColumn = "单位地址";
//        String telColumn = "电话";
//        String zipColumn = "邮编";
//        String typeColumn = "贸易方式";
//
//        GroupMerge merge = new GroupMerge(dbHost,dbName,dbUser,dbPass);
//        Arrays.asList(months).parallelStream().forEach(month -> {
//            String origin = "data2004"+month;
//            merge.runResultOnline(origin, targetTable, exportColumn, quantityColumn, valueColumn, nameColumn, addrColumn, telColumn, zipColumn, typeColumn);
//        });
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("create table `2004_result` (select `");
//        sb.append(nameColumn);
//        sb.append("`, `");
//        sb.append(typeColumn);
//        sb.append("`, SUM(`SUM(");
//        sb.append(quantityColumn);
//        sb.append(")`),SUM(`SUM(");
//        sb.append(valueColumn);
//        sb.append(")`),`");
//        sb.append(addrColumn);
//        sb.append("`,`");
//        sb.append(telColumn);
//        sb.append("`,`");
//        sb.append(zipColumn);
//        sb.append("` from `");
//        sb.append(targetTable);
//        sb.append("` group by `");
//        sb.append(nameColumn);
//        sb.append("`,`");
//        sb.append(typeColumn);
//        sb.append("` )");
//
//        System.out.println(sb.toString());
//        merge.runSql(sb.toString());



        //2006年
//        String dbHost = "10.50.5.14";
//        String dbName = "db_hk_test";
//        String dbUser = "user_hk";
//        String dbPass = "itkhiyjedwsdcfjhkh345";
//
//        //表列表
//        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
//
//        String targetTable = "2006_result_bymonth";
//
//        //字段
//        String exportColumn = "进出口";
//        String quantityColumn = "数量";
//        String valueColumn = "金额";
//        String nameColumn = "企业名称";
//        String addrColumn = "地址";
//        String telColumn = "电话";
//        String zipColumn = "邮政编码";
//        String typeColumn = "贸易方式";
//
//        GroupMerge merge = new GroupMerge(dbHost,dbName,dbUser,dbPass);
//        Arrays.asList(months).parallelStream().forEach(month -> {
//            String origin = "2006"+month;
//            merge.runResultOnline(origin, targetTable, exportColumn, quantityColumn, valueColumn, nameColumn, addrColumn, telColumn, zipColumn, typeColumn);
//        });
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("create table `2006_result` (select `");
//        sb.append(nameColumn);
//        sb.append("`, `");
//        sb.append(typeColumn);
//        sb.append("`, SUM(`SUM(");
//        sb.append(quantityColumn);
//        sb.append(")`),SUM(`SUM(");
//        sb.append(valueColumn);
//        sb.append(")`),`");
//        sb.append(addrColumn);
//        sb.append("`,`");
//        sb.append(telColumn);
//        sb.append("`,`");
//        sb.append(zipColumn);
//        sb.append("` from `");
//        sb.append(targetTable);
//        sb.append("` group by `");
//        sb.append(nameColumn);
//        sb.append("`,`");
//        sb.append(typeColumn);
//        sb.append("` )");
//
//        System.out.println(sb.toString());
//        merge.runSql(sb.toString());

    }


}
