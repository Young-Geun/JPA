package choi.jpa.api;

import choi.jpa.domain.Order;
import choi.jpa.domain.OrderItem;
import choi.jpa.repository.OrderRepository;
import choi.jpa.repository.OrderSearch;
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
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     *     - 엔티티가 변하면 API 스펙이 변한다.
     *     - 트랜잭션 안에서 지연 로딩 필요
     *     - 양방향 연관관계 문제
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기환
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); // Lazy 강제 초기화
        }

        return all;
    }

}
