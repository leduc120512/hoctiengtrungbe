# Web học tiếng Trung — Backend API

Backend REST API cho website học tiếng Trung: từ vựng HSK, khoá học/bài học, bài kiểm tra
và flashcard ôn tập theo thuật toán lặp lại ngắt quãng SM-2.

Frontend tách riêng (React/Next/Vue tuỳ bạn) và gọi vào API này qua JWT.

---

## 1. Công nghệ

| Thành phần | Phiên bản | Ghi chú |
|---|---|---|
| Java | 17 (biên dịch bằng JDK 26) | `--release 17` |
| Spring Boot | 4.1.1 | Spring Framework 7.0.9 |
| Spring Security | 7.1.1 | JWT, stateless |
| Spring Data JPA / Hibernate | 4.1.1 / 7.4.5 | `ddl-auto: validate` |
| Flyway | 12.4.0 | quản lý toàn bộ schema |
| MySQL | 8.0 | `utf8mb4_0900_ai_ci` |
| JJWT | 0.12.6 | HS256 |
| springdoc-openapi | 3.0.0 | Swagger UI |

> **Lưu ý về Spring Boot 4** — bản này tách auto-configuration thành từng module riêng theo
> công nghệ. Chỉ khai báo `flyway-core` là **không đủ**: Flyway sẽ không được auto-configure,
> migration im lặng không chạy rồi Hibernate báo `missing table`. Phải dùng
> `spring-boot-starter-flyway`. Tương tự, Boot 4 dùng **Jackson 3** (`tools.jackson.databind`),
> chỉ riêng annotation vẫn ở `com.fasterxml.jackson.annotation`.

---

## 2. Chạy lần đầu

### 2.1 Tạo database

```bash
mysql -u root -p < scripts/00_create_database.sql
```

Script tạo database `hoctiengtrung` (utf8mb4) và user riêng `hoctiengtrung` / `HocTiengTrung@2026`.

### 2.2 Khai báo biến môi trường

| Biến | Mặc định | Ý nghĩa |
|---|---|---|
| `DB_USERNAME` | `root` | user MySQL |
| `DB_PASSWORD` | *(rỗng)* | mật khẩu MySQL |
| `JWT_SECRET` | chuỗi dev | **tối thiểu 32 byte**, app fail-fast nếu ngắn hơn |

PowerShell:
```powershell
$env:DB_USERNAME="hoctiengtrung"
$env:DB_PASSWORD="HocTiengTrung@2026"
$env:JWT_SECRET="doi-chuoi-nay-thanh-secret-that-dai-it-nhat-32-byte"
```

### 2.3 Chạy

```bash
./mvnw spring-boot:run
```

Flyway tự chạy 11 migration (schema + toàn bộ dữ liệu mẫu) ngay lần khởi động đầu tiên.
**Không cần import tay.**

- API: <http://localhost:8080/api/v1>
- Swagger UI: <http://localhost:8080/swagger-ui.html>

### 2.4 Cách khác — import thẳng file SQL

Nếu muốn nạp dữ liệu sẵn thay vì để Flyway chạy:

```bash
mysql -u root -p < scripts/hoctiengtrung_full.sql
```

File này gồm cả `CREATE DATABASE`, toàn bộ schema, dữ liệu và bảng `flyway_schema_history`
(đã đánh dấu ở version 11) — nên khi bật app lên Flyway sẽ **không** chạy lại migration.

---

## 3. Tài khoản có sẵn

| Email | Mật khẩu | Quyền |
|---|---|---|
| `admin@hoctiengtrung.vn` | `Admin@123` | `ROLE_ADMIN` + `ROLE_USER` |
| `demo@hoctiengtrung.vn` | `Demo@123` | `ROLE_USER` |

> Đây là mật khẩu demo. **Đổi ngay trước khi deploy.**

---

## 4. Dữ liệu mẫu

| Bảng | Số dòng | Nội dung |
|---|---|---|
| `words` | 300 | HSK 1 (150) + HSK 2 (150): giản thể, phồn thể, pinyin có dấu, pinyin số, nghĩa Việt/Anh, từ loại |
| `word_examples` | 300 | mỗi từ 1 câu ví dụ kèm pinyin và bản dịch |
| `topics` | 15 | 14 chủ đề chủ điểm + "Hư từ & ngữ pháp" |
| `word_topics` | 385 | gán từ vào chủ đề |
| `courses` / `lessons` | 6 / 44 | HSK 1–6, nội dung bài viết bằng Markdown tiếng Việt |
| `lesson_grammar` | 72 | điểm ngữ pháp kèm cấu trúc và ví dụ |
| `lesson_words` | 288 | từ vựng gắn với từng bài |
| `quizzes` / `questions` / `question_options` | 12 / 120 / 288 | 4 dạng câu hỏi, có giải thích đáp án |
| `decks` / `flashcards` | 10 / 432 | bộ thẻ theo cấp HSK và theo chủ đề |

