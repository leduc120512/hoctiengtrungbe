# Đưa web lên mạng — hướng dẫn từng bước

Kiến trúc khi chạy thật:

```
Trình duyệt ──► Vercel (FE "Mỗi Ngày 中文", tĩnh, miễn phí)
                    │  gọi API qua VITE_API_URL
                    ▼
               Render (BE Spring Boot trong Docker, gói Free)
                    │  DB_URL
                    ▼
               MySQL bên ngoài (Aiven Free) ◄── Flyway tự tạo schema + dữ liệu lần đầu
                    │
               Gemini API (Google AI Studio, miễn phí) ── sinh câu luyện nghe
```

Thứ tự bắt buộc: **MySQL → Render (BE) → Vercel (FE) → nối CORS**. Mỗi bước có cách kiểm tra
"đã xong chưa" ở cuối.

---

## Bước 1 — Tạo MySQL miễn phí (Aiven)

Render không có MySQL. Aiven cho 1 MySQL free (1 GB) không cần thẻ.

1. Vào <https://console.aiven.io> → đăng ký bằng Google/GitHub.
2. **Create service** → chọn **MySQL** → plan **Free** → chọn region gần Singapore (`aws-ap-southeast-1`)
   → đặt tên `hoctiengtrung` → **Create**. Chờ ~2 phút tới khi trạng thái *Running*.
3. Mở service → tab **Overview** → ghi lại 5 thứ trong khối *Connection information*:
   - **Host** (dạng `mysql-xxxx.aivencloud.com`)
   - **Port** (dạng `2xxxx`)
   - **User** (`avnadmin`)
   - **Password** (bấm con mắt để hiện)
   - **Database** mặc định là `defaultdb`
4. Ghép thành `DB_URL` (đổi host/port cho đúng, giữ nguyên phần sau dấu `?`):

   ```
   jdbc:mysql://mysql-xxxx.aivencloud.com:2xxxx/defaultdb?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&sslMode=REQUIRED
   ```

   > `sslMode=REQUIRED` là bắt buộc với Aiven. Không cần tạo bảng gì cả — Flyway sẽ làm ở Bước 2.

**Kiểm tra:** trang service hiện *Running* và bạn đã có đủ 5 giá trị + chuỗi `DB_URL`.

---

## Bước 2 — Deploy BE lên Render

Repo: <https://github.com/leduc120512/hoctiengtrungbe> (đã có `Dockerfile` + `render.yaml`).

