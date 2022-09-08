package com.sdps.common.log.service;

import com.sdps.common.log.model.Audit;

/**
 * 审计日志接口
 *
 */
public interface IAuditService {
    void save(Audit audit);
}
