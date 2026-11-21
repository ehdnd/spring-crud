package io.github.ehdnd.crud.web.member;

import static io.github.ehdnd.crud.support.MemberFixture.createMember;
import static io.github.ehdnd.crud.support.TestSessions.loginSession;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberService;
import io.github.ehdnd.crud.global.error.BusinessException;
import io.github.ehdnd.crud.global.error.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MemberService memberService;

  @Test
  void members() throws Exception {
    // given
    Member member1 = createMember(1L, "loginId1", "password1");
    Member member2 = createMember(2L, "loginId2", "password2");
    given(memberService.findMembers()).willReturn(List.of(member1, member2));

    // when, then
    getMembers()
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].loginId").value("loginId1"))
        .andExpect(jsonPath("$[0].password").doesNotExist())
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].loginId").value("loginId2"))
        .andExpect(jsonPath("$[1].password").doesNotExist());
  }

  @Test
  void add() throws Exception {
    // given
    given(memberService.join("loginId", "password")).willReturn(
        createMember(1L, "loginId", "password"));

    // when, then
    postMember("loginId", "password")
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.loginId").value("loginId"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  // 검증 규칙은 DTO 테스트가 수행
  @Test
  void add_invalid() throws Exception {
    // when, then
    postMember("", "password")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_INPUT"));

    // 검증은 메서드 호출 전에 실행된다.
    then(memberService).shouldHaveNoInteractions();
  }

  @Test
  void add_duplicateLoginId() throws Exception {
    // given
    given(memberService.join("loginId", "password")).willThrow(
        new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID));

    // when, then
    postMember("loginId", "password")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("DUPLICATE_LOGIN_ID"));
  }

  @Test
  void member() throws Exception {
    // given
    given(memberService.getMemberById(1L)).willReturn(createMember(1L, "loginId", "password"));

    // when, then
    getMember(1L)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.loginId").value("loginId"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  void member_notFound() throws Exception {
    // given
    given(memberService.getMemberById(1L)).willThrow(
        new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

    // when, then
    getMember(1L)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MEMBER_NOT_FOUND"));
  }

  @Test
  void editPassword() throws Exception {
    // when, then
    patchPassword("old", "new", loginSession(1L))
        .andExpect(status().isNoContent());

    // 서비스를 믿고 리포지토리에게 요청 전달했는지만 확인하면 되는거 아닌가?
    then(memberService).should().updatePassword(1L, "old", "new");
  }

  @Test
  void editPassword_notLoggedIn() throws Exception {
    // 인터셉터가 throw 한다.
    // when, then
    patchPassword("password", "newPassword", new MockHttpSession())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

    then(memberService).shouldHaveNoInteractions();
  }

  @Test
  void editPassword_invalid() throws Exception {
    // when, then
    patchPassword("password", "", loginSession(1L))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_INPUT"));

    then(memberService).shouldHaveNoInteractions();
  }

  @Test
  void editPassword_wrongCurrentPassword() throws Exception {
    // given
    willThrow(new BusinessException(ErrorCode.PASSWORD_MISMATCH)).given(memberService)
        .updatePassword(1L, "wrong", "new");

    // when, then
    patchPassword("wrong", "new", loginSession(1L))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("PASSWORD_MISMATCH"));
  }

  @Test
  void editPassword_staleSession() throws Exception {
    // given: 세션의 memberId(1L)는 있지만 그 회원이 없다 -> 서비스가 UNAUTHORIZED
    willThrow(new BusinessException(ErrorCode.UNAUTHORIZED)).given(memberService)
        .updatePassword(1L, "password", "newPassword");

    // when, then
    patchPassword("password", "newPassword", loginSession(1L))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

    // 업데이트 여부는 서비스가 맡는거다.
  }

  // === HELPERS ===

  private ResultActions getMembers() throws Exception {
    return mockMvc.perform(get("/members"));
  }

  private ResultActions postMember(String loginId, String password) throws Exception {
    return mockMvc.perform(post("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"loginId": "%s", "password": "%s"}
            """.formatted(loginId, password)));
  }

  private ResultActions getMember(Long id) throws Exception {
    return mockMvc.perform(get("/members/{id}", id));
  }

  private ResultActions patchPassword(String currentPassword, String newPassword,
      MockHttpSession session)
      throws Exception {
    return mockMvc.perform(patch("/members/me/password")
        .session(session)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"currentPassword": "%s", "newPassword": "%s"}
            """.formatted(currentPassword, newPassword)));
  }
}
