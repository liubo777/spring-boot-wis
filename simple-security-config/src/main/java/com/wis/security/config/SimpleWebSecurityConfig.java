package com.wis.security.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.wis.security.ctrl.DefaultController;
import com.wis.security.filter.EncryptUsernamePasswordAuthenticationFilter;
import com.wis.security.handler.DefaultLoginErrorHandler;
import com.wis.security.handler.DefaultLoginSuccessHandler;
import com.wis.security.handler.LoginErrorHandler;
import com.wis.security.handler.LoginSuccessHandler;
import com.wis.security.pojo.CaptchaAuthenticationDetailsSource;
import com.wis.security.pojo.WisSecurityConfigurationProperty;
import com.wis.security.provider.CaptchaDaoAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by liuBo
 * 2019/12/5.
 */
@Slf4j
@Import({CaptchaAuthenticationDetailsSource.class,DefaultController.class})
public class SimpleWebSecurityConfig extends WebSecurityConfigurerAdapter {
    public static String[] DEFAULT_IGNORES="/css/**,/js/**,/images/**,/webjars/**,/**/favicon.ico,/captcha.jpg,/static/**".split(",");
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private WisSecurityConfigurationProperty wisSecurityConfigurationProperty;

    @Autowired
    private CaptchaAuthenticationDetailsSource authenticationDetailsSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/captcha.jpg").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .authenticationDetailsSource(authenticationDetailsSource)
                    .loginPage("/login")
                    .successHandler(loginSuccessHandler())
                    .failureHandler(loginErrorHandler())
                    .permitAll();

        http.exceptionHandling().accessDeniedPage(wisSecurityConfigurationProperty.getErrorPage());

        http.addFilterBefore(encryptUsernamePasswordAuthenticationFilter(true,true),UsernamePasswordAuthenticationFilter.class);


        http.sessionManagement()
                .sessionFixation()
                .changeSessionId()
                .maximumSessions(wisSecurityConfigurationProperty.getMaxSessionNum())
                .maxSessionsPreventsLogin(true)
                .expiredUrl("/login?error=expired");


    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth){
        DaoAuthenticationProvider provider = new CaptchaDaoAuthenticationProvider(wisSecurityConfigurationProperty);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        auth.authenticationProvider(provider);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        List<String> ignores=new ArrayList<String>(Arrays.asList(DEFAULT_IGNORES));
        if(wisSecurityConfigurationProperty.getErrorPage()!=null){
            ignores.add(wisSecurityConfigurationProperty.getErrorPage());
        }
        if(wisSecurityConfigurationProperty.getSecurityIgnore()!=null && wisSecurityConfigurationProperty.getSecurityIgnore().size()>0){
            ignores.addAll(wisSecurityConfigurationProperty.getSecurityIgnore());
        }
        ignores.forEach((item)->log.info("ignored resource:{}",item.trim()));
        web.ignoring().antMatchers(ignores.toArray(new String[0]));
    }


    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 需按实际替代
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public LoginSuccessHandler loginSuccessHandler(){
        return new DefaultLoginSuccessHandler();
    }

    /**
     * 需按实际替代
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public LoginErrorHandler loginErrorHandler(){
        return new DefaultLoginErrorHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /**
     * 登录界面的解密方法
     * @return
     */
    public UsernamePasswordAuthenticationFilter encryptUsernamePasswordAuthenticationFilter(boolean encryptUsername, boolean encryptPassword) throws Exception {
        UsernamePasswordAuthenticationFilter filter = new EncryptUsernamePasswordAuthenticationFilter(encryptUsername,encryptPassword);
        //只有post请求才拦截
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));

        return filter;
    }




    @Bean
    @ConditionalOnMissingBean
    public Producer producer(){
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","150");
        properties.setProperty("kaptcha.image.height","50");
        properties.setProperty("kaptcha.textproducer.char.string","0123456789");
        properties.setProperty("kaptcha.textproducer.char.length","4");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

}
