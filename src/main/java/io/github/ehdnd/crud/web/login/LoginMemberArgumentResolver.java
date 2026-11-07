package io.github.ehdnd.crud.web.login;

import io.github.ehdnd.crud.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public @Nullable Object resolveArgument(MethodParameter parameter,
      @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
      @Nullable WebDataBinderFactory binderFactory) throws Exception {

    HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
    HttpSession session = request.getSession(false);
    if (session == null) {
      return null;
    }

    return session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    log.info("LoginMemberArgumentResolver.supportsParameter");
    boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
    boolean hasLongType = Long.class.isAssignableFrom(parameter.getParameterType());
    return hasLoginAnnotation && hasLongType;
  }
}
