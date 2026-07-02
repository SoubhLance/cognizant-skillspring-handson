package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.PublisherDto;
import com.libraryManagementSystem.dto.PublisherRequest;
import com.libraryManagementSystem.entity.Publisher;
import com.libraryManagementSystem.mapper.PublisherMapper;
import com.libraryManagementSystem.repository.PublisherRepository;
import com.libraryManagementSystem.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private PublisherMapper publisherMapper;

    @Override
    public List<PublisherDto> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(publisherMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PublisherDto getPublisherById(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found with ID: " + id));
        return publisherMapper.toDto(publisher);
    }

    @Override
    @Transactional
    public PublisherDto createPublisher(PublisherRequest request) {
        if (publisherRepository.existsByName(request.getName())) {
            throw new RuntimeException("Publisher already exists with name: " + request.getName());
        }
        Publisher publisher = publisherMapper.toEntity(request);
        publisher = publisherRepository.save(publisher);
        return publisherMapper.toDto(publisher);
    }

    @Override
    @Transactional
    public PublisherDto updatePublisher(Long id, PublisherRequest request) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found with ID: " + id));

        if (!publisher.getName().equals(request.getName()) && publisherRepository.existsByName(request.getName())) {
            throw new RuntimeException("Publisher already exists with name: " + request.getName());
        }

        publisherMapper.updateEntity(request, publisher);
        publisher = publisherRepository.save(publisher);
        return publisherMapper.toDto(publisher);
    }

    @Override
    @Transactional
    public void deletePublisher(Long id) {
        if (!publisherRepository.existsById(id)) {
            throw new RuntimeException("Publisher not found with ID: " + id);
        }
        publisherRepository.deleteById(id);
    }
}
