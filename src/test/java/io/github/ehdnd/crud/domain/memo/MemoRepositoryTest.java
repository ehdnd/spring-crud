package io.github.ehdnd.crud.domain.memo;

import static org.assertj.core.api.Assertions.*;

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
    Memo memo = new Memo("A", "B");

    // when
    memoRepository.save(memo);

    // then
    Memo findMemo = memoRepository.findById(memo.getId());
    assertThat(findMemo).isEqualTo(memo);
  }
}