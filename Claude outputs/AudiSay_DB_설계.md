# 개요와 공통 규칙

AudiSay DB는 **18개 테이블**, 185개 칼럼이다. 팀원 ERD(30개)를 기준으로 1:1 테이블과 작은 목록 테이블을 합치고, 다른 칼럼으로 구할 수 있는 값과 쓰는 곳이 없는 값을 빼서 줄였다. 실제 DB는 PostgreSQL이고, 이 문서의 타입은 PostgreSQL 기준이다(ERDCloud 그림은 MySQL 표기라 TIMESTAMPTZ가 TIMESTAMP로, TEXT[]가 JSON으로 보인다).

| 영역 | 테이블 | 한 줄 설명 |
| --- | --- | --- |
| 회원 | users | 회원 계정과 시각장애인 인증 결과 |
| 회원 | user\_settings | 회원별 재생 설정 (1:1) |
| 영상 | videos | 유튜브 영상과 업로드 영상의 공통 정보 |
| 영상 | video\_uploads | 업로드 영상의 파일 정보 (1:1) |
| 영상 | favorites | 즐겨찾기 |
| 작업 | jobs | 영상 1개의 해설 생성 작업 |
| 작업 | job\_chunks | 긴 영상을 나눈 조각별 처리 상태 |
| 작업 | generation\_requests | 누가 언제 해설을 요청했는지, 하루 이용 횟수 계산 근거 |
| 해설 | descriptions | 영상의 해설 한 벌 |
| 해설 | characters | 등장인물과 별칭 |
| 해설 | transcript\_segments | 대사와 시각 |
| 해설 | scenes | 장면 시각과 장면 설명 |
| 해설 | screen\_texts | 화면에 나온 글자와 중요도 |
| 해설 | description\_lines | 시점 × 상세도별 해설 문장과 음성 |
| 해설 | chapters | 장면 목차와 검색 키워드 |
| 챗봇 | chat\_logs | 챗봇 입력 한 건과 처리 결과(질문이면 답변) |
| 운영 | ai\_call\_logs | AI 호출 한 건의 모델, 토큰, 비용 |
| 신고 | reports | 해설 문장 신고 |

**이름과 타입**

- 테이블과 칼럼 이름은 영어 소문자 snake\_case, 테이블은 복수형이다.
- 기본 키는 `id`(BIGINT, 자동 증가)다. 1:1 테이블(user\_settings, video\_uploads)은 상대 테이블의 id를 그대로 기본 키로 쓴다. 복합 키는 favorites(user\_id, video\_id) 하나다.
- 다른 테이블을 가리키는 칼럼은 `<대상>_id`로 끝난다.
- 정해진 값 목록이 있는 칼럼(상태, 종류 등)은 VARCHAR에 영어 대문자 값을 넣고, DB의 CHECK 제약으로 목록 밖의 값을 막는다. 값 목록은 각 칼럼 설명에 적었다.
- 영상 안의 시각은 밀리초 정수(`_ms`)로 저장한다. INTEGER로 약 596시간까지 담을 수 있다.
- 일시는 시간대가 포함된 TIMESTAMPTZ로 저장한다. 하루 이용 횟수 같은 날짜 계산은 한국 시간(KST) 기준이다.
- 별칭, 키워드처럼 짧은 목록은 TEXT[]로 저장한다.

**생성·삭제 규칙**

- `created_at`, `updated_at` 같은 기록 시각은 기본값이 현재 시각이다. `updated_at`은 수정할 때 애플리케이션(JPA)이 갱신한다.
- 회원은 실제로 지우지 않는다. 탈퇴하면 `withdrawn_at`에 탈퇴 시각을 기록한다(소프트 삭제).
- 해설(descriptions)을 지우면 그 아래 장면, 대사, 화면 글자, 해설 문장, 목차, 인물이 함께 지워진다. 단, 신고가 걸린 해설 문장이 있으면 지워지지 않는다(신고 근거 보호).
- 역할별 이용 제한 수치(최대 길이, 하루 횟수, 최대 용량)는 DB가 아니라 `application.yml`에 둔다. 카테고리 목록, 유튜브 카테고리 대응표, 카테고리별 해설 지침은 코드에 둔다.
- 처리 중에만 의미 있는 값(실시간 진행률, 답변 스트리밍 상태 등)은 Redis에 두고, DB에는 남겨야 하는 결과만 저장한다.

## 회원 영역

### users · 회원

가입한 사람 한 명당 한 행이다. 가입하면 `GENERAL`로 시작하고, 시각장애인 인증을 신청하면 인증 시각을 남기고 `VISUALLY_IMPAIRED`로 바뀐다. 인증은 형식상 자동 승인이라 서류 파일 칼럼은 없다. 탈퇴해도 행을 지우지 않는다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 회원 ID | BIGINT | N | 자동 증가 | 기본 키 |
| email | 이메일 | VARCHAR(255) | N |  | 로그인 ID. 탈퇴하지 않은 회원 사이에서만 중복 금지라, 탈퇴한 이메일로 다시 가입할 수 있다 |
| password\_hash | 비밀번호 | VARCHAR(100) | N |  | BCrypt로 암호화한 비밀번호. 원문은 저장하지 않는다 |
| name | 이름 | VARCHAR(50) | N |  | 화면과 관리자 목록에 보이는 이름 |
| role | 역할 | VARCHAR(20) | N | GENERAL | GENERAL(일반 회원) / VISUALLY\_IMPAIRED(시각장애인 회원) / ADMIN(관리자). 역할에 따라 이용 제한과 권한이 달라진다. VISUALLY\_IMPAIRED는 인증 시각이 있어야만 될 수 있다 |
| verified\_at | 시각장애인 인증 시각 | TIMESTAMPTZ | Y |  | 인증이 승인된 시각. 형식상 자동 승인이라 신청 즉시 기록한다 |
| withdrawn\_at | 탈퇴 시각 | TIMESTAMPTZ | Y |  | 탈퇴한 시각(소프트 삭제). 값이 있으면 로그인할 수 없다 |
| created\_at | 가입 시각 | TIMESTAMPTZ | N | 현재 시각 | 가입한 시각 |
| updated\_at | 수정 시각 | TIMESTAMPTZ | N | 현재 시각 | 회원 정보가 마지막으로 바뀐 시각 |

### user\_settings · 재생 설정

