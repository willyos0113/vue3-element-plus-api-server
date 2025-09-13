package tw.idv.yiwei.user.service;

import java.util.Map;

import tw.idv.yiwei.user.entity.LoginDto;
import tw.idv.yiwei.user.entity.RegisterDto;
import tw.idv.yiwei.user.entity.Users;

public interface UserService {
	public Users register(RegisterDto registerDto);
	
	public Map<String, Object> login(LoginDto loginDto);
}
