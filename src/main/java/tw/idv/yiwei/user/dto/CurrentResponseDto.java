package tw.idv.yiwei.user.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tw.idv.yiwei.user.entity.Users;

@Data
@Builder
public class CurrentResponseDto {
	// 使用者基本資料
	private String id;
	private String name;
	private String identity;
	private LocalDateTime updateTime;

	// 工廠方法 - 從 Users 實體轉換為安全的回應 DTO (排除敏感資料)
	public static CurrentResponseDto fromEntity(Users user) {
		return CurrentResponseDto.builder().id(user.getId()).name(user.getName()).identity(user.getIdentity())
				.updateTime(user.getUpdateTime()).build();
	}
}
