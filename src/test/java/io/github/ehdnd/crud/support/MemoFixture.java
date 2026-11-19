package io.github.ehdnd.crud.support;

import io.github.ehdnd.crud.domain.memo.Memo;
import org.springframework.test.util.ReflectionTestUtils;

public final class MemoFixture {

  private MemoFixture() {
  }

  public static Memo createMemo(Long id, String title, String content, Long memberId) {
    Memo memo = new Memo(title, content, memberId);
    ReflectionTestUtils.setField(memo, "id", id);
    return memo;
  }
}
