package choi.jpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")
    String getUsername();

    /*
        Open Proejctions
        => @Value 사용
           : Closed Projections 과 다르게 모든 필드를 가져온 후 명시된 필드를 추출
     */

}
