package tw.idv.yiwei.user.service;

import java.util.Map;

import tw.idv.yiwei.user.dto.LoginRequestDto;
import tw.idv.yiwei.user.dto.RegisterRequestDto;
import tw.idv.yiwei.user.entity.Users;

public interface UserService {
	public Users register(RegisterRequestDto registerDto);
	
	public Map<String, Object> login(LoginRequestDto loginDto);
	
	public Users currentUser(String token);
}
