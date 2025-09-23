package tw.idv.yiwei.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {
	@NotBlank(message = "Email不得為空")
	@Email(message = "Email格式不正確")
	private String email;
	
	@NotBlank(message = "密碼不得為空")
	@Size(min = 6, max = 30, message = "長度必須為6~30字元")
	private String password;

	private String identity;
}
