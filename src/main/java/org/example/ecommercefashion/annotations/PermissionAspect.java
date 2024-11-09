package org.example.ecommercefashion.annotations;

import com.longnh.exceptions.ExceptionHandle;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.ecommercefashion.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

  private final JwtService jwtService;
  private final HttpServletRequest request;

  @Autowired
  public PermissionAspect(JwtService jwtService, HttpServletRequest request) {
    this.jwtService = jwtService;
    this.request = request;
  }

  @Around("@annotation(checkPermission)")
  public Object checkPermission(ProceedingJoinPoint joinPoint, CheckPermission checkPermission)
      throws Throwable {
    String[] requiredPermissions = checkPermission.value();
    String token = getTokenFromRequest();
    Set<SimpleGrantedAuthority> userPermissions =
        jwtService.extractAuthoritiesSystem(token, jwtService.getJwtKey());

    Set<SimpleGrantedAuthority> uppercasedUserPermissions =
        userPermissions.stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getAuthority().toUpperCase()))
            .collect(Collectors.toSet());

    boolean hasPermission =
        Arrays.stream(requiredPermissions)
            .map(String::toUpperCase)
            .anyMatch(
                permission ->
                    uppercasedUserPermissions.contains(new SimpleGrantedAuthority(permission)));

    if (!hasPermission) {
      throw new ExceptionHandle(HttpStatus.FORBIDDEN, "Không có quyền truy cập");
    }

    return joinPoint.proceed();
  }

  private String getTokenFromRequest() {
    String authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7);
    }

    throw new ExceptionHandle(
        HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc không có token trong request.");
  }
}
