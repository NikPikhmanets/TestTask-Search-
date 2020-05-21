package com.testtask.task.repository;

import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class SearchRepositoryImpl implements SearchRepository {

    private final Set<String> urls;
    private final Object key;

    public SearchRepositoryImpl() {
        this.urls = new HashSet<>();
        key = new Object();
    }

    @Override
    public Boolean addUrl(String url) {
        synchronized (key) {
            return urls.add(url);
        }
    }
}
