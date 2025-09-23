package tw.idv.yiwei.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.idv.yiwei.user.entity.Users;

public interface UsersRepository extends JpaRepository<Users, String> {

	// ✅ 直接使用 JpaRepository 提供的方法：
	// Users save(Users user) - 新增用戶
	// List<Users> findAll() - 查詢所有用戶
	// Optional<Users> findById(String id) - 根據ID查詢
	// void deleteById(String id) - 根據ID刪除
	// boolean existsById(String id) - 檢查是否存在

	// 自動生成 SQL: SELECT COUNT(*) > 0 FROM users WHERE name = ?
	public boolean existsByName(String name);

	// 自動生成 SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
	public boolean existsByEmail(String email);

	// 自動生成 SQL: SELECT * FROM users WHERE name = ?
	public Users findByEmail(String email);
	
	// 預設方法 Optional<Users> findById(String id)
}