회원 한 명당 한 행이며 users와 1:1이다. 가입할 때 기본값으로 만들고, 플레이어를 열면 이 값으로 시작한다. 보는 중에 바꾼 설정도 여기에 저장돼 다음 영상에 이어진다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| user\_id | 회원 ID | BIGINT | N |  | 기본 키이자 users.id를 가리킨다. 회원이 지워지면 같이 지워진다 |
| detail\_level | 상세도 | SMALLINT | N | 2 | 해설을 얼마나 자세히 들을지. 1(최소) / 2(기본) / 3(최대). 상세도에 따라 재생할 해설 문장과 읽어 줄 화면 글자의 범위가 달라진다 |
| narration\_mode | 해설 방식 | VARCHAR(20) | N | AUTO | AUTO(빈 구간에 해설, 모자라면 멈춤) / WITH\_VIDEO(영상을 멈추지 않고 원래 소리를 줄여 해설) / PAUSE\_AFTER(해설마다 멈춤) |
| speech\_rate | 말하기 속도 | NUMERIC(2,1) | N | 1.0 | 해설 음성 재생 속도 0.5\~2.5배. 음성 파일을 다시 만들지 않고 재생 속도로 조절한다 |
| sound\_mix | 소리 구성 | VARCHAR(30) | N | DIALOGUE\_AND\_NARRATION | DIALOGUE\_ONLY(해설 끄기, 원래 소리만) / DIALOGUE\_AND\_NARRATION(원래 소리 + 해설) |
| subtitle\_reading | 자막 읽기 | VARCHAR(20) | N | DIFFERENT\_ONLY | ALL(모두 읽음) / DIFFERENT\_ONLY(대사와 다른 자막만) / OFF(읽지 않음) |
| original\_volume | 해설 중 원래 소리 크기 | SMALLINT | N | 100 | 해설이 나오는 동안 영상 소리 크기(%). 0\~100 |
| voice\_code | 해설 목소리 | VARCHAR(50) | Y |  | 선택한 TTS 목소리 코드. 비어 있으면 기본 목소리. TTS 서비스가 정해지면 값 목록을 정한다 (목소리 선택 P3) |
| seek\_step\_sec | n초 이동 간격 | SMALLINT | N | 10 | 재생바 화살표와 앞뒤 이동 키의 간격. 5 / 10 / 30초 |
| high\_contrast | 고대비 모드 | BOOLEAN | N | FALSE | 저시력 사용자를 위한 고대비 화면 켜기 |
| font\_size\_level | 글자 크기 | SMALLINT | N | 3 | 화면 글자 크기 단계 1\~5 (3이 보통) |
| tutorial\_completed\_at | 튜토리얼 완료 시각 | TIMESTAMPTZ | Y |  | 처음 쓸 때 나오는 음성 사용법 안내를 끝내거나 건너뛴 시각. 비어 있으면 다음 접속 때 튜토리얼을 보여 준다 |
| updated\_at | 수정 시각 | TIMESTAMPTZ | N | 현재 시각 | 설정을 마지막으로 바꾼 시각 |

## 영상 영역

### videos · 영상

유튜브 영상과 업로드 영상을 한 테이블에 담는다. 같은 유튜브 영상은 여러 명이 요청해도 한 행만 두고, 누가 요청했는지는 generation\_requests에 남긴다. 업로드 영상의 파일 정보는 video\_uploads에 따로 있다. 유튜브 영상은 youtube\_video\_id가 있고 uploader\_id가 비어 있어야 하며, 업로드 영상은 그 반대다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 영상 ID | BIGINT | N | 자동 증가 | 기본 키 |
| uploader\_id | 올린 회원 ID | BIGINT | Y |  | 영상을 올린 회원(users.id). 업로드 영상만 값이 있다. 비공개 영상은 이 회원과 관리자만 볼 수 있다 |
| source\_type | 출처 | VARCHAR(10) | N |  | YOUTUBE(유튜브 링크) / UPLOAD(파일 업로드). 처리 경로와 플레이어가 이 값으로 갈린다 |
| youtube\_video\_id | 유튜브 영상 ID | VARCHAR(20) | Y |  | 유튜브 링크에서 떼어 낸 11자리 ID (watch, youtu.be, shorts 형식 모두). 중복 금지라 같은 영상은 한 행만 있다. 유튜브 영상만 값이 있다 |
| title | 제목 | VARCHAR(300) | N |  | 유튜브는 YouTube API의 제목, 업로드는 올린 사람이 입력한 제목. Whisper 힌트 문장과 해설 프롬프트에도 넣는다 |
| source\_description | 유튜브 설명 | TEXT | Y |  | 유튜브 설명란 내용. 해설 생성 때 인물 이름·용어를 알아내는 참고 자료로 쓴다 |
| channel\_title | 채널명 | VARCHAR(200) | Y |  | 유튜브 채널 이름. 검색 결과와 목록에서 읽어 준다 |
| thumbnail\_url | 썸네일 주소 | VARCHAR(500) | Y |  | 목록에 보여 줄 썸네일 이미지 주소 (저시력·일반 회원용) |
| duration\_sec | 길이(초) | INTEGER | N |  | 영상 길이. 역할별 최대 길이 제한(일반 5분, 시각장애인 60분)과 조각 나누기에 쓴다 |
| category | 카테고리 | VARCHAR(20) | Y |  | EDUCATION(교육) / COOKING(요리·음식) / GAME(게임) / MUSIC(음악) / SCIENCE\_IT(과학·IT) / NEWS(뉴스) / ENTERTAINMENT(예능) / SPORTS(스포츠) / ETC(기타). 카테고리별 해설 지침을 고르는 데 쓴다. 분류 전에는 비어 있다 |
| visibility | 공개 범위 | VARCHAR(10) | N | PRIVATE | PUBLIC(모두에게 공개) / PRIVATE(올린 사람과 관리자만). 유튜브 영상은 PUBLIC으로 저장하고, 업로드 영상은 올릴 때 고른다 |
| status | 영상 상태 | VARCHAR(15) | N | REGISTERED | REGISTERED(등록됨, 해설 없음) / UPLOADING(파일 업로드 중) / READY(해설까지 준비됨, 재생 가능) / REJECTED(사전 확인에서 거절됨) / UNAVAILABLE(유튜브에서 삭제·비공개됨) |
| created\_at | 등록 시각 | TIMESTAMPTZ | N | 현재 시각 | 영상이 처음 등록된 시각 |
| updated\_at | 수정 시각 | TIMESTAMPTZ | N | 현재 시각 | 영상 정보가 마지막으로 바뀐 시각 |

### video\_uploads · 업로드 파일

