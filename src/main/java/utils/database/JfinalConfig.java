package utils.database;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;

public class JfinalConfig {

    public static void init(String url, String user, String pass) {
        DruidPlugin druidPlugin = new DruidPlugin(url, user, pass);
        druidPlugin.start();
        ActiveRecordPlugin recordPlugin = new ActiveRecordPlugin(druidPlugin);
        recordPlugin.start();
    }


}
