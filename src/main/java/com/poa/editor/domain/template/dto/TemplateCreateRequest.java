package com.poa.editor.domain.template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TemplateCreateRequest(
        Long folderId,
        @NotBlank(message = "템플릿 이름은 필수입니다.") String name,
        @NotNull(message = "템플릿 내용은 필수입니다.") String content,
        boolean isPublic,
        boolean isTemp,
        int orderIndex
) {}
