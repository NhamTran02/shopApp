package com.example.shopapp.filter;

import com.example.shopapp.components.JWTTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;

import java.io.IOException;

@Component
@Log4j2
public class JwtTokenFilter extends OncePerRequestFilter{
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JWTTokenUtil jwtTokenUtil;


    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("-------------------prefilter----------------");

            if (isBypassToken(request)) {
                filterChain.doFilter(request, response);//enable bypass
                return;
            }

            final String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader==null || !authorizationHeader.startsWith("Bearer ")){
                response.sendError(HttpServletResponse.SC_FORBIDDEN,"Unauthorized");
                return;
            }
            if (authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")) {
                final String token = authorizationHeader.substring(7);
                final String phoneNumber=jwtTokenUtil.extractPhoneNumber(token);

                if(phoneNumber!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
                    UserDetails userDetails =userDetailsService.loadUserByUsername(phoneNumber);
                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken=
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
            filterChain.doFilter(request, response); //enable bypass
        }catch (Exception e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Unauthorized");
        }

    }
    private boolean isBypassToken(@NotNull HttpServletRequest request) {
        final String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return true;
        }
        return false;
    }
}
