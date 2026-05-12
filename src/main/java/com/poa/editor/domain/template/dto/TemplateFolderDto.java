package com.poa.editor.domain.template.dto;

import com.poa.editor.domain.template.TemplateFolder;

import java.time.LocalDateTime;

public record TemplateFolderDto(
        Long id,
        Long parentId,
        String name,
        boolean isPublic,
        int orderIndex,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int templateCount
) {
    /** templateCount 없이 생성 (생성/수정 응답용) */
    public static TemplateFolderDto from(TemplateFolder folder) {
        return from(folder, 0);
    }

    /** templateCount 포함 생성 (목록 조회용) */
    public static TemplateFolderDto from(TemplateFolder folder, int templateCount) {
        return new TemplateFolderDto(
                folder.getId(),
                folder.getParentId(),
                folder.getName(),
                folder.isPublic(),
                folder.getOrderIndex(),
                folder.getCreatedBy(),
                folder.getCreatedAt(),
                folder.getUpdatedAt(),
                templateCount
        );
    }
}
