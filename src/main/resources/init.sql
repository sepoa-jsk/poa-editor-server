-- MariaDB 초기화 스크립트
-- MariaDB 관리자 계정으로 실행하세요: mysql -u root -p < init.sql

CREATE DATABASE IF NOT EXISTS poa_editor
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'poa_user'@'localhost'
  IDENTIFIED BY 'poa_password';

GRANT ALL PRIVILEGES ON poa_editor.* TO 'poa_user'@'localhost';

FLUSH PRIVILEGES;
