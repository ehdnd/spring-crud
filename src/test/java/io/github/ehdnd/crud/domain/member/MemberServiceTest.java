package io.github.ehdnd.crud.domain.member;

import static io.github.ehdnd.crud.support.ErrorCodeAssertions.assertErrorCode;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.ehdnd.crud.global.error.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.Test;

class MemberServiceTest {

  MemberRepository memberRepository = new MemberRepository();
  MemberService memberService = new MemberService(memberRepository);

  @Test
  void join() {
    // given
    // when
    Member member = memberService.join("loginId", "password");

    // then
    assertThat(member.getLoginId()).isEqualTo("loginId");
    assertThat(member.getPassword()).isEqualTo("password");

    assertThat(memberRepository.findById(member.getId())).contains(member);
  }

  @Test
  void join_duplicateLoginId() {
    // given
    memberRepository.save(new Member("loginId", "password"));

    // when, then
    assertErrorCode(() -> memberService.join("loginId", "different"), ErrorCode.DUPLICATE_LOGIN_ID);

    assertThat(memberRepository.findAll()).hasSize(1);
  }

  @Test
  void findMembers() {
    // given
    Member member1 = memberRepository.save(new Member("loginId1", "password"));
    Member member2 = memberRepository.save(new Member("loginId2", "password"));

    // when
    List<Member> members = memberService.findMembers();

    // then
    assertThat(members).containsExactlyInAnyOrder(member1, member2);
  }

  @Test
  void getMemberById() {
    // given
    Member member = memberRepository.save(new Member("loginId", "password"));

    // when
    Member found = memberService.getMemberById(member.getId());

    // then
    assertThat(found).isEqualTo(member);
  }

  @Test
  void getMemberById_notFound() {
    // when, then
    assertErrorCode(() -> memberService.getMemberById(1L), ErrorCode.MEMBER_NOT_FOUND);
  }

  @Test
  void updatePassword() {
    // given
    Member member = memberRepository.save(new Member("loginId", "password"));

    // when
    memberService.updatePassword(member.getId(), "password", "new");

    // then
    assertThat(member.getPassword()).isEqualTo("new");
  }

  @Test
  void updatePassword_memberNotFound() {
    // when, then
    assertErrorCode(() -> memberService.updatePassword(1L, "old", "new"), ErrorCode.UNAUTHORIZED);
  }

  @Test
  void updatePassword_wrongPassword() {
    // given
    Member member = memberRepository.save(new Member("loginId", "password"));

    // when, then
    assertErrorCode(() -> memberService.updatePassword(member.getId(), "wrong", "new"),
        ErrorCode.PASSWORD_MISMATCH);

    assertThat(member.getPassword()).isEqualTo("password");
  }
}