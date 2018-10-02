package utils;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {

	private static Logger log = LoggerFactory.getLogger(DBUtil.class);
	
	public static Connection getConnection() throws URISyntaxException, SQLException {
		return getConn();
	}
	
	private static Connection getConn() throws URISyntaxException, SQLException {

		String dbUrl = System.getenv("JDBC_DATABASE_URL");
		log.info("Getting the database connection at {}", dbUrl);
		return DriverManager.getConnection(dbUrl);

	}
}
