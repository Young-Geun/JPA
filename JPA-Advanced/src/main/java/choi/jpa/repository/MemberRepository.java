package choi.jpa.repository;

import choi.jpa.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

/*
     - EntityManager는 기본적으로 @Autowired를 사용할 수 없으며, @PersistenceContext를 사용해야한다.
       하지만 Spring Data JPA의 경우,
       생성자 주입 방식을 지원하여 @PersistenceContext를 사용하지 않고 아래의 코드처럼 생성자 주입방식을 사용할 수 있다.
 */
//    @PersistenceContext
//    private EntityManager em;

    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