업로드 영상 하나당 한 행이며 videos와 1:1이다. 브라우저가 S3에 직접 올리는 멀티파트 업로드와 ffmpeg 변환 상태를 기록한다. 유튜브 영상은 여기에 행이 없다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| video\_id | 영상 ID | BIGINT | N |  | 기본 키이자 videos.id를 가리킨다. 영상이 지워지면 같이 지워진다 |
| original\_key | 원본 파일 위치 | VARCHAR(500) | N |  | S3에 올라간 원본 파일의 키. 워커가 분석할 때 여기서 받는다 |
| s3\_upload\_id | 멀티파트 업로드 ID | VARCHAR(255) | Y |  | S3 멀티파트 업로드의 UploadId. 업로드가 끊기면 이 값으로 S3에 올라간 조각을 물어보고 이어서 올린다 |
| original\_filename | 원본 파일명 | VARCHAR(255) | N |  | 사용자가 올린 파일 이름 (예: 가족여행.mp4). 내 영상 목록에 표시 |
| content\_type | 파일 형식 | VARCHAR(30) | N |  | video/mp4 / video/quicktime(MOV) / video/webm. 업로드 사전 검사에서 다른 형식은 거절한다 |
| size\_bytes | 파일 크기 | BIGINT | N |  | 바이트 단위 용량. 0보다 커야 하고, 역할별 최대 용량(시각장애인 2GB)을 넘으면 거절한다 |
| upload\_status | 업로드 상태 | VARCHAR(15) | N | INITIATED | INITIATED(시작) / UPLOADING(조각 올리는 중) / COMPLETED(완료, 해설 생성 시작) / ABORTED(중단) |
| playback\_key | 재생용 파일 위치 | VARCHAR(500) | Y |  | ffmpeg로 변환한 재생용 파일의 S3 키. 플레이어는 이 파일을 재생한다. 질문 답변 때 그 시점 화면을 뽑는 데도 쓴다 |
| playback\_format | 재생 형식 | VARCHAR(5) | Y |  | MP4(시연용) / HLS(확장 시). 플레이어가 재생 방식을 고르는 데 쓴다 |
| transcode\_status | 변환 상태 | VARCHAR(10) | N | PENDING | PENDING(대기) / RUNNING(변환 중) / DONE(완료) / FAILED(실패). DONE이 돼야 재생할 수 있다 |
| created\_at | 생성 시각 | TIMESTAMPTZ | N | 현재 시각 | 업로드를 시작한 시각 |
| completed\_at | 업로드 완료 시각 | TIMESTAMPTZ | Y |  | 모든 조각이 올라가 업로드가 끝난 시각 |

### favorites · 즐겨찾기

회원이 즐겨찾기에 넣은 영상 하나당 한 행이다. (회원, 영상) 쌍이 기본 키라 같은 영상을 두 번 넣을 수 없다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| user\_id | 회원 ID | BIGINT | N |  | 기본 키(1). users.id. 회원이 지워지면 같이 지워진다 |
| video\_id | 영상 ID | BIGINT | N |  | 기본 키(2). videos.id. 영상이 지워지면 같이 지워진다. 영상이 UNAVAILABLE이면 목록에서 '더 이상 볼 수 없는 영상'으로 안내한다 |
| created\_at | 추가 시각 | TIMESTAMPTZ | N | 현재 시각 | 즐겨찾기에 넣은 시각. 목록 정렬에 쓴다 |

## 작업 영역

### jobs · 해설 생성 작업

영상 하나의 해설을 만드는 작업 한 건이다. 요청이 들어오면 작업을 만들고 RabbitMQ에 넣으며, 워커가 단계별로 처리하면서 상태와 진행률을 갱신한다. 실시간 진행률은 Redis와 SSE로 보내고 여기에는 최종 상태를 남긴다. 같은 영상에 진행 중(QUEUED, RUNNING)인 작업은 하나만 있을 수 있다. 상세 오류 내용은 DB가 아니라 서버·워커 로그에 남기고, 작업 ID로 찾는다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 작업 ID | BIGINT | N | 자동 증가 | 기본 키. SSE 진행률 구독과 큐 메시지에 이 번호를 쓴다 |
| video\_id | 영상 ID | BIGINT | N |  | 해설을 만들 영상(videos.id) |
| requested\_by | 요청 회원 ID | BIGINT | N |  | 작업을 처음 만든 회원(users.id). 다시 만들기는 관리자 |
| job\_type | 작업 종류 | VARCHAR(15) | N | GENERATE | GENERATE(처음 생성) / REGENERATE(관리자의 해설 다시 만들기) |
| pipeline | 처리 경로 | VARCHAR(15) | N |  | GEMINI\_URL(유튜브 주소를 Gemini에 넘김) / LOCAL(업로드 파일을 Whisper, 장면 추출, OCR로 분석) |
| status | 작업 상태 | VARCHAR(15) | N | QUEUED | QUEUED(대기) / RUNNING(처리 중) / SUCCEEDED(성공) / PARTIAL(일부 구간만 성공, 그 구간까지 제공) / FAILED(실패) / CANCELLED(취소) |
| current\_stage | 현재 단계 | VARCHAR(15) | Y |  | 지금 처리 중인 단계 (예: PREPARE, STT, SCENE, OCR, DESCRIBE, MERGE, TTS, SUMMARY). "음성 인식 중" 같은 진행 안내에 쓴다. 단계 이름은 1주차 계약에서 확정 |
| progress | 진행률 | SMALLINT | N | 0 | 0\~100. 워커가 보고한 마지막 값 |
| error\_code | 오류 코드 | VARCHAR(50) | Y |  | 실패 원인 코드. 다시 요청을 권할지(일시적 오류) 이유만 안내할지(영상 삭제·비공개 등) 고르는 데 쓰고, 실패 원인별 통계에도 쓴다 |
| queued\_at | 등록 시각 | TIMESTAMPTZ | N | 현재 시각 | 작업이 만들어져 큐에 들어간 시각 |
| started\_at | 시작 시각 | TIMESTAMPTZ | Y |  | 워커가 처음 처리를 시작한 시각. 대기 시간 = started\_at - queued\_at |
| finished\_at | 종료 시각 | TIMESTAMPTZ | Y |  | 성공·실패·취소로 끝난 시각. 생성 시간(NFR-08) = finished\_at - started\_at |

### job\_chunks · 작업 조각

