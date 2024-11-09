package org.example.ecommercefashion.security;

import com.longnh.exceptions.ExceptionHandle;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.JwtEnum;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class JwtService {

  @Value("${jwt.key}")
  private String jwtKey;

  @Value("${jwt.expiration}")
  private long jwtExpiration;

  @Value("${refresh-token.key}")
  private String jwtRefreshKey;

  @Value(("${refresh-token.expiration}"))
  private long jwtRefreshExpiration;

  private final PermissionRepository permissionRepository;

  private Set<String> getPermissionsUserByRBAC(User user) {
    return permissionRepository.findAllPermissionsUserByRBAC(user.getId());
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, jwtRefreshExpiration, jwtRefreshKey);
  }

  public boolean isTokenValid(String token, UserDetails userDetails, String key) {
    final String userName = extractUserName(token, key);
    return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token, key);
  }

  public String extractUserName(String token, String key) {
    return extractClaim(token, Claims::getSubject, getSigningKey(key));
  }

  public boolean isTokenExpired(String token, String key) {
    return extractExpiration(token, key).before(new Date());
  }

  private Claims extractAllClaims(String token, Key key) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  private Date extractExpiration(String token, String key) {
    return extractClaim(token, Claims::getExpiration, getSigningKey(key));
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, Key key) {
    final Claims claims = extractAllClaims(token, key);
    return claimsResolver.apply(claims);
  }

  private String buildToken(
      Map<String, Object> extraClaims, UserDetails userDetails, long expiration, String key) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey(key), SignatureAlgorithm.HS256)
        .compact();
  }

  private Key getSigningKey(String key) {
    byte[] keyBytes = Decoders.BASE64.decode(key);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    buildClaimsWithPermissionsSystem(extraClaims, (User) userDetails);
    return buildToken(extraClaims, userDetails, jwtExpiration, jwtKey);
  }

  private void buildClaimsWithPermissionsSystem(Map<String, Object> extraClaims, User user) {
    Set<String> permissionsSystemInAclEntry = getPermissionsUserByRBAC(user);
    Set<String> permissionsRbac = getPermissionsUserByRBAC(user);

    Set<String> allPermissions = new HashSet<>(permissionsSystemInAclEntry);
    allPermissions.addAll(permissionsRbac);
    extraClaims.put(JwtEnum.AUTHORITIES_SYSTEM.val(), allPermissions);
    extraClaims.put(JwtEnum.USER_ID.val(), user.getId());
  }

  public String generateNewRefreshTokenWithOldExpiryTime(
      String oldRefreshToken, UserDetails userDetails) {
    Date oldExpiryTime = extractExpiration(oldRefreshToken, jwtRefreshKey);
    return buildTokenWithExpiry(new HashMap<>(), userDetails, oldExpiryTime, jwtRefreshKey);
  }

  private String buildTokenWithExpiry(
      Map<String, Object> extraClaims, UserDetails userDetails, Date expiration, String key) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(expiration)
        .signWith(getSigningKey(key), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractVerifyEmail(String token, String key) {
    if (!validateVerifyEmailToken(token, key)) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.OTP_IS_EXPIRED.val());
    }
    return extractClaim(token, Claims::getSubject, getSigningKey(key));
  }

  private boolean validateVerifyEmailToken(String token, String key) {
    final Claims claims = extractAllClaims(token, getSigningKey(key));
    final Boolean isEmailVerified = claims.get(JwtEnum.IS_EMAIL_VERIFIED.val(), Boolean.class);

    return (isEmailVerified && !isTokenExpired(token, key));
  }

  public Set<SimpleGrantedAuthority> extractAuthoritiesSystem(String token, String key) {
    Claims claims = extractAllClaims(token, getSigningKey(key));
    List<String> authoritiesList = claims.get(JwtEnum.AUTHORITIES_SYSTEM.val(), List.class);

    if (authoritiesList == null) {
      return Set.of();
    }

    return authoritiesList.stream()
        .map(String::toUpperCase)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
  }

  public JwtResponse decodeToken(String token) {
    token = removeBearer(token);
    System.out.println(token);
    Claims claims = extractAllClaims(token, getSigningKey(jwtKey));

    JwtResponse jwtResponse = new JwtResponse();
    jwtResponse.setAuthoritiesSystem(claims.get(JwtEnum.AUTHORITIES_SYSTEM.val(), List.class));
    jwtResponse.setUserId(claims.get(JwtEnum.USER_ID.val(), Long.class));
    jwtResponse.setSub(claims.getSubject());
    return jwtResponse;
  }

  public String removeBearer(String token) {
    if (token != null) {
      if (token.startsWith("Bearer ")) {
        return token.substring(7);
      }
      return token;
    }
    throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_TOKEN_NOT_FOUND);
  }

  public Long getIdUserByToken(String token) {
    JwtResponse jwtResponse = decodeToken(token);
    return jwtResponse.getUserId();
  }

}
