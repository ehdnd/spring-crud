package io.github.ehdnd.crud.domain.member;

import io.github.ehdnd.crud.global.error.BusinessException;
import io.github.ehdnd.crud.global.error.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  public Member join(String loginId, String password) {

    // 서비스는 에러 메시지용, 실제로는 DB의 UNIQUE 제약.
    // TODO existsByLoginId 로 검사 메서드 분리하자
    if (memberRepository.findByLoginId(loginId).isPresent()) {
      throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
    }
    return memberRepository.save(new Member(loginId, password));
  }

  public List<Member> findMembers() {
    return memberRepository.findAll();
  }

  public Member getMemberById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
  }

  public void updatePassword(Long id, String oldPassword, String newPassword) {

    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

    if (!member.matchesPassword(oldPassword)) {
      throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
    }

    member.updatePassword(newPassword);
  }
}