긴 영상을 1\~2분 단위로 나눈 조각 하나당 한 행이다. 여러 워커가 조각을 동시에 처리하고, 실패한 조각만 최대 3번 다시 시도한다. 10분 이하 영상은 조각이 하나다. 조각별 처리 단계는 저장하지 않고, 작업 전체 단계는 jobs.current\_stage로 본다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 조각 ID | BIGINT | N | 자동 증가 | 기본 키 |
| job\_id | 작업 ID | BIGINT | N |  | 소속 작업(jobs.id). 작업이 지워지면 같이 지워진다 |
| chunk\_index | 조각 순서 | INTEGER | N |  | 0부터 시작하는 조각 번호. 같은 작업 안에서 중복 금지. 합칠 때 이 순서로 잇는다 |
| start\_ms | 시작 시각(ms) | INTEGER | N |  | 조각이 시작하는 영상 시각 |
| end\_ms | 끝 시각(ms) | INTEGER | N |  | 조각이 끝나는 영상 시각. 시작보다 커야 한다 |
| status | 조각 상태 | VARCHAR(10) | N | PENDING | PENDING(대기) / RUNNING(처리 중) / SUCCEEDED(성공) / FAILED(실패, 재시도 남음) / DEAD(3번 실패해 DLQ로 감) |
| attempts | 시도 횟수 | SMALLINT | N | 0 | 0\~3. 3번 실패하면 DEAD |
| context\_summary | 앞 조각 요약 | TEXT | Y |  | 긴 영상 3단계 처리에서 이 조각에 함께 넣는 전체 줄거리·인물·앞 조각 요약. 조각 경계에서 흐름이 끊기지 않게 한다 |
| last\_error | 마지막 오류 | TEXT | Y |  | 마지막 실패의 오류 내용 |
| worker\_id | 처리 워커 | VARCHAR(100) | Y |  | 이 조각을 가져간 워커 이름 (GPU 노트북 워커 포함). 하트비트가 끊기면 작업을 되돌리는 데 쓴다 |
| started\_at | 시작 시각 | TIMESTAMPTZ | Y |  | 마지막 시도를 시작한 시각 |
| finished\_at | 종료 시각 | TIMESTAMPTZ | Y |  | 성공 또는 최종 실패한 시각 |
| updated\_at | 수정 시각 | TIMESTAMPTZ | N | 현재 시각 | 상태가 마지막으로 바뀐 시각. 오래 RUNNING인 조각을 찾는 데 쓴다 |

### generation\_requests · 해설 생성 요청

회원이 해설을 요청하거나 영상을 올릴 때마다 한 행이다. 같은 영상을 여러 명이 요청해도 작업은 하나지만, 요청은 사람마다 남는다. **하루 이용 횟수 = 오늘 날짜에 counted가 TRUE인 행의 수**다. 내 영상 목록도 이 테이블로 만든다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 요청 ID | BIGINT | N | 자동 증가 | 기본 키 |
| user\_id | 회원 ID | BIGINT | N |  | 요청한 회원(users.id) |
| video\_id | 영상 ID | BIGINT | N |  | 요청한 영상(videos.id) |
| job\_id | 작업 ID | BIGINT | Y |  | 연결된 작업(jobs.id). 기존 해설을 그대로 쓴 경우(REUSED)나 거절된 경우는 비어 있다 |
| result | 요청 결과 | VARCHAR(15) | N |  | NEW\_JOB(새 작업 생성) / JOINED\_JOB(이미 진행 중인 작업에 합류) / REUSED(완성된 해설 바로 재생) / REJECTED(제한·사전 확인으로 거절) |
| counted | 횟수 차감 여부 | BOOLEAN | N |  | 이 요청이 하루 이용 횟수를 사용했는지. 새로 만들 때만 차감하고, 합류·재사용·거절은 차감하지 않는 것을 기본으로 한다 |
| request\_date | 요청 날짜 | DATE | N |  | 한국 시간(KST) 기준 날짜. 하루 이용 횟수를 이 날짜로 센다 |
| requested\_at | 요청 시각 | TIMESTAMPTZ | N | 현재 시각 | 요청한 시각. 내 영상 목록 정렬에 쓴다 |

## 해설 영역

해설 한 벌(descriptions) 아래에 장면, 대사, 화면 글자, 해설 문장, 목차, 인물이 달린다. 아래 테이블은 모두 description\_id로 해설에 묶이고, 해설이 지워지면 같이 지워진다.

### descriptions · 해설

영상의 해설 한 벌이다. 관리자가 해설을 다시 만들면 새 행이 생긴다. 새 해설이 완성되기 전까지는 기존 해설이 계속 재생되고, 완성되면 기존 해설은 ARCHIVED로 바뀐다. 영상당 PUBLISHED는 하나만 있을 수 있다. 만든 순서는 `id`나 `created_at`으로 알 수 있다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 해설 ID | BIGINT | N | 자동 증가 | 기본 키 |
| video\_id | 영상 ID | BIGINT | N |  | 해설의 영상(videos.id). 영상이 지워지면 같이 지워진다 |
| job\_id | 작업 ID | BIGINT | N |  | 이 해설을 만든 작업(jobs.id). 작업 하나가 해설 하나만 만든다(중복 금지) |
| status | 해설 상태 | VARCHAR(15) | N | GENERATING | GENERATING(만드는 중) / PUBLISHED(재생에 쓰는 현재 해설) / FAILED(생성 실패) / ARCHIVED(이전 해설 보관) |
| summary\_text | 30초 요약 | TEXT | Y |  | 영상을 열면 재생 전에 들려줄지 묻는 요약 문장. 길이(0\~30초)는 검증 후 결정 |
| summary\_audio\_key | 요약 음성 위치 | VARCHAR(500) | Y |  | 30초 요약을 TTS로 만든 음성 파일의 S3 키 |
| summary\_duration\_ms | 요약 음성 길이(ms) | INTEGER | Y |  | 요약 음성 길이. "약 25초짜리 요약이 있어요" 안내에 쓴다 |
| model\_name | 생성 모델 | VARCHAR(50) | Y |  | 해설을 만든 AI 모델 이름. 모델별 품질 비교에 쓴다 |
| created\_at | 생성 시각 | TIMESTAMPTZ | N | 현재 시각 | 해설 생성을 시작한 시각 |

### characters · 등장인물

해설에서 부르는 인물 한 명당 한 행이다. 같은 인물을 영상 끝까지 같은 이름으로 부르기 위한 테이블이다(인물 이름 일관성). 긴 영상의 조각을 나눠 처리할 때 각 조각에 이 목록을 함께 넘긴다. 이름을 모르는 사람은 AI가 붙인 임시 이름(예: 화자1)으로 만든다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 인물 ID | BIGINT | N | 자동 증가 | 기본 키 |
| description\_id | 해설 ID | BIGINT | N |  | 소속 해설(descriptions.id) |
| name | 인물 이름 | VARCHAR(100) | N |  | 해설에서 부르는 이름 (예: 민수, 7번 선수, 화자1). 같은 해설 안에서 중복 금지 |
| aliases | 별칭 | TEXT[] | Y |  | 같은 인물을 가리키는 다른 이름·등번호·방 번호 목록 (예: {7번, 쏘니}). "지금 노래하는 사람은 2번 방"처럼 화면 글자와 사람을 잇는 데 쓴다 |
| appearance | 외형 | TEXT | Y |  | 외형 묘사 (예: 20대 남자, 파란 후드). 이름을 모를 때 같은 사람을 알아보는 데 쓴다 |
| name\_source | 이름 출처 | VARCHAR(15) | Y |  | DIALOGUE(대사에서 부른 이름) / SCREEN\_TEXT(이름 자막·등번호) / AI(AI가 붙인 임시 이름) |

### transcript\_segments · 대사

