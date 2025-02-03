package com.example.demo.global.util;

import com.example.demo.domain.user.application.CustomUserDetailsService;
import com.example.demo.domain.user.dao.RefreshTokenRepository;
import com.example.demo.domain.user.domain.RefreshToken;
import com.example.demo.domain.user.dto.response.TokenDto;
import com.example.demo.domain.user.exception.InvalidTokenException;
import com.example.demo.domain.user.exception.TokenExpiredExcpetion;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;


    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private static final long ACCESS_TIME = 60 * 1000000000L;   // 1시간
    private static final long REFRESH_TIME = 30 * 24 * 60 * 60 * 1000L;    // 30일
    public static final String ACCESS_TOKEN = "Authorization";
    public static final String REFRESH_TOKEN = "Refresh-Token";

    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 환경 변수로 지정한 SecretKey값으로 JWT 서명 키를 초기화
     *
     * @PostConstruct 어노테이션에 의해 의존성 주입이 완료된 후 자동으로 실행 base64로 인코딩된 비밀 키를 디코딩, JWT 토큰 생성 및 검증에 사용할 서명 키를 생성
     */
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * Request로부터 입력받은 Type에 해당하는 Token값을 반환
     *
     * @param request 사용자의 HTTP Request
     * @param type    Token의 종류
     * @return AccessToken값 혹은 RefreshToken값
     */
    public String getHeaderToken(HttpServletRequest request, String type) {
        String token;
        if (HttpHeaders.AUTHORIZATION.equals(type)) {
            token = request.getHeader(HttpHeaders.AUTHORIZATION);
        } else {
            token = request.getHeader(REFRESH_TOKEN);
        }

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);  // "Bearer " 제거
        }
        return token;
    }

    /**
     * User의 email을 입력받아 AccessToken 및 RefreshToken 생성 및 해당 정보 반환
     *
     * @param email User의 Email
     * @return JWT 정보를 DTO로 반환
     */
    public TokenDto createAllToken(String email) {
        Date now = new Date();

        String accessToken = createToken(email, "Access");

        String refreshToken = createToken(email, "Refresh");

        Instant refreshTokenExpiredAt = Instant.now().plus(30, ChronoUnit.DAYS);

        return TokenDto.of(accessToken, refreshToken, refreshTokenExpiredAt);
    }

    /**
     * 해당 Email에 입력받은 Type의 JWT Token을 생성
     *
     * @param email User의 Email
     * @param type  Token의 종류
     * @return 생성한 Token값
     */
    public String createToken(String email, String type) {

        Date date = new Date();

        Claims claims = Jwts.claims();
        claims.put("email", email);

        long time = ACCESS_TIME;

        if (type.equals("Refresh")) {
            time = REFRESH_TIME;
        }

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(date.getTime() + time))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    /**
     * 해당 토큰이 유효한지 검증
     *
     * @param token JWT 토큰값
     * @return Token의 유효 여부(True, False)
     */
    public Boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredExcpetion();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    /**
     * 해당 RefreshToken이 유효한지 1차 검증 및 Redis에 저장된 RefreshToken과 동일한지 2차 검증
     *
     * @param token RefreshToken값
     * @return RefreshToken 유효 여부 (True, False)
     */
    public Boolean refreshTokenValidation(String token) {
        if (!validateToken(token)) {
            return false;
        }

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByEmail(getEmailFromToken(token));
        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    /**
     * 해당 Email로부터 UserDetails객체를 불러온 이후 인증 객체 생성
     *
     * @param email User의 Email
     * @return 인증 객체(Authentication)
     */
    public Authentication createAuthentication(String email) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * Token값으로부터 해당 User의 Email정보을 추출
     *
     * @param token Token값 (Access)
     * @return Email
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
                .get("email", String.class);
    }

    /**
     * Response의 Header에 AccessToken값을 설정
     *
     * @param response    HTTP Response
     * @param accessToken AccessToken값
     */
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
    }

    /**
     * Response의 Header에 RefreshToken값을 설정
     *
     * @param response     HTTP Response
     * @param refreshToken RefreshToken값
     */
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader(REFRESH_TOKEN, refreshToken);
    }

    /**
     * 토큰의 만료 시간을 반환하는 메서드
     *
     * @param token JWT 토큰 값
     * @return 만료 시간 (Date)
     */
    public Date getExpiredAt(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration(); // Claims에서 만료 시간 추출
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

}
