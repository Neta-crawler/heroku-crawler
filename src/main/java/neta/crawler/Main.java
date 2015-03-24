package neta.crawler;

import neta.crawler.base.ConnectionManager;
import neta.crawler.servlet.NetaCrawlerServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Heroku的にJettyを起動するMainクラス。
 *
 * @author izumi_j
 *
 */
public final class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		ConnectionManager.prepareDataSource();

		final int port = HerokuEnvs.getPort();
		logger.info("PORT from env = {}", port);

		final Server server = new Server(port);

		final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.addServlet(new ServletHolder(new NetaCrawlerServlet()), "/");

		server.setHandler(servletContextHandler);
		server.start();
		logger.info("Server started.");
		server.join();
	}

}
