package choi.jpa.repository;

import choi.jpa.entity.Member;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

/*
    클래스명 규칙
    - 리포지토리 인터페이스 이름 + Impl
      Ex) MemberRepository + Impl = MemberRepositoryImpl

      => 설정으로 이름 규칙을 따르지 않아도 되지만 관례를 따르는 걸 권장
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m").getResultList();
    }
}
