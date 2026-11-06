package io.github.ehdnd.crud.domain.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  public Member join(String loginId, String password) {

    if (memberRepository.findByLoginId(loginId).isPresent()) {
      return null;
    }
    return memberRepository.save(new Member(loginId, password));
  }

  public Member changePassword(Long id, String currentPassword, String newPassword) {

    Member member = memberRepository.findById(id);
    if (member == null) {
      return null;
    }

    if (!member.matchesPassword(currentPassword)) {
      return null;
    }

    return memberRepository.updatePassword(id, newPassword);
  }
}
