package io.github.ehdnd.crud.domain.login;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import io.github.ehdnd.crud.global.error.BusinessException;
import io.github.ehdnd.crud.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

  private final MemberRepository memberRepository;

  public Member login(String loginId, String password) {
    return memberRepository.findByLoginId(loginId)
        .filter(m -> m.matchesPassword(password))
        .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));
  }

  public Member me(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));
  }
}
