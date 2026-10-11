package extra.movie;

import java.util.Arrays;

/*
 * 영화 객체배열 관리
 *   MAX_SIZE  : 밑줄 = static → private static final (상수)
 *   movieList : Movie 객체의 "주소"를 담는 배열
 *   size      : 지금 몇 개 들어 있는지 = 다음에 넣을 인덱스
 *
 *  다이어그램의 ◇── 선(집합 관계) : MovieManager가 Movie 여러 개를 "가지고" 관리한다
 */
public class MovieManager {
    private static final int MAX_SIZE = 100;
    private Movie[] movieList = new Movie[MAX_SIZE];
    private int size = 0;

    // 추가 : 빈 칸(size 위치)에 넣고 size 증가
    public void add(Movie movie) {
        if (size == MAX_SIZE) {
            System.out.println("더 이상 영화를 추가할 수 없습니다.");
            return;
        }
        movieList[size++] = movie;
    }

    // 전체 목록 : 뒤쪽 null 칸은 빼고 실제 들어 있는 만큼만 복사해서 돌려줌
    public Movie[] getList() {
        return Arrays.copyOf(movieList, size);
    }

    // 제목으로 찾기 : 처음 찾은 영화 하나, 없으면 null
    // (다이어그램 철자는 serachByTitle 인데 오타라서 searchByTitle 로 씀)
    public Movie searchByTitle(String title) {
        for (int i = 0; i < size; i++) {                 // movieList.length 까지 돌면 null에서 NPE!
            if (movieList[i].getTitle().equals(title)) { // 문자열 비교는 == 말고 equals
                return movieList[i];
            }
        }
        return null;
    }
}
