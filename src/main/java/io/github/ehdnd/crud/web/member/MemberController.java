package io.github.ehdnd.crud.web.member;

import io.github.ehdnd.crud.domain.member.Member;
import io.github.ehdnd.crud.domain.member.MemberRepository;
import io.github.ehdnd.crud.web.member.dto.MemberResponse;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {

  private final MemberRepository memberRepository;

  @GetMapping()
  public List<MemberResponse> members() {
    List<Member> members = memberRepository.findAll();
    Stream<MemberResponse> responseStream = members.stream()
        .map(member -> new MemberResponse(member.getId(), member.getLoginId()));
    return responseStream.toList();
  }


}
