package com.wis.security.filter;

import com.wis.security.encoder.RSA_Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liuBo
 * 2019/12/16.
 */
public class EncryptUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter implements OrderedFilter {
    /**
     * 过滤器顺序
     */
    private int FILTER_ORDER = -1000;

    private boolean encryptUsername;
    private boolean encryptPassword;

    public static final String SPRING_SECURITY_LAST_USERNAME_KEY="SPRING_SECURITY_LAST_USERNAME_KEY";

    @Autowired
    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    public EncryptUsernamePasswordAuthenticationFilter(boolean encryptUsername, boolean encryptPassword) {
        this.encryptUsername=encryptUsername;
        this.encryptPassword=encryptPassword;
    }
    @Override
    public int getOrder() {
        return FILTER_ORDER;
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }
        //登录请求
        EncryptRequestWrapper loginRequest = new EncryptRequestWrapper(request);
        chain.doFilter(loginRequest, response);
    }

    private class EncryptRequestWrapper extends HttpServletRequestWrapper {
        public EncryptRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        /**
         * * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
         * * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
         * * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
         */
        @Override
        public String getParameter(String name) {
            if((encryptUsername && name.equals(getUsernameParameter()))
                    ||(encryptPassword && name.equals(getPasswordParameter()))){
                //解压
                //log.debug("decrypt {}",name);
                String value=getOrgRequest().getParameter(name);
                try {
                    value= RSA_Encrypt.decrypt(value.toString(),true);
                } catch (Exception e) {
                    e.printStackTrace();
                    value="";
                }
                //log.debug("value={}",value);
                return value;
            }
            return super.getParameter(name);
        }

        /** * 获取最原始的request * * @return */
        public HttpServletRequest getOrgRequest() {
            return (HttpServletRequest)this.getRequest();
        }
    }


}
