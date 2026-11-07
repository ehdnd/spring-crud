package io.github.ehdnd.crud.web.member;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import io.github.ehdnd.crud.domain.member.MemberService;
import io.github.ehdnd.crud.web.login.Login;
import io.github.ehdnd.crud.web.member.dto.MemberPasswordUpdateRequest;
import io.github.ehdnd.crud.web.member.dto.MemberResponse;
import io.github.ehdnd.crud.web.member.dto.MemberSaveRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

  private final MemberRepository memberRepository;
  private final MemberService memberService;

  @GetMapping()
  public List<MemberResponse> members() {
    return memberRepository.findAll().stream().map(MemberResponse::from).toList();
  }

  @PostMapping()
  public MemberResponse add(@Validated @RequestBody MemberSaveRequest memberSaveRequest) {

    Member member = memberService.join(memberSaveRequest.loginId(), memberSaveRequest.password());
    if (member == null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
    }

    return MemberResponse.from(member);
  }

  @GetMapping("/{id}")
  public MemberResponse member(@PathVariable Long id) {

    Member member = memberRepository.findById(id).orElse(null);
    if (member == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return MemberResponse.from(member);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping("/me/password")
  public void editPassword(@Login Long memberId,
      @Validated @RequestBody MemberPasswordUpdateRequest memberPasswordUpdateRequest) {

    Member member = memberRepository.findById(memberId).orElse(null);
    if (member == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    if (!member.matchesPassword(memberPasswordUpdateRequest.currentPassword())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
    }

    memberRepository.updatePassword(memberId, memberPasswordUpdateRequest.newPassword());

    // TODO 에러코드 만들기
    // 실무 표준: ErrorCode + BusinessException + @RestControllerAdvice
    // global/error/ErrorCode.java - enum 으로 관리
    // global/error/BusinessException
    // HTTP 응답으로 반환은 전역 핸들러 하나가 한다고? web/GlobalExceptionHandler

  }

}
