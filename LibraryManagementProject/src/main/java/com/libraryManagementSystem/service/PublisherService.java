package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.PublisherDto;
import com.libraryManagementSystem.dto.PublisherRequest;
import java.util.List;

public interface PublisherService {
    List<PublisherDto> getAllPublishers();
    PublisherDto getPublisherById(Long id);
    PublisherDto createPublisher(PublisherRequest request);
    PublisherDto updatePublisher(Long id, PublisherRequest request);
    void deletePublisher(Long id);
}
