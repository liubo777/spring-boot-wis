### 简单介绍  
基于SpringSecurity做的一个安全认证配置集，集成了页面的username，password加密和后台的解密、cors配置、验证码校验

#### application.yml: 
```$xslt
wis:
  security:
    security-ignore:
      - /core/**
      - /img/**
      - /sass/**
      - /css/**
      - /error/**
      - /fonts/**
      - /js/**
      - /static/**
```



#### pom:  
```xml
    <repositories>
        <repository>
            <name>mvn-release-repository</name>
            <id>mvn-release-repository</id>
            <url>https://raw.github.com/liubo777/mvn-repository/release</url>
        </repository>
    </repositories>
    
    <dependencies>
         <dependency>
                     <groupId>com.wis.security</groupId>
                     <artifactId>simple-security-config</artifactId>
                     <version>1.0</version>
          </dependency>    
    </dependencies>        
    
```
 
#### 使用（简单验证码本地登录）:  
```java

    @Configuration
    public class SimpleSecurityConfig  extends SimpleWebSecurityConfig {
        /**
         * 需按实际替代
         * @return
         */
        @Bean
        public LoginSuccessHandler loginSuccessHandler(){
            return new CustomerLoginSuccessHandler();
        }
        
        @Override
        protected void configure(HttpSecurity http) throws Exception {
    
            http.authorizeRequests()
                    .antMatchers("/urban/**","/schuser/**").hasAuthority("106010")
                    .antMatchers("/district/**").hasAuthority("106020")
                    .antMatchers("/school/**").hasAuthority("106030")
                    .antMatchers("/ditch/**").hasAuthority("106040");
            super.configure(http);
        }
    
    
    }
    
    @Slf4j
    public class CustomerLoginSuccessHandler extends LoginSuccessHandler {
        @Autowired
        private TbokUserMapper tbokUserMapper;
    
        @Override
        public void onLogonSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) {
            //输出登录提示信息
            String authName = authentication.getName();
            if (authName != null) {
                //验证通过的处理
                //保存当前用户的信息
                TbokUser dto = tbokUserMapper.selectByUserName(authName);
                request.getSession().setAttribute(Constant.KEY_LOGIN_USER, dto);
    
            }
        }
    
    }
    
    
    @Service
    public class UserDetailService implements UserDetailsService {
        @Autowired
        private TbokUserMapper tbokUserMapper;
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            TbokUser user = tbokUserMapper.selectByUserName(username);
            if (user==null){
                throw new UsernameNotFoundException("用户不存在");
            }
            if (user.getUserRole()==null){
                throw new UsernameNotFoundException("用户无权限登录");
            }
            List<GrantedAuthority> authorities = new ArrayList<>();
            Stream.of(user.getUserRole().split(",")).forEach(r->authorities.add(new SimpleGrantedAuthority(r)));
            user.setAuthorityList(authorities);
            return user;
        }
    }

```



