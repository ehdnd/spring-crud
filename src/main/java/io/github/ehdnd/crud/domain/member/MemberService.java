package io.github.ehdnd.crud.domain.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  public Member join(String loginId, String password) {

    if (memberRepository.findByLoginId(loginId).isPresent()) {
      return null;
    }
    return memberRepository.save(new Member(loginId, password));
  }

  // TODO getById 같은거 넣고 없으면 예외 처리해라
}
