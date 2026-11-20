package io.github.ehdnd.crud.web.member;

import static io.github.ehdnd.crud.support.MemberFixture.createMember;
import static io.github.ehdnd.crud.support.TestSessions.loginSession;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
    // given
    given(memberService.join("", "password")).willThrow(
        new BusinessException(ErrorCode.INVALID_INPUT));

    // when, then
    postMember("", "password")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
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
  @Disabled
  void updatePassword() throws Exception {
    // given
    // 쓸게 없나?

    // when, then
    patchPassword("password", "newPassword", loginSession(1L))
        .andExpect(status().isNoContent());

    // 리포지토리를 믿고 리포지토리에게 요청 전달했는지만 확인하면 되는거 아닌가? - 네
  }

  @Test
  @Disabled
  void updatePassword_notLoggedIn() throws Exception {
    // given
    // throw 는 어떻게 하지?

    patchPassword("password", "newPassword", new MockHttpSession())
        .andExpect(status().isUnauthorized());

    then(memberRepository).shouldHaveNoInteractions();
  }

  @Test
  @Disabled
  void updatePassword_invalid() throws Exception {
    // 마찬가지

    // when, then
    patchPassword("password", "", loginSession(1L))
        .andExpect(status().isBadRequest());
    // TODO json 필드로 에러 확인 필요

    // 검증은 컨트롤러 메서드 진입 전에 실패하므로 findById 조차 호출되지 않아야 한다.
    then(memberRepository).shouldHaveNoInteractions();
  }

  @Test
  @Disabled
  void updatePassword_wrongCurrentPassword() throws Exception {
    // 마찬가지

    // given
    given(memberRepository.findById(1L)).willReturn(
        Optional.of(createMember(1L, "loginId", "password")));

    // when, then
    patchPassword("wrongPassword", "newPassword", loginSession(1L))
        .andExpect(status().isBadRequest())
        // 검증 실패 400 과 구분: 비밀번호 불일치로 난 400 인지 사유까지 확인한다.
        // TODO 비밀번호 불일치와 검증실패 구분이 필요하다.
        .andExpect(jsonPath("$.detail").value("현재 비밀번호가 일치하지 않습니다."));

    // 서비스에서 예외 던지는거임 (인터셉터 아님)
  }

  @Test
  @Disabled
  void updatePassword_staleSession() throws Exception {
    // 마찬가지

    // given
    given(memberRepository.findById(1L)).willReturn(Optional.empty());

    // when, then
    patchPassword("password", "newPassword", loginSession(1L))
        .andExpect(status().isUnauthorized());

    // 에러 나고 업데이트 안된거 확인해야하나? 에러낫으면 요청 안가는거 아닌가? 서비스를 믿어야하는거지 이제.
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
