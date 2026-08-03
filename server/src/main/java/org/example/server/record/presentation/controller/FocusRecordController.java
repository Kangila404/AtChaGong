package org.example.server.record.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.record.application.FocusRecordService;
import org.example.server.record.presentation.dto.req.CreateFocusRecordRequest;
import org.example.server.record.presentation.dto.res.FocusRecordResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Record API")
@RestController
@RequestMapping("/api/v1/focus-records")
@RequiredArgsConstructor
public class FocusRecordController {

    private final FocusRecordService focusRecordService;

    @PostMapping
    public ResponseEntity<FocusRecordResponse> createFocusRecord(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody CreateFocusRecordRequest request
    ) {
        FocusRecordResponse response = focusRecordService.createFocusRecord(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
