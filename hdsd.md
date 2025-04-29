# Hướng dẫn sử dụng Hệ thống Saga Orchestration

## 1. Tổng quan về Kiến trúc Dự án

### 1.1 Cấu trúc Module
Dự án được chia thành 3 microservice chính:
- **Account Service (Port: 8585)**: Quản lý tài khoản và số dư
- **Transaction Service (Port: 8484)**: Xử lý các giao dịch
- **Fraud Detection Service (Port: 8686)**: Kiểm tra gian lận

### 1.2 Kiến trúc Phân lớp
Mỗi service được tổ chức theo mô hình phân lớp:
- **Controller Layer**: Xử lý HTTP requests
- **Service Layer**: Logic nghiệp vụ
- **Repository Layer**: Tương tác với database
- **Event Layer**: Xử lý các sự kiện Kafka
- **Model Layer**: Định nghĩa các entity và DTO

### 1.3 Saga Orchestrator
- Transaction Service đóng vai trò là Saga Orchestrator
- Sử dụng Kafka để điều phối các bước trong saga
- Các topic chính:
  - transaction.initiated.v1
  - account.balance.reserved.v1
  - fraud.transaction.verified.v1
  - account.balance.completed.v1
  - account.balance.rollback.v1

## 2. Cài đặt và Cấu hình

### 2.1 Yêu cầu Hệ thống
- Docker và Docker Compose
- Java 21
- Maven

### 2.2 Cấu hình Môi trường
Dự án sử dụng các công nghệ:
- PostgreSQL: Database chính
- Kafka: Message broker
- Zookeeper: Quản lý Kafka cluster
- Kafka UI: Giao diện quản lý Kafka (port 8082)

### 2.3 Khởi động Hệ thống
```bash
# Khởi động toàn bộ hệ thống
docker compose up --build -d
```

## 3. Hướng dẫn Sử dụng

### 3.1 Tạo Tài khoản
```bash
curl --location 'localhost:8585/api/v1/digital-documents/library/accounts' \
--header 'Content-Type: application/json' \
--data '{
    "userId": 19
}'
```

### 3.2 Các Kịch bản Giao dịch

#### 3.2.1 Happy Case - Gửi tiền hợp lệ
- Gửi tiền với số tiền <= 10.000
```bash
curl --location 'localhost:8484/api/v1/digital-documents/library/transactions' \
--header 'Content-Type: application/json' \
--data '{
    "accountId": 1,
    "amount": 10.01,
    "type": "DEPOSIT"
}'
```

#### 3.2.2 Failure Case 1 - Rút tiền vượt số dư
- Rút tiền với số tiền > số dư hiện có
```bash
curl --location 'localhost:8484/api/v1/digital-documents/library/transactions' \
--header 'Content-Type: application/json' \
--data '{
    "accountId": 1,
    "amount": 10000000.01,
    "type": "WITHDRAW"
}'
```

#### 3.2.3 Failure Case 2 - Phát hiện gian lận
- Gửi tiền với số tiền > 10.000
```bash
curl --location 'localhost:8484/api/v1/digital-documents/library/transactions' \
--header 'Content-Type: application/json' \
--data '{
    "accountId": 1,
    "amount": 10000000.01,
    "type": "DEPOSIT"
}'
```

## 4. Chi tiết về Saga Flow

### 4.1 Happy Case Flow
1. Transaction Service nhận request gửi tiền
2. Publish event `transaction.initiated.v1`
3. Account Service nhận event và reserve balance
4. Publish event `account.balance.reserved.v1`
5. Fraud Service kiểm tra và verify
6. Publish event `fraud.transaction.verified.v1`
7. Account Service hoàn tất giao dịch
8. Publish event `account.balance.completed.v1`

### 4.2 Compensation Flow
1. Khi phát hiện lỗi, hệ thống tự động rollback
2. Publish event `account.balance.rollback.v1`
3. Các service thực hiện compensation logic

## 5. Cấu hình Quan trọng

### 5.1 Database
- PostgreSQL running on port 5432
- Username: postgres
- Password: postgres
- Database: postgres

### 5.2 Kafka
- Bootstrap servers: localhost:29092
- Topics được tự động tạo
- UI available at: http://localhost:8082

## 6. Xử lý Sự cố Thường gặp

### 6.1 Connection Issues
- Kiểm tra các service đã khởi động đúng port
- Verify Kafka và PostgreSQL đã sẵn sàng
- Kiểm tra network trong docker-compose

### 6.2 Transaction Failures
- Kiểm tra logs của từng service
- Verify số dư tài khoản
- Kiểm tra Kafka topics và messages

## 7. Tổng kết

### 7.1 Điểm mạnh
- Kiến trúc microservice rõ ràng
- Xử lý lỗi và compensation tốt
- Monitoring thông qua Kafka UI

### 7.2 Đề xuất Cải thiện
- Thêm retry mechanism cho các service
- Implement idempotency cho các operation
- Thêm monitoring và alerting
- Tăng cường logging và tracing 