package neta.crawler.process.logic;

import java.io.IOException;
import java.net.MalformedURLException;
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

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 
 * @author izumikawa_t
 *
 */
public final class SpotlightCrawler implements Crawler{
	private static final Logger logger = LoggerFactory.getLogger(SpotlightCrawler.class);

	private static final String URL = "http://spotlight-media.jp";
	private static final Category category = Category.FUNNY;
	
	@Override
    public List<Article> collect() throws IOException {		
		final Document doc = Jsoup.parse(getAjaxPage(URL+"/item/"));
		logger.trace(doc.html());

		final List<Article> articles = new ArrayList<>();

		// 個別記事ページのaタグから辿る
		final Elements els = doc.select("a.p-pickup__image");
		for (Element el : els) {
			final Article a = collectSinglePage(URL + el.attr("href"));
			articles.add(a);
		}

		return articles;
	}
	
	private String getAjaxPage(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		WebClient webClient = new WebClient( BrowserVersion.FIREFOX_24);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.waitForBackgroundJavaScript(10000);
		webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getCookieManager().setCookiesEnabled(true);
		HtmlPage page = webClient.getPage(url);
		return page.asXml();
	}
	
	private Article collectSinglePage(String url) throws IOException{
		logger.trace("URL = {}", url);
		final Document doc = JsoupUtils.formatDocument(Jsoup.connect(url).get());
		
		final Element articleBody = doc.getElementsByAttributeValue("itemprop", "articleBody").first();
		final Element articleTitle = doc.getElementsByTag("title").first();
		
		Element contentTitle = Document.createShell(url).createElement("h2");	
		contentTitle.text(articleTitle.text());
		
		doc.body().children().remove();
		doc.body().appendChild(contentTitle).appendChild(articleBody);

		logger.trace(doc.html());

		Article a = new Article();
		a.url = url;
		a.title = articleTitle.text();
		a.date = new java.util.Date();
		a.htmlBody = doc.html();
		a.category = category.getName();
		try{
			a.pageImage = doc.getElementsByTag("img").first().attr("src");
		}catch (Exception e){
			//握りつぶす
			a.pageImage = "";
		}

		return a;
	}
}
