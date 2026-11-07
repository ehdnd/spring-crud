package io.github.ehdnd.crud.web.login;

import static io.github.ehdnd.crud.support.MemberFixture.createMember;
import static io.github.ehdnd.crud.support.TestSessions.loginSession;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ehdnd.crud.domain.login.LoginService;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import io.github.ehdnd.crud.web.SessionConst;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MemberRepository memberRepository;
  @MockitoBean
  LoginService loginService;

  @Test
  void login() throws Exception {
    // 세션에 id가 들어갔나

    // 테스트 대상이 직접 호출하는 것만 stub
    given(loginService.login("loginId", "password"))
        .willReturn(Optional.of(createMember(1L, "loginId", "password")));

    // when, then
    postLogin("loginId", "password", new MockHttpSession())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.loginId").value("loginId"))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(request().sessionAttribute(SessionConst.LOGIN_MEMBER_ID, 1L));
  }


  @Test
  void login_changeSessionId() throws Exception {
    // given
    MockHttpSession session = new MockHttpSession();
    String beforeId = session.getId();

    given(loginService.login("loginId", "password"))
        .willReturn(Optional.of(createMember(1L, "loginId", "password")));

    // when
    postLogin("loginId", "password", session)
        .andExpect(status().isOk());

    // then
    assertThat(session.getId()).isNotEqualTo(beforeId);
    assertThat(session.getAttribute(SessionConst.LOGIN_MEMBER_ID)).isEqualTo(1L);
  }


  @Test
  void login_fail() throws Exception {
    // given
    given(loginService.login("loginId", "wrong")).willReturn(Optional.empty());

    // when, then
    postLogin("loginId", "wrong", new MockHttpSession())
        .andExpect(status().isUnauthorized())
        .andExpect(request().sessionAttributeDoesNotExist(SessionConst.LOGIN_MEMBER_ID));

  }

  @Test
  void login_invalid() throws Exception {
    postLogin("", "password", new MockHttpSession())
        .andExpect(status().isBadRequest());

    then(loginService).shouldHaveNoInteractions();
  }

  @Test
  void logout() throws Exception {
    // 세션이 무효화 되었나
    MockHttpSession mockHttpSession = new MockHttpSession();
    mockHttpSession.setAttribute(SessionConst.LOGIN_MEMBER_ID, 1L);

    // when, then
    mockMvc.perform(post("/logout")
            .session(mockHttpSession))
        .andExpect(status().isNoContent());

    assertThat(mockHttpSession.isInvalid()).isTrue();
  }

  @Test
  void me() throws Exception {
    // given
    given(memberRepository.findById(1L)).willReturn(Optional.of(createMember(1L, "loginId", "password")));

    // when, then
    getMe(loginSession(1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.loginId").value("loginId"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }


  @Test
  void me_notLoggedIn() throws Exception {
    // when, then
    mockMvc.perform(get("/me"))
        .andExpect(status().isUnauthorized());

    then(memberRepository).shouldHaveNoInteractions();
  }

  @Test
  void me_staleSession() throws Exception {
    // given
    given(memberRepository.findById(1L)).willReturn(Optional.empty());

    // when, then
    getMe(loginSession(1L))
        .andExpect(status().isUnauthorized());
  }

  // === HELPERS ===

  private ResultActions getMe(MockHttpSession session) throws Exception {
    return mockMvc.perform(get("/me")
        .session(session));
  }

  private ResultActions postLogin(String loginId, String password, MockHttpSession session)
      throws Exception {
    return mockMvc.perform(post("/login")
        .session(session)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"loginId":"%s", "password":"%s"}
            """.formatted(loginId, password))
    );
  }

}