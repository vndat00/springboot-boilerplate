package com.vndat00.springbootboilerplate.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NoteRequest {
    @NotBlank
    @Size(max = 255)
    private String note;

    @Min(1)
    @Max(10)
    private Integer priority;

    private List<UUID> attachmentFileIds;
}
