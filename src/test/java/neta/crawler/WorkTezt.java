package neta.crawler;

import neta.crawler.process.logic.VowtvCrawler;

import org.junit.Test;

public class WorkTezt {
	@Test
	public void test() throws Exception{
		new VowtvCrawler().collect();
	}
}
