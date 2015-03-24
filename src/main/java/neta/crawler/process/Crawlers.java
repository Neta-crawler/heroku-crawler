package neta.crawler.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import neta.crawler.process.logic.AgohigeCrawler;

/**
 * クローラーの一覧。
 *
 * @author izumi_j
 *
 */
public enum Crawlers {
	AGOHIGE(new AgohigeCrawler());

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
