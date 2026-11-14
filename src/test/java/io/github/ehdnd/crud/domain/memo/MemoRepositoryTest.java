package io.github.ehdnd.crud.domain.memo;

import static org.assertj.core.api.Assertions.assertThat;

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
    Memo memo = new Memo("A", "B", 1L);

    // when
    memoRepository.save(memo);

    // then
    Memo findMemo = memoRepository.findById(memo.getId()).orElse(null);
    assertThat(findMemo).isEqualTo(memo);
  }

  @Test
  void findByMemberId() {
    // TODO 테스트 작성하기
  }
}