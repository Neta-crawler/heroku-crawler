package neta.crawler;

import neta.crawler.base.ConnectionManager;
import neta.crawler.process.CrawlerProcess;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WEBクローラーのMainクラス。<br>
 * Herokuのスケジューラから実行される想定。
 *
 * @author izumi_j
 *
 */
public final class Batch {
	private static final Logger logger = LoggerFactory.getLogger(Batch.class);

	private Batch() {
	}

	public static void main(String[] args) {
		final StopWatch sw = new StopWatch();
		sw.start();
		try {
			ConnectionManager.prepareDataSource();
			new CrawlerProcess().execute();
			ConnectionManager.commitAndClose();
		} catch (Throwable e) {
			ConnectionManager.rollbackAndClose();
			logger.error("Failed!", e);
		} finally {
			ConnectionManager.disposeDataSource();
			sw.stop();
			logger.info("Batch finished. time = {}s", Math.floor(sw.getTime() / 1000));
		}
	}

}
