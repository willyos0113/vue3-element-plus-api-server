package tw.idv.yiwei.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tw.idv.yiwei.user.dto.LoginRequestDto;
import tw.idv.yiwei.user.dto.LoginResponseDto;
import tw.idv.yiwei.user.dto.RegisterRequestDto;
import tw.idv.yiwei.user.dto.RegisterResponseDto;
import tw.idv.yiwei.user.entity.Users;
import tw.idv.yiwei.user.service.UserService;
import tw.idv.yiwei.utils.BusinessException;
import tw.idv.yiwei.utils.CoreResponseDto;

@RestController
@RequestMapping("api/users")
public class UserController {

	@Autowired
	private UserService service;

	@PostMapping("register")
	public ResponseEntity<CoreResponseDto<RegisterResponseDto>> register(
			@Valid @RequestBody RegisterRequestDto registerRequestDto, BindingResult bindingResult) {

		// (1) 表單驗證失敗處理
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.badRequest().body(CoreResponseDto.validationError(errors));
		}

		// (2) 送至服務層，並處理成功和例外回應
		try {
			Users saveUsers = service.register(registerRequestDto);
			
			// a. 註冊成功
			var registerResponseDto = RegisterResponseDto.fromEntity(saveUsers);
			return ResponseEntity.status(201).body(CoreResponseDto.success(registerResponseDto, "註冊成功"));
		} catch (BusinessException e) {
			
			// b. 重複 email 或重複 name 例外發生
			return ResponseEntity.badRequest().body(CoreResponseDto.businessError(e.getMessage()));
		} catch (Exception e) {
			
			// c. 其他未預期例外發生
			return ResponseEntity.status(500).body(CoreResponseDto.systemError());
		}
	}

	@PostMapping("login")
	public ResponseEntity<CoreResponseDto<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto,
			BindingResult bindingResult) {

		// (1) 表單驗證失敗處理
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.badRequest().body(CoreResponseDto.validationError(errors));
		}

		// (2) 送至服務層，並處理成功和例外回應
		try {
			Map<String, Object> usersAndToken = service.login(loginRequestDto);
			Users queryUsers = (Users) usersAndToken.get("users");
			String accessToken = (String) usersAndToken.get("token");
			Long expireTime = (Long) usersAndToken.get("expiresIn");
			var loginResponseDto = LoginResponseDto.fromEntityWithToken(queryUsers, accessToken, expireTime);

			// a. 登入成功
			return ResponseEntity.status(200).body(CoreResponseDto.success(loginResponseDto, "登入成功"));
		} catch (BusinessException e) {

			// b. 查無使用者或密碼例外發生
			return ResponseEntity.status(401).body(CoreResponseDto.businessError(e.getMessage()));
		} catch (Exception e) {
			
			// c. 其他未預期例外發生
			return ResponseEntity.status(500).body(CoreResponseDto.systemError());
		}
	}

	@GetMapping("current")
	public void currentUser() {
		//
	}
}
