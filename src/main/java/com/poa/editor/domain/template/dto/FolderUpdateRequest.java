package com.poa.editor.domain.template.dto;

import jakarta.validation.constraints.NotBlank;

public record FolderUpdateRequest(
        Long parentId,
        @NotBlank(message = "폴더 이름은 필수입니다.") String name,
        boolean isPublic,
        int orderIndex
) {}
