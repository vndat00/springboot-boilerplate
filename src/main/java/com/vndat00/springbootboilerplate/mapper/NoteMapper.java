package com.vndat00.springbootboilerplate.mapper;

import com.vndat00.springbootboilerplate.config.SpringMapStructConfig;
import com.vndat00.springbootboilerplate.domain.model.Note;
import com.vndat00.springbootboilerplate.payload.request.NoteRequest;
import com.vndat00.springbootboilerplate.payload.response.NoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = SpringMapStructConfig.class)
public interface NoteMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "content", source = "note")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  Note toEntity(NoteRequest request);

  @Mapping(
      target = "id",
      expression = "java(note.getId() != null ? note.getId().toString() : null)")
  @Mapping(target = "note", source = "content")
  @Mapping(target = "attachments", ignore = true)
  NoteResponse toResponse(Note note);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "content", source = "note")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  void updateEntity(NoteRequest request, @MappingTarget Note note);
}
