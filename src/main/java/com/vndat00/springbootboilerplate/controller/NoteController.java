package com.vndat00.springbootboilerplate.controller;

import com.vndat00.springbootboilerplate.common.CommonFunction;
import com.vndat00.springbootboilerplate.payload.general.ResponseDataAPI;
import com.vndat00.springbootboilerplate.payload.request.NoteRequest;
import com.vndat00.springbootboilerplate.payload.response.ErrorResponse;
import com.vndat00.springbootboilerplate.payload.response.NoteResponse;
import com.vndat00.springbootboilerplate.service.NoteService;
import com.vndat00.springbootboilerplate.utils.PagingUtils;
import com.vndat00.springbootboilerplate.utils.ResponseDataUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class NoteController {

  private static final ErrorResponse NOTE_NOT_FOUND = new ErrorResponse("ERR.NOTE_NOT_FOUND", "Note not found");

  private final NoteService noteService;

  @PostMapping("/notes")
  public ResponseEntity<ResponseDataAPI> create(@Valid @RequestBody NoteRequest request) {
    return ResponseEntity.ok(ResponseDataUtils.toResponseData(noteService.create(request)));
  }

  @GetMapping("/notes/{id}")
  public ResponseEntity<ResponseDataAPI> getById(@PathVariable UUID id) {
    NoteResponse note = noteService.getById(id);
    if (note == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDataAPI.error(NOTE_NOT_FOUND));
    }
    return ResponseEntity.ok(ResponseDataUtils.toResponseData(note));
  }

  @GetMapping("/notes")
  public ResponseEntity<ResponseDataAPI> getAll(
      @RequestParam(name = "sort", defaultValue = "created_at") String sortBy,
      @RequestParam(name = "order", defaultValue = "desc") String order,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "paging", defaultValue = "10") int paging,
      @RequestParam(value = "content", defaultValue = "") String content) {
    Pageable normalizedPage = PagingUtils.makePageRequest(sortBy, order, page, paging);
    return ResponseEntity.ok(
        noteService.getAll(normalizedPage, CommonFunction.handleContentSearch(content)));
  }

  @PutMapping("/notes/{id}")
  public ResponseEntity<ResponseDataAPI> update(
      @PathVariable UUID id, @Valid @RequestBody NoteRequest request) {
    NoteResponse updated = noteService.update(id, request);
    if (updated == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDataAPI.error(NOTE_NOT_FOUND));
    }
    return ResponseEntity.ok(ResponseDataUtils.toResponseData(updated));
  }

  @DeleteMapping("/notes/{id}")
  public ResponseEntity<ResponseDataAPI> delete(@PathVariable UUID id) {
    boolean deleted = noteService.delete(id);
    if (!deleted) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDataAPI.error(NOTE_NOT_FOUND));
    }
    return ResponseEntity.ok(ResponseDataUtils.toResponseData());
  }
}
