package com.example.CVRUK_backend.authentication.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.example.CVRUK_backend.authentication.dto.DummyItemRequest;
import com.example.CVRUK_backend.authentication.dto.DummyItemResponse;
import com.example.CVRUK_backend.common.exception.resourceNotFoundException;

@Service
public class dummyCrudService {

    private final Map<Long, DummyRecord> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public DummyItemResponse create(DummyItemRequest request, String createdBy) {
        long id = idGenerator.incrementAndGet();
        LocalDateTime now = LocalDateTime.now();
        DummyRecord record = new DummyRecord(id, request.title(), request.description(), createdBy, now, now);
        store.put(id, record);
        return toResponse(record);
    }

    public List<DummyItemResponse> getAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(DummyRecord::id))
                .map(this::toResponse)
                .toList();
    }

    public DummyItemResponse getById(Long id) {
        return toResponse(require(id));
    }

    public DummyItemResponse update(Long id, DummyItemRequest request) {
        DummyRecord existing = require(id);
        DummyRecord updated = new DummyRecord(
                existing.id(),
                request.title(),
                request.description(),
                existing.createdBy(),
                existing.createdAt(),
                LocalDateTime.now());
        store.put(id, updated);
        return toResponse(updated);
    }

    public void delete(Long id) {
        DummyRecord existing = require(id);
        store.remove(existing.id());
    }

    private DummyRecord require(Long id) {
        DummyRecord record = store.get(id);
        if (record == null) {
            throw new resourceNotFoundException("Dummy item not found for id: " + id);
        }
        return record;
    }

    private DummyItemResponse toResponse(DummyRecord record) {
        return new DummyItemResponse(
                record.id(),
                record.title(),
                record.description(),
                record.createdBy(),
                record.createdAt(),
                record.updatedAt());
    }

    private record DummyRecord(
            Long id,
            String title,
            String description,
            String createdBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
    }
}
