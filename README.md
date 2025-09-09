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
