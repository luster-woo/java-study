package extra.movie;

/*
 * 클래스 다이어그램 읽는 법
 *   빨간 네모(□) = private   초록 동그라미(●) = public
 *   이름: 타입               → 필드          예) id: int        → private int id;
 *   메서드(파라미터): 리턴타입 → 메서드        예) getId(): int  → public int getId()
 *   C 표시                    → 생성자
 *   밑줄                      → static
 */
public class Movie {
    private int id;
    private String title;
    private String director;
    private String genre;
    private int runningTime;

    public Movie() {}

    public Movie(int id, String title, String director, String genre, int runningTime) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.runningTime = runningTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getRunningTime() { return runningTime; }
    public void setRunningTime(int runningTime) { this.runningTime = runningTime; }

    @Override
    public String toString() {
        return "Movie [id=" + id + ", title=" + title + ", director=" + director
                + ", genre=" + genre + ", runningTime=" + runningTime + "]";
    }
}
