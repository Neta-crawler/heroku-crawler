package neta.crawler.process;

import java.io.IOException;
import java.util.List;

import neta.crawler.process.dto.Article;

/**
 * サイト毎のクローラーのインターフェース。
 *
 * @author izumi_j
 *
 */
public interface Crawler {

	/**
	 * WEBサイトからネタを集める処理を実装。
	 *
	 * @return list of contents
	 * @throws IOException
	 */
	List<Article> collect() throws IOException;
}
