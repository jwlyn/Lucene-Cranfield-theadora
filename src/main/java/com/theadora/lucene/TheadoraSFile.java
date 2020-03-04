package com.theadora.lucene;


import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Paths;

/**
 * @author theadora
 */
public class TheadoraSFile {

  private TheadoraSFile() {}

  public static void main(String[] args) throws Exception {
    String usage =
      "Usage:\tjava com.theadora.lucene.TheadoraSFile [-index dir] [-field f] [-queries file] [-query string] [-paging hitsPerPage]";
    if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
      System.out.println(usage);
      System.exit(0);
    }
    String index = "index";
    String field = "contents";
    String queries = null;
    String queryString = null;
    int hitsPerPage = 10;
    
    for(int i = 0;i < args.length;i++) {
      if ("-index".equals(args[i])) {
        index = args[i+1];
        i++;
      } else if ("-field".equals(args[i])) {
        field = args[i+1];
        i++;
      } else if ("-queries".equals(args[i])) {
        queries = args[i+1];
        i++;
      } else if ("-query".equals(args[i])) {
        queryString = args[i+1];
        i++;
      }else if ("-paging".equals(args[i])) {
        hitsPerPage = Integer.parseInt(args[i+1]);
        if (hitsPerPage <= 0) {
          System.err.println("There must be at least 1 hit per page.");
          System.exit(1);
        }
        i++;
      }
    }

    IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
    IndexSearcher indexSearcher = new IndexSearcher(indexReader);

    TheadoraAnalyzer analyzer = new TheadoraAnalyzer(TheadoraEnglishStopWordsSet.ENGLISH_STOP_WORDS_SET);

    QueryParser parser = new QueryParser(field, analyzer);
      if (queries == null && queryString == null) {

          File file = new File("results.txt");
          PrintWriter printWriter = new PrintWriter(file, "UTF-8");

          BufferedReader br = new BufferedReader(new FileReader(new File("cran/cran.qry")));
  			String line;
  			String line4 = "";
  			int i = 0;
  			
  			while ((line = br.readLine()) != null) {
  				if(line.contains(".I")) {
  					if(line4 != "")
  					{
  					Query query1 = parser.parse(line4);
	                  
	                  TopDocs topDocs = indexSearcher.search(query1, 10);
	                  
	                  ScoreDoc[] hits = topDocs.scoreDocs;
	                  for(ScoreDoc sd:hits)
	                  {
	                	   int s = sd.doc - 2;
                          printWriter.println((i) + " Q0 " + s + " 0 " + sd.score + " STANDARD ");
	                  }
  					}
  					i = i + 1;
  					line4 = "";
  					continue;
  				}else if(line.contains(".W")){
  					continue;
  				} else {
  					line4 += line.replace("*", "");
  					line4 = line4.replace("?", "");
  				}
  			}
  			Query topQuery1 = parser.parse(line4);
            
            TopDocs topDocs = indexSearcher.search(topQuery1, 10);
            
            ScoreDoc[] hits = topDocs.scoreDocs;
            for(ScoreDoc sd:hits)
            {
          	   int s = sd.doc;
                printWriter.println((i) + " Q0 " + s + " 0 " + sd.score + " STANDARD ");
            }
          printWriter.close();
                   
      }
      indexReader.close();
  }
}