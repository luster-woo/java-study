package day07.a_di;

public class SWasher implements Washer {
    public SWasher() {
        System.out.println("SWasher 생성");
    }

    @Override
    public void wash(String clothes) {
        System.out.println("SWasher로 세탁: " + clothes);
    }
}
