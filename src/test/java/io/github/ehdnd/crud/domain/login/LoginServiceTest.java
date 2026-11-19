package io.github.ehdnd.crud.domain.login;

import static io.github.ehdnd.crud.support.ErrorCodeAssertions.assertErrorCode;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import io.github.ehdnd.crud.global.error.ErrorCode;
import org.junit.jupiter.api.Test;

class LoginServiceTest {

  MemberRepository memberRepository = new MemberRepository();
  LoginService loginService = new LoginService(memberRepository);

  @Test
  void login() {
    // given
    Member member = memberRepository.save(new Member("loginId", "password"));

    // when
    Member loggedIn = loginService.login("loginId", "password");

    // then - 실제값, 기대값
    assertThat(loggedIn.getLoginId()).isEqualTo(member.getLoginId());
    assertThat(loggedIn.getPassword()).isEqualTo(member.getPassword());
  }

  @Test
  void login_idNotFound() {
    // given
    memberRepository.save(new Member("loginId", "password"));

    // when, then
    assertErrorCode(() -> loginService.login("other", "password"), ErrorCode.LOGIN_FAILED);
  }

  @Test
  void login_wrongPassword() {
    // given
    memberRepository.save(new Member("loginId", "password"));

    // when, then
    assertErrorCode(() -> loginService.login("loginId", "wrong"), ErrorCode.LOGIN_FAILED);
  }

  @Test
  void me() {
    // given
    Member member = memberRepository.save(new Member("loginId", "password"));

    // when
    Member me = loginService.me(member.getId());

    // then
    assertThat(me.getLoginId()).isEqualTo(member.getLoginId());
  }

  @Test
  void me_memberNotFound() {
    // when, then
    assertErrorCode(() -> loginService.me(1L), ErrorCode.UNAUTHORIZED);
  }
}