package tw.idv.yiwei.utils;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreResponseDto<T> {
	// === 操作結果 ===
	private boolean success;
	private String message; // 其他錯誤
	private Map<String, String> errors; // 欄位驗證錯誤
	private Long responseTime; // 回應時間戳記

	// === 傳輸資料 ===
	private T data;

	// === 建構用靜態方法(搭配 Lombok's Builder) ===
	// (1) 操作成功且有資料
	public static <T> CoreResponseDto<T> success(T data, String message) {
		return CoreResponseDto.<T>builder().success(true).message(message).data(data)
				.responseTime(System.currentTimeMillis()).build();
	}

	// (2) 操作成功且無資料
	public static <T> CoreResponseDto<T> success(String message) {
		return CoreResponseDto.<T>builder().success(true).message(message).responseTime(System.currentTimeMillis())
				.build();
	}

	// (3) 表單驗證錯誤
	public static <T> CoreResponseDto<T> validationError(Map<String, String> errors) {
		return CoreResponseDto.<T>builder().success(false).errors(errors).responseTime(System.currentTimeMillis())
				.build();
	}

	// (4) 業務邏輯錯誤
	public static <T> CoreResponseDto<T> businessError(String message) {
		return CoreResponseDto.<T>builder().success(false).message(message).responseTime(System.currentTimeMillis())
				.build();
	}

	// (5) 系統錯誤
	public static <T> CoreResponseDto<T> systemError() {
		return CoreResponseDto.<T>builder().success(false).message("系統錯誤，請稍後再試")
				.responseTime(System.currentTimeMillis()).build();
	}
}
