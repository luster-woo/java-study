package com.ssafy.fw_06_rest.controller;

import com.ssafy.fw_06_rest.dto.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

// [04] RestClient: "내 서버" 가 다른 서버에 대신 요청하는 프록시 역할
// 브라우저 JS 는 SOP 때문에 api.vworld.kr 을 직접 못 부름 → 같은 출처인 내 서버(/api/v1/etc/sidos)를 부르고
// 내 서버가 RestClient 로 vworld 에 일반 요청 → 결과를 그대로 전달
@Slf4j
@RestController
@RequestMapping("/api/v1/etc")
@Tag(name = "EtcRestController", description = "기타 유틸리티성 기능 제공 (RestClient 프록시)")
public class EtcRestController {

    @Value("${key.vworld:}")      // application.properties 의 key.vworld (없으면 빈 문자열)
    private String vworldKey;

    @GetMapping("/sidos")
    @Operation(summary = "시/도 조회", description = "국토교통부 디지털트윈국토(vworld)에서 시/도 정보 조회. key.vworld 설정 필요")
    public ResponseEntity<?> getSido() {
        if (!StringUtils.hasText(vworldKey)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "application.properties 의 key.vworld 에 vworld 인증키를 넣고 서버를 재시작하세요."));
        }

        // ① URI 조립: {numOfRows}, {key} 는 빈칸 → buildAndExpand 로 순서대로 채움
        URI uri = UriComponentsBuilder.fromUriString("https://api.vworld.kr/ned/data/admCodeList")
                .queryParam("format", "json")
                .queryParam("domain", "localhost")
                .queryParam("numOfRows", "{numOfRows}")
                .queryParam("key", "{key}")
                .encode()                                    // 채워질 값의 특수문자도 안전하게 인코딩
                .buildAndExpand(20, vworldKey)               // 시/도는 17개라 20개 요청
                .toUri();

        // ② 요청 보내고 ③ Map 으로 받기
        // 교재: RestClient.create(uri).get()... / 여기서는 완성된 URI 를 uri(..) 로 그대로 넘김 (이중 인코딩 방지)
        Map<String, Object> resultMap = RestClient.create()
                .get()                                       // HTTP 메서드 선택
                .uri(uri)
                .retrieve()                                  // 요청 실행
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});   // 제네릭 타입은 이렇게

        return ResponseEntity.ok(resultMap);                 // 받은 그대로 브라우저에 전달
    }

    // 키 없이 RestClient 를 연습해 보는 API: 내 서버의 회원 목록 API(/api/v1/members)를 RestClient 로 호출
    @GetMapping("/members-by-restclient")
    @Operation(summary = "[연습] RestClient 로 회원 목록 API 호출", description = "인증키 없이 RestClient 동작 확인용")
    public List<Member> getMembersByRestClient() {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();   // http://localhost:8080

        RestClient client = RestClient.builder()
                .baseUrl(baseUrl)                            // 기본 주소
                .defaultHeader("My-Header", "day10")         // 모든 요청에 붙는 헤더
                .build();

        return client.get()
                .uri("/api/v1/members")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Member>>() {});   // JSON 배열 → List<Member>
    }
}
