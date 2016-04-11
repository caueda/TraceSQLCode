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
	private File folder;
	private FilenameFilter textFilter;	
	
	public SearchEngine(File folder, final String[] filterExtension) {
		this.folder = folder;		
		this.textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				for(String ext : filterExtension) {
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
	
	public static void main(String[] args) {
		File pasta = new File("C:\\Java\\c8757550_view_mt\\Cepromat_Fiplan\\Implementacao\\banco\\view");
		SearchEngine engine = new SearchEngine(pasta, new String[] {".sql",".map"});
		String patternHQL = ".*from.*VO.*";
		String patternSearch = ".*acwtb0541.*";		
		List<ResultBean> result = engine.search(patternSearch);
		for(ResultBean bean : result) {
			if(bean != null)
				System.out.println(bean.toString());
		}
		System.out.println("Finished.");
	}
	
	protected boolean match(String line, String regex) {
		return Pattern.matches(regex, line.toLowerCase());		
	}
	
	public List<ResultBean> search(String regex){
                this.listaResultBean.clear();
		runThrough(folder, regex);
		return listaResultBean;
	}
	
	public void runThrough(File folder, String regex) {
                regex = regex.replace("%", ".*");                
		if(folder.isDirectory()) {
			File[] files = folder.listFiles(textFilter);
			for(File f : files) {
				if(f.isDirectory()) {
					runThrough(f, regex);
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
