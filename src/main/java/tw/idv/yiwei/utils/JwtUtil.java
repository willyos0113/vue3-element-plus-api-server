package tw.idv.yiwei.utils;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import tw.idv.yiwei.user.entity.Users;

@Component
public class JwtUtil {
	// 密鑰 - 用來簽署 jwt 的核心元素，建議需夠長如至少 256 位元
	@Value("${jwt.secret-key}")
	private String secretKey;
	// 過期時間 - 定義了 jwt 的有效期限為 15 分鐘
	@Value("${jwt.expire-time}")
	private long expireTime;

	/**
	 * 創建 jwt token 方法
	 * 
	 * @param userinfo  - 使用者基本資料 userinfo = { id, identity, name, }
	 * @param ttlMillis - token 過期時間
	 * @return jwt 字串，包含 header, payload, signature 三部分
	 */
	public String createJWT(Users userinfo, Long ttlMillis) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		// (1) 取得當前時間戳記
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		if (ttlMillis == null)
			ttlMillis = expireTime;

		// (2) 計算過期時間戳記
		long expMillis = nowMillis + ttlMillis;
		Date expDate = new Date(expMillis);

		// (3) 取得加密後的密鑰
		SecretKey secretKey = generateKey();

		// (4) 創建 jwt
		return Jwts.builder()

				// 標準聲明
				.setIssuer("vue3-element-plus-api-server").setIssuedAt(now)

				// 自定義聲明
				.claim("id", userinfo.getId()).claim("name", userinfo.getName()).claim("identity", userinfo.getIdentity())

				// 簽名
				.signWith(secretKey, signatureAlgorithm).setExpiration(expDate).compact();
	}

	/**
	 * 密鑰加密方法
	 * 
	 * @return SecretKey 物件，用來為 jwt 附加簽章時所用
	 */
	private SecretKey generateKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	/**
	 * 驗證 jwt Token 方法
	 * 
	 * @param token - token 字串
	 * @return 驗證成功或失敗
	 */
	public boolean validateToken(String token) {
		try {
			// (1) 設定密鑰，驗證完整性、過期時間及格式
			Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(token);
			return true;

			// (2) 驗證失敗會拋出 JwtException 例外
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * 從 Token 中取得使用者 id 之方法 (ps: validateToken() 成功後，才能保證 .parseClaimsJws() 不發生例外)
	 * 
	 * @param token - token 字串
	 * @return payload 中的主旨 (通常放使用者 id)
	 */
	public String getUserIdFromToken(String token) {
		// (1) 設定密鑰，驗證完整性、過期時間及格式，並只取出 payload 的部分
		Claims claims = Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(token).getBody();

		// (2) 從 payload 中再取出自定義 id 的部分並回傳
		return (String) claims.get("id");
	}

	/**
	 * 取得 jwt 過期時間 (毫秒)
	 * 
	 * @return JWT 的過期時間，單位為毫秒
	 */
	public long getExpireTime() {
		return expireTime;
	}
}
