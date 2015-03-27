package neta.crawler;

import java.io.IOException;
import java.util.List;

import neta.crawler.base.ConnectionManager;
import neta.crawler.process.dao.ArticleDao;
import neta.crawler.process.dto.Article;
import neta.crawler.process.logic.AgohigeCrawler;
import neta.crawler.process.logic.VowtvCrawler;
import neta.crawler.process.logic.common.JsoupUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

public class WorkTezt {
	final ArticleDao dao = new ArticleDao();
	
	@Test
	public void test() throws Exception{
		new VowtvCrawler().collect();
		new AgohigeCrawler().collect();
	}
		
	@Test
	public void replace() throws Exception{
		ConnectionManager.prepareDataSource();
		
		List<Article> articles = dao.selectAll();
		for (Article a: articles){
			if (a.url.indexOf("christmas1224") !=-1){
				Article result = collectSinglePage(a.url);
				result.id = a.id;
				updateHtmlFromList(result);
			}else{
				Article result  = vcollectSinglePage(a.url);
				result.id = a.id;
				updateHtmlFromList(result);
			}
		}
		
		ConnectionManager.commitAndClose();
	}
	
	private void updateHtmlFromList(Article a){
		dao.updateHtmlBody(a.id, a.htmlBody);
	}
	
	
	private Article collectSinglePage(String url) throws IOException {
		Document doc = JsoupUtils.formatDocument(Jsoup.connect(url).get());
		
		final Element articleHeader = doc.getElementsByClass("article-header").first();
		final Element articleBody = doc.getElementsByClass("article-body").first();
	
		doc.body().children().remove();
		doc.body().appendChild(articleHeader).appendChild(articleBody);
		doc.getElementsByTag("img").addClass("img-responsive");
		
		Article a = new Article();	
		a.htmlBody = doc.html();

		return a;
	}
	
	private Article vcollectSinglePage(String url) throws IOException {
		Document doc =JsoupUtils.formatDocument(Jsoup.connect(url).get());

		final Element articleHeader = doc.getElementsByClass("entry-header").first();
		final Element articleBody = doc.getElementsByClass("entry-content").first();

		doc.body().children().remove();
		doc.body().appendChild(articleHeader).appendChild(articleBody);
		
		doc.getElementsByTag("img").addClass("img-responsive");

		Article a = new Article();
		a.htmlBody = doc.html();

		return a;
	}
	
}
