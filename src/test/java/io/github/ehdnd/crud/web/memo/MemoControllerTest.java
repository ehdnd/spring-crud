package io.github.ehdnd.crud.web.memo;

import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ehdnd.crud.domain.memo.Memo;
import io.github.ehdnd.crud.domain.memo.MemoRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(MemoController.class)
class MemoControllerTest {

  // 매핑, 요청변환, 검증, 다음 계층 호출, 응답 반환, 예외, 상태코드

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  MemoRepository memoRepository;

  /**
   * 검증 실패 입력값 목록
   * '@MethodSource("invalidRequests")' 가 붙은 테스트가 실행될 때 JUnit 이 호출
   * Arguments 하나 = 테스트 1회 실행
   * (설명, title, content) 순서로 테스트 메서드 파라미터에 들어간다.
   */
  static Stream<Arguments> invalidRequests() {
    return Stream.of(
        argumentSet("title 공백", " ", "content"),
        argumentSet("title 21자", "a".repeat(21), "content"),
        argumentSet("content 공백", "title", " "),
        argumentSet("content 1001자", "title", "a".repeat(1001))
    );
  }

  @Test
  void add() throws Exception {
    // given
    given(memoRepository.save(any(Memo.class))).willReturn(memo(1L, "title", "content"));

    // when, then
    postMemo("title", "content")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("title"))
        .andExpect(jsonPath("$.content").value("content"));
  }

  @ParameterizedTest(name = "add - {argumentSetName}")
  @MethodSource("invalidRequests")
  void add_invalid(String title, String content) throws Exception {
    // when, then
    postMemo(title, content).andExpect(status().isBadRequest());

    then(memoRepository).should(never()).save(any(Memo.class));
  }

  @Test
  void add_maxLength() throws Exception {
    // when, then
    postMemo("a".repeat(20), "a".repeat(1000)).andExpect(status().isOk());

    then(memoRepository).should().save(any(Memo.class));
  }

  @Test
  void edit() throws Exception {
    // given
    Memo updatedMemo = memo(1L, "newTitle", "newContent");
    given(memoRepository.update(eq(1L), any(Memo.class))).willReturn(updatedMemo);

    // when, then
    patchMemo(1L, "newTitle", "newContent")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("newTitle"))
        .andExpect(jsonPath("$.content").value("newContent"));

    then(memoRepository).should().update(eq(1L), argThat(memo ->
        memo.getTitle().equals("newTitle") && memo.getContent().equals("newContent")));
  }

  @ParameterizedTest(name = "edit - {argumentSetName}")
  @MethodSource("invalidRequests")
  void edit_invalid(String title, String content) throws Exception {
    patchMemo(1L, title, content).andExpect(status().isBadRequest());

    then(memoRepository).should(never()).update(any(), any(Memo.class));
  }


  @Test
  void edit_maxLength() throws Exception {
    // when, then
    patchMemo(1L, "a".repeat(20), "a".repeat(1000)).andExpect(status().isOk());

    then(memoRepository).should().update(eq(1L), any(Memo.class));
  }

  // === HELPERS ===

  private ResultActions postMemo(String title, String content) throws Exception {
    return mockMvc.perform(post("/memos")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"title": "%s", "content": "%s"}
            """.formatted(title, content)));
  }

  private ResultActions patchMemo(Long id, String title, String content) throws Exception {

    return mockMvc.perform(patch("/memos/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"title": "%s", "content": "%s"}
            """.formatted(title, content)));
  }

  private static Memo memo(Long id, String title, String content) {
    Memo memo = new Memo(title, content);
    memo.setId(id);
    return memo;
  }
}
