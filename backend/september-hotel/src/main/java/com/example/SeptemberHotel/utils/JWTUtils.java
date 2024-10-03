package com.example.SeptemberHotel.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;


/*
*  Tạo, phân tích và xác thực JWT
* */
public class JWTUtils {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // for 7 days

    private final SecretKey Key;

    // Khởi tạo khóa bí mật (SecretKey) sử dụng một chuỗi bí mật (secretString).
    public JWTUtils(){
        String secretString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.Key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    // Tạo một JWT mới cho người dùng dựa trên thông tin từ UserDetails.
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername()) // Thiết lập chủ đề của token là tên người dùng (username).
                .issuedAt(new Date(System.currentTimeMillis())) // Thiết lập thời điểm tạo token.
                .expiration(new  Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Thiết lập thời điểm hết hạn của token dựa trên EXPIRATION_TIME.
                .signWith(Key) // Ký token bằng khóa bí mật (Key) đã được khởi tạo trong constructor.
                .compact(); // Chuyển đổi đối tượng JWT thành chuỗi ký tự
    }

    // Trích xuất tên người dùng (username) từ JWT.
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    // Trích xuất bất kỳ thông tin nào từ JWT dựa trên hàm (Function) được cung cấp.
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        return claimsTFunction.apply(Jwts.parser().verifyWith(Key).build().parseSignedClaims(token).getPayload());
    }


    //  Kiểm tra xem token có hợp lệ hay không
    public boolean isValidToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Kiểm tra xem token đã hết hạn hay chưa.
    private boolean isTokenExpired(String token){
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
