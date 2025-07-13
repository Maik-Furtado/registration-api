package br.com.mkanton.cadastroapi.security;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {

    private static final KeyGenerator keyGenerator = new KeyGenerator();

    // Token expiration time in milliseconds (15 minutos)
    private static final int EXPIRATION_TIME = 900_000;

    private static final Key key = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode(
                    keyGenerator.generateKey(256, KeyGenerator.EncodingType.BASE64)
            )
    );

    /**
     * Generates a JWT containing the username and role as claims.
     *
     * @param username Username(subject).
     * @param role The role associated with the user.
     * @return Signed JWT Token.
     */
    public static String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    /**
     * Validates a token by checking its signature and expiration.
     *
     * @param token JWT Token.
     * @return true if the token is valid; false otherwise.
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token JWT Token.
     * @return Username extracted from token.
     */
    public static String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extract the role from the JWT token.
     *
     * @param token JWT Token .
     * @return Role extracted from token.
     */
    public static String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}
