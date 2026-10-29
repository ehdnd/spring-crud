package io.github.ehdnd.crud.web.memo;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemoController.class)
class MemoControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MemoRepository memoRepository;

  @Test
  void add() throws Exception {
    // given
    Memo savedMemo = new Memo("title", "content");
    savedMemo.setId(1L);
    given(memoRepository.save(any(Memo.class))).willReturn(savedMemo);

    // when, then
    mockMvc.perform(post("/memos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "title", "content": "content"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("title"))
        .andExpect(jsonPath("$.content").value("content"));
  }

  @Test
  void add_blankTitle() throws Exception {
    // when, then
    mockMvc.perform(post("/memos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": " ", "content": "content"}
                """))
        .andExpect(status().isBadRequest());

    then(memoRepository).should(never()).save(any(Memo.class));
  }

  @Test
  void add_tooLongTitle() throws Exception {
    String title = "a".repeat(21);

    // when, then
    mockMvc.perform(post("/memos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "%s", "content": "content"}
                """.formatted(title)))
        .andExpect(status().isBadRequest());

    then(memoRepository).should(never()).save(any(Memo.class));
  }

  @Test
  void add_blankContent() throws Exception {
    // when, then
    mockMvc.perform(post("/memos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "title", "content": " "}
                """))
        .andExpect(status().isBadRequest());

    then(memoRepository).should(never()).save(any(Memo.class));
  }

  @Test
  void add_tooLongContent() throws Exception {
    String content = "a".repeat(1001);

    // when, then
    mockMvc.perform(post("/memos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "title", "content": "%s"}
                """.formatted(content)))
        .andExpect(status().isBadRequest());

    then(memoRepository).should(never()).save(any(Memo.class));
  }

  @Test
  void add_maxLength() throws Exception {
    String title = "a".repeat(20);
    String content = "a".repeat(1000);

    // when, then
    mockMvc.perform(post("/memos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "%s", "content": "%s"}
                """.formatted(title, content)))
        .andExpect(status().isOk());

    then(memoRepository).should().save(any(Memo.class));
  }

  @Test
  void edit() throws Exception {
    // given
    Memo updatedMemo = new Memo("newTitle", "newContent");
    updatedMemo.setId(1L);
    given(memoRepository.update(eq(1L), any(Memo.class))).willReturn(updatedMemo);

    // when, then
    mockMvc.perform(patch("/memos/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "newTitle", "content": "newContent"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("newTitle"))
        .andExpect(jsonPath("$.content").value("newContent"));

    then(memoRepository).should().update(eq(1L), argThat(memo ->
        memo.getTitle().equals("newTitle") && memo.getContent().equals("newContent")));
  }

  @Test
  void edit_blankTitle() throws Exception {
    // when, then
    mockMvc.perform(patch("/memos/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": " ", "content": "newContent"}
                """))
        .andExpect(status().isBadRequest());

    then(memoRepository).should(never()).update(any(), any(Memo.class));
  }

  @Test
  void edit_tooLongTitle() throws Exception {
    String title = "a".repeat(21);

    // when, then
    mockMvc.perform(patch("/memos/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "%s", "content": "newContent"}
                """.formatted(title)))
        .andExpect(status().isBadRequest());

    then(memoRepository).should(never()).update(any(), any(Memo.class));
  }

  @Test
  void edit_blankContent() throws Exception {
    // when, then
    mockMvc.perform(patch("/memos/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "newTitle", "content": " "}
                """))
        .andExpect(status().isBadRequest());

    then(memoRepository).should(never()).update(any(), any(Memo.class));
  }

  @Test
  void edit_tooLongContent() throws Exception {
    String content = "a".repeat(1001);

    // when, then
    mockMvc.perform(patch("/memos/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "newTitle", "content": "%s"}
                """.formatted(content)))
        .andExpect(status().isBadRequest());

    then(memoRepository).should(never()).update(any(), any(Memo.class));
  }

  @Test
  void edit_maxLength() throws Exception {
    String title = "a".repeat(20);
    String content = "a".repeat(1000);

    // when, then
    mockMvc.perform(patch("/memos/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title": "%s", "content": "%s"}
                """.formatted(title, content)))
        .andExpect(status().isOk());

    then(memoRepository).should().update(eq(1L), any(Memo.class));
  }
}
