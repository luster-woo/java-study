package day04.e_generic;

// 와일드카드 예제용 상속 구조 : Object ← Person ← SpiderMan ← VenomSpider
class Person {
    String name() { return "Person"; }
    @Override public String toString() { return name(); }
}

class SpiderMan extends Person {
    @Override String name() { return "SpiderMan"; }
}

class VenomSpider extends SpiderMan {
    @Override String name() { return "VenomSpider"; }
}
