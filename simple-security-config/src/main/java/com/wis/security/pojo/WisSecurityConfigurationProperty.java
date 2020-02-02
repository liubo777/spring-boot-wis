package com.wis.security.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置参数类
 * Created by liuBo
 * 2019/12/9.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WisSecurityConfigurationProperty {
    @Builder.Default
    String defaultLoginUrl = "/login";
    @Builder.Default
    String mode = "simple";
    @Builder.Default
    Integer maxSessionNum = 10;
    @Builder.Default
    Boolean captcha = true;
    @Builder.Default
    String errorPage="/500";
    List<String> csrfIgnore;
    //不经过spring security 验证
    List<String> securityIgnore;
    List<String> corsApprove;
    @Builder.Default
    List<String> corsMethods = new ArrayList(){{add("GET");add("POST");}};
    @Builder.Default
    String corsPattern = "/**";
    List<PropItem> propItems;



    class PropItem {
        String name;
        String val;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }
    }

}


