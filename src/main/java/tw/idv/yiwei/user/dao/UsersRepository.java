package tw.idv.yiwei.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.idv.yiwei.user.entity.Users;

public interface UsersRepository extends JpaRepository<Users, String> {

	// 自動生成 SQL: SELECT COUNT(*) > 0 FROM Users WHERE name = :name
	public boolean existsByName(String name);

	// 自動生成 SQL: SELECT COUNT(*) > 0 FROM Users WHERE email = :email
	public boolean existsByEmail(String email);

	// 自動生成 SQL: SELECT * FROM Users WHERE email = :email
	public Users findByEmail(String email);
	
	// 自動生成 SQL: SELECT * FROM Users WHERE id = :id
	// Optional<Users> findById(String id)
}
