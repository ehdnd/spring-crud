package io.github.ehdnd.crud.web.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import io.github.ehdnd.crud.web.member.dto.MemberResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MemberRepository memberRepository;

  @Test
  void members() throws Exception {
    // given
    Member member1 = member(1L, "loginId1", "password1");
    Member member2 = member(2L, "loginId2", "password2");
    given(memberRepository.findAll()).willReturn(List.of(member1, member2));

    // when, then
    mockMvc.perform(get("/members"))
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
    given(memberRepository.findByLoginId("loginId")).willReturn(Optional.empty());
    given(memberRepository.save(any(Member.class))).willReturn(member(1L, "loginId", "password"));

    // when, then
    mockMvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"loginId": "loginId", "password": "password"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.loginId").value("loginId"))
        .andExpect(jsonPath("$.password").doesNotExist());

    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    then(memberRepository).should().save(captor.capture());
    Member captured = captor.getValue();
    assertThat(captured.getId()).isNull();
    assertThat(captured.getLoginId()).isEqualTo("loginId");
    assertThat(captured.getPassword()).isEqualTo("password");
  }

  @Test
  void member() throws Exception {
    // given
    given(memberRepository.findById(any())).willReturn(member(1L, "loginId", "password"));

    // when, then
    mockMvc.perform(get("/members/1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.loginId").value("loginId"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  // === HELPER ===

  private static Member member(Long id, String loginId, String password) {
    Member member = new Member(loginId, password);
    ReflectionTestUtils.setField(member, "id", id);
    return member;
  }
}
