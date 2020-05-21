package com.testtask.task.controller;

import com.testtask.task.converter.ConverterDocumentDto;
import com.testtask.task.model.DocumentDto;
import com.testtask.task.service.SearchService;
import org.apache.lucene.document.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SearchController {

    private final ConverterDocumentDto converterDocumentDto;
    private final SearchService searchService;

    public SearchController(ConverterDocumentDto converterDocumentDto, SearchService searchService) {
        this.converterDocumentDto = converterDocumentDto;
        this.searchService = searchService;
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @PostMapping("/index")
    public String index(@RequestParam(value = "uri") String uri) {
        int recursion = 2;

        searchService.indexing(uri, recursion);

        return "root";
    }

    @GetMapping
    public String root() {
        return searchService.isIndexing() ? "root" : "index";
    }

    @PostMapping("/search")
    @ResponseBody
    public List<DocumentDto> search(@RequestParam(value = "query") String query) {

        List<Document> results = searchService.search(query);
//        List<DocumentDto> dataOfPageDtoList = converterDocumentDto.getDataOfPageDtoList(results);
        return converterDocumentDto.getDataOfPageDtoList(results);

//        return "list";
    }
}
