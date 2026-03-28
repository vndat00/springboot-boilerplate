package com.vndat00.springbootboilerplate.service;

import com.vndat00.springbootboilerplate.payload.general.ResponseDataAPI;
import com.vndat00.springbootboilerplate.payload.request.NoteRequest;
import com.vndat00.springbootboilerplate.payload.request.NoteSearchRequest;
import com.vndat00.springbootboilerplate.payload.response.NoteResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface NoteService {
  NoteResponse create(NoteRequest request);

  NoteResponse getById(UUID id);

  ResponseDataAPI getAll(Pageable pageable, NoteSearchRequest searchRequest);

  NoteResponse update(UUID id, NoteRequest request);

  boolean delete(UUID id);
}
