package io.github.ehdnd.crud.support;

import io.github.ehdnd.crud.web.SessionConst;
import org.springframework.mock.web.MockHttpSession;

public final class TestSessions {

  private TestSessions() {
  }

  public static MockHttpSession loginSession(Long memberId) {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER_ID, memberId);
    return session;
  }
}
