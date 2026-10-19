-- [05, 06] 트랜잭션 실습용 테이블 (서버가 켜질 때마다 새로 만들어짐)
DROP TABLE IF EXISTS tx_address;
DROP TABLE IF EXISTS tx_log;
DROP TABLE IF EXISTS tx_member;

CREATE TABLE tx_member (
    mno  INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- 주소는 회원을 참조(FK) → 회원을 지우려면 주소부터 지워야 함 (교재 예시와 같은 상황)
CREATE TABLE tx_address (
    ano   INT AUTO_INCREMENT PRIMARY KEY,
    mno   INT NOT NULL,
    title VARCHAR(100),
    FOREIGN KEY (mno) REFERENCES tx_member (mno)
);

-- 전파 속성(REQUIRES_NEW) 실습용 로그 테이블
CREATE TABLE tx_log (
    id  INT AUTO_INCREMENT PRIMARY KEY,
    msg VARCHAR(200)
);
