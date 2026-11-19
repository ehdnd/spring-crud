package io.github.ehdnd.crud.web.login;


import static io.github.ehdnd.crud.support.ErrorCodeAssertions.assertErrorCode;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.ehdnd.crud.global.error.ErrorCode;
import io.github.ehdnd.crud.web.SessionConst;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

class LoginCheckInterceptorTest {

  LoginCheckInterceptor interceptor = new LoginCheckInterceptor();
  MockHttpServletRequest request = new MockHttpServletRequest();
  MockHttpServletResponse response = new MockHttpServletResponse();

  @Test
  void preHandle_noSession() {
    // when, then
    assertErrorCode(() -> interceptor.preHandle(request, response, new Object()),
        ErrorCode.UNAUTHORIZED);

    assertThat(request.getSession(false)).isNull();
  }

  @Test
  void preHandle_noLoginAttribute() {
    // given
    request.setSession(new MockHttpSession());

    // when, then
    assertErrorCode(() -> interceptor.preHandle(request, response, new Object()),
        ErrorCode.UNAUTHORIZED);
  }

  @Test
  void preHandle_loggedIn() {
    // given
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, 1L);
    request.setSession(session);

    // when
    boolean result = interceptor.preHandle(request, response, new Object());

    // then
    assertThat(result).isTrue();
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
  }
}