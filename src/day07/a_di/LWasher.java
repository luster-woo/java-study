package day07.a_di;

public class LWasher implements Washer {
    public LWasher() {
        System.out.println("LWasher 생성");
    }

    @Override
    public void wash(String clothes) {
        System.out.println("LWasher로 세탁: " + clothes);
    }
}
