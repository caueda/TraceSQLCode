package br.com.somewhere.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchEngine {
	private List<ResultBean> listaResultBean = new ArrayList<ResultBean>();
	
	public SearchEngine() {
		super();						
	}
	
	public List<ResultBean> getListaResultBean(){
		return this.listaResultBean;
	}

	/**
	 * Set a extension filter for files.
	 * @param filters
	 */
	public FilenameFilter getFilenameFilterExt(final String ... filters) {
		return new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				for(String ext : filters) {
					File test = new File(dir, name);
					if(test.isDirectory()) return true;
					if (lowercaseName.endsWith(ext.toLowerCase())) {
						return true;
					} 
				}
				return false;
			}
		};
	}
	
	protected void addResult(ResultBean bean) {
		this.listaResultBean.add(bean);
	}
	
//	public static void main(String[] args) {
//		File pasta = new File("C:\\Java\\cXXXXXXX_view_xx\\xxxxxxx_Fiplan\\Implementacao\\banco\\view");
//		SearchEngine engine = new SearchEngine(pasta, new String[] {".sql",".map"});
//		String patternHQL = ".*from.*VO.*";
//		String patternSearch = ".*acwtb0541.*";		
//		List<ResultBean> result = engine.search(patternSearch);
//		for(ResultBean bean : result) {
//			if(bean != null)
//				System.out.println(bean.toString());
//		}
//		System.out.println("Finished.");
//	}
	
	protected boolean match(String line, String regex) {
		return Pattern.matches(regex, line.toLowerCase());		
	}
	
	public List<ResultBean> search(String pathFile, String[] textFilter, String regex){
        clearSearch();
        File folder = new File(pathFile);        
		runThrough(folder, textFilter, regex);		
		return new ArrayList<ResultBean>(listaResultBean);
	}
	
	public void clearSearch() {
		this.listaResultBean.clear();
	}
		
	public void runThrough(File folder, String[] textFilter, String regex) {		
        regex = regex.replace("%", ".*");                
		if(folder.isDirectory()) {
			File[] files = folder.listFiles(getFilenameFilterExt(textFilter));
			for(File f : files) {
				if(f.isDirectory()) {
					runThrough(f, textFilter, regex);
				} else {
					BufferedReader br = null;
					try {
						br = new BufferedReader(new FileReader(f));
						String line = null;
						int lineNumber = 0;
						while((line = br.readLine()) != null) {
							lineNumber++;
							if(match(line, regex)) {
								ResultBean bean = new ResultBean();
								bean.setFileName(f.getName());
								bean.setPath(f.getAbsolutePath());
								bean.setLine(String.valueOf(lineNumber));
								bean.setSnippet(line);
								addResult(bean);								
							}
						}
					} catch(Exception e) {						
						e.printStackTrace();
					} finally {
						try {br.close();} catch(Exception e) {};
					}
				}
			}
		}
	}
}
