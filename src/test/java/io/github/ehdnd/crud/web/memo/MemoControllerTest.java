package io.github.ehdnd.crud.web.memo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ehdnd.crud.domain.memo.Memo;
import io.github.ehdnd.crud.domain.memo.MemoRepository;
import io.github.ehdnd.crud.domain.memo.MemoService;
import io.github.ehdnd.crud.web.SessionConst;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemoController.class)
@Import(MemoService.class)
class MemoControllerTest {

  // 매핑, 요청변환, 검증, 다음 계층 호출, 응답 반환, 예외, 상태코드

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MemoRepository memoRepository;

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
    Memo memo = memo(1L, "title", "content", 1L);
    ReflectionTestUtils.setField(memo, "createdAt", LocalDateTime.of(2026, 11, 14, 10, 0, 0));
    ReflectionTestUtils.setField(memo, "updatedAt", LocalDateTime.of(2026, 11, 14, 11, 30, 0));

    given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

    // when, then
    getMemo(1L, loginSession(1L))
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
    // 없는 memoId 조회하면 404
    given(memoRepository.findById(1L)).willReturn(Optional.empty());

    getMemo(1L, loginSession(1L))
        .andExpect(status().isNotFound());
  }

  @Test
  void memo_notMyMemo() throws Exception {
    Memo memo = memo(1L, "title", "content", 1L);
    given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

    getMemo(1L, loginSession(2L))
        .andExpect(status().isNotFound());
  }

  @Test
  void memos() throws Exception {
    // 두 회원이 메모 모두 조회 시 자신의 메모만 제공
    // 컨트롤러: 리포지토리에서 받은 결과를 그대로 리턴하는가
    // 리포지토리: 두 명의 메모가 섞여있을 때 한 회원의 메모만 돌려주는가 - `MemoRepositoryTest`

    given(memoRepository.findByMemberId(1L)).willReturn(List.of(memo(1L, "title", "content", 1L)));

    getMemos(loginSession(1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("title"))
        .andExpect(jsonPath("$[0].content").value("content"));
  }

  @Test
  void add() throws Exception {
    // given
    given(memoRepository.save(any(Memo.class))).willReturn(memo(1L, "title", "content", 1L));

    // when, then
    postMemo("title", "content", loginSession(1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("title"))
        .andExpect(jsonPath("$.content").value("content"))
        .andExpect(jsonPath("$.memberId").doesNotExist());

    ArgumentCaptor<Memo> captor = ArgumentCaptor.forClass(Memo.class);
    then(memoRepository).should().save(captor.capture());
    assertThat(captor.getValue()).usingRecursiveComparison()
        .isEqualTo(new Memo("title", "content", 1L));
  }

  @ParameterizedTest(name = "add - {argumentSetName}")
  @MethodSource("invalidRequests")
  void add_invalid(String title, String content) throws Exception {
    // when, then
    postMemo(title, content, loginSession(1L))
        .andExpect(status().isBadRequest());

    then(memoRepository).should(never()).save(any(Memo.class));
  }

  @Test
  void add_maxLength() throws Exception {
    // given
    given(memoRepository.save(any(Memo.class))).willAnswer(inv -> inv.getArgument(0));

    // when, then
    postMemo("a".repeat(20), "a".repeat(1000), loginSession(1L))
        .andExpect(status().isOk());

    then(memoRepository).should().save(any(Memo.class));
  }

  @Test
  void add_notLoggedIn() throws Exception {
    // when, then
    postMemo("title", "content", new MockHttpSession())
        .andExpect(status().isUnauthorized());

    then(memoRepository).shouldHaveNoInteractions();
  }

  @Test
  void edit() throws Exception {
    // given
    Memo memo = memo(1L, "title", "content", 1L);
    given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

    // when, then
    putMemo(1L, "newTitle", "newContent", loginSession(1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("newTitle"))
        .andExpect(jsonPath("$.content").value("newContent"))
        .andExpect(jsonPath("$.memberId").doesNotExist());

    assertThat(memo.getTitle()).isEqualTo("newTitle");
    assertThat(memo.getContent()).isEqualTo("newContent");
  }

  @ParameterizedTest(name = "edit - {argumentSetName}")
  @MethodSource("invalidRequests")
  void edit_invalid(String title, String content) throws Exception {
    putMemo(1L, title, content, loginSession(1L))
        .andExpect(status().isBadRequest());

    // 검증에 실패해서 findById 도 호출되지 않았음을 검증.
    then(memoRepository).shouldHaveNoInteractions();
  }


  @Test
  void edit_maxLength() throws Exception {
    // given
    Memo memo = memo(1L, "title", "content", 1L);
    given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

    // when, then
    putMemo(1L, "a".repeat(20), "a".repeat(1000), loginSession(1L))
        .andExpect(status().isOk());
  }

  @Test
  void edit_notFound() throws Exception {
    // given
    given(memoRepository.findById(1L)).willReturn(Optional.empty());

    // when, then
    putMemo(1L, "a".repeat(20), "a".repeat(1000), loginSession(1L))
        .andExpect(status().isNotFound());
  }

  @Test
  void edit_notMyMemo() throws Exception {
    // given
    Memo memo = memo(1L, "title", "content", 1L);
    given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

    // when, then
    putMemo(1L, "newTitle", "newContent", loginSession(2L))
        .andExpect(status().isNotFound());

    assertThat(memo.getTitle()).isEqualTo("title");
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

  private static MockHttpSession loginSession(Long memberId) {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, memberId);
    return session;
  }

  private static Memo memo(Long id, String title, String content, Long memberId) {
    Memo memo = new Memo(title, content, memberId);
    ReflectionTestUtils.setField(memo, "id", id);
    return memo;
  }
}
