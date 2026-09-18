-- Tao database + user rieng cho ung dung.
-- Chay bang tai khoan root:
--   mysql -u root -p < scripts/00_create_database.sql

CREATE DATABASE IF NOT EXISTS hoctiengtrung
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

-- User rieng cho app (khuyen dung thay vi dung root)
CREATE USER IF NOT EXISTS 'hoctiengtrung'@'localhost' IDENTIFIED BY 'HocTiengTrung@2026';
GRANT ALL PRIVILEGES ON hoctiengtrung.* TO 'hoctiengtrung'@'localhost';
FLUSH PRIVILEGES;

SELECT 'Database hoctiengtrung da san sang' AS ket_qua;
