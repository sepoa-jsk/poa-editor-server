package com.poa.editor.domain.template.dto;

import jakarta.validation.constraints.NotBlank;

public record TemplateUpdateRequest(
        Long folderId,
        @NotBlank(message = "템플릿 이름은 필수입니다.") String name,
        String content,
        boolean isPublic,
        int orderIndex
) {}
