package neta.crawler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import neta.crawler.base.ConnectionManager;
import neta.crawler.process.dao.ArticleDao;
import neta.crawler.process.dto.Article;

import org.junit.Test;

public class Recorrect {
	final ArticleDao dao = new ArticleDao();
	
	@Test
	public void execute() throws Exception{						
		List<ExecDto> dtoList = new ArrayList<ExecDto>();
		for (String f : getClassesName("neta.crawler.process.logic")){
			
			Class<?> clazz = Class.forName("neta.crawler.process.logic."+ f);
			
			Method recollect = clazz.getDeclaredMethod("collectSinglePage", new Class[]{String.class});
			recollect.setAccessible(true);
			
			Object instance = clazz.newInstance();
			Field url = clazz.getDeclaredField("URL");
			url.setAccessible(true);
			
			ExecDto dto = new ExecDto();
			
			dto.setClazz(clazz);
			dto.setInstance(instance);
			dto.setRecollect(recollect);
			dto.setUrl((String) url.get(this));			
			
			dtoList.add(dto);
		}
		
		//準備したクラスでDBに格納しているURLに対して再度処理する
		ConnectionManager.prepareDataSource();
		List<Article> articles = dao.selectAll();
		List<Article> recollectArticles = new ArrayList<Article>();
		
		for (Article a: articles){
			for (ExecDto dto:dtoList){
				if (a.url.indexOf(dto.url) != -1){
					Article result = ((Article) dto.getRecollect().invoke(dto.getInstance(), a.url));
					result.id = a.id;
					recollectArticles.add(result);
					break;
				}
			}
		}
		
		//結果をupdate
		//念のため、ループの中で詰めて、再度ループしてupd		
		for (Article a: recollectArticles){
			dao.update(a);
		}
		
		ConnectionManager.commitAndClose();
	}
	
	class ExecDto{
		private Class<?> clazz;
		private Object instance;
		private Method recollect;
		private String url;
		
		public Class<?> getClazz() {
			return clazz;
		}
		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}
		public Object getInstance() {
			return instance;
		}
		public void setInstance(Object instance) {
			this.instance = instance;
		}
		public Method getRecollect() {
			return recollect;
		}
		public void setRecollect(Method recollect) {
			this.recollect = recollect;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}		
	}
	
	
	/**
	 * 特定パッケージのクラス名一覧を取得する
	 * @param targetPackageName
	 * @return
	 * @throws IOException
	 */
	private List<String> getClassesName(String targetPackageName) throws IOException{
		String resourceName = convertResourceName(targetPackageName);
		Enumeration<URL> resourceUrls = Thread.currentThread().getContextClassLoader().getResources(resourceName);
		
		if (resourceUrls == null){
			return Collections.emptyList();
		}
		
		while (resourceUrls.hasMoreElements()) {
			URL url = resourceUrls.nextElement();
			String protocol = url.getProtocol();
			if ("file".equals(protocol)){
				return findClassesWithFile(targetPackageName, new File(url.getFile()));
			}else if ("jar".equals(protocol)){
				return Collections.emptyList();
			}
		}
		return Collections.emptyList();
	}
	
	private List<String> findClassesWithFile(String targetPackageName, File dir) {
	    List<String> classNames = new ArrayList<String>();
	    
	    for (String path : dir.list()){
	    	File entry = new File(dir, path);	    	
	    	if (entry.isFile() && isClassFile(entry.getName())){
	    		classNames.add(fileNameToClassName(entry.getName()));
	    	}	    	
	    }
	    return classNames;
    }

	private String fileNameToClassName(String name) {
	    return name.substring(0, name.length()- ".class".length());
    }

	private boolean isClassFile(String fileName) {
        return fileName.endsWith(".class");
    }
	
	private String convertResourceName(String packageName){
		return packageName.replace(".", "/");
	}
	
}