---

## 5. Kiến trúc

```
com.example.bewebtiengtrung
├── common/          BaseEntity, DTO dùng chung, exception + GlobalExceptionHandler
├── config/          AppProperties, CORS, JPA auditing, OpenAPI
├── security/        SecurityConfig, JwtService, JwtAuthenticationFilter, SecurityUtils
└── module/
    ├── user/        User, Role  (các module khác import từ đây)
    ├── auth/        đăng ký, đăng nhập, refresh token, hồ sơ
    ├── vocabulary/  Word, WordExample, Topic
    ├── course/      Course, Lesson, LessonGrammar, Enrollment, UserLessonProgress
    ├── quiz/        Quiz, Question, QuestionOption, QuizAttempt, QuizAttemptAnswer
    └── srs/         Deck, Flashcard, ReviewState, ReviewLog, Sm2Calculator
```

Mỗi module chia `entity / repository / service / controller / dto / mapper`.

**Quy ước xuyên suốt**

- Luồng: `Controller → Service (interface + Impl) → Repository`. Controller không chứa
  logic nghiệp vụ và không gọi thẳng repository.
- Service **không bao giờ** trả về entity — luôn trả DTO. Entity không được serialize ra JSON.
- DTO là Java `record`, request có `@Valid` + jakarta validation.
- Lỗi trả về theo chuẩn **RFC 7807 `ProblemDetail`**, kèm `code`, `timestamp`, `path`;
  lỗi validation có thêm map `errors` theo từng field.
- Phân trang trả `PageResponse<T>`; mặc định `size=20`.
- Mọi quan hệ `@ManyToOne` đều `LAZY`; `open-in-view: false`.
- Schema do Flyway sở hữu; Hibernate chỉ `validate` nên lệch entity/DB là app fail ngay khi khởi động.

---

## 6. API

Tất cả dưới tiền tố `/api/v1`. File [api.http](api.http) chứa sẵn request mẫu cho mọi endpoint
(mở bằng IntelliJ/WebStorm rồi bấm ▶).

### Xác thực — `/auth`, `/me`
| Method | Path | Quyền |
|---|---|---|
| POST | `/auth/register` | public |
| POST | `/auth/login` | public |
| POST | `/auth/refresh` | public |
| POST | `/auth/logout` · `/auth/logout-all` | user |
| GET · PUT | `/me` | user |
| POST | `/me/change-password` | user |

### Từ vựng — `/topics`, `/words`
| Method | Path | Quyền |
|---|---|---|
| GET | `/topics` · `/topics/{slug}` | public |
| GET | `/words?q=&hskLevel=&topicId=&page=&size=` | public |
| GET | `/words/{id}` · `/words/random?count=&hskLevel=` | public |

### Khoá học — `/courses`, `/lessons`, `/me`
| Method | Path | Quyền |
|---|---|---|
| GET | `/courses?level=&q=` · `/courses/{slug}` | public |
| GET | `/lessons/{id}` | public |
| POST · DELETE | `/courses/{id}/enroll` | user |
| GET | `/me/enrollments` | user |
| PUT | `/me/lessons/{lessonId}/progress` | user |
| GET | `/me/courses/{courseId}/progress` | user |

### Kiểm tra — `/quizzes`, `/me/attempts`
| Method | Path | Quyền |
|---|---|---|
| GET | `/quizzes?courseId=&lessonId=&hskLevel=` · `/quizzes/{id}` | public |
| POST | `/quizzes/{id}/attempts` | user |
| POST | `/me/attempts/{attemptId}/submit` | user |
| GET | `/me/attempts` · `/me/attempts/{id}` | user |

> **Bảo mật:** `GET /quizzes/{id}` **không** trả `isCorrect`, `correctText`, `explanation`.
> Đáp án và giải thích chỉ lộ ra trong kết quả sau khi đã nộp bài.

