package com.vndat00.springbootboilerplate.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notes")
public class Note extends AbstractEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String content;

    @Min(1)
    @Max(10)
    private Integer priority;
}
