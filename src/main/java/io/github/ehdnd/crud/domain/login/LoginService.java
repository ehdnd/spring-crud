package io.github.ehdnd.crud.domain.login;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import io.github.ehdnd.crud.web.login.dto.LoginRequest;
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

  public Member login(LoginRequest loginRequest) {

    Member member = memberRepository.findByLoginId(loginRequest.loginId())
        .filter(m -> m.getPassword().equals(loginRequest.password()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    log.info("login success member={}", member);

    return member;
  }
}
