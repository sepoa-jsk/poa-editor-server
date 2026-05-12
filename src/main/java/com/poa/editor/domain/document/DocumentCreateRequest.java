package com.poa.editor.domain.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentCreateRequest(
        @NotBlank String title,
        @NotNull  String content
) {}
