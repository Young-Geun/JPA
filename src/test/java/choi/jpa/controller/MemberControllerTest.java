package choi.jpa.controller;

import choi.jpa.domain.Member;
import choi.jpa.service.MemberService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberControllerTest {

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;

    @Test
    public void findAll() {
        List<Member> lists = memberService.findMembers();
        assertEquals(0, lists.size());
    }
}