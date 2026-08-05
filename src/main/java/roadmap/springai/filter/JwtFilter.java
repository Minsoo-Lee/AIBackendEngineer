package roadmap.springai.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import roadmap.springai.util.JwtUtil;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // 1. Authorization 헤더에서 토큰 추출
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 2. 토큰 검증
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);

                // 3. SecurityContext에 인증 정보 저장
                UsernamePasswordAuthenticationToken auth
                        = new UsernamePasswordAuthenticationToken(
                                username, null, List.of());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 4. 다음 필터로 전달
        filterChain.doFilter(request, response);
    }
}
