-- init.sql
CREATE USER IF NOT EXISTS 'kang'@'%' IDENTIFIED BY 'kang123!@#';
GRANT ALL PRIVILEGES ON learning-mate.* TO 'kang'@'%';
FLUSH PRIVILEGES;