영상에서 나온 말 한 덩어리에 한 행이다. 업로드 영상은 Whisper가, 유튜브 영상은 Gemini가 받아 적는다. 대사 사이 빈 구간을 계산해 해설을 넣고, 질문 답변에서 질문 시점 앞뒤 대사를 가져오는 데 쓴다. 누가 말했는지는 인물(characters)로 연결한다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 대사 ID | BIGINT | N | 자동 증가 | 기본 키 |
| description\_id | 해설 ID | BIGINT | N |  | 소속 해설(descriptions.id) |
| character\_id | 인물 ID | BIGINT | Y |  | 말한 사람(characters.id). 알 수 없으면 비어 있다. 인물이 지워지면 비워진다 |
| start\_ms | 시작 시각(ms) | INTEGER | N |  | 말이 시작하는 영상 시각 |
| end\_ms | 끝 시각(ms) | INTEGER | N |  | 말이 끝나는 영상 시각. 시작보다 같거나 커야 한다 |
| text | 대사 내용 | TEXT | N |  | 받아 적은 말. "말과 같은 자막" 판단과 질문 답변 근거에 쓴다 |

### scenes · 장면

화면이 바뀌는 단위로 나눈 장면 하나에 한 행이다. **질문 답변의 기본 자료**여서 장면 설명을 자세히 저장한다. 재생바를 움직이면 장면 이름을 읽어 준다. 같은 해설 안에서 장면 구간은 겹치지 않는다(경계가 맞닿는 것은 허용).

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 장면 ID | BIGINT | N | 자동 증가 | 기본 키 |
| description\_id | 해설 ID | BIGINT | N |  | 소속 해설(descriptions.id) |
| scene\_index | 장면 순서 | INTEGER | N |  | 1부터 시작하는 장면 번호. 같은 해설 안에서 중복 금지 |
| start\_ms | 시작 시각(ms) | INTEGER | N |  | 장면이 시작하는 영상 시각 |
| end\_ms | 끝 시각(ms) | INTEGER | N |  | 장면이 끝나는 영상 시각. 시작보다 커야 한다 |
| title | 장면 이름 | VARCHAR(100) | Y |  | 짧은 장면 이름 (예: 주방 장면, 첫 골). 재생바 안내와 "현재 위치 알려주기"에서 읽어 준다 |
| scene\_description | 장면 설명 | TEXT | N |  | 장면에 보이는 사람, 행동, 장소, 사물을 자세히 적은 글. 해설 문장보다 자세하며, 질문이 들어오면 이 글로 먼저 답한다. "지금 장면 설명" 버튼도 이 글을 쓴다 |
| keyframe\_key | 대표 프레임 위치 | VARCHAR(500) | Y |  | 장면을 대표하는 사진의 S3 키. 업로드 영상만 있다. 저장한 설명으로 답할 수 없을 때 이 사진을 AI에 보내 다시 본다 |

### screen\_texts · 화면 글자

화면에 나온 글자 덩어리 하나에 한 행이다. 중요도(상/중/하)와 상세도에 따라 읽을지, 원문으로 읽을지, 요약해서 읽을지가 정해진다. 상은 상세도 2부터 원문, 중은 상세도 3에서 요약, 하는 읽지 않는다. 장면과는 시각(start\_ms)으로 연결한다. 글자 크기·위치는 워커가 중요도를 판단할 때만 쓰고 저장하지 않는다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 화면 글자 ID | BIGINT | N | 자동 증가 | 기본 키 |
| description\_id | 해설 ID | BIGINT | N |  | 소속 해설(descriptions.id) |
| character\_id | 인물 ID | BIGINT | Y |  | 이름 자막·등번호처럼 특정 인물을 가리킬 때 연결(characters.id). 인물이 지워지면 비워진다 |
| start\_ms | 시작 시각(ms) | INTEGER | N |  | 글자가 나타난 영상 시각 |
| end\_ms | 끝 시각(ms) | INTEGER | N |  | 글자가 사라진 영상 시각. 떠 있던 시간은 중요도 판단에도 쓴다 |
| text\_type | 글자 종류 | VARCHAR(15) | N | OTHER | SUBTITLE(자막) / TITLE(제목) / SLIDE(슬라이드) / TABLE(표) / FORMULA(수식) / SCOREBOARD(점수판) / NAME\_TAG(이름 자막) / UI(앱 화면 글자) / OTHER(기타). 자막 읽기 설정은 SUBTITLE에만 적용된다 |
| headline | 제목 | VARCHAR(300) | Y |  | 긴 글의 제목. 긴 글은 제목만 읽고 '자세히'로 본문을 안내한다 |
| full\_text | 전체 글자 | TEXT | N |  | 화면에 나온 글 원문 그대로 |
| summary\_text | 요약 | TEXT | Y |  | 중요도 '중'인 글을 상세도 3에서 읽을 때 쓰는 요약 |
| importance | 중요도 | VARCHAR(6) | N |  | HIGH(상) / MEDIUM(중) / LOW(하). 크기·위치, 나온 시간, 변화는 코드로, 대사가 가리키는지와 카테고리 지침은 AI로 판단한다 |
| same\_as\_dialogue | 대사와 같은 자막 여부 | BOOLEAN | N | FALSE | 말한 내용과 같은 자막이면 TRUE. 자막 읽기가 DIFFERENT\_ONLY일 때 읽지 않는다 |

### description\_lines · 해설 문장

실제로 재생되는 해설 문장이다. **한 시점(line\_index)에 상세도 1\~3이 각각 한 행**이라, 한 시점에 보통 3행이 있다. 플레이어는 사용자 상세도에 맞는 행만 골라 재생한다. 음성 파일 정보도 같은 행에 있다.

