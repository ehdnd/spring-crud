package io.github.ehdnd.crud.domain.memo;

import io.github.ehdnd.crud.global.error.BusinessException;
import io.github.ehdnd.crud.global.error.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoService {

  private final MemoRepository memoRepository;

  public List<Memo> findMemos(Long memberId) {
    return memoRepository.findByMemberId(memberId);
  }

  public Memo getMemo(Long memberId, Long memoId) {
    return memoRepository.findById(memoId).filter(m -> m.isOwnedBy(memberId))
        .orElseThrow(() -> new BusinessException(
            ErrorCode.MEMO_NOT_FOUND));
  }

  public Memo addMemo(String title, String content, Long memberId) {
    return memoRepository.save(new Memo(title, content, memberId));
  }

  public Memo updateMemo(Long memberId, Long memoId, String title, String content) {

    Memo memo = getMemo(memoId, memberId);
    memo.update(title, content);
    return memo;
  }
}
