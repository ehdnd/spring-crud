package io.github.ehdnd.crud.domain.memo;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class MemoRepositoryTest {

  MemoRepository memoRepository = new MemoRepository();

  @AfterEach
  void afterEach() {
    memoRepository.clear();
  }

  @Test
  void save() {
    // given
    LocalDateTime now = LocalDateTime.now();
    Memo memo = new Memo("A", "B", now, now);

    // when
    memoRepository.save(memo);

    // then
    Memo findMemo = memoRepository.findById(memo.getId());
    assertThat(findMemo).isEqualTo(memo);
  }
}