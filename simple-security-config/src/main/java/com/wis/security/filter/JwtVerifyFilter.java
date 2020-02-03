package com.wis.security.filter;

import com.wis.security.pojo.WisSecurityConfigurationProperty;
import com.wis.security.util.JwtUtils;
import com.wis.security.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by liuBo
 * 2020/2/3.
 */
public class JwtVerifyFilter extends OncePerRequestFilter {


    private WisSecurityConfigurationProperty wisSecurityConfigurationProperty;
    private List<RequestMatcher> requiresAuthenticationRequestMatcher;
    public JwtVerifyFilter() {
    }

    public JwtVerifyFilter(WisSecurityConfigurationProperty wisSecurityConfigurationProperty, List<RequestMatcher> matcher) {
        this.wisSecurityConfigurationProperty = wisSecurityConfigurationProperty;
        this.requiresAuthenticationRequestMatcher = matcher;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        boolean ifContinue = false;
        for (RequestMatcher requestMatcher : requiresAuthenticationRequestMatcher) {
            if (requestMatcher.matches(request)){
                ifContinue = true;
                break;
            }
        }
        if (!ifContinue){
            chain.doFilter(request, response);
            return ;
        }
        String token = request.getHeader("token");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write("{\"code\":\"500\",\"msg\":\"token invalid\"}");
            return;
        }
        token = token.replaceAll("Bearer ","");
        Map map = JwtUtils.verifyToken(token,wisSecurityConfigurationProperty.getJwtSecret());
        if (map==null){
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write("{\"code\":\"403\",\"msg\":\"token expired\"}");
            return;
        }
        request.setAttribute("jwtinfo",map);
        chain.doFilter(request, response);
    }

}
