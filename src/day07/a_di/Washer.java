package day07.a_di;

// 세탁기 "규격" - WasherUser는 이 인터페이스만 알고, 실제 어떤 세탁기인지는 모름
public interface Washer {
    void wash(String clothes);
}
