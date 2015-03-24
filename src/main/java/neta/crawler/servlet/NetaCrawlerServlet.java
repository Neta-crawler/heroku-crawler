package neta.crawler.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import neta.crawler.process.dao.ArticleDao;
import neta.crawler.process.dto.Article;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * デバッグ用サーブレット。
 *
 * @author izumi_j
 *
 */
@SuppressWarnings("serial")
public final class NetaCrawlerServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(NetaCrawlerServlet.class);

	private String template;

	@Override
	public void init() {
		try (final InputStream is = this.getClass().getClassLoader().getResourceAsStream("index.html");) {
			template = IOUtils.toString(is, Charsets.UTF_8);
			logger.debug("Html loaded.");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.debug("Request received. URL = {}", req.getRequestURI());

		try {
			final Document doc = Jsoup.parse(template);

			final ArticleDao dao = new ArticleDao();
			final List<Article> articles = dao.selectAll();

			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			final Element table = doc.createElement("table").addClass("table");
			doc.getElementById("container").appendChild(table);

			final Element trH = table.appendElement("tr");
			trH.appendElement("th").text("id");
			trH.appendElement("th").text("url");
			trH.appendElement("th").text("title");
			trH.appendElement("th").text("created_at");

			for (Article a : articles) {
				final Element tr = table.appendElement("tr");
				tr.appendElement("td").text(String.valueOf(a.id));
				tr.appendElement("td").appendElement("a").text(a.url).attr("href", a.url).attr("target", "_blank");
				tr.appendElement("td").text(a.title);
				tr.appendElement("td").text(sdf.format(a.created_at));
			}

			resp.setCharacterEncoding(Charsets.UTF_8.name());
			resp.getWriter().print(doc.html());
		} catch (Throwable e) {
			logger.error("Error!", e);
			throw e;
		}
	}
}
