package com.wis.security.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.wis.security.ctrl.DefaultController;
import com.wis.security.filter.EncryptUsernamePasswordAuthenticationFilter;
import com.wis.security.filter.JwtVerifyFilter;
import com.wis.security.handler.*;
import com.wis.security.pojo.CaptchaAuthenticationDetailsSource;
import com.wis.security.pojo.WisSecurityConfigurationProperty;
import com.wis.security.provider.CaptchaDaoAuthenticationProvider;
import com.wis.security.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

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

        //添加csrf验证
        if (!wisSecurityConfigurationProperty.getCsrfEnabled()){
            if (wisSecurityConfigurationProperty.getCsrfIgnore()!=null&&wisSecurityConfigurationProperty.getCsrfIgnore().size()>0){
                http.csrf().ignoringAntMatchers(wisSecurityConfigurationProperty.getCsrfIgnore().toArray(new String[0]));
            }
        }else {
            http.csrf().disable();
        }

        //关闭cors
        if (!wisSecurityConfigurationProperty.getCorsEnabled()){
            http.cors().disable();
        }



        //如果有jwt，那么jwt定义的拦截规则不进行安全认证
        if (StringUtils.isNotEmpty(wisSecurityConfigurationProperty.getJwtSecret())){
            http.authorizeRequests().antMatchers(wisSecurityConfigurationProperty.getJwtApprove().toArray(new String[0])).permitAll();
        }

        http.authorizeRequests()
                .antMatchers("/captcha.jpg").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .authenticationDetailsSource(wisSecurityConfigurationProperty.getCaptcha()?authenticationDetailsSource:webAuthenticationDetailsSource())
                    .loginPage("/login")
                    .successHandler(loginSuccessHandler())
                    .failureHandler(loginErrorHandler())
                    .permitAll();

        http.exceptionHandling().accessDeniedPage(wisSecurityConfigurationProperty.getErrorPage());


        http.addFilterBefore(encryptUsernamePasswordAuthenticationFilter(true,true),UsernamePasswordAuthenticationFilter.class);
        if (StringUtils.isNotEmpty(wisSecurityConfigurationProperty.getJwtSecret())){
            http.addFilterBefore(jwtVerifyFilter(),UsernamePasswordAuthenticationFilter.class);
        }

        if (wisSecurityConfigurationProperty.getSession()){
            http.sessionManagement()
                    .sessionFixation()
                    .changeSessionId()
                    .maximumSessions(wisSecurityConfigurationProperty.getMaxSessionNum())
                    .maxSessionsPreventsLogin(true)
                    .expiredUrl("/login?error=expired");
        }else{
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }


    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth){
        DaoAuthenticationProvider provider;
        //验证码
        if (wisSecurityConfigurationProperty.getCaptcha()){
            provider = new CaptchaDaoAuthenticationProvider(wisSecurityConfigurationProperty);
            provider.setPasswordEncoder(passwordEncoder);
            provider.setUserDetailsService(userDetailsService);
        //非验证码
        }else{
            provider = new DaoAuthenticationProvider();
            provider.setPasswordEncoder(passwordEncoder);
            provider.setUserDetailsService(userDetailsService);
        }

        auth.authenticationProvider(provider);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        List<String> ignores=new ArrayList<String>(Arrays.asList(DEFAULT_IGNORES));
        //异常页面不经过安全认证
        if(wisSecurityConfigurationProperty.getErrorPage()!=null){
            ignores.add(wisSecurityConfigurationProperty.getErrorPage());
        }
        //自定义不经过安全认证的url
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
        if (StringUtils.isEmpty(wisSecurityConfigurationProperty.getJwtSecret())){
            return new DefaultLoginSuccessHandler();
        }else{
            return new DefaultJwtLoginSuccessHandler();
        }
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

    /**
     * 登录界面的解密方法
     * @return
     */
    public JwtVerifyFilter jwtVerifyFilter() throws Exception {
        List<RequestMatcher> matchers = new ArrayList<>();
        wisSecurityConfigurationProperty.getJwtApprove().stream().forEach(s->{
            matchers.add(new AntPathRequestMatcher(s, "POST"));
            matchers.add(new AntPathRequestMatcher(s, "GET"));
        });
        JwtVerifyFilter filter = new JwtVerifyFilter(wisSecurityConfigurationProperty,matchers);

        return filter;
    }

    @Bean
    public WebAuthenticationDetailsSource webAuthenticationDetailsSource(){
        return new WebAuthenticationDetailsSource();
    }

//    @Bean
//    @ConditionalOnProperty(name = "wis.security.corsEnabled" , havingValue = "true")
//    @ConditionalOnMissingBean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                log.info("init cors mapping");
//                if (StringUtils.isNotEmpty(wisSecurityConfigurationProperty.getCorsPattern())){
//                    registry.addMapping(wisSecurityConfigurationProperty.getCorsPattern())
//                            .allowedHeaders("*")
//                            .allowedMethods(wisSecurityConfigurationProperty.getCorsMethods().toArray(new String[0]))
//                            .allowedOrigins(wisSecurityConfigurationProperty.getCorsOrigins().toArray(new String[0]));
//                }
//
//            }
//        };
//    }



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
