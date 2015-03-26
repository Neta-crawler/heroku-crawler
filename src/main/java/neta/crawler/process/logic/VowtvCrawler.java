package neta.crawler.process.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import neta.crawler.process.Crawler;
import neta.crawler.process.dto.Article;
import neta.crawler.process.logic.common.JsoupUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VowtvCrawler implements Crawler {
	private static final Logger logger = LoggerFactory.getLogger(VowtvCrawler.class);
	private static final String URL = "http://vowtv.jp/";

	@Override
    public List<Article> collect() throws IOException {
	    final Document doc = Jsoup.connect(URL).get();	    
	    final List<Article> articles = new ArrayList<Article>();
	    
	    //個別記事ページ = h2のクラスentry-title下
	    final Elements els = doc.select("h2.entry-title > a[href]");
	    for (Element el : els) {
			final Article a = collectSinglePage(el.attr("href"));
			articles.add(a);
		}
	    
	    return articles;
    }
	
	private Article collectSinglePage(String url) throws IOException {
		logger.trace("URL = {}", url);
		final Document doc = JsoupUtils.formatDocument(Jsoup.connect(url).get());

		final Element articleHeader = doc.getElementsByClass("entry-header").first();
		final Element articleBody = doc.getElementsByClass("entry-content").first();

		doc.body().children().remove();
		doc.body().appendChild(articleHeader).appendChild(articleBody);

		logger.trace(doc.html());

		Article a = new Article();
		a.url = url;
		a.title = articleHeader.select(".entry-title").text();
		a.date = new java.util.Date();
		a.htmlBody = doc.html();

		return a;
	}
	
}
