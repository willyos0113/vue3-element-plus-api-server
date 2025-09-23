package tw.idv.yiwei.user.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.idv.yiwei.user.dao.UsersRepository;
import tw.idv.yiwei.user.dto.LoginRequestDto;
import tw.idv.yiwei.user.dto.RegisterRequestDto;
import tw.idv.yiwei.user.entity.Users;
import tw.idv.yiwei.user.service.UserService;
import tw.idv.yiwei.utils.BusinessException;
import tw.idv.yiwei.utils.JwtUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UsersRepository repo;
	@Autowired
	private JwtUtil jwtUtil;

	@Override
	@Transactional
	public Users register(RegisterRequestDto registerDto) {
		// (1) 檢查 name 和 email 是否重複
		String inputName = registerDto.getName();
		String inputEmail = registerDto.getEmail();
		if (repo.existsByName(inputName)) {
			throw new BusinessException("使用者名稱重複");
		}
		if (repo.existsByEmail(inputEmail)) {
			throw new BusinessException("信箱地址重複");
		}

		// (2) 密碼加密(先不做...)
		// ...

		// (3) 建立 Users 實體 (id, updateTime 交由 SQL 處理)
		var saveUsers = new Users();
		saveUsers.setName(registerDto.getName());
		saveUsers.setEmail(registerDto.getEmail());
		saveUsers.setPassword(registerDto.getPassword());
		saveUsers.setIdentity(registerDto.getIdentity());

		// (4) 儲存使用者
		return repo.save(saveUsers);
	}

	@Override
	@Transactional
	public Map<String, Object> login(LoginRequestDto loginDto) {
		// (1) 查無使用者或密碼
		var queryUsers = repo.findByName(loginDto.getName());
		if (queryUsers == null || !queryUsers.getPassword().equals(loginDto.getPassword())) {
			throw new BusinessException("密碼或使用者錯誤");
		}

		// (2) 生成 jwt token
		String jwtToken = jwtUtil.createJWT(queryUsers.getId(), queryUsers.getId(), null);

		// (3) 取得 jwt 過期時間
		long expireInSeconds = jwtUtil.getExpireTime() / 1000;

		// (4) 將使用者和 jwt token 回傳至控制器層
		return Map.of("token", jwtToken, "users", queryUsers, "expiresIn", expireInSeconds);
	}

	@Override
	@Transactional
	public Users currentUser(String token) {
		// (1) 驗證 token 並查詢使用者 id
		String userId = jwtUtil.getUserIdFromToken(token);
		if (userId == null || userId.isEmpty()) {
			throw new BusinessException("無法取得使用者 id");
		}

		// (2) 透過使用者 id，查詢使用者資料(1 row)
		Users queryUsers = repo.findById(userId).orElse(null);
		if(queryUsers == null) {
			throw new BusinessException("找不到使用者資料");
		}
		return queryUsers;
	}
}
