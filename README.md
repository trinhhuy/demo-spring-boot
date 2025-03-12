# MESSAGE

### 1. **Tổng quan về dự án**
- **Tên dự án**: Dự án này có thể là Quản lý sách
- **Mục đích**: API Backend
- **Công nghệ chính**: Spring Boot, Spring Data JPA, Spring Security
- **Kiến trúc**: Monolithic

### 2. **Cấu trúc dự án**
- Các thư mục chính (`src/main/java`, `src/main/resources`, `test`, `config`, v.v.)
- Cách tổ chức mã nguồn (Controller, Service, Repository, DTO, Models, Config, Security)

### 3. **Các thành phần chính**
- **Endpoints**: 
  - POST /api/auth/register  allPermit
  - POST /api/auth/login     allPermit
  - GET  /api/books          required: Bearer Token
  - POST /api/books          required: Bearer Token
- **Cách xử lý dữ liệu**: Sử dụng JPA, driver mysql
- **Cách xử lý security**: dùng Spring Security, JWT
- **Cơ sở dữ liệu**: Dùng MySQL

### 4. **Cách chạy dự án**
- Yêu cầu môi trường 
  - Database
- Cách config file (`application.properties` hoặc `application.yml`)
    - SPRING_DATASOURCE_URL
    - SPRING_DATASOURCE_USERNAME
    - SPRING_DATASOURCE_PASSWORD
    - SPRING_JPA_HIBERNATE_DDL_AUTO

### 5. **Lỗi**
- lỗi vòng lặp khi sử dụng @Data cùng với 
  - @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Book> books; 
  - @ManyToOne 
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

# Swagger

### Lỗi
- do sử dụng Params: **@RequestHeader("Authorization") String token**
  - Authorization param xuất hiện nhưng không được thêm vào request 
  - => chưa xử lý được
- Xử lý thay thế: **@Parameter(hidden = true)** 
  - => Mục đích: Ẩn Authorization param
- Hướng xử lý mong muốn: Bỏ **@RequestHeader("Authorization") String token**
  - Vì trong trong context đã có UserDetails, không cần phải truyền từ Controller, ta sẽ lấy trong Service
  - => Kết hợp Sử dụng Authorize thay vì Authorization param