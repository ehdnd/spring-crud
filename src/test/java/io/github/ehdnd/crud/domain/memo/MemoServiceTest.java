package io.github.ehdnd.crud.domain.memo;

import static io.github.ehdnd.crud.support.ErrorCodeAssertions.assertErrorCode;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.ehdnd.crud.global.error.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.Test;

class MemoServiceTest {

  MemoRepository memoRepository = new MemoRepository();
  MemoService memoService = new MemoService(memoRepository);

  @Test
  void findMemos() {
    // 이미 memoRepository 에서 검증한다.
    // given
    Memo memo1 = memoRepository.save(new Memo("title1", "content1", 10L));
    Memo memo2 = memoRepository.save(new Memo("title2", "content2", 10L));
    Memo memoA = memoRepository.save(new Memo("title", "content", 20L));

    // when
    List<Memo> memos = memoService.findMemos(10L);

    // then
    assertThat(memos).containsExactlyInAnyOrder(memo1, memo2);
  }

  @Test
  void getMemo() {
    // given
    Memo memo = memoRepository.save(new Memo("title", "content", 10L));

    // when
    Memo found = memoService.getMemo(10L, memo.getId());

    // then
    assertThat(found).isEqualTo(memo);
  }

  @Test
  void getMemo_notFound() {
    // when, then
    assertErrorCode(() -> memoService.getMemo(10L, 1L), ErrorCode.MEMO_NOT_FOUND);
  }

  @Test
  void getMemo_notMyMemo() {
    // given
    Memo memo = memoRepository.save(new Memo("title", "content", 10L));

    // when, then
    assertErrorCode(() -> memoService.getMemo(20L, memo.getId()), ErrorCode.MEMO_NOT_FOUND);
  }

  @Test
  void addMemo() {
    // given
    // when
    Memo memo = memoService.addMemo("title", "content", 10L);

    // then
    assertThat(memo.getTitle()).isEqualTo("title");
    assertThat(memo.getContent()).isEqualTo("content");
    assertThat(memo.getMemberId()).isEqualTo(10L);

    assertThat(memoRepository.findById(memo.getId())).contains(memo);
  }


  @Test
  void updateMemo() {
    // given
    Memo memo = memoRepository.save(new Memo("title", "content", 10L));

    // when
    Memo updated = memoService.updateMemo(10L, memo.getId(), "newTitle", "newContent");

    // then
    assertThat(updated.getTitle()).isEqualTo("newTitle");
    assertThat(updated.getContent()).isEqualTo("newContent");
    // 진짜 업데이트해서 리포에 저장된게 한개라는 건 뭐 따로 안해도 되나? 어디까지 해야하지
    // -> 이 메서드가 약속한 건? 수정 후 다시 조회하면 새 내용이 나온다.
  }

  @Test
  void updateMemo_notFound() {

    assertErrorCode(() -> memoService.updateMemo(10L, 1L, "newTitle", "newContent"),
        ErrorCode.MEMO_NOT_FOUND);
  }

  @Test
  void updateMemo_notMyMemo() {
    // given
    Memo memo = memoRepository.save(new Memo("title", "content", 10L));

    // when, then
    assertErrorCode(
        () -> memoService.updateMemo(20L, memo.getId(), "newTitle", "newContent"),
        ErrorCode.MEMO_NOT_FOUND);

    assertThat(memo.getTitle()).isEqualTo("title");
    assertThat(memo.getContent()).isEqualTo("content");
  }
}