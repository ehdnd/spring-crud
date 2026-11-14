package io.github.ehdnd.crud.domain.memo;

import io.github.ehdnd.crud.global.error.BusinessException;
import io.github.ehdnd.crud.global.error.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemoService {

  // 이 유스케이스에서 없는 건 실패라 판단해 예외 던짐. 비즈니스 예외 발생.

  private final MemoRepository memoRepository;

  public MemoService(MemoRepository memoRepository) {
    this.memoRepository = memoRepository;
  }

  public List<Memo> findMemos(Long memberId) {
    return memoRepository.findByMemberId(memberId);
  }

  public Memo getMemo(Long memberId, Long memoId) {
    return memoRepository.findById(memoId).filter(m -> m.isOwnedBy(memberId))
        .orElseThrow(() -> new BusinessException(
            ErrorCode.MEMO_NOT_FOUND));
  }
}
