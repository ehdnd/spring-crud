package io.github.ehdnd.crud.web.login;

import io.github.ehdnd.crud.domain.login.LoginService;
import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import io.github.ehdnd.crud.web.SessionConst;
import io.github.ehdnd.crud.web.login.dto.LoginRequest;
import io.github.ehdnd.crud.web.member.dto.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

  private final MemberRepository memberRepository;
  private final LoginService loginService;

  @PostMapping("/login")
  public MemberResponse login(@Validated @RequestBody LoginRequest loginRequest,
      HttpServletRequest request) {

    Member member = loginService.login(loginRequest.loginId(), loginRequest.password())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    log.info("login success member={}", member);

    HttpSession session = request.getSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, member.getId());
    request.changeSessionId();

    return MemberResponse.from(member);
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
  }

  @GetMapping("/me")
  public MemberResponse me(@Login Long memberId) {
    // 인터셉터 이후 (true) = 세션이 있고 LOGIN_MEMBER_ID 존재도 보장됨
    Member member = memberRepository.findById(memberId).orElse(null);
    if (member == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    return MemberResponse.from(member);
  }
}
