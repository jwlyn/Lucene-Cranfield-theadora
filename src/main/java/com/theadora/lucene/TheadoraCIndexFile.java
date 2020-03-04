package com.theadora.lucene;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

/**
 * @author theadora
 */
public class TheadoraCIndexFile {
  
  private TheadoraCIndexFile() {}

  public static void main(String[] args) {
    String usage = "java com.theadora.lucene.TheadoraCIndexFile"
                 + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
                 + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                 + "in INDEX_PATH that can be searched with TheadoraSFile";
    String indexPath = "index";
    String docsPath = "doc";
    boolean create = true;
    for(int i=0;i<args.length;i++) {
      if ("-index".equals(args[i])) {
        indexPath = args[i+1];
        i++;
      } else if ("-docs".equals(args[i])) {
        docsPath = args[i+1];
        i++;
      } else if ("-update".equals(args[i])) {
        create = false;
      }
    }

    if (docsPath == null) {
      System.exit(1);
    }

    final Path docDir = Paths.get(docsPath);
    if (!Files.isReadable(docDir)) {
      System.out.println("doc directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }
    
    Date start = new Date();
    try {

      Directory directory = FSDirectory.open(Paths.get(indexPath));
      TheadoraAnalyzer analyzer = new TheadoraAnalyzer(TheadoraEnglishStopWordsSet.ENGLISH_STOP_WORDS_SET);
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

      if (create) {
        iwc.setOpenMode(OpenMode.CREATE);
      } else {
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      }
      IndexWriter writer = new IndexWriter(directory, iwc);
      indexTheadoraDocs(writer, docDir);
      writer.close();

      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (IOException e) {
        e.printStackTrace();
    }
  }
  static void indexTheadoraDocs(final IndexWriter writer, Path path) throws IOException {
    if (Files.isDirectory(path)) {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          try {
            indexTheadoraDoc(writer, file, attrs.lastModifiedTime().toMillis());
          } catch (IOException ignore) {
            ignore.printStackTrace();
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } else {
      indexTheadoraDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
    }
  }

  static void indexTheadoraDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
    try (InputStream stream = Files.newInputStream(file)) {
      Document document = new Document();
      Field pathField = new StringField("path", file.toString(), Field.Store.YES);
      document.add(pathField);
      document.add(new LongPoint("modified", lastModified));
      document.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
      
      if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
        writer.addDocument(document);
      } else {
        writer.updateDocument(new Term("path", file.toString()), document);
      }
    }
  }
}



























































