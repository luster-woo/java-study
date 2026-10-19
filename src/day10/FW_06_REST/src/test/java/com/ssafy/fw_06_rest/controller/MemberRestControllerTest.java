package com.ssafy.fw_06_rest.controller;

import com.ssafy.fw_06_rest.dto.Member;
import com.ssafy.fw_06_rest.exception.MemberNotFoundException;
import com.ssafy.fw_06_rest.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberRestController.class)   // 웹 계층만 로딩 (RestExceptionHandler 같은 advice 도 포함)
class MemberRestControllerTest {

    @Autowired
    MockMvc mock;

    @MockitoBean
    MemberService mService;                // 가짜 서비스

    @Test
    void registMemberTest() throws Exception {
        // given: 서버가 @RequestBody 이므로 JSON 문자열로 요청
        String json = """
                {"email": "mvctest@ssafy.com", "name": "hong", "password": "pass"}
                """;
        when(mService.registMember(any(Member.class))).thenReturn(1);

        // when & then
        mock.perform(post("/api/v1/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isCreated())                                  // 201
            .andExpect(jsonPath("$.member.email", equalTo("mvctest@ssafy.com")))
            .andExpect(jsonPath("$.member.name", equalTo("hong")));
    }

    @Test
    void getMembersTest() throws Exception {
        when(mService.getMembers()).thenReturn(List.of(
                Member.builder().mno(1).email("a@ssafy.com").name("kim").build(),
                Member.builder().mno(2).email("b@ssafy.com").name("lee").build()));

        mock.perform(get("/api/v1/members"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))                             // JSON 배열
            .andExpect(jsonPath("$[1].name", equalTo("lee")));
    }

    @Test
    void getMemberTest() throws Exception {
        when(mService.getMember(1)).thenReturn(
                Member.builder().mno(1).email("a@ssafy.com").name("kim").build());

        mock.perform(get("/api/v1/members/1"))                                // @PathVariable
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mno", equalTo(1)))
            .andExpect(jsonPath("$.email", equalTo("a@ssafy.com")));
    }

    @Test
    void getMemberNotFoundTest() throws Exception {
        when(mService.getMember(99)).thenThrow(new MemberNotFoundException(99));

        mock.perform(get("/api/v1/members/99"))
            .andExpect(status().isNotFound())                                 // 404
            .andExpect(jsonPath("$.detail", equalTo("99번 회원이 없습니다.")));  // ProblemDetail
    }

    @Test
    void deleteMemberTest() throws Exception {
        mock.perform(delete("/api/v1/members/1"))
            .andExpect(status().isNoContent());                               // 204
    }

    @Test
    void deleteMemberNotFoundTest() throws Exception {
        doThrow(new MemberNotFoundException(99)).when(mService).deleteMember(99);

        mock.perform(delete("/api/v1/members/99"))
            .andExpect(status().isNotFound());
    }
}
