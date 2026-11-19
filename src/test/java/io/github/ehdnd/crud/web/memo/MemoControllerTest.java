package io.github.ehdnd.crud.web.memo;

import static io.github.ehdnd.crud.support.MemoFixture.createMemo;
import static io.github.ehdnd.crud.support.TestSessions.loginSession;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ehdnd.crud.domain.memo.Memo;
import io.github.ehdnd.crud.domain.memo.MemoService;
import io.github.ehdnd.crud.global.error.BusinessException;
import io.github.ehdnd.crud.global.error.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemoController.class)
class MemoControllerTest {

  // 매핑, 요청변환, 검증, 다음 계층 호출, 응답 반환, 예외, 상태코드
  // -> 요청 해석 (검증, 로그인), 서비스에 인자 전달, 응답으로 변환만 남는다.

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MemoService memoService;

  static Stream<Arguments> invalidRequests() {
    return Stream.of(
        argumentSet("title 공백", " ", "content"),
        argumentSet("title 21자", "a".repeat(21), "content"),
        argumentSet("content 공백", "title", " "),
        argumentSet("content 1001자", "title", "a".repeat(1001))
    );
  }

  @Test
  void memo() throws Exception {
    // 내 메모를 조회하면 200 + 정상 응답
    // given
    Memo memo = createMemo(1L, "title", "content", 10L);
    ReflectionTestUtils.setField(memo, "createdAt", LocalDateTime.of(2026, 11, 14, 10, 0, 0));
    ReflectionTestUtils.setField(memo, "updatedAt", LocalDateTime.of(2026, 11, 14, 11, 30, 0));

    given(memoService.getMemo(10L, 1L)).willReturn(memo);

    // when, then
    getMemo(1L, loginSession(10L))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "id": 1,
              "title": "title",
              "content": "content",
              "createdAt": "2026-11-14T10:00:00",
              "updatedAt": "2026-11-14T11:30:00"
            }
            """, JsonCompareMode.STRICT));
  }

  @Test
  void memo_notFound() throws Exception {
    given(memoService.getMemo(10L, 1L)).willThrow(new BusinessException(ErrorCode.MEMO_NOT_FOUND));

    getMemo(1L, loginSession(10L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MEMO_NOT_FOUND"))
        .andExpect(jsonPath("$.detail").exists());
  }

  @Test
  void memos() throws Exception {
    // 서비스가 준 목록은 JSON 배열로 변경
    // 리포지토리: 두 명의 메모가 섞여있을 때 한 회원의 메모만 돌려주는가 - `MemoRepositoryTest`

    given(memoService.findMemos(10L)).willReturn(List.of(createMemo(1L, "title", "content", 10L)));

    getMemos(loginSession(10L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("title"))
        .andExpect(jsonPath("$[0].content").value("content"));
  }

  @Test
  void add() throws Exception {
    // given
    given(memoService.addMemo("title", "content", 10L)).willReturn(
        createMemo(1L, "title", "content", 10L));

    // when, then
    postMemo("title", "content", loginSession(10L))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("title"))
        .andExpect(jsonPath("$.content").value("content"));
  }

  @ParameterizedTest(name = "add - {argumentSetName}")
  @MethodSource("invalidRequests")
  void add_invalid(String title, String content) throws Exception {
    // when, then
    postMemo(title, content, loginSession(10L))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_INPUT"));

    then(memoService).shouldHaveNoInteractions();
  }

  @Test
  void add_maxLength() throws Exception {
    // given
    String maxTitle = "a".repeat(20);
    String maxContent = "a".repeat(1000);
    given(memoService.addMemo(maxTitle, maxContent, 10L))
        .willReturn(createMemo(1L, maxTitle, maxContent, 10L));

    // when, then
    postMemo(maxTitle, maxContent, loginSession(10L))
        .andExpect(status().isCreated());
  }

  @Test
  void add_notLoggedIn() throws Exception {
    // when, then
    postMemo("title", "content", new MockHttpSession())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

    then(memoService).shouldHaveNoInteractions();
  }

  @Test
  void edit() throws Exception {
    // given
    Memo memo = createMemo(1L, "newTitle", "newContent", 10L);
    given(memoService.updateMemo(10L, 1L, "newTitle", "newContent")).willReturn(memo);

    // when, then
    putMemo(1L, "newTitle", "newContent", loginSession(10L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("newTitle"))
        .andExpect(jsonPath("$.content").value("newContent"));
  }

  @ParameterizedTest(name = "edit - {argumentSetName}")
  @MethodSource("invalidRequests")
  void edit_invalid(String title, String content) throws Exception {
    putMemo(1L, title, content, loginSession(10L))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_INPUT"));

    then(memoService).shouldHaveNoInteractions();
  }

  @Test
  void edit_maxLength() throws Exception {
    // given
    String maxTitle = "a".repeat(20);
    String maxContent = "a".repeat(1000);
    Memo memo = createMemo(1L, maxTitle, maxContent, 10L);

    given(memoService.updateMemo(10L, 1L, maxTitle, maxContent)).willReturn(memo);

    // when, then
    putMemo(1L, maxTitle, maxContent, loginSession(10L))
        .andExpect(status().isOk());
  }

  // === HELPERS ===

  private ResultActions getMemo(Long memoId, MockHttpSession session)
      throws Exception {
    return mockMvc.perform(get("/memos/" + memoId)
        .session(session));
  }

  private ResultActions getMemos(MockHttpSession session)
      throws Exception {
    return mockMvc.perform(get("/memos")
        .session(session));
  }

  private ResultActions postMemo(String title, String content, MockHttpSession session)
      throws Exception {
    return mockMvc.perform(post("/memos")
        .session(session)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"title": "%s", "content": "%s"}
            """.formatted(title, content)));
  }

  private ResultActions putMemo(Long memoId, String title, String content,
      MockHttpSession session)
      throws Exception {
    return mockMvc.perform(put("/memos/{id}", memoId)
        .session(session)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"title": "%s", "content": "%s"}
            """.formatted(title, content)));
  }
}
