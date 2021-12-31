package idv.app.database;

import idv.common.file.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class WikiDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        Properties properties = FileUtil.getProRelative("jdbc.properties");
        List<String> tables = new ArrayList<>();
        FileUtil.readFile(new File("tablelist.properties"), tables);
        try {
            mainFunction(properties.getProperty("jdbc.db"), properties.getProperty("jdbc.url"),
                    properties.getProperty("jdbc.user"), properties.getProperty("jdbc.pwd"), tables, "/Users/chimney/out.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成数据库信息的 git 模板
     */
    private static ConnectionDB db;

    /**
     * @param dbName ip user password 数据库的相关信息，filName 生成文件的位置
     * @author sunxinli
     */
    public static void mainFunction(String dbName, String ip, String user, String password, List<String> tables, String fileName)
            throws IOException {
        db = new ConnectionDB("jdbc:mysql://" + ip + ":3306/" + dbName + "?useUnicode=true&characterEncoding=utf8",
                user, password);

        File f = new File(fileName);

        // 输出参数，并修改模板中的部分内容
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));


        for (String table : tables) {

            String comment = getCommentsOfTable(table);
            System.out.println("- " + table + " " + comment);
            System.out.println();
            System.out.println("|名称|描述|类型|");
            System.out.println("|:----    |:-------    |:--- |");
            output.write("- " + table + " " + comment);
            output.newLine();
            output.newLine();
            output.write("|名称|描述|类型|");
            output.newLine();
            output.write("|:----    |:-------    |:--- |");
            output.newLine();


//                output.write("# "+comment + "\r\n");
//                output.write("### "+table + "\r\n");
//                output.write(lineTxt + "\r\n");
//                output.write(lists + "\r\n");

            String sql = "SELECT  table_name 表名,COLUMN_NAME 列名,  COLUMN_TYPE 数据类型,  DATA_TYPE 字段类型,  "
                    + "CHARACTER_MAXIMUM_LENGTH 长度,  IS_NULLABLE 是否为空,  COLUMN_DEFAULT 默认值,  COLUMN_COMMENT 备注  "
                    + "FROM   INFORMATION_SCHEMA.COLUMNS  where  table_schema ='" + dbName + "' and table_name = '" + table
                    + "'";
            List<Map<String, Object>> rs = db.excuteQuery(sql, null);
            for (Map<String, Object> r : rs) {

//                String[] line = {table, comment, r.get("列名").toString(),
//                        r.get("数据类型").toString(), r.get("备注").toString()};
//                for (String cell : line) {
//                    System.out.print(cell + " ");
//                }
//                System.out.println();
//                    String text = lineTxt;
//                    text = text.replace("备注", r.get("备注").toString());
//                    text = text.replace("列名", r.get("列名").toString());
//                    text = text.replace("字段类型", r.get("字段类型").toString());
//                    text = text.replace("是否为空", r.get("是否为空").toString());
//                    System.out.println(text);
//
//                    output.write(text + "\r\n");
                System.out.print("|");
                System.out.print(r.get("列名").toString());
                System.out.print("|");
                System.out.print(r.get("备注").toString());
                System.out.print("|");
                System.out.print(r.get("数据类型").toString());
                System.out.print("|");

                System.out.println();

                output.write("|"+r.get("列名").toString()+"|"+r.get("备注").toString()+"|"+r.get("数据类型").toString()+"|");
                output.newLine();


            }
            System.out.println();
            output.newLine();
        }
//            output.write(lineTxt + "\r\n");
            output.close();

    }

    /**
     * 获取表格注释信息
     */
    public static String getCommentsOfTable(String tableName) {

        List<Map<String, Object>> x = db.excuteQuery("show  create  table " + tableName, null);

        int index = x.get(0).get("Create Table").toString().indexOf("COMMENT=");

        if (index < 0) {
            // 表格没有注释信息
            return "";
        }
        String comment = x.get(0).get("Create Table").toString().substring(index + 9);

        comment = comment.substring(0, comment.length() - 1);

        return comment;
    }

    /**
     * 获取数据库下的所有表格
     */
//    public static List<String> getTableNames(String dbName) {
//        // 获取数据库下所有表格
//        String sql = "SHOW TABLES";
//
//        List<String> tbs = new ArrayList<String>();
//
//        List<Map<String, Object>> rs = db.excuteQuery(sql, null);
//
//        for (Map<String, Object> r : rs) {
//
//            for (String key : r.keySet()) {
//                tbs.add(r.get(key).toString());
//            }
//        }
//        return tbs;
//    }


}
