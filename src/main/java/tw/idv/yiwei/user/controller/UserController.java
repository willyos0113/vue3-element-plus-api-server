package tw.idv.yiwei.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tw.idv.yiwei.user.entity.LoginDto;
import tw.idv.yiwei.user.entity.RegisterDto;
import tw.idv.yiwei.user.entity.Users;
import tw.idv.yiwei.user.service.UserService;
import tw.idv.yiwei.utils.BusinessException;

@RestController
@RequestMapping("api/users")
public class UserController {

	@Autowired
	private UserService service;

	@PostMapping("register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterDto registerDto, BindingResult bindingResult) {

		// 表單驗證失敗處理
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.badRequest().body(errors);
		}

		// 送至服務層，並處理成功和例外回應
		try {
			var saveUsers = service.register(registerDto);
			// (1) 註冊成功
			return ResponseEntity.status(201)
					.body(Map.of("success", true, "message", "註冊成功", "userId", saveUsers.getId()));
		} catch (BusinessException e) {
			// (2) 重複 email 或重複 name 例外發生
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
		} catch (Exception e) {
			// (3) 其他未預期例外發生
			return ResponseEntity.status(500).body(Map.of("success", false, "message", "註冊失敗，請稍候再嘗試"));
		}
	}

	@PostMapping("login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, BindingResult bindingResult) {

		// 表單驗證失敗處理
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.badRequest().body(errors);
		}

		// 送至服務層，並處理成功和例外回應
		try {
			var usersAndToken = service.login(loginDto);
			var users = (Users) usersAndToken.get("users");
			// (1) 登入成功
			return ResponseEntity.status(200).body(Map.of(
					"success", true, 
					"message", "登入成功", 
					"token", usersAndToken.get("token"),
					"expiresIn", usersAndToken.get("expiresIn"),
					"users", Map.of(
							"id", users.getId(),
		                    "name", users.getName()
							)
					));
		} catch (BusinessException e) {
			// (2) 查無使用者或密碼例外發生
			return ResponseEntity.status(401).body(Map.of("success", false, "message", e.getMessage()));
		} catch (Exception e) {
			// (3) 其他未預期例外發生
			return ResponseEntity.status(500).body(Map.of("success", false, "message", "登入失敗，請稍候再嘗試"));
		}
	}
}
