# CLAUDE.md — cam_stamp

Project cá nhân: làm app camera thật, đồng thời học lập trình Android từ đầu. Người làm chưa biết Kotlin/Java/Android.

## Cách làm việc (quan trọng)

- Giải thích chậm, từng bước nhỏ — không dồn nhiều thay đổi rồi tóm tắt cuối.
- Không giả định biết thuật ngữ — giải thích ngắn gọn khái niệm mới (Activity, Composable, Gradle...) khi dùng lần đầu.
- Khi tạo/sửa file: nói file đó là gì, vì sao cần, trước khi viết nội dung.
- Ưu tiên các bước có thể build/chạy thử ngay trên điện thoại thật để thấy kết quả.
- Giao tiếp bằng tiếng Việt.

## Môi trường (xác minh 2026-11-03)

- JDK 17: `C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot`
- Android SDK: `C:\Android\Sdk` (platform 36, build-tools 36.0.0)
- Thiết bị test: Samsung SM-S936B qua USB/adb
- Không dùng Android Studio, làm qua CLI (gradlew, adb, sdkmanager)

## Quy tắc mã nguồn

- Comments bằng tiếng việt dễ hiểu
- Nếu các là các thuật ngữ chuyên ngành thì dùng tiếng anh
- Ưu tiên dùng các comment bằng TODO TREE như TODO FIXME BUG HACK XXX NOTE OPTIMIZE và [ ]/[x]