관리자가 문장(`text`)을 고치면 같은 트랜잭션에서 `audio_key`와 `audio_duration_ms`를 비우고 음성을 다시 만든다. 플레이어는 `audio_key`가 있으면 음성 파일을 재생하고, 비어 있으면 브라우저 음성으로 `text`를 읽는다(NFR-03).

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 해설 문장 ID | BIGINT | N | 자동 증가 | 기본 키. 신고가 이 번호를 가리킨다 |
| description\_id | 해설 ID | BIGINT | N |  | 소속 해설(descriptions.id) |
| line\_index | 해설 순서 | INTEGER | N |  | 시점 번호. 같은 시점의 상세도 1\~3 문장이 같은 번호를 갖는다. (해설, 순서, 상세도)는 중복 금지. "이전/다음 문장" 이동과 해설 대본 목록에 쓴다 |
| detail\_level | 상세도 | SMALLINT | N |  | 1(최소) / 2(기본) / 3(최대). 사용자의 user\_settings.detail\_level과 같은 행을 재생한다 |
| start\_ms | 해설 시작 시각(ms) | INTEGER | N |  | 해설을 시작할 영상 시각 |
| gap\_end\_ms | 빈 구간 끝(ms) | INTEGER | Y |  | 해설을 넣을 빈 구간(다음 대사 시작)의 끝. 해설 음성 길이 ÷ 말하기 속도가 이 구간보다 길면 영상을 멈추고 해설한다(재생 시점에 계산). 비어 있으면 빈 구간이 없다는 뜻 |
| importance | 중요도 | SMALLINT | N | 2 | 1\~3, 3이 가장 중요. 빈 구간이 모자라거나 대사가 빽빽한 영상에서 중요한 해설부터 남기는 데 쓴다 |
| text | 해설 문장 | TEXT | N |  | 읽어 줄 해설 문장. 화면 글자 원문을 포함할 수 있다 |
| voice\_code | 목소리 | VARCHAR(50) | Y |  | 음성을 만든 TTS 목소리 코드 |
| audio\_key | 음성 파일 위치 | VARCHAR(500) | Y |  | TTS로 미리 만든 음성 파일의 S3 키. 비어 있으면(아직 안 만들었거나, 실패했거나, 문장을 고친 직후) 브라우저 음성으로 대신 읽는다 |
| audio\_duration\_ms | 음성 길이(ms) | INTEGER | Y |  | 1.0배 속도 기준 음성 길이. 빈 구간에 들어가는지 계산하는 데 쓴다 |
| created\_at | 생성 시각 | TIMESTAMPTZ | N | 현재 시각 | 문장이 만들어진 시각 |
| updated\_at | 수정 시각 | TIMESTAMPTZ | N | 현재 시각 | 문장이나 음성이 마지막으로 바뀐 시각 |

### chapters · 장면 목차

장면 목차 항목 하나에 한 행이다. 여러 장면을 묶은 큰 단위로, "장면 목차 이동"과 "말로 장면 이동"에 쓴다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 목차 ID | BIGINT | N | 자동 증가 | 기본 키 |
| description\_id | 해설 ID | BIGINT | N |  | 소속 해설(descriptions.id) |
| chapter\_index | 목차 순서 | INTEGER | N |  | 1부터 시작. 같은 해설 안에서 중복 금지 |
| start\_ms | 시작 시각(ms) | INTEGER | N |  | 목차를 고르면 이동할 영상 시각 |
| end\_ms | 끝 시각(ms) | INTEGER | N |  | 목차 구간의 끝. 시작보다 커야 한다 |
| title | 목차 제목 | VARCHAR(100) | N |  | 목차에서 읽어 주는 제목 (예: 전반전, 재료 손질) |
| summary | 목차 요약 | TEXT | Y |  | 이 구간에서 일어나는 일 한두 문장. "비슷한 장면이 여러 개면 전후 상황을 설명하고 고르게 함"에 쓴다 |
| keywords | 키워드 | TEXT[] | Y |  | 말로 장면 이동에서 찾을 단어 목록 (예: {골, 슛팅}). GIN 인덱스로 빠르게 찾는다 |

## 챗봇·운영 영역

### chat\_logs · 챗봇 기록

챗봇으로 들어온 입력 한 건에 한 행이다. 타자든 말(Whisper로 변환)이든 모두 여기에 남고, 발화 분석이 질문인지 이동인지 설정 변경인지 구분한 결과를 같이 저장한다. **질문(QUESTION)과 지금 장면 설명(SCENE\_NOW)일 때만 답변 칼럼을 채우고**, 나머지 분류에 답변을 넣으면 DB가 막는다. 질문 기록 화면과 응답 시간 측정(NFR-02)에 쓴다. 답변을 만드는 동안의 상태(대기, 스트리밍 중)는 Redis에 둔다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 챗봇 기록 ID | BIGINT | N | 자동 증가 | 기본 키 |
| user\_id | 회원 ID | BIGINT | N |  | 입력한 회원(users.id) |
| video\_id | 영상 ID | BIGINT | Y |  | 보던 영상(videos.id). 영상 밖에서 연 챗봇(예: 검색 화면)이면 비어 있다 |
| description\_id | 해설 ID | BIGINT | Y |  | 입력 당시 재생 중이던 해설(descriptions.id). 해설을 다시 만들어도 어느 해설을 보고 답했는지 남는다. 해설이 없던 때면 비어 있고, 해설이 지워지면 비워진다 |
| target\_chapter\_id | 이동 목차 ID | BIGINT | Y |  | SEEK\_SCENE일 때 찾은 목차(chapters.id). 목차가 지워지면 비워진다 |
| video\_time\_ms | 입력 영상 시점(ms) | INTEGER | Y |  | 챗봇 키를 누른 순간의 영상 시각. 질문 답변은 이 시각의 장면과 앞뒤 대사를 근거로 한다 |
| input\_mode | 입력 방식 | VARCHAR(5) | N |  | TEXT(타자) / VOICE(마이크, Whisper 변환) |
| utterance | 발화 내용 | TEXT | N |  | 입력된 문장. 음성이면 Whisper가 인식한 문장이고, 챗봇 창에 그대로 표시한다 |
| intent | 발화 분류 | VARCHAR(15) | N |  | QUESTION(질문) / SCENE\_NOW(지금 장면 설명) / SEEK\_SCENE(말로 장면 이동) / SEEK\_TIME(시각 지정 이동) / SETTING(설정 변경) / ACTION(기능 실행) / UNKNOWN(모름) |
| target\_ms | 이동 시각(ms) | INTEGER | Y |  | SEEK\_TIME일 때 이동할 시각 ("3분 20초로 가 줘" → 200000) |
| intent\_latency\_ms | 분류 시간(ms) | INTEGER | Y |  | 발화 분석에 걸린 시간. 목표 p95 1.5초 |
| answer\_text | 답변 | TEXT | Y |  | QUESTION·SCENE\_NOW일 때만 채운다. 화면에서 확인되지 않으면 "확인되지 않아요"라고 답한다. 답변에 실패하면 비어 있다 |
| answer\_source | 답변 근거 | VARCHAR(15) | Y |  | STORED(저장한 장면 설명·대사) / FRAME(업로드 영상 화면을 다시 봄) / GEMINI\_REQUERY(유튜브 영상을 Gemini가 다시 봄) / CACHE(같은 질문의 저장된 답변) / UNANSWERABLE(답할 수 없음) |
| first\_audio\_ms | 첫 음성까지 시간(ms) | INTEGER | Y |  | 입력부터 답변 음성이 처음 나오기까지 걸린 시간. 목표 p95 3초 (논의 필요) |
| total\_latency\_ms | 전체 응답 시간(ms) | INTEGER | Y |  | 입력부터 답변이 끝날 때까지 걸린 시간 |
| created\_at | 입력 시각 | TIMESTAMPTZ | N | 현재 시각 | 입력한 시각. 질문 횟수 제한을 정하면 이 시각으로 하루 질문 수를 센다 |

