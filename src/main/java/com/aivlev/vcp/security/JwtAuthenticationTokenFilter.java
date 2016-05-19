package com.aivlev.vcp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by aivlev on 5/12/16.
 */
public class JwtAuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    private JWTTokenUtils jwtTokenUtils;
//
//    @Value("${jwt.header}")
//    private String tokenHeader;
//
//    private String usernameParameter = "login";
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        Cookie cookie = WebUtils.getCookie((HttpServletRequest)request, "X-AUTH-VCP");
//        String authToken = null;
//        if(cookie != null){
//            authToken = cookie.getValue();
//        }
//
//        // authToken.startsWith("Bearer ")
//        // String authToken = header.substring(7);
//        String username = jwtTokenUtils.getUsernameFromToken(authToken);
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//            if (jwtTokenUtils.validateToken(authToken, userDetails)) {
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//        chain.doFilter(request, response);
//    }

}
