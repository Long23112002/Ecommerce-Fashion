package org.example.ecommercefashion.security;

import com.longnh.utils.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.response.ErrorResponse;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getServletPath().startsWith("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String email;
    final String jwtKey = jwtService.getJwtKey();

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7);
    try {
      email = jwtService.extractUserName(jwt, jwtKey);
    } catch (JwtException e) {
      writeException(response, request);
      return;
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
      if (jwtService.isTokenValid(jwt, userDetails, jwtKey)) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();


        Set<GrantedAuthority> modifiableAuthorities = new HashSet<>(authorities);

        Set<SimpleGrantedAuthority> permissions = jwtService.extractAuthoritiesSystem(jwt, jwtKey);

        modifiableAuthorities.addAll(permissions);

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, modifiableAuthorities);

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    }
    filterChain.doFilter(request, response);
  }

  private void writeException(HttpServletResponse response, HttpServletRequest request)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String path = request.getRequestURI();
    String timestamp = Instant.now().toString();
    ErrorResponse errorResponse =
        new ErrorResponse(
            HttpServletResponse.SC_UNAUTHORIZED, ErrorMessage.JWT_EXPIRED.val(), path, timestamp);

    response.getWriter().write(JsonParser.toJson(errorResponse));
  }


}
