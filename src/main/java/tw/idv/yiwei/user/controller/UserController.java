package tw.idv.yiwei.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;
import tw.idv.yiwei.user.dto.LoginRequestDto;
import tw.idv.yiwei.user.dto.LoginResponseDto;
import tw.idv.yiwei.user.dto.RegisterRequestDto;
import tw.idv.yiwei.user.dto.RegisterResponseDto;
import tw.idv.yiwei.user.entity.Users;
import tw.idv.yiwei.user.service.UserService;
import tw.idv.yiwei.utils.BusinessException;
import tw.idv.yiwei.utils.CoreResponseDto;
import tw.idv.yiwei.utils.JwtUtil;

@RestController
@RequestMapping("api/users")
public class UserController {

	private final JwtUtil jwtUtil;

	@Autowired
	private UserService service;

	UserController(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

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
	public ResponseEntity<?> currentUser(@RequestHeader("Authorization") String authorizationHeader) {
		// (1) header 格式檢查
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("標頭驗證資料不符合格式");
		}
		// (2) 提取 jwt token
		String token = authorizationHeader.substring(7);

		// (3) 解析 jwt token
		try {
			Users queryUsers = service.currentUser(token);

			// a. token 驗證成功
			return ResponseEntity.status(200).body("成功提取 token");
		} catch (ExpiredJwtException e) {

			// b. token 逾時失效
			e.printStackTrace();
			return null;
		} catch (SignatureException e) {

			// c. token 簽名失效
			e.printStackTrace();
			return null;
		} catch (Exception e) {

			// d. token 驗證或資料抓取失敗
			e.printStackTrace();
			return null;
		}
	}
}
