package com.example.demo.filter;

import com.example.demo.service.CustomUserDetailService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private JwtUtil jwtUtil ;
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String bearertoken = httpServletRequest.getHeader("Authorization");
        String username = null ;
        String token = null ;
        if(bearertoken != null && bearertoken.startsWith("Bearer")){
            token = bearertoken.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
           UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){

                UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(userDetails, null ,userDetails.getAuthorities());
                 upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                  SecurityContextHolder.getContext().setAuthentication(upat);

            }else {
                System.out.println("invalid  Token   !!!!");
            }

            }catch (Exception ex)
            {
              ex.printStackTrace();
            }
        } else {
            System.out.println("invalid Bearer Token format  !!!!");
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
