package br.com.somewhere.core;

import java.io.File;
import java.util.List;


public class Main {
	public static void main(String[] args) {
		if(args == null || args.length == 0) {
			System.out.println("<div class=\"table-responsive\">");
			System.out.println("<table class=\"table table-bordered table-hover table-condensed\">");
			
			System.out.println("<thead><tr>");
			System.out.println("<th>Resultado</th>");
			System.out.println("</tr></thead>");
			System.out.println("<tr><td>Nenhum parâmetro informado.</td></tr>");
			System.out.println("</table></div>");
		} else {
			File pasta = new File("C:\\Java\\c8757550_view_mt\\Cepromat_Fiplan\\Implementacao\\banco\\view");
			SearchEngine engine = new SearchEngine(pasta, new String[] {".sql",".map"});
			String patternHQL = ".*from.*VO.*";
			String patternSearch = ".*id_unidade_orcamentaria.*";		
			List<ResultBean> result = engine.search(patternSearch);
			System.out.println("<div class=\"table-responsive\">");
			System.out.println("<table class=\"table table-bordered table-hover table-condensed\">");					
			for(ResultBean bean : result) {
				if(bean != null) {				
					System.out.println("<thead><tr>");
					System.out.println("<th>" + bean.getFileName() + "</th>");
					System.out.println("</tr></thead>");
					System.out.println("<tr><td>Nenhum parâmetro informado.</td></tr>");
				}
			}
			System.out.println("</table></div>");
		}
	}
}
