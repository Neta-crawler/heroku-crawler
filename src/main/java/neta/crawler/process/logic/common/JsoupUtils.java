package neta.crawler.process.logic.common;

import org.jsoup.nodes.Document;

public class JsoupUtils {
	
	/**
	 * 一旦不要な要素を削除する(js,css,見た目変更要素)
	 * @param doc
	 * @return
	 */
	public static Document formatDocument(Document doc){
		doc.getElementsByTag("script").remove();
		doc.getElementsByTag("link").attr("rel", "stylesheet").remove();
		doc.getElementsByTag("style").remove();
		doc.getElementsByTag("img").addClass("img-responsive");
		return doc;
	}
}
