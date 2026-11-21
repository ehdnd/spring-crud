package io.github.ehdnd.crud.web.member;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberService;
import io.github.ehdnd.crud.web.login.Login;
import io.github.ehdnd.crud.web.member.dto.MemberPasswordUpdateRequest;
import io.github.ehdnd.crud.web.member.dto.MemberResponse;
import io.github.ehdnd.crud.web.member.dto.MemberSaveRequest;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

  private final MemberService memberService;

  @GetMapping
  public List<MemberResponse> members() {
    return memberService.findMembers().stream().map(MemberResponse::from).toList();
  }

  @PostMapping
  public ResponseEntity<MemberResponse> add(
      @Validated @RequestBody MemberSaveRequest memberSaveRequest) {

    Member member = memberService.join(memberSaveRequest.loginId(), memberSaveRequest.password());
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(member.getId()).toUri();

    return ResponseEntity.created(location).body(MemberResponse.from(member));
  }

  @GetMapping("/{id}")
  public MemberResponse member(@PathVariable Long id) {

    return MemberResponse.from(memberService.getMemberById(id));
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping("/me/password")
  public void editPassword(@Login Long memberId,
      @Validated @RequestBody MemberPasswordUpdateRequest memberPasswordUpdateRequest) {

    memberService.updatePassword(memberId, memberPasswordUpdateRequest.currentPassword(),
        memberPasswordUpdateRequest.newPassword());
  }

}
