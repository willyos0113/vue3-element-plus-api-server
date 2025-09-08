package tw.idv.yiwei.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tw.idv.yiwei.user.entity.RegisterDto;

@RestController
@RequestMapping("api/users/register")
public class RegisterController {

	@PostMapping(value = "")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterDto registerDto, BindingResult bindingResult) {
		// 表單驗證錯誤處理
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.badRequest().body(errors);
		}

		// 表單驗證成功回應 .ok('xxx')
		return ResponseEntity.ok("註冊成功");
	}
}
