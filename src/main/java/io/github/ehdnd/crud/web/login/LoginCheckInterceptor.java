package io.github.ehdnd.crud.web.login;

import io.github.ehdnd.crud.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

  // 인증을 담당 (인가가 아님)

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER_ID) == null) {
      log.info("미인증 사용자 요청");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // TODO 예외처리 단계에서 응답 형식 통일
      return false;
    }
    return true;
  }
}
