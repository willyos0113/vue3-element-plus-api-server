package tw.idv.yiwei.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {
	@NotBlank(message = "用戶名不得為空")
	@Size(min = 2, max = 30, message = "長度必須為2~30字元")
	private String name;

	@NotBlank(message = "密碼不得為空")
	@Size(min = 6, max = 30, message = "長度必須為6~30字元")
	private String password;

	private String identity;
}