### ai\_call\_logs · AI 호출 기록

AI를 한 번 부를 때마다 한 행이다. 해설 생성(작업·조각)과 챗봇 중 한 곳에만 연결되며, 둘 이상에 연결하면 DB가 막는다. GMS 크레딧 사용량과 영상 1분당 토큰(NFR-12)을 계산하고, Prometheus·Grafana 지표의 근거가 된다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 호출 ID | BIGINT | N | 자동 증가 | 기본 키 |
| job\_id | 작업 ID | BIGINT | Y |  | 작업 단위 호출(전체 요약, 목차 등)일 때 연결(jobs.id) |
| chunk\_id | 조각 ID | BIGINT | Y |  | 조각 단위 호출(음성 인식, 장면 해설 등)일 때 연결(job\_chunks.id) |
| chat\_log\_id | 챗봇 기록 ID | BIGINT | Y |  | 챗봇 호출(음성 인식, 발화 분석, 질문 답변)일 때 연결(chat\_logs.id) |
| purpose | 용도 | VARCHAR(15) | N |  | STT(음성 인식) / DESCRIBE(장면 해설) / SHORTEN(문장 줄이기) / SUMMARY(요약) / CHAPTER(목차) / CLASSIFY(카테고리 분류) / QUESTION(질문 답변) / INTENT(발화 분석) / TTS(음성 합성) |
| model | 모델명 | VARCHAR(50) | N |  | 사용한 모델 (예: gemini-flash, whisper-1, gpt-4o-mini). 어디서 불렀는지도 모델 이름으로 안다 |
| input\_tokens | 입력 토큰 | INTEGER | N | 0 | 보낸 토큰 수. 이미지·영상 토큰 포함 |
| output\_tokens | 출력 토큰 | INTEGER | N | 0 | 받은 토큰 수 |
| image\_count | 이미지 수 | SMALLINT | N | 0 | 함께 보낸 이미지(프레임) 수. 장면 추출이 얼마나 줄였는지 확인하는 데 쓴다 |
| estimated\_credits | 예상 크레딧 | NUMERIC(10,2) | N | 0 | 호출 당시 단가로 계산한 GMS 크레딧. 단가가 바뀌어도 그때 값이 남는다 |
| latency\_ms | 응답 시간(ms) | INTEGER | Y |  | AI가 응답하는 데 걸린 시간 |
| is\_fallback | 대체 모델 여부 | BOOLEAN | N | FALSE | 서킷 브레이커가 열려 대체 모델로 호출했는지 (NFR-10) |
| error\_code | 오류 코드 | VARCHAR(50) | Y |  | 실패 원인 (예: 한도 초과, 시간 초과). 비어 있으면 성공이고, 실패하면 반드시 채운다(원인을 모르면 UNKNOWN). 실패율 지표에 쓴다 |
| created\_at | 호출 시각 | TIMESTAMPTZ | N | 현재 시각 | 호출한 시각. 기간별 사용량 집계에 쓴다 |

## 신고 영역

### reports · 신고

시청자가 "이 해설이 틀렸어요"라고 알린 기록이다. 해설 문장 한 줄에 대해 한 행이 생기며, **같은 회원은 같은 문장을 한 번만 신고할 수 있다**. 관리자는 신고 목록에서 상태를 바꾸고, 필요하면 해설 문장을 직접 고친다. 신고가 남아 있는 문장은 지울 수 없게 막아 두어 근거가 사라지지 않게 한다.

| 칼럼 | 논리명 | 타입 | NULL | 기본값 | 설명 |
| --- | --- | --- | --- | --- | --- |
| id | 신고 ID | BIGINT | N | 자동 증가 | 기본 키 |
| line\_id | 해설 문장 ID | BIGINT | N |  | 신고한 문장(description\_lines.id). 신고가 있으면 문장을 지울 수 없다 |
| user\_id | 신고자 ID | BIGINT | N |  | 신고한 회원(users.id) |
| reported\_at\_ms | 신고 영상 시점(ms) | INTEGER | N |  | 신고 키를 누른 순간의 영상 시각. 관리자가 그 장면을 바로 찾아볼 수 있다 |
| line\_text\_snapshot | 신고 당시 문장 | TEXT | N |  | 신고할 때의 문장 내용을 그대로 복사해 둔 것. 나중에 문장이 고쳐져도 무엇을 신고했는지 남는다 |
| reason | 신고 사유 | VARCHAR(10) | N |  | WRONG(내용이 틀림) / MISSING(중요한 장면이 빠짐) / OVERLAP(대사와 겹침) / OTHER(기타) |
| reason\_detail | 기타 사유 | VARCHAR(500) | Y |  | 직접 적은 설명. OTHER일 때 주로 쓴다 |
| status | 처리 상태 | VARCHAR(10) | N | PENDING | PENDING(접수) / RESOLVED(처리 완료) / REJECTED(문제없음으로 반려) |
| handled\_at | 처리 시각 | TIMESTAMPTZ | Y |  | RESOLVED·REJECTED로 바꾼 시각 |
| created\_at | 신고 시각 | TIMESTAMPTZ | N | 현재 시각 | 신고한 시각. 신고 목록은 (status, created\_at) 순으로 조회한다 |

## DB 제약 조건과 자주 쓰는 조회

### 데이터를 지키는 규칙

코드에서 실수해도 DB가 잘못된 데이터를 거절하도록 걸어 둔 규칙이다.

| 규칙 이름 | 걸린 곳 | 막는 상황 |
| --- | --- | --- |
| uq\_users\_email\_active | users.email | 탈퇴하지 않은(withdrawn\_at이 빈) 회원끼리 같은 이메일 금지. 탈퇴한 뒤에는 같은 이메일로 다시 가입할 수 있다 |
| ck\_users\_verified | users | 시각장애인(VISUALLY\_IMPAIRED) 역할인데 인증 시각(verified\_at)이 비어 있음 |
| ck\_videos\_source | videos | 유튜브 영상은 youtube\_video\_id가 있고 업로더가 없어야 하며, 업로드 영상은 그 반대여야 한다 |
| uq\_jobs\_active\_video | jobs.video\_id | 한 영상에 진행 중(QUEUED·RUNNING)인 작업이 둘 이상. 여러 명이 동시에 요청해도 작업은 하나만 돌고 나머지는 합류(JOINED\_JOB)한다 |
| uq\_descriptions\_published | descriptions.video\_id | 한 영상에 공개(PUBLISHED) 해설이 둘 이상. 새 해설을 공개하려면 이전 해설을 먼저 ARCHIVED로 바꾼다 |
| ex\_scenes\_no\_overlap | scenes | 같은 해설 안에서 장면 구간이 서로 겹침 |
| ck\_chat\_logs\_answer | chat\_logs | 질문·지금 장면 설명이 아닌데 답변 칼럼이 채워짐 |
| ck\_ai\_call\_logs\_target | ai\_call\_logs | AI 호출 하나가 작업·조각·챗봇 중 둘 이상에 연결됨 |
| uq\_reports\_user\_line | reports | 한 회원이 같은 문장을 두 번 신고 |
| 범위 검사(CHECK) | 여러 테이블 | 상세도 1\~3, 말하기 속도 0.5\~2.5, 원본 음량 0\~100, 글자 크기 1\~5, 진행률 0\~100, 재시도 0\~3, 끝 시각이 시작 시각보다 앞섬, 목록에 없는 상태값 |

