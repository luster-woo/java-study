package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 상품 서비스는 상품 리포지토리에 단순히 위임만 하는 클래스
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * 상품 수정 (7장) — 변경 감지 사용 (권장)
     * 컨트롤러에서 엔티티를 만들지 말고, 식별자와 변경할 데이터만 넘겨받아
     * 트랜잭션 안에서 영속 상태 엔티티를 조회해 직접 바꾼다 → 커밋 시 UPDATE
     */
    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity) {
        Item item = itemRepository.findOne(id); // 영속 상태
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        // itemRepository.save() 호출 필요 없음
        // 더 좋게: item.change(name, price, stockQuantity) 같은 의미 있는 메서드를 엔티티에 만들기
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
