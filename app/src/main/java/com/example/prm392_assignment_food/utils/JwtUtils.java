package com.example.prm392_assignment_food.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Lớp tiện ích để xử lý JSON Web Tokens (JWT).
 */
public final class JwtUtils {

    // Private constructor để ngăn việc tạo đối tượng từ lớp này
    private JwtUtils() {}

    /**
     * Lấy User ID từ một JWT token.
     *
     * @param token Chuỗi JWT.
     * @return User ID dưới dạng String, hoặc null nếu token không hợp lệ hoặc không chứa claim "userId".
     */
    public static String getUserId(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            // Thay "userId" bằng tên claim chính xác trong token của bạn nếu nó khác
            return decodedJWT.getClaim("userId").asString();
        } catch (JWTDecodeException e) {
            // Token không hợp lệ
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy Role của user từ một JWT token.
     *
     * @param token Chuỗi JWT.
     * @return Role dưới dạng String, hoặc null nếu token không hợp lệ hoặc không chứa claim "role".
     */
    public static String getRole(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            // Thay "role" bằng tên claim chính xác trong token của bạn nếu nó khác
            return decodedJWT.getClaim("role").asString();
        } catch (JWTDecodeException e) {
            // Token không hợp lệ
            e.printStackTrace();
            return null;
        }
    }
}
