package io.github.ehdnd.crud.domain.login;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

  private final MemberRepository memberRepository;

  public Optional<Member> login(String loginId, String password) {
    return memberRepository.findByLoginId(loginId)
        .filter(m -> m.matchesPassword(password));
  }
}