### Flashcard & SRS — `/decks`, `/me/srs`
| Method | Path | Quyền |
|---|---|---|
| GET · POST | `/decks` | GET public/user · POST user |
| GET · PUT · DELETE | `/decks/{id}` | theo quyền sở hữu |
| GET · POST | `/decks/{id}/cards` | |
| PUT · DELETE | `/cards/{id}` | chủ sở hữu |
| POST | `/decks/{id}/subscribe` | user |
| GET | `/me/srs/due?deckId=&limit=` | user |
| POST | `/me/srs/review` | user |
| GET | `/me/srs/stats` | user |

### Quản trị — `/admin/**` (bắt buộc `ROLE_ADMIN`)
CRUD cho `words`, `words/{id}/examples`, `topics`, `courses`, `lessons`,
`lessons/{id}/grammar`, `lessons/{id}/words`, `quizzes`, `quizzes/{id}/questions`.

---

## 7. Thuật toán ôn tập SM-2

Cài trong `module/srs/service/Sm2Calculator.java` — hàm thuần, không side-effect.

`rating`: `AGAIN`(q=0) · `HARD`(q=3) · `GOOD`(q=4) · `EASY`(q=5)

```
Nếu q < 3:   repetitions = 0; intervalDays = 1; lapses++; status = RELEARNING
                          dueAt = now + 10 phút   (học lại ngay trong phiên)
Ngược lại:   repetitions++
             repetitions == 1  -> intervalDays = 1
             repetitions == 2  -> intervalDays = 6
             còn lại           -> intervalDays = round(intervalDays × easeFactor)
             status = REVIEW;  dueAt = now + intervalDays ngày

easeFactor = max(1.3, easeFactor + (0.1 − (5−q) × (0.08 + (5−q) × 0.02)))
```

Mỗi lần chấm đều ghi thêm một dòng vào `review_logs` để dựng thống kê và chuỗi ngày học liên tiếp.

---

## 8. Cách chấm điểm bài kiểm tra

| Dạng câu hỏi | Cách chấm |
|---|---|
| `SINGLE_CHOICE`, `LISTENING`, `IMAGE_CHOICE` | đúng nếu option được chọn có `isCorrect` |
| `MULTIPLE_CHOICE` | hiện xử lý như chọn một đáp án (xem mục 10) |
| `FILL_BLANK`, `TRANSLATION` | so với `correctText` sau khi trim, hạ chữ thường, gộp khoảng trắng |

`score` = tổng điểm các câu đúng · `passed` = `score × 100 / maxScore ≥ quiz.passScore`.

Nộp bài bị từ chối khi: không phải chủ sở hữu lượt làm (403), lượt làm không ở trạng thái
`IN_PROGRESS` (400), hoặc đã quá `timeLimitSeconds` (lượt làm bị đánh dấu `EXPIRED`).

---

## 9. Bảo mật

- Mật khẩu băm bằng **BCrypt**.
- Access token JWT HS256, mặc định sống 1 giờ; claim: `sub` (user id), `email`, `roles`, `iss`.
- Refresh token là chuỗi ngẫu nhiên, DB **chỉ lưu SHA-256** của nó, không lưu bản gốc.
- **Xoay vòng refresh token**: mỗi lần `/auth/refresh` thu hồi token cũ và cấp token mới.
  Dùng lại token cũ → 401.
- Đổi mật khẩu sẽ thu hồi toàn bộ refresh token của tài khoản.
- Stateless hoàn toàn, không session; CORS cấu hình qua `app.cors.allowed-origins`.

---

## 10. Những điểm còn hạn chế

Ghi lại để bạn nắm, không phải lỗi ẩn:

1. **`MULTIPLE_CHOICE` chưa chấm đa đáp án.** Model nộp bài hiện gửi một `selectedOptionId`
   cho mỗi câu, nên dạng này đang được chấm như `SINGLE_CHOICE`. Dữ liệu mẫu không dùng dạng
   này. Muốn hỗ trợ đầy đủ cần đổi `selectedOptionId` thành danh sách.
2. **`lesson_words.sort_order` không được map trong JPA.** Quan hệ khai báo `@ManyToMany` thuần
   nên cột này chỉ dùng ở tầng SQL/seed; thứ tự từ vựng trong bài lấy theo id. Muốn dùng thứ tự
   tuỳ ý cần tách thành entity trung gian.
3. **Chưa có `audio_url`.** Cột đã có sẵn trong schema nhưng dữ liệu mẫu để `NULL` — cần nguồn
   file phát âm rồi cập nhật sau.
4. **Test mới phủ phần logic lõi.** Đã có unit test cho `Sm2Calculator` và phần chấm điểm
   (mục 13), nhưng chưa có test tầng controller/repository. Nên bổ sung `@WebMvcTest` cho
   các endpoint và `@DataJpaTest` cho truy vấn tìm kiếm khi dự án lớn dần.
