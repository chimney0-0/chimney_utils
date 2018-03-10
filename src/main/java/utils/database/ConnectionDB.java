package utils.database;

import com.google.common.base.Joiner;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库连接类 说明:封装了 无参，有参，存储过程的调用
 * 
 * @author iflytek
 * 
 */
public class ConnectionDB {

	private static Logger log = Logger.getLogger(ConnectionDB.class);
	private static Map<String, String[]> dbInfoMap = new HashMap<String, String[]>() {
		{

		}

	};

	public static String JINHUA_USER = "db_jinhua_user";
	public static String HOSPITAL = "db_hospital";
	public static String HOSPITAL_LOCAL = "db_hospital_local";
	public static String BITEST_DEV = "bitest_dev";
	public static String BITEST = "bitest";
	public static String TEST1 = "test1";
	public static String TEST2 = "TEST2";

	public static String DB_QZ_REPLACE_AD_ONLINE = "db_qz_replace_ad_online";
	public static String DB_QZ_REPLACE_AD_LOCAL = "db_qz_replace_ad_local";

	public static String DB_WEB_WAREHOUSE = "db_web_warehouse";
	public static String DB_PLATFORM = "db_platform";

	public static String DB_WEB_WAREHOUSE_TEST = "db_web_warehouse_test";
	public static String DB_PLATFORM_TEST = "db_platform_test";

	public static String DB_VLAN = "db_vlan";
	public static String DB_CAIJI = "db_caiji";
	public static String DB_CAIJI_BAIDU_SEARCH = "db_caiji_baidu_search";
	public static String DB_CAIJI_GSXT = "db_caiji_gsxt";
	public static String DB_MIAO = "db_miao";

	public static String DB_MIAO_HAIGUAN = "db_miao_haiguan";

	/**
	 * 数据库驱动类名称
	 */
	private String DRIVER = "com.mysql.jdbc.Driver";

	/**
	 * 连接字符串
	 */
	private String URL = null;
	/**
	 * 用户名
	 */
	private String USERNAME = null;

	/**
	 * 密码
	 */
	private String PASSWORD = null;

	/**
	 * 连接字符串
	 */
	// private static final String URLSTR =
	// "jdbc:mysql://192.168.222.113:3306/db_qz";
	// /**
	// * 用户名
	// */
	// private static final String USERNAME = "root";
	//
	// /**
	// * 密码
	// */
	// private static final String USERPASSWORD = "123456";

	/**
	 * 创建数据库连接对象
	 */
	private Connection connnection = null;

	/**
	 * 创建PreparedStatement对象
	 */
	private PreparedStatement preparedStatement = null;

	/**
	 * 创建Statement对象
	 */
	private Statement statement = null;

	/**
	 * 创建CallableStatement对象
	 */
	private CallableStatement callableStatement = null;

	/**
	 * 创建结果集对象
	 */
	private ResultSet resultSet = null;

	// static {
	// try {
	// // 加载数据库驱动程序
	// Class.forName(DRIVER);
	// } catch (ClassNotFoundException e) {
	// System.out.println("加载驱动错误");
	// System.out.println(e.getMessage());
	// }
	// }

	private String type;

	public ConnectionDB(String type) {
		this.type = type;
	}

	public ConnectionDB(Connection connnection) {
		this.connnection = connnection;
	}

	public ConnectionDB(String URL, String USERNAME, String PASSWORD) {
		this.URL = URL;
		this.USERNAME = USERNAME;
		this.PASSWORD = PASSWORD;
	}

	public ConnectionDB(String URL, String USERNAME, String PASSWORD, String DRIVER) {
		this.URL = URL;
		this.USERNAME = USERNAME;
		this.PASSWORD = PASSWORD;
		this.DRIVER = DRIVER;

	}

