package extra.movie;

/*
 * [실행 결과]
 * --- 전체 영화 목록 ---
 * Movie [id=1, title=인셉션, director=크리스토퍼 놀란, genre=SF, runningTime=148]
 * Movie [id=2, title=기생충, director=봉준호, genre=드라마, runningTime=132]
 * Movie [id=3, title=인터스텔라, director=크리스토퍼 놀란, genre=SF, runningTime=169]
 * --- 제목 검색 ---
 * 기생충 → Movie [id=2, title=기생충, director=봉준호, genre=드라마, runningTime=132]
 * 타이타닉 → null
 */
public class MovieTest {
    public static void main(String[] args) {
        MovieManager mm = new MovieManager();

        // 1. 추가 - 생성자 2가지 모두 사용
        mm.add(new Movie(1, "인셉션", "크리스토퍼 놀란", "SF", 148));
        mm.add(new Movie(2, "기생충", "봉준호", "드라마", 132));
        Movie m3 = new Movie();                          // 기본 생성자 + setter
        m3.setId(3);
        m3.setTitle("인터스텔라");
        m3.setDirector("크리스토퍼 놀란");
        m3.setGenre("SF");
        m3.setRunningTime(169);
        mm.add(m3);

        // 2. 전체 목록
        System.out.println("--- 전체 영화 목록 ---");
        for (Movie m : mm.getList()) {
            System.out.println(m);
        }

        // 3. 제목으로 검색
        System.out.println("--- 제목 검색 ---");
        System.out.println("기생충 → " + mm.searchByTitle("기생충"));
        System.out.println("타이타닉 → " + mm.searchByTitle("타이타닉"));
    }
}
