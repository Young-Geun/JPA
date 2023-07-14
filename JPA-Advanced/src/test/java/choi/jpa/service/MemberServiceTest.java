package choi.jpa.service;

import choi.jpa.domain.Member;
import choi.jpa.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    public void join() throws Exception {
        // given
        Member member = new Member();
        member.setName("choi");

        // when
        Long saveId = memberService.join(member);

        // then
        /*
            테스트 코드에서 @Transactional를 사용하게 되면 rollback이 되기 때문에 콘솔창에 insert쿼리를 찾아볼 수 없다.
            쿼리를 확인하고 싶을 경우, 아래 2개의 방법을 사용할 수 있다.
            1. @Transactional(false)로 옵션 주기 : insert 쿼리를 확인할 수 있으나, 테스트 데이터가 그대로 저장된다.
            2. em.flush() : insert 쿼리를 확인할 수 있고, 테스트 데이터도 지워진다.
         */
        em.flush();
        Assert.assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class)
    public void Duplicate() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("choi");

        Member member2 = new Member();
        member2.setName("choi");

        // when
        memberService.join(member1);
        memberService.join(member2);

        // then
        Assert.fail("예외가 발생해야 한다.");
    }

}