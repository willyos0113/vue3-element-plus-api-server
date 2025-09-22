package tw.idv.yiwei.user.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tw.idv.yiwei.user.entity.Users;

@Data
@Builder
public class LoginResponseDto {
	// 使用者基本資料
	private String id;
	private String name;
	private String identity;
	private LocalDateTime updateTime;

	// Token 相關資訊
	private String accessToken; // 存取 token
	private Long expiresIn; // token 過期時間 (秒)

	// 工廠方法 - 從 Users 實體轉換為安全的回應 DTO (排除敏感資料)
	public static LoginResponseDto fromEntityWithToken(Users user, String token, Long expireTime) {
		return LoginResponseDto.builder().id(user.getId()).name(user.getName()).identity(user.getIdentity())
				.updateTime(user.getUpdateTime()).accessToken(token).expiresIn(expireTime).build();
	}
}
