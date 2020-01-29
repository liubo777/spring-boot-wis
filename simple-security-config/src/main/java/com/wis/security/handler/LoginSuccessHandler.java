package com.wis.security.handler;

import com.wiscess.textbook.common.Constant;
import com.wiscess.textbook.mapper.TbokUserMapper;
import com.wiscess.textbook.model.TbokUser;
import com.wiscess.textbook.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liuBo
 * 2019/12/16.
 */
@Slf4j
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	@Autowired
    private DictService dictService;
	
    public LoginSuccessHandler(){
        log.debug("loginSuccessHandler init");
        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {
        if(authentication.isAuthenticated()){
            onLogonSuccess(request,response,authentication);
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
    @Autowired
    private TbokUserMapper tbokUserMapper;
    protected void onLogonSuccess(HttpServletRequest request, HttpServletResponse response,
                                  Authentication authentication) {
        //输出登录提示信息
        String authName = authentication.getName();
        if (authName != null) {
            //验证通过的处理
            //保存当前用户的信息
            TbokUser dto = tbokUserMapper.selectByUserName(authName);
            String roleName = dictService.getNameById(Integer.valueOf(dto.getUserRole()));
            dto.setRoleName(roleName);
            request.getSession().setAttribute(Constant.KEY_LOGIN_USER, dto);
            //查询当前用户对应角色的菜单
        }
    }

    public String getIpAddress(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if	 (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
