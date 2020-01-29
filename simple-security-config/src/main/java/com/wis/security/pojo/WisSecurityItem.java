package com.wis.security.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by liuBo
 * 2019/12/9.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WisSecurityItem {
    String name;
    String val;
}
