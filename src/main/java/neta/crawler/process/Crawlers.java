package neta.crawler.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import neta.crawler.process.logic.AgohigeCrawler;
import neta.crawler.process.logic.SpotlightCrawler;
import neta.crawler.process.logic.VowtvCrawler;

/**
 * クローラーの一覧。
 *
 * @author izumi_j
 *
 */
public enum Crawlers {
	AGOHIGE(new AgohigeCrawler()),
	VOWTV(new VowtvCrawler()),
	SPOTLIGHT(new SpotlightCrawler());
	
	private final Crawler crawler;

	private Crawlers(Crawler crawler) {
		this.crawler = crawler;
	}

	public static Collection<Crawler> implementations() {
		final List<Crawler> impls = new ArrayList<>();
		for (Crawlers e : Crawlers.values()) {
			impls.add(e.crawler);
		}
		return impls;
	}
}
