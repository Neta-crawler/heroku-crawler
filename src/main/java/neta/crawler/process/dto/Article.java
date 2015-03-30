package neta.crawler.process.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public final class Article {
	public long id = 0;
	public String url = StringUtils.EMPTY;
	public String title = StringUtils.EMPTY;
	public Date date = new Date(0);
	public String htmlBody = StringUtils.EMPTY;
	public Date created_at = new Date(0);
	public Date updated_at = new Date(0);
	public String category = StringUtils.EMPTY;
	public String pageImage = StringUtils.EMPTY;
	
	@Override
    public String toString() {
	    return "Article [id=" + id + ", url=" + url + ", title=" + title + ", date=" + date + ", htmlBody=" + htmlBody + ", created_at=" + created_at
	            + ", updated_at=" + updated_at + ", category=" + category + ", pageImage=" + pageImage + "]";
    }
	
}
