package io.github.ehdnd.crud.web.error;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ehdnd.crud.domain.memo.MemoService;
import io.github.ehdnd.crud.web.SessionConst;
import io.github.ehdnd.crud.web.memo.MemoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemoController.class)
class GlobalExceptionHandlerTest {

  // 에러 응답 본문의 모양은 여기서만 확인한다. (엔드포인트는 Memo 를 빌려 쓴다)
  // 각 컨트롤러 테스트는 상태 코드와 code 만 확인한다.

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MemoService memoService;

  @Test
  void validationFailure() throws Exception {
    // message 문구는 로케일에 따라 달라지므로 값이 아니라 존재만 확인
    mockMvc.perform(post("/memos")
            .session(loginSession(10L))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": " ", "content": "content"}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.detail").exists())
        .andExpect(jsonPath("$.errors.length()").value(1))
        .andExpect(jsonPath("$.errors[0].field").value("title"))
        .andExpect(jsonPath("$.errors[0].message").isNotEmpty());
  }

  @Test
  void unexpectedException() throws Exception {
    // 예상 못 한 예외 -> 500 + 고정 문구 (예외 메시지는 응답에 나가지 않는다)
    given(memoService.getMemo(10L, 1L)).willThrow(new IllegalStateException("내부 정보"));

    mockMvc.perform(get("/memos/1").session(loginSession(10L)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
        .andExpect(jsonPath("$.detail").value("서버 오류가 발생했습니다."));
  }

  private static MockHttpSession loginSession(Long memberId) {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, memberId);
    return session;
  }
}
