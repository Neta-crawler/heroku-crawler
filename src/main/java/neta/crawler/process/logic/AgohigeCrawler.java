package neta.crawler.process.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import neta.crawler.process.Crawler;
import neta.crawler.process.dto.Article;
import neta.crawler.process.dto.enumelate.Category;
import neta.crawler.process.logic.common.JsoupUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author izumi_j
 *
 */
public final class AgohigeCrawler implements Crawler {
	private static final Logger logger = LoggerFactory.getLogger(AgohigeCrawler.class);

	private static final String URL = "http://blog.livedoor.jp/christmas1224/";
	private static final Category category = Category.FUNNY;

	@Override
	public List<Article> collect() throws IOException {
		final Document doc = Jsoup.connect(URL).get();

		final List<Article> articles = new ArrayList<>();

		// 個別記事ページのaタグから辿る
		final Elements els = doc.select("a[title=個別記事ページへ]");
		for (Element el : els) {
			final Article a = collectSinglePage(el.attr("href"));
			articles.add(a);
		}

		return articles;
	}

	private Article collectSinglePage(String url) throws IOException {
		logger.trace("URL = {}", url);
		final Document doc = JsoupUtils.formatDocument(Jsoup.connect(url).get());
		
		final Element articleHeader = doc.getElementsByClass("article-header").first();
		final Element articleBody = doc.getElementsByClass("article-body").first();
		
		doc.body().children().remove();
		doc.body().appendChild(articleHeader).appendChild(articleBody);

		logger.trace(doc.html());

		Article a = new Article();
		a.url = url;
		a.title = articleHeader.select(".article-title").first().children().first().text();
		a.date = new java.util.Date();
		a.htmlBody = doc.html();
		a.category = category.getName();

		return a;
	}
}
