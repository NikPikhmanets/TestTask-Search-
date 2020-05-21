package com.testtask.task.service;

import org.apache.lucene.document.Document;

import java.util.List;

public interface SearchService {
    void indexing(String uri, int recursion);

    Boolean isIndexing();

    List<Document> search(String query);
}
