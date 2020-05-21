package com.testtask.task.converter;

import com.testtask.task.model.DocumentDto;
import org.apache.lucene.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.testtask.task.Constants.*;

@Component
public class ConverterDocumentDto {

    public DocumentDto getDataOfPageDto(Document document) {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setUrl(document.get(URL_DOCUMENT));
        documentDto.setTitle(document.get(TITLE_DOCUMENT));
        documentDto.setBody(document.get(BODY_DOCUMENT));

        return documentDto;
    }

    public List<DocumentDto> getDataOfPageDtoList(List<Document> list) {
        return list.stream()
                .map(this::getDataOfPageDto)
                .collect(Collectors.toList());
    }
}
