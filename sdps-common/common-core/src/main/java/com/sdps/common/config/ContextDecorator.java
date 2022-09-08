package com.sdps.common.config;

import com.sdps.common.context.LoginUserContextHolder;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


public class ContextDecorator implements TaskDecorator {
    @Override
//	@Nonnull
    public Runnable decorate(Runnable runnable) {

        // 获取主线程中的请求信息（我们的用户信息也放在里面）
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
               return () -> {
            try {
                // 将主线程的请求信息，设置到子线程中
                RequestContextHolder.setRequestAttributes(requestAttributes,true);
                runnable.run();
            } finally {
                // 线程结束，清空这些信息，否则可能造成内存泄漏
                RequestContextHolder.resetRequestAttributes();
                LoginUserContextHolder.clear();
            }
        };
    }

}