1. Vào <https://dashboard.render.com> → đăng nhập bằng GitHub → cho Render quyền đọc repo `hoctiengtrungbe`.
2. **New +** → **Blueprint** → chọn repo `hoctiengtrungbe` → Render đọc `render.yaml` và hiện form biến môi trường.
3. Điền các biến (những biến có sẵn giá trị thì để nguyên):

   | Biến | Điền gì |
   |---|---|
   | `DB_URL` | chuỗi ở Bước 1 |
   | `DB_USERNAME` | `avnadmin` |
   | `DB_PASSWORD` | mật khẩu Aiven |
   | `ADMIN_PASSWORD` | **bỏ trống / không tạo** nếu muốn giữ tài khoản `2222`/`2222` (V17). Nếu đặt, app đổi mật khẩu của `2222` thành giá trị này lúc khởi động |
   | `CORS_ALLOWED_ORIGINS` | tạm điền `http://localhost:5173` — sẽ sửa ở Bước 4 |
   | `GEMINI_API_KEY` | để trống, thêm ở Bước 5 (hoặc lấy trước tại <https://aistudio.google.com/apikey>) |
   | `JWT_SECRET` | Render tự sinh |

4. **Apply** → Render build Docker (lần đầu 5–8 phút). Xem tab **Logs**: phải thấy lần lượt
   `Successfully applied 16 migrations` (Flyway đang tạo DB) và `Started BewebtiengtrungApplication`.
5. Ghi lại URL service: `https://hoctiengtrung-api.onrender.com` (tên có thể khác).

**Kiểm tra:** mở `https://<url-render>/actuator/health` → `{"status":"UP"}`;
mở `https://<url-render>/swagger-ui.html` → thấy danh sách API.
Đăng nhập thử ngay trong Swagger: `POST /api/v1/auth/login` với `{"email":"2222","password":"2222"}` → nhận `accessToken`.

> Gói Free của Render **ngủ sau 15 phút không dùng**; lần gọi đầu sau đó chậm 30–60 giây. Bình thường.

---

## Bước 3 — Deploy FE lên Vercel

Repo: <https://github.com/maillexuanduc05122004/hoctiengtrung> (đã có `vercel.json`).

1. Vào <https://vercel.com> → đăng nhập bằng GitHub → **Add New… → Project** → **Import** repo `hoctiengtrung`.
2. Framework: Vercel tự nhận **Vite**. Build command `npm run build`, output `dist` (để mặc định).
3. Mục **Environment Variables** → thêm:

   | Name | Value |
   |---|---|
   | `VITE_API_URL` | URL Render ở Bước 2, ví dụ `https://hoctiengtrung-api.onrender.com` (**không** có `/` cuối, **không** có `/api/v1`) |

4. **Deploy** (2–3 phút). Ghi lại URL: `https://hoctiengtrung-xxxx.vercel.app`.

**Kiểm tra:** mở URL Vercel → trang chủ, Buổi học, Lật thẻ… hoạt động (phần này offline, không cần BE).
Vào **Câu của tôi** → báo "Không lấy được từ và câu của bạn" là đúng (chưa nối CORS — sang Bước 4).

---

## Bước 4 — Nối FE ↔ BE (CORS)

1. Render → service `hoctiengtrung-api` → **Environment** → sửa `CORS_ALLOWED_ORIGINS` thành:

   ```
   https://hoctiengtrung-xxxx.vercel.app,http://localhost:5173
   ```

   (URL Vercel thật của bạn, không có `/` cuối; nếu gắn domain riêng thì thêm vào, cách nhau dấu phẩy.)
2. **Save Changes** → Render tự khởi động lại (~1 phút).

**Kiểm tra:** trang Vercel → **Câu của tôi** → không cần đăng nhập, thấy ngay **89 từ đã học** và **90 câu**
(máy chủ tự dùng tài khoản `2222` cho request không mang token — biến `DEFAULT_ACCOUNT`). Lần mở sau hiện
tức thì nhờ bản chụp trong máy, kèm "Đang cập nhật từ máy chủ…" tới khi Render thức dậy.

---

## Bước 5 — Bật AI sinh câu (miễn phí)

1. <https://aistudio.google.com/apikey> → đăng nhập Google → **Create API key** → copy.
2. Render → **Environment** → thêm `GEMINI_API_KEY` = key vừa copy → **Save Changes**.
3. Chờ khởi động lại, mở `https://<url-render>/api/v1/ai/status` ngay trên trình duyệt (không cần token) →
   `{"enabled":true,"provider":"gemini","model":"gemini-2.5-flash"}`.

**Kiểm tra:** Vercel → Câu của tôi → **Nghe câu** → **Tạo câu mới bằng AI** → sau 10–20 giây có câu mới,
gắn nhãn *AI*, ghép từ vốn từ đã học và ưu tiên 10 từ mới nhất. Thông báo cho biết bao nhiêu câu bị loại vì
dùng chữ chưa học, trùng, hay chỉ là câu cũ đổi chỗ — đó là bộ lọc đang bảo vệ bạn.

---

## Bước 6 — Thêm từ mới sau này

Câu của tôi → **Thêm từ & câu** → dán mỗi dòng một từ (`学习 xuéxí học`, hoặc chỉ `xuéxí học` nếu chưa
nhớ chữ) → **Kiểm tra** → xem bảng đối chiếu từ điển (chữ Hán, pinyin có khớp, nghĩa Anh tự điền) →
**Thêm N từ**. Có AI thì gõ đại cũng được — chỉ tiếng Việt (`học`, `bạn bè`), pinyin không dấu, hay
`gợi ý 10 từ về đồ ăn` — rồi bấm **Điền bằng AI**: AI điền đủ bốn phần, vẫn ra bảng duyệt ấy. Từ mới vào
sổ đã học ngay; bấm **Tạo câu mới bằng AI** để có câu ghép từ vừa học với vốn từ cũ.

---

## Cập nhật code về sau

Cả hai nền tảng đều tự deploy khi có commit mới:

```bash
# BE
cd bewebtiengtrung && git add -A && git commit -m "..." && git push        # Render build lại Docker

# FE
cd untitled1 && git add -A && git commit -m "..." && git push               # Vercel build lại
```

Thêm bảng/dữ liệu ⇒ viết `V17__...sql` mới trong `src/main/resources/db/migration/` — **không sửa** file
đã chạy. Flyway áp dụng tự động ở lần deploy kế tiếp.

---

## Sự cố thường gặp

| Triệu chứng | Nguyên nhân | Cách xử lý |
|---|---|---|
| Render log `Communications link failure` | `DB_URL` sai host/port hoặc thiếu `sslMode=REQUIRED` | Sửa `DB_URL`, Save |
| FE báo "Không kết nối được máy chủ" | BE đang ngủ (Free) hoặc `VITE_API_URL` sai | Đợi 60 s rồi thử lại; kiểm tra biến trên Vercel rồi **Redeploy** |
| Đăng nhập lỗi CORS trong Console | `CORS_ALLOWED_ORIGINS` thiếu URL Vercel | Bước 4 |
| Đăng nhập 2222/2222 báo sai | trên Render còn biến `ADMIN_PASSWORD` (app đổi mật khẩu theo nó) | Xoá biến `ADMIN_PASSWORD`, Save, đợi khởi động lại |
| `/ai/status` báo `enabled:false` | thiếu `GEMINI_API_KEY` | Bước 5 |
| Tạo câu trả 429 | hết hạn mức free của Gemini | chờ vài phút |
