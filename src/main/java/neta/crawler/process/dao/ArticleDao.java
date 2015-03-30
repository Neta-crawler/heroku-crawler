package neta.crawler.process.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import neta.crawler.base.ConnectionManager;
import neta.crawler.process.dto.Article;

import org.apache.commons.dbutils.DbUtils;

/**
 * DAO。
 *
 * @author izumi_j
 *
 */
public final class ArticleDao {

	private static final String INS = "INSERT INTO articles (url, title, date, html_body, created_at, updated_at, category, page_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPD = "UPDATE articles set url=?, title=?, html_body=?, updated_at=?, category=?, page_image=?  where id = ?";
	private static final String UPD_BODY = "UPDATE articles set html_body = ? where id = ?";
	private static final String SEL_ALL = "SELECT * FROM articles ORDER BY id DESC";
	private static final String SEL_BY_URL = "SELECT * FROM articles WHERE url = ?";

	/**
	 * INSERT。
	 *
	 * @param a
	 * @return new id
	 */
	public long insert(Article a) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = ConnectionManager.getConnection().prepareStatement(INS, Statement.RETURN_GENERATED_KEYS);
			int i = 0;
			ps.setString(++i, a.url);
			ps.setString(++i, a.title);
			ps.setTimestamp(++i, new java.sql.Timestamp(a.date.getTime()));
			ps.setString(++i, a.htmlBody);
			ps.setTimestamp(++i, new java.sql.Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(++i, new java.sql.Timestamp(System.currentTimeMillis()));
			ps.setString(++i, a.category);
			ps.setString(++i, a.pageImage);

			ps.executeUpdate();

			rs = ps.getGeneratedKeys();
			rs.next();
			return rs.getLong(1);

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps);
		}
	}

	/**
	 * SELECT all。
	 *
	 * @return list of articles
	 */
	public List<Article> selectAll() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = ConnectionManager.getConnection().prepareStatement(SEL_ALL);
			rs = ps.executeQuery();

			final List<Article> articles = new ArrayList<>();
			while (rs.next()) {
				articles.add(toDto(rs));
			}
			return articles;

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps);
		}
	}

	/**
	 * SELECT by url。
	 *
	 * @param url
	 * @return article
	 */
	public Article selectByUrl(String url) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = ConnectionManager.getConnection().prepareStatement(SEL_BY_URL);
			ps.setString(1, url);
			rs = ps.executeQuery();

			if (rs.next()) {
				return toDto(rs);
			} else {
				return null;
			}

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps);
		}
	}
	
	public void updateHtmlBody(long id, String htmlBody) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = ConnectionManager.getConnection().prepareStatement(UPD_BODY);
			int i = 0;
			ps.setString(++i, htmlBody);
			ps.setLong(++i, id);
			
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps);
		}
	}
	
	/**
	 * UPD = "UPDATE articles set url=?, title=?, html_body=?, updated_at=?, category=? where id = ?";
	 * @param article
	 */
	public void update(Article a) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = ConnectionManager.getConnection().prepareStatement(UPD);
			int i = 0;
			ps.setString(++i, a.url);
			ps.setString(++i, a.title);
			ps.setString(++i, a.htmlBody);
			ps.setTimestamp(++i, new java.sql.Timestamp(System.currentTimeMillis()));
			ps.setString(++i, a.category);
			ps.setString(++i, a.pageImage);
			ps.setLong(++i, a.id);
			
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps);
		}
	}

	private Article toDto(ResultSet rs) throws SQLException {
		final Article a = new Article();
		a.id = rs.getLong(1);
		a.url = rs.getString(2);
		a.title = rs.getString(3);
		a.date = new java.util.Date(rs.getTimestamp(4).getTime());
		a.htmlBody = rs.getString(5);
		a.created_at = new java.util.Date(rs.getTimestamp(6).getTime());
		a.updated_at = new java.util.Date(rs.getTimestamp(7).getTime());
		a.category  = rs.getString("category");
		a.pageImage = rs.getString("page_image");
		
		return a;
	}
}