5. **Chưa có giới hạn tần suất (rate limiting)** trên `/auth/login` và `/auth/register`.
6. **`BewebtiengtrungApplicationTests` cần MySQL đang chạy** vì dùng `@SpringBootTest` với
   datasource thật. Muốn chạy test độc lập nên thêm Testcontainers.

---

## 11. Cấu trúc migration

```
src/main/resources/db/migration/
├── V1__init_schema.sql                23 bảng, 32 khoá ngoại
├── V2__seed_reference_data.sql        roles, users, 14 chủ đề
├── V3..V6__seed_words_*.sql           300 từ HSK1 + HSK2, ví dụ, gán chủ đề
├── V7__seed_courses.sql               khoá học, bài học, ngữ pháp, từ vựng theo bài
├── V8__seed_quizzes.sql               đề thi, câu hỏi, đáp án
├── V9__seed_decks.sql                 bộ thẻ + flashcard (INSERT ... SELECT từ bảng words)
├── V10__seed_topic_hu_tu.sql          chủ đề "Hư từ & ngữ pháp" cho các từ chưa có chủ đề
├── V11__normalize_part_of_speech.sql  gộp 19 biến thể từ loại về 11 giá trị chuẩn
├── V12__sync_flashcard_hint.sql       đồng bộ flashcards.hint theo words.part_of_speech
└── V13__pinyin_tone_sensitive.sql     cho cột pinyin phân biệt thanh điệu (xem mục 12)
```

**Không sửa file migration đã chạy** — Flyway kiểm tra checksum và sẽ báo lỗi.
Cần đổi gì thì thêm `V14__...sql` mới.

---

## 12. Ghi chú quan trọng về collation

Database dùng `utf8mb4_0900_ai_ci` — **ai = accent insensitive**, tức là bỏ qua dấu khi so sánh.
Với tiếng Việt điều này rất tiện (gõ `hoc` ra `học`), nhưng với **pinyin thì sai nghiêm trọng**:
dấu thanh không phải dấu phụ, nó là một phần danh tính của từ.

Hậu quả đã đo được trên MySQL 8.0.46 trước khi sửa:

```
INSERT 好 'hǎo'  -> OK
INSERT 好 'hào'  -> ERROR 1062 Duplicate entry '好-hào'
```

Nghĩa là khoá `UNIQUE(simplified, pinyin)` **không lưu được chữ đa âm (多音字)** —
好 hǎo/hào, 教 jiāo/jiào, 数 shǔ/shù, 得 dé/děi/de… đều là hiện tượng rất phổ biến.

`V13` xử lý bằng cách cho **riêng cột `pinyin`** dùng `utf8mb4_0900_as_cs`
(accent + case sensitive). Các cột khác giữ nguyên `ai_ci`.

Đánh đổi kèm theo: `LIKE '%hao%'` không còn khớp `'hǎo'`. Vì vậy `WordRepository.search`
tìm thêm trong `pinyin_numbered` (`'hao3'` — chuỗi ASCII), nên **gõ pinyin không dấu vẫn ra kết quả**.
Đã kiểm chứng: `?q=hao` trả về 好, 号, 你好, 多少, 少.

> Khi bạn viết thêm truy vấn hoặc migration đụng tới cột có dấu, nhớ ép
> `COLLATE utf8mb4_bin` nếu cần so sánh chính xác từng ký tự — nếu không,
> điều kiện `<>` sẽ luôn sai và lệnh `UPDATE` âm thầm không cập nhật dòng nào.

---

## 13. Kiểm thử

```bash
./mvnw test        # 24 test, tất cả pass
```

| Bộ test | Số test | Nội dung |
|---|---|---|
| `Sm2CalculatorTest` | 11 | chuỗi 1→6→15 ngày, AGAIN đặt lại chuỗi, chặn dưới hệ số dễ 1.3, tính thuần, chặn null |
| `QuizGradingTest` | 12 | trắc nghiệm, tự luận (chuẩn hoá khoảng trắng/hoa thường), chống gửi id phương án của câu khác |
| `BewebtiengtrungApplicationTests` | 1 | nạp được Spring context (cần MySQL đang chạy) |

Ngoài ra toàn bộ API đã được kiểm thử trực tiếp end-to-end: đăng nhập, xoay vòng refresh token,
phân quyền admin, không rò rỉ đáp án, chấm điểm đúng 10/10, và chuỗi SM-2.
