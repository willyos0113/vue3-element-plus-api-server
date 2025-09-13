package tw.idv.yiwei.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.idv.yiwei.user.dao.UsersRepository;
import tw.idv.yiwei.user.entity.LoginDto;
import tw.idv.yiwei.user.entity.RegisterDto;
import tw.idv.yiwei.user.entity.Users;
import tw.idv.yiwei.user.service.UserService;
import tw.idv.yiwei.utils.BusinessException;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UsersRepository repo;

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

		// 儲存用戶
		return repo.save(saveUsers);
	}

	@Override
	@Transactional
	public Users login(LoginDto loginDto) {
		// 查無使用者或密碼
		var queryUsers = repo.findByName(loginDto.getName());
		if (queryUsers == null || !queryUsers.getPassword().equals(loginDto.getPassword())) {
			throw new BusinessException("密碼或使用者錯誤");
		}

		// 傳送 webToken(先不處理...)

		return queryUsers;
	}

}