	/**
	 * 建立数据库连接
	 * 
	 * @return 数据库连接
	 */
	public Connection getConnection() {

		try {
			if (connnection == null || connnection.isClosed() == true) {

				Class.forName(DRIVER);
				// 获取连接
				connnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connnection;
	}

	/**
	 * insert update delete SQL语句的执行的统一方法
	 * 
	 * @param sql
	 *            SQL语句
	 * @param params
	 *            参数数组，若没有参数则为null
	 * @return 受影响的行数
	 */
	public int executeUpdate(String sql, Object[] params) {
		// 受影响的行数
		int affectedLine = 0;

		try {
			// 获得连接
			connnection = this.getConnection();
			// 调用SQL
			preparedStatement = connnection.prepareStatement(sql);

			// 参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 执行
			affectedLine = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// 释放资源
			// closeAll();
		}
		return affectedLine;
	}

	/**
	 * insert update delete SQL语句的执行的统一方法
	 * 
	 * @param sql
	 *            SQL语句
	 * @return 受影响的行数
	 */
	public int executeUpdate(String sql) {
		// 受影响的行数
		int affectedLine = 0;

		try {
			// 获得连接
			connnection = this.getConnection();
			// 调用SQL
			statement = connnection.createStatement();

			// 执行
			affectedLine = statement.executeUpdate(sql);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// 释放资源
			// closeAll();
		}
		return affectedLine;
	}

	/**
	 * SQL 查询将查询结果直接放入ResultSet中
	 * 
	 * @param sql
	 *            SQL语句
	 * @param params
	 *            参数数组，若没有参数则为null
	 * @return 结果集
	 */
	public ResultSet executeQueryRS(String sql, Object[] params) {
		try {

			// 获得连接
			connnection = this.getConnection();

			// 调用SQL
			preparedStatement = connnection.prepareStatement(sql);

			if (this.DRIVER.contains("mysql")) {
				preparedStatement.setFetchSize(Integer.MIN_VALUE);
			} else {
				preparedStatement.setFetchSize(100);
			}

			// 参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 执行
			resultSet = preparedStatement.executeQuery();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return resultSet;
	}

	

	/**
	 * SQL 查询将查询结果：一行一列
	 * 
	 * @param sql
	 *            SQL语句
	 * @param params
	 *            参数数组，若没有参数则为null
	 * @return 结果集
	 */
	public Object executeQuerySingle(String sql, Object[] params) {
		Object object = null;
		try {
			// 获得连接
			connnection = this.getConnection();

			// 调用SQL
			preparedStatement = connnection.prepareStatement(sql);

			// 参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 执行
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				object = resultSet.getObject(1);
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			closeAll();
		}

		return object;
	}

	/**
	 * 获取结果集，并将结果放在List中
	 * 
	 * @param sql
	 *            SQL语句
	 * @return List 结果集
	 */
	public List<Map<String, Object>> excuteQuery(String sql, Object[] params) {

		// 执行SQL获得结果集
		ResultSet rs = executeQueryRS(sql, params);

		// 创建ResultSetMetaData对象
		ResultSetMetaData rsmd = null;

		// 结果集列数
		int columnCount = 0;
		try {
			rsmd = rs.getMetaData();

			// 获得结果集列数
			columnCount = rsmd.getColumnCount();
		} catch (SQLException e1) {
			System.out.println(e1.getMessage());
		}

		// 创建List
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			// 将ResultSet的结果保存到List中
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					map.put(rsmd.getColumnLabel(i), rs.getObject(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// 关闭所有资源
			// closeAll();
			try {

				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return list;
	}

	public void insert(ConnectionDB db, List<String> columnsList, List<Object> paramsList, String tableName) {
		log.info(" insert start");
		db.insert(columnsList.toArray(new String[columnsList.size()]), paramsList.toArray(), tableName);

		paramsList.clear();
		log.info(" insert end");
	}

	public void insert(String[] colums, Object[] params, String tableName) {

		StringBuffer sql = new StringBuffer();
		// ignore
		sql.append(" insert  ignore   into `" + tableName + "`  ( `" + Joiner.on("`,`").useForNull("").join(colums)
				+ "`  )   values  ");

		List<String> recordList = new ArrayList<>();
		List<String> paramList = new ArrayList<>();

		for (int i = 1; i <= params.length; i++) {

			paramList.add("?");

			if (i >= colums.length && i % colums.length == 0) {
				recordList.add("(" + Joiner.on(",").join(paramList) + ")");
				paramList.clear();
			}

		}

		sql.append(Joiner.on(",").join(recordList));

		// System.out.println(sql .toString());

		int result = this.executeUpdate(sql.toString(), params);

		log.info(tableName + "新增:" + result);

		this.closeAll();

	}

	/**
	 * 存储过程带有一个输出参数的方法
	 * 
	 * @param sql
	 *            存储过程语句
	 * @param params
	 *            参数数组
	 * @param outParamPos
	 *            输出参数位置
	 * @param SqlType
	 *            输出参数类型
	 * @return 输出参数的值
	 */
	public Object excuteQuery(String sql, Object[] params, int outParamPos, int SqlType) {
		Object object = null;
		connnection = this.getConnection();
		try {
			// 调用存储过程
			callableStatement = connnection.prepareCall(sql);

			// 给参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					callableStatement.setObject(i + 1, params[i]);
				}
			}

			// 注册输出参数
			callableStatement.registerOutParameter(outParamPos, SqlType);

			// 执行
			callableStatement.execute();

			// 得到输出参数
			object = callableStatement.getObject(outParamPos);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// 释放资源
			closeAll();
		}

		return object;
	}

	/**
	 * 关闭所有资源
	 */
	public void closeAll() {
		// 关闭结果集对象
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		// 关闭PreparedStatement对象
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		// 关闭PreparedStatement对象
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		// 关闭CallableStatement 对象
		if (callableStatement != null) {
			try {
				callableStatement.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		// 关闭Connection 对象
		if (connnection != null) {
			try {
				connnection.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void main(String[] args) throws SQLException {
		// ConnectionDB db = new
		// ConnectionDB("jdbc:mysql://172.16.40.125:3306/db_court_judgement?useUnicode=true&characterEncoding=utf8","root","SXAD13579@$^*)");
		// ConnectionDB db = new
		// ConnectionDB("jdbc:oracle:thin:@130.36.57.40:1521:ORAJH","HZYS","123456","oracle.jdbc.driver.OracleDriver");

		ConnectionDB db = new ConnectionDB("jdbc:oracle:thin:@192.168.222.27:1521:orcl", "TEST", "SXAD",
				"oracle.jdbc.driver.OracleDriver");

		// String[] columns = { "id", "name" };
		// String[] values = { "1", "a", "2", "b" };
		// db.insert(columns, values, "test");

		// ResultSet resultSet = db.executeQueryRS("select * from
		// judgement_ZSCQ", null);

		ResultSet resultSet = db.executeQueryRS("select * from test.abc", null);

		while (resultSet.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			System.out.println(resultSet.getObject(1));
		}

	}
}