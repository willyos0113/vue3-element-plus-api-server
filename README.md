# Vue 3 + ElementPlus 後端 API 伺服器

## ⚙️ 伺服器架構

- 採 spring-boot 技術，免去傳統繁瑣的組態設定
- 實作後台登入、註冊及數據管理等功能
- 提供 vue3-element-plus 前端專案使用

## 🚀 API 接口設計

- 數據管理功能

  - GET /api/profiles
  - GET /api/profiles/{id}
  - POST /api/profiles/edit/{id}
  - POST /api/profiles/add
  - DELETE /api/profiles/delete/{id}

- 註冊登入功能
  - POST /api/users/register
  - GET /api/users/current
  - POST /api/users/login

## 📝 資料庫設計

- users

  - id: VARCHAR(100), PK
  - name: VARCHAR(100), UK, NOT NULL
  - email: VARCHAR(100), UK, NOT NULL
  - password: VARCHAR(255), NOT NULL
  - identity: VARCHAR(100)
  - update_time: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP

- profiles
  - still under construction...
 
## 📋 操作流程

### 1. 🐳 啟動 MySQL Docker
```bash
docker run -d --name vue3-mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=vue3_app \
  mysql:8.0
```

### 2. 🗄️ 建立資料表
```sql
CREATE TABLE users (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    identity VARCHAR(100),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(100),
    title VARCHAR(200),
    description TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 3. 🚀 啟動 Spring Boot
- 確認 `application.yml` 資料庫連接設定
- 執行 `./mvnw spring-boot:run`
- 訪問 http://localhost:8080/swagger-ui.html

### 4. 📮 Postman 測試順序

#### 用戶功能

- 用戶註冊
```
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "name": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "identity": "user"
}
```

- 用戶登入
```
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```
**重要**: 複製回應中的 `token`

- 獲取當前用戶
```
GET http://localhost:8080/api/users/current
Authorization: Bearer {token}
```

#### Profile CRUD 操作功能

- 新增 Profile
```
POST http://localhost:8080/api/profiles/add
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "我的 Profile",
  "description": "測試描述"
}
```

- 獲取所有 Profiles
```
GET http://localhost:8080/api/profiles
Authorization: Bearer {token}
```

- 獲取特定 Profile
```
GET http://localhost:8080/api/profiles/{id}
Authorization: Bearer {token}
```

- 編輯 Profile
```
POST http://localhost:8080/api/profiles/edit/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "更新的 Profile",
  "description": "更新的描述"
}
```

- 刪除 Profile
```
DELETE http://localhost:8080/api/profiles/delete/{id}
Authorization: Bearer {token}
```

## ⚠️ 重點提醒

1. **JWT Token**: 登入後將 token 設定到 Authorization header: `Bearer {token}`
2. **Swagger UI**: 訪問 http://localhost:8080/swagger-ui.html 查看 API 文檔
3. **資料庫連接**: 確認 Spring Boot 能正常連接到 MySQL Docker 容器
4. **測試順序**: 必須先註冊、登入獲得 token，才能測試其他需要認證的 API
