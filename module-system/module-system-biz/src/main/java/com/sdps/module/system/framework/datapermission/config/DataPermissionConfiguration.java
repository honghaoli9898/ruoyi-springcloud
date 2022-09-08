package com.sdps.module.system.framework.datapermission.config;

import com.sdps.common.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.module.system.dal.dataobject.dept.DeptDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * system 模块的数据权限 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class DataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer sysDeptDataPermissionRuleCustomizer() {
        return rule -> {
            // dept
            rule.addDeptColumn(AdminUserDO.class);
            rule.addDeptColumn(DeptDO.class, "id");
            // user
            rule.addUserColumn(AdminUserDO.class, "id");
        };
    }

}
