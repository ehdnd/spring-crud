package io.github.ehdnd.crud.web.login;

import io.github.ehdnd.crud.global.error.BusinessException;
import io.github.ehdnd.crud.global.error.ErrorCode;
import io.github.ehdnd.crud.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

  // 인증을 담당 (인가가 아님)

  @Override
  public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER_ID) == null) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }
    return true;
  }
}
