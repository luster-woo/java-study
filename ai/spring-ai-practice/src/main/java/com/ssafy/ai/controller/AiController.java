package com.ssafy.ai.controller;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.ai.service.E_MultiModalService;
import com.ssafy.ai.service.EtlService;
import com.ssafy.ai.service.F_ConversationService;
import com.ssafy.ai.service.G_RAGService;

import lombok.RequiredArgsConstructor;

/**
 * ChatBot — 지금까지 만든 서비스를 /chat 하나에서 요청 내용에 따라 골라 부름
 *   file 있음      → 이미지 설명(멀티모달)
 *   useRag == true → 문서 검색 + 대화 기억
 *   그 밖          → 대화 기억만
 * 화면: http://localhost:8080/chat.html
 */
@RestController
@RequiredArgsConstructor
public class AiController {

    private final E_MultiModalService multiModalService;
    private final F_ConversationService conversationService;
    private final G_RAGService ragService;
    private final EtlService etlService;

    @PostMapping(value = "/chat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> chat(@RequestParam("message") String message,
                                  // 교재는 required 기본값(true) → 파일 없이 보내면 400. 선택 입력으로 바꿈
                                  @RequestParam(value = "file", required = false) MultipartFile file,
                                  @RequestParam(value = "userId") String userId,
                                  @RequestParam(value = "useRag", defaultValue = "false") boolean useRag) {
        try {
            Object result;
            if (file != null && !file.isEmpty()) {
                ByteArrayResource resource = new ByteArrayResource(file.getBytes());
                var mime = MimeTypeUtils.parseMimeType(
                        file.getContentType() != null ? file.getContentType() : "image/png");
                result = multiModalService.chatBotImageDescription(resource, mime, message, userId);
            } else if (useRag) {
                // filter 없이 topK 3 → 적재된 모든 문서 대상
                SearchRequest searchRequest = SearchRequest.builder().query(message).topK(3).build();
                result = ragService.ragGeneration(message, searchRequest, userId);
            } else {
                result = conversationService.historyGeneration(message, userId);
            }
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("요청 처리 중 오류 발생 " + e.getMessage());
        }
    }

    /** (교재 외 추가) RAG 문서 적재 버튼용 — 테스트를 안 돌리고 화면에서 바로 RAG를 써 보려고 */
    @PostMapping("/etl")
    public String etl() {
        etlService.loadAll();
        return "적재 완료";
    }
}
