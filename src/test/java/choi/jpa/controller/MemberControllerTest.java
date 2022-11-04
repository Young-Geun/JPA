package choi.jpa.controller;

import choi.jpa.domain.Member;
import choi.jpa.repository.MemberRepository;
import choi.jpa.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional // test에 있으면 데이터를 롤백시킴
public class MemberControllerTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    public void findAll() {
        List<Member> lists = memberService.findMembers();
        assertEquals(0, lists.size());
    }

    @Test
    @Rollback(value = false) // DB의 데이터를 직접 보고 싶을 때, 설정
    public void save() {
        // given
        Member member = new Member();
        member.setName("choi");

        // when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());
    }

}