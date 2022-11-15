package choi.jpa.api;

import choi.jpa.domain.Order;
import choi.jpa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * ManyToOne OR OneToOne 관계에서의 최적화
 * Order
 * Order > Member
 * Order > Delivery
 *
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     *     : 아래의 문제 발생
     *       1. 양방향 관계 문제 발생
     *         -> @JsonIgnore을 사용해서 해결할 수는 있음
     *       2. No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor... 오류 발생
     *         -> Hibernate5Module을 등록해서 해결할 수는 있음
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }

        return all;
    }

}
