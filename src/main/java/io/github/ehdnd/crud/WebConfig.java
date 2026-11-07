package io.github.ehdnd.crud;

import io.github.ehdnd.crud.web.login.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginCheckInterceptor())
        .order(1)
        .addPathPatterns("/**")
        // 요청한 사람이 누군지 알아야 동작하는가
        .excludePathPatterns("/members", "/login", "/logout", "/members/*", "/error");
  }
}