### 지울 때 규칙

부모 행을 지우면 연결된 자식 행을 어떻게 할지 정해 둔 것이다.

| 방식 | 의미 | 해당 연결 |
| --- | --- | --- |
| 함께 삭제(CASCADE) | 부모가 사라지면 의미가 없는 데이터라 같이 지운다 | 회원 → 설정·즐겨찾기 / 영상 → 업로드 정보·즐겨찾기·생성 요청·해설 / 해설 → 인물·대사·장면·화면 글자·해설 문장·목차 / 작업 → 조각 |
| 연결만 끊기(SET NULL) | 기록은 남기고 가리키던 칸만 비운다 | 생성 요청의 작업, 대사·화면 글자의 인물, 챗봇 기록의 영상·해설·목차, AI 호출 기록의 작업·조각·챗봇 |
| 삭제 막기(NO ACTION) | 자식이 남아 있으면 부모를 지울 수 없다 | 업로드 영상·작업·생성 요청·챗봇·신고가 있는 회원 / 작업이 있는 영상 / 해설이 가리키는 작업 / 신고가 있는 해설 문장 |

그래서 **회원과 영상은 실제로 지우지 않는다**. 회원은 탈퇴 시각(withdrawn\_at)만 기록하고, 영상은 상태를 UNAVAILABLE로 바꿔 목록에서 숨긴다. 이렇게 하면 이용 기록과 신고 근거가 사라지지 않는다.

### 자주 쓰는 조회

백엔드가 가장 자주 날리는 조회와, 그 조회가 빠르게 돌도록 만든 인덱스다. `:이름`은 코드에서 넣는 값이다.

**1. 시청 화면 — 공개 해설의 내 상세도 문장 가져오기**

```sql
SELECT l.line_index, l.start_ms, l.gap_end_ms, l.importance,
       l.text, l.audio_key, l.audio_duration_ms
FROM descriptions d
JOIN description_lines l ON l.description_id = d.id
WHERE d.video_id = :videoId
  AND d.status = 'PUBLISHED'
  AND l.detail_level = :detailLevel
ORDER BY l.start_ms;
```

공개 해설은 영상당 하나라서 uq\_descriptions\_published로 바로 찾고, 문장은 (description\_id, detail\_level, start\_ms) 인덱스 순서대로 읽는다. audio\_key가 비어 있는 문장은 브라우저 음성으로 읽는다. 대사(transcript\_segments)와 화면 글자(screen\_texts)도 같은 방식으로 start\_ms 순으로 가져온다.

**2. 오늘 이용 횟수 — 해설 생성 제한 확인**

```sql
SELECT COUNT(*)
FROM generation_requests
WHERE user_id = :userId
  AND request_date = :todayKst
  AND counted = TRUE;
```

제한 횟수 자체는 DB가 아니라 application.yml에 둔다. request\_date는 한국 시간 기준 날짜라 자정 계산을 따로 하지 않아도 된다. (user\_id, request\_date) 인덱스를 쓴다.

**3. 말로 장면 이동 — 키워드로 목차 찾기**

```sql
SELECT c.id, c.title, c.start_ms
FROM chapters c
WHERE c.description_id = :descriptionId
  AND c.keywords && ARRAY[:keyword1, :keyword2]
ORDER BY c.start_ms;
```

`&&`는 "두 목록에 겹치는 단어가 하나라도 있으면"이라는 뜻이다. keywords 칼럼에 걸린 GIN 인덱스(gx\_chapters\_keywords)가 이 검색을 빠르게 한다. 키워드로 못 찾으면 목차 제목·요약을 AI에 넘겨 고르게 한다.

**4. 질문 답변 근거 — 지금 시각의 장면과 앞뒤 대사**

```sql
-- 지금 시각이 들어 있는 장면
SELECT s.title, s.scene_description, s.keyframe_key
FROM scenes s
WHERE s.description_id = :descriptionId
  AND :nowMs >= s.start_ms AND :nowMs < s.end_ms;

-- 앞뒤 30초 대사 (말한 사람 이름 포함)
SELECT t.start_ms, c.name AS speaker, t.text
FROM transcript_segments t
LEFT JOIN characters c ON c.id = t.character_id
WHERE t.description_id = :descriptionId
  AND t.start_ms BETWEEN :nowMs - 30000 AND :nowMs + 30000
ORDER BY t.start_ms;
```

장면은 구간이 겹치지 않으므로 항상 하나만 나온다. 저장된 설명으로 답하지 못하면 업로드 영상은 keyframe\_key의 화면을, 유튜브 영상은 Gemini로 다시 보는 방식(answer\_source)으로 넘어간다.

**5. 관리자 신고 목록 — 처리할 신고를 오래된 순으로**

```sql
SELECT r.id, r.reason, r.reason_detail, r.line_text_snapshot,
       r.reported_at_ms, l.text AS current_text, d.video_id
FROM reports r
JOIN description_lines l ON l.id = r.line_id
JOIN descriptions d ON d.id = l.description_id
WHERE r.status = 'PENDING'
ORDER BY r.created_at
LIMIT 20;
```

(status, created\_at) 인덱스를 쓴다. 신고 당시 문장(line\_text\_snapshot)과 지금 문장(current\_text)을 나란히 보여 주면 이미 고쳐진 신고인지 바로 알 수 있다.

**6. AI 사용량 — 날짜·용도별 크레딧 합계**

```sql
SELECT DATE(created_at AT TIME ZONE 'Asia/Seoul') AS day, purpose,
       COUNT(*) AS calls,
       COUNT(*) FILTER (WHERE error_code IS NOT NULL) AS failures,
       SUM(estimated_credits) AS credits,
       SUM(input_tokens + output_tokens) AS tokens
FROM ai_call_logs
WHERE created_at >= :from
GROUP BY day, purpose
ORDER BY day, credits DESC;
```

created\_at 인덱스로 기간을 먼저 좁힌다. 용도(purpose)별로 나눠 보면 어느 단계가 크레딧을 많이 쓰는지 알 수 있고, error\_code로 실패 건수도 함께 본다.
