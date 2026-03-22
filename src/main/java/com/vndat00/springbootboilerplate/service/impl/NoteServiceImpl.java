package com.vndat00.springbootboilerplate.service.impl;

import com.vndat00.springbootboilerplate.constant.MessageConstant;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageStatus;
import com.vndat00.springbootboilerplate.domain.model.Note;
import com.vndat00.springbootboilerplate.domain.model.StorageObject;
import com.vndat00.springbootboilerplate.exception.BadRequestException;
import com.vndat00.springbootboilerplate.exception.NotFoundException;
import com.vndat00.springbootboilerplate.mapper.NoteMapper;
import com.vndat00.springbootboilerplate.mapper.StorageObjectMapper;
import com.vndat00.springbootboilerplate.payload.general.ResponseDataAPI;
import com.vndat00.springbootboilerplate.payload.request.NoteRequest;
import com.vndat00.springbootboilerplate.payload.response.NoteResponse;
import com.vndat00.springbootboilerplate.payload.response.storage.StorageObjectResponse;
import com.vndat00.springbootboilerplate.repository.NoteRepository;
import com.vndat00.springbootboilerplate.repository.StorageObjectRepository;
import com.vndat00.springbootboilerplate.service.NoteService;
import com.vndat00.springbootboilerplate.utils.ResponseDataUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
  private final NoteRepository noteRepository;
  private final StorageObjectRepository storageObjectRepository;
  private final NoteMapper noteMapper;
  private final StorageObjectMapper storageObjectMapper;

  @Override
  public NoteResponse create(NoteRequest request) {
    Note note = noteMapper.toEntity(request);
    Note saved = noteRepository.save(note);

    List<StorageObject> attachments =
        storageObjectRepository.findAllByIdIn(request.getAttachmentFileIds());
    attachments.forEach(
        storageObject -> {
          if (storageObject.getStatus() != null
              && (StorageStatus.ATTACHED.equals(storageObject.getStatus())
                  || storageObject.getNote() != null)) {
            throw new BadRequestException(MessageConstant.BAD_REQUEST);
          }
          storageObject.setNote(saved);
          storageObject.setStatus(StorageStatus.ATTACHED);
        });
    storageObjectRepository.saveAll(attachments);
    return toNoteResponse(
        saved, attachments.stream().map(storageObjectMapper::toResponse).toList());
  }

  @Override
  public NoteResponse getById(UUID id) {
    Note note =
        noteRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(MessageConstant.NOTE_NOT_FOUND));

    List<StorageObjectResponse> attachments =
        storageObjectRepository.findAllByNoteId(note.getId()).stream()
            .map(storageObjectMapper::toResponse)
            .toList();

    return toNoteResponse(note, attachments);
  }

  @Override
  public ResponseDataAPI getAll(Pageable pageable, String content) {
    Page<Note> notes = noteRepository.findAllBySearch(pageable, content);
    List<UUID> noteIds = notes.getContent().stream().map(Note::getId).toList();

    Map<UUID, List<StorageObjectResponse>> attachmentMap = loadAttachmentMap(noteIds);

    return ResponseDataUtils.toResponseData(
        notes, note -> toNoteResponse(note, attachmentMap.getOrDefault(note.getId(), List.of())));
  }

  @Override
  public NoteResponse update(UUID id, NoteRequest request) {
    return noteRepository
        .findById(id)
        .map(
            note -> {
              noteMapper.updateEntity(request, note);
              Note updated = noteRepository.save(note);
              return noteMapper.toResponse(updated);
            })
        .orElse(null);
  }

  @Override
  public boolean delete(UUID id) {
    return noteRepository
        .findById(id)
        .map(
            note -> {
              noteRepository.delete(note);
              return true;
            })
        .orElse(false);
  }

  private NoteResponse toNoteResponse(Note note, List<StorageObjectResponse> attachments) {
    NoteResponse noteResponse = noteMapper.toResponse(note);
    noteResponse.setAttachments(attachments);
    return noteResponse;
  }

  private Map<UUID, List<StorageObjectResponse>> loadAttachmentMap(List<UUID> noteIds) {
    if (noteIds == null || noteIds.isEmpty()) {
      return Map.of();
    }
    return storageObjectRepository.findAllByNoteIdIn(noteIds).stream()
        .collect(
            Collectors.groupingBy(
                storageObject -> storageObject.getNote().getId(),
                Collectors.mapping(storageObjectMapper::toResponse, Collectors.toList())));
  }
}
