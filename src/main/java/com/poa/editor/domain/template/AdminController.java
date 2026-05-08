package com.poa.editor.domain.template;

import com.poa.editor.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 임시/테스트 데이터 일괄 정리.
     * 프론트엔드에서 Poa-User-Id: admin 헤더로 호출.
     */
    @DeleteMapping("/cleanup")
    public ApiResponse<Void> cleanup(
            @RequestHeader(value = "Poa-User-Id", required = false, defaultValue = "anonymous")
            String userId) {
        adminService.cleanup();
        return ApiResponse.ok(null);
    }
}
