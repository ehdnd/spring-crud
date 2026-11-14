package io.github.ehdnd.crud.domain.memo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
    // given
    Memo memoA = memoRepository.save(new Memo("A", "a", 1L));
    Memo memoB = memoRepository.save(new Memo("B", "b", 1L));
    memoRepository.save(new Memo("C", "c", 2L));

    // when
    List<Memo> result = memoRepository.findByMemberId(1L);

    // then
    assertThat(result).containsExactlyInAnyOrder(memoA, memoB);
  }

  @Test
  void findByMemberId_noMemo() {
    // given
    memoRepository.save(new Memo("A", "a", 1L));

    // when
    List<Memo> result = memoRepository.findByMemberId(2L);

    // then
    assertThat(result).isEmpty();
  }
}