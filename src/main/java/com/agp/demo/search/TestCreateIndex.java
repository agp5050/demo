package com.agp.demo.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestCreateIndex {
    @Test
    public void testLuceneCreateIndex() throws IOException {
        //指定索引库的存放位置Directory对象
        Directory directory = FSDirectory.open(Paths.get("D:\\test2"));
        //索引库还可以存放到内存中
        //Directory directory = new RAMDirectory();

        //指定一个标准分析器，对文档内容进行分析
        Analyzer analyzer = new StandardAnalyzer();

        //创建indexwriterCofig对象
        //第一个参数： Lucene的版本信息，可以选择对应的lucene版本也可以使用LATEST
        //第二根参数：分析器对象
        IndexWriterConfig config = new IndexWriterConfig( analyzer);

        //创建一个indexwriter对象
        IndexWriter indexWriter = new IndexWriter(directory, config);

        //原始文档的路径
        File file = new File("D:\\test");
        File[] fileList = file.listFiles();
        for (File file2 : fileList) {
            //创建document对象
            Document document = new Document();

            //创建field对象，将field添加到document对象中

            //文件名称
            String fileName = file2.getName();
            //创建文件名域
            //第一个参数：域的名称
            //第二个参数：域的内容
            //第三个参数：是否存储
            Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);

            //文件的大小
            long fileSize  = file2.length();
            System.out.println("fileSize:"+fileSize);
            //文件大小域
            Field fileSizeField = new NumericDocValuesField("fileSize", fileSize);

            //文件路径
            String filePath = file2.getPath();
            //文件路径域（不分析、不索引、只存储）
            Field filePathField = new StoredField("filePath", filePath);

            //文件内容
            StringBuilder stringBuilder=new StringBuilder();
            Files
                    .readAllLines(file2.toPath())
                    .stream()
                    .forEach(e->stringBuilder.append(e));
            String fileContent =stringBuilder.toString();
                    //String fileContent = FileUtils.readFileToString(file2, "utf-8");
            //文件内容域
            Field fileContentField = new TextField("fileContent", fileContent, Field.Store.YES);

            document.add(fileNameField);
            document.add(fileSizeField);
            document.add(filePathField);
            document.add(fileContentField);
            //使用indexwriter对象将document对象写入索引库，此过程进行索引创建。并将索引和document对象写入索引库。
            indexWriter.addDocument(document);
        }
        //关闭IndexWriter对象。
        indexWriter.close();
    }
}
