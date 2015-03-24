package neta.crawler.process;

import java.io.IOException;
import java.util.List;

import neta.crawler.process.dao.ArticleDao;
import neta.crawler.process.dto.Article;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * クロール処理。
 *
 * @author izumi_j
 *
 */
public final class CrawlerProcess {
	private static final Logger logger = LoggerFactory.getLogger(CrawlerProcess.class);

	public void execute() {

		final ArticleDao dao = new ArticleDao();

		for (Crawler crawler : Crawlers.implementations()) {
			final StopWatch sw = new StopWatch();
			sw.start();
			logger.debug("Start! {}", crawler.getClass().getSimpleName());

			try {
				final List<Article> articles = crawler.collect();

				if (CollectionUtils.isEmpty(articles)) {
					continue;
				}

				for (Article a : articles) {
					if (dao.selectByUrl(a.url) == null) {
						dao.insert(a);
					}
				}

			} catch (IOException e) {
				logger.warn("Failed to collect!", e);
			}

			sw.stop();
			logger.debug("Finish {}! time = {}ms", crawler.getClass().getSimpleName(), sw.getTime());
		}
	}
}
