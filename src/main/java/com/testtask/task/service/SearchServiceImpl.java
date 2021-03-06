package com.testtask.task.service;

import com.testtask.task.model.DataOfPage;
import com.testtask.task.repository.SearchRepository;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.testtask.task.Constants.*;

@Service
public class SearchServiceImpl implements SearchService {

    private final TextExtractor textExtractor;
    private final SearchRepository searchRepository;

    public SearchServiceImpl(TextExtractor textExtractor,
                             SearchRepository searchRepository) {
        this.textExtractor = textExtractor;
        this.searchRepository = searchRepository;
    }

    @Override
    public void indexing(String url, int recursion) {
        try (Directory memoryIndex = new MMapDirectory(Paths.get("c:\\index\\"))) {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());

            try (IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig)) {
                indexingDocuments(writer, url, recursion);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void indexingDocuments(IndexWriter writer, String url, int recursion) throws IOException {
        int step = recursion - 1;

        if (!searchRepository.addUrl(url)) {
            return;
        }
        Optional<DataOfPage> dataOfPage = textExtractor.getDataOfPage(url);

        if (dataOfPage.isPresent()) {
            writer.addDocument(getDocument(dataOfPage.get()));

            if (step != 0) {
                ExecutorService service = Executors.newFixedThreadPool(10);

                for (String urlFromPage : dataOfPage.get().getUrls()) {
                    service.execute(() -> {
                        indexing(urlFromPage, step);
                    });
                }
            }
        }
    }

    private Document getDocument(DataOfPage data) {
        Document document = new Document();
        document.add(new TextField(TITLE_DOCUMENT, data.getTitle(), Field.Store.YES));
        document.add(new TextField(BODY_DOCUMENT, data.getBody(), Field.Store.YES));
        document.add(new TextField(URL_DOCUMENT, data.getUrl(), Field.Store.YES));

        return document;
    }

    @Override
    public Boolean isIndexing() {
        return false;
    }

    @Override
    public List<Document> search(String valueQuery) {
        Term term = new Term(BODY_DOCUMENT, valueQuery);
        Query query = new TermQuery(term);

        return searchIndex(query);
    }

    public List<Document> searchIndex(Query query) {
        try (Directory memoryIndex = new MMapDirectory(Paths.get("c:\\index\\"))) {
            IndexReader indexReader = DirectoryReader.open(memoryIndex);
            IndexSearcher searcher = new IndexSearcher(indexReader);
            TopDocs topDocs = searcher.search(query, 10);
            List<Document> documents = new ArrayList<>();

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }
            return documents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Document> searchIndex(Query query, Sort sort) {
//        try {
//            IndexReader indexReader = DirectoryReader.open(memoryIndex);
//            IndexSearcher searcher = new IndexSearcher(indexReader);
//            TopDocs topDocs = searcher.search(query, 10, sort);
//            List<Document> documents = new ArrayList<>();
//
//            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
//                documents.add(searcher.doc(scoreDoc.doc));
//            }
//            return documents;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return null;
    }
}
