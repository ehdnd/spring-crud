package io.github.ehdnd.crud.domain.login;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

  private final MemberRepository memberRepository;

  public Member login(String loginId, String password) {

    Member member = memberRepository.findByLoginId(loginId)
        .filter(m -> m.getPassword().equals(password))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    log.info("login success member={}", member);

    return member;
  }
}
