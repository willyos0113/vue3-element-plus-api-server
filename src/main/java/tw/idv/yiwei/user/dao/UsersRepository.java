package tw.idv.yiwei.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.idv.yiwei.user.entity.Users;

public interface UsersRepository extends JpaRepository<Users, String> {

	// ✅ 直接使用 JpaRepository 提供的方法：
	// save(Users user) - 新增用戶
	// findAll() - 查詢所有用戶
	// findById(String id) - 根據ID查詢
	// deleteById(String id) - 根據ID刪除
	// existsById(String id) - 檢查是否存在

	// 自動生成 SQL: SELECT COUNT(*) > 0 FROM users WHERE name = ?
	public boolean existsByName(String name);

	// 自動生成 SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
	public boolean existsByEmail(String email);

	// 自動生成 SQL: SELECT * FROM users WHERE name = ?
	public Users findByName(String name);
}
