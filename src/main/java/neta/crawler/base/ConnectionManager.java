package neta.crawler.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import neta.crawler.HerokuEnvs;
import neta.crawler.HerokuEnvs.DatabaseInfo;

import org.apache.commons.dbutils.DbUtils;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Heroku的にConnectionを取得するクラス。
 *
 * @author izumi_j
 *
 */
public final class ConnectionManager {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

	private static final HerokuEnvs.DatabaseInfo DB_INFO = HerokuEnvs.getDatabaseInfo();

	private static DataSource ds;

	synchronized public static void prepareDataSource() {
		try {
			final PoolProperties p = new PoolProperties();

			final DatabaseInfo dbInfo = HerokuEnvs.getDatabaseInfo();
			p.setUrl(dbInfo.url);
			p.setUsername(dbInfo.username);
			p.setPassword(dbInfo.password);

			p.setDriverClassName(org.postgresql.Driver.class.getName());
			p.setDefaultAutoCommit(true);
			p.setInitialSize(1);
			p.setMaxActive(1);
			p.setMaxIdle(1);
			p.setMinIdle(1);

			p.setValidationQuery("SELECT 1");
			p.setTestOnBorrow(true);

			p.setJdbcInterceptors(LoggingInterceptor.class.getName());

			ds = new DataSource(p);

		} catch (Exception e) {
			logger.error("Failed to create dataSource!", e);
			throw new IllegalStateException(e);
		}
	}

	synchronized public static void disposeDataSource() {
		ds.close(true);
	}

	public static class LoggingInterceptor extends JdbcInterceptor {
		@Override
		public void reset(ConnectionPool parent, PooledConnection con) {
			logger.trace("called #reset");
		}

		@Override
		public void disconnected(ConnectionPool parent, PooledConnection con, boolean finalizing) {
			logger.trace("called #disconnected");
		}
	}

	private static final ThreadLocal<Connection> HOLDER = new ThreadLocal<Connection>() {
		@Override
		protected Connection initialValue() {
			return null;
		}
	};

	private static Connection getConnectionInternal() {
		try {
			if (ds == null) {
				Connection conn = DriverManager.getConnection(DB_INFO.url, DB_INFO.username, DB_INFO.password);
				conn.setAutoCommit(false);
				return conn;
			} else {
				return ds.getConnection();
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @return connection
	 */
	public static Connection getConnection() {
		Connection conn = HOLDER.get();
		if (conn == null) {
			conn = getConnectionInternal();
			HOLDER.set(conn);
		}
		return conn;
	}

	public static void close() {
		Connection conn = HOLDER.get();
		if (conn != null) {
			DbUtils.closeQuietly(conn);
		}
		HOLDER.remove();
	}

	public static void commitAndClose() {
		Connection conn = HOLDER.get();
		if (conn != null) {
			logger.debug("Commit and close Connection.");
			DbUtils.commitAndCloseQuietly(conn);
		}
		HOLDER.remove();
	}

	public static void rollbackAndClose() {
		Connection conn = HOLDER.get();
		if (conn != null) {
			logger.debug("Rollback and close Connection.");
			DbUtils.rollbackAndCloseQuietly(conn);
		}
		HOLDER.remove();
	}
}
