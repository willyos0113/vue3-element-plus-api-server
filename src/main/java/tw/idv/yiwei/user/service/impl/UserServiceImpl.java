package tw.idv.yiwei.user.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.idv.yiwei.user.dao.UsersRepository;
import tw.idv.yiwei.user.entity.LoginDto;
import tw.idv.yiwei.user.entity.RegisterDto;
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
	public Users register(RegisterDto registerDto) {
		// 檢查 name 和 email 是否重複
		String inputName = registerDto.getName();
		String inputEmail = registerDto.getEmail();
		if (repo.existsByName(inputName)) {
			throw new BusinessException("使用者名稱重複");
		}
		if (repo.existsByEmail(inputEmail)) {
			throw new BusinessException("信箱地址重複");
		}

		// 密碼加密(先不做...)
		// ...

		// 建立 Users 實體 (id, updateTime 交由 SQL 處理)
		var saveUsers = new Users();
		saveUsers.setName(registerDto.getName());
		saveUsers.setEmail(registerDto.getEmail());
		saveUsers.setPassword(registerDto.getPassword());
		saveUsers.setIdentity(registerDto.getIdentity());

		// 儲存使用者
		return repo.save(saveUsers);
	}

	@Override
	@Transactional
	public Map<String, Object> login(LoginDto loginDto) {
		// 查無使用者或密碼
		var queryUsers = repo.findByName(loginDto.getName());
		if (queryUsers == null || !queryUsers.getPassword().equals(loginDto.getPassword())) {
			throw new BusinessException("密碼或使用者錯誤");
		}

		// 生成 jwt token
		String jwtToken = jwtUtil.createJWT(queryUsers.getId(), queryUsers.getId(), null);

		// 取得 jwt 過期時間
		long expireInSeconds = jwtUtil.getExpireTime() / 1000;

		// 將使用者資料和 jwt token 回傳至服務層
		return Map.of("token", jwtToken, "users", queryUsers, "expiresIn", expireInSeconds);
	}

}
