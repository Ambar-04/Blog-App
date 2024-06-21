package com.springboot.blog.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { //OncePerRequestFilter Filter base class that aims to guarantee a single execution per request dispatch.
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException { //FilterChain filterChain represents the remaining filters that need to be executed after your filter as OncePerRequestFilter allows single execution per request dispatch

        // get JWT token from http request
        String token = getTokenFromRequest(request);

        // validate the JWT token
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // get username from token
            String username = jwtTokenProvider.getUsername(token);

            // load the userDetails associated with token from the username
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // set the authentication
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // ensures the proper flow of the request through the filter chain, allowing subsequent filters to process the request and generate a response.
        // If you omit the filterChain.doFilter(request, response) line, the request will not progress further in the filter chain, and subsequent filters will not be executed.
        filterChain.doFilter(request, response);

    }

    private String getTokenFromRequest(HttpServletRequest request){
        // request contains the header where client passes jwt token in each request
        // from header fetch the value of "key" to get the bearerToken
        String bearerToken = request.getHeader("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
