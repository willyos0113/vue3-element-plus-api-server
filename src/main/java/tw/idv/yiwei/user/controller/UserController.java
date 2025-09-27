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
import io.jsonwebtoken.MalformedJwtException;
import jakarta.validation.Valid;
import tw.idv.yiwei.user.dto.CurrentResponseDto;
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
			return ResponseEntity.status(HttpStatus.CREATED).body(CoreResponseDto.success(registerResponseDto, "註冊成功"));
		} catch (BusinessException e) {

			// b. 重複 email 或重複 name 例外
			return ResponseEntity.badRequest().body(CoreResponseDto.businessError(e.getMessage()));
		} catch (Exception e) {

			// c. 其他未預期例外
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CoreResponseDto.systemError());
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
			return ResponseEntity.status(HttpStatus.OK).body(CoreResponseDto.success(loginResponseDto, "登入成功"));
		} catch (BusinessException e) {

			// b. 查無使用者或密碼例外
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CoreResponseDto.businessError(e.getMessage()));
		} catch (Exception e) {

			// c. 其他未預期例外
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CoreResponseDto.systemError());
		}
	}

	@GetMapping("current")
	public ResponseEntity<CoreResponseDto<CurrentResponseDto>> currentUser(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		// (1) header 格式檢查
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(CoreResponseDto.businessError("Header's token 資料格式不符"));
		}

		// (2) 提取 jwt token
		String token = authorizationHeader.substring(7);

		// (3) 解析 jwt token 並驗證
		try {
			Users queryUsers = service.currentUser(token);

			// a. token 驗證成功，並取得對應的使用者資料
			var currentResponseDto = CurrentResponseDto.fromEntity(queryUsers);
			return ResponseEntity.status(HttpStatus.OK)
					.body(CoreResponseDto.success(currentResponseDto, "token 有效並取得使用者資料"));

			// b. token 逾時失效
		} catch (ExpiredJwtException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CoreResponseDto.businessError("token 逾時失效"));

			// c. token 格式錯誤
		} catch (MalformedJwtException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CoreResponseDto.businessError("token 格式錯誤"));

			// d. 無法取得使用者 id / 找不到使用者資料
		} catch (BusinessException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CoreResponseDto.businessError(e.getMessage()));

			// e. 其他未預期例外
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CoreResponseDto.systemError());
		}
	}
}
