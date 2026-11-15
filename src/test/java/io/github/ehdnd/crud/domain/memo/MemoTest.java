package io.github.ehdnd.crud.domain.memo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MemoTest {

  @Test
  void isOwnedBy() {
    // given
    Memo memo = new Memo("title", "content", 10L);

    // when, then
    assertThat(memo.isOwnedBy(10L)).isTrue();
  }

  @Test
  void isOwnedBy_otherMember() {
    // given
    Memo memo = new Memo("title", "content", 10L);

    // when, then
    assertThat(memo.isOwnedBy(20L)).isFalse();
  }

  @Test
  void isOwnedBy_null() {
    // given
    Memo memo = new Memo("title", "content", 10L);

    // when, then
    assertThat(memo.isOwnedBy(null)).isFalse();
  }

  @Test
  void update() {
    // given
    Memo memo = new Memo("title", "content", 10L);

    // when
    memo.update("newTitle", "newContent");

    // then
    assertThat(memo.getTitle()).isEqualTo("newTitle");
    assertThat(memo.getContent()).isEqualTo("newContent");
  }

  @Test
  void update_updatedAt() {
    // given
    LocalDateTime past = LocalDateTime.of(2026, 1, 1, 0, 0);
    Memo memo = new Memo("title", "content", 10L);
    memo.setCreatedAt(past);
    memo.setUpdatedAt(past);

    // when
    memo.update("newTitle", "newContent");

    // then: 수정 시각만 갱신되고 생성 시각은 그대로
    assertThat(memo.getUpdatedAt()).isAfter(past);
    assertThat(memo.getCreatedAt()).isEqualTo(past);
  }
}
