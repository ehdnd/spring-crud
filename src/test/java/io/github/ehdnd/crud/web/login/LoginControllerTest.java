package io.github.ehdnd.crud.web.login;


import io.github.ehdnd.crud.domain.login.LoginService;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MemberRepository memberRepository;
  @MockitoBean
  LoginService loginService;

  @Test
  void login() {
    // 세션에 id가 들어갔나
  }

  @Test
  void logout() {
    // 세션이 무효화 되었나
  }
}