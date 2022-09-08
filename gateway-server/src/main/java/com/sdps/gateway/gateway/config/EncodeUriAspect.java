package com.sdps.gateway.gateway.config;

import java.net.URI;
import java.util.List;

import com.sdps.gateway.gateway.constant.LoginServerConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

@Aspect
@Component
public class EncodeUriAspect {

    @Pointcut("execution(* org.springframework.web.reactive.DispatcherHandler.handle(..))")
    public void requestServer() {
    }

    @Around("requestServer()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        Object[] paramValues = proceedingJoinPoint.getArgs();
        Object obj = paramValues[0];
        if (obj instanceof ServerWebExchange) {
            ServerWebExchange exchange = (ServerWebExchange) obj;
            ServerHttpRequest request = exchange.getRequest();
            String originUriString = request.getURI().toString();
            List<String> clusterId = request.getHeaders().get(
                    LoginServerConstants.cluster_id);
            List<String> serverType = request.getHeaders().get(
                    LoginServerConstants.server_type);
            if (CollUtil.isNotEmpty(clusterId)
                    && CollUtil.isNotEmpty(serverType)) {
                originUriString = handleUrl(clusterId.get(0), originUriString,
                        serverType.get(0));
            }
            URI replaced = new URI(originUriString);
            ServerHttpRequest newRequest = request.mutate().uri(replaced)
                    .build();
            obj = exchange.mutate().request(newRequest).build();
        }
        return proceedingJoinPoint.proceed(new Object[]{obj});
    }

    private String handleUrl(String clusterId, String url, String serverType) {
        switch (serverType) {
            case "A":
                url = StrUtil.replace(url, LoginServerConstants.ambari_url_path,
                        LoginServerConstants.ambari_url_path + "-" + clusterId);
                break;
            case "S":
                url = StrUtil.replace(url, LoginServerConstants.sdo_url_path,
                        LoginServerConstants.sdo_url_path + "-" + clusterId);
                break;
            case "G":
                url = StrUtil.replace(url, LoginServerConstants.grafana_url_path,
                        LoginServerConstants.grafana_url_path + "-" + clusterId);
                break;
            case "L":
                url = StrUtil.replace(url, LoginServerConstants.logsearch_url_path,
                        LoginServerConstants.logsearch_url_path + "-" + clusterId);
                break;
            case "SHBASE":
                url = StrUtil.replace(url, LoginServerConstants.s_hbase_url_path,
                        LoginServerConstants.s_hbase_url_path + "-" + clusterId);
                break;
            case "SFL2":
                url = StrUtil.replace(url, LoginServerConstants.sfl2_url_path,
                        LoginServerConstants.sfl2_url_path + "-" + clusterId);
                break;
            case "R":
                url = StrUtil.replace(url, LoginServerConstants.ssm_url_path,
                        LoginServerConstants.ssm_url_path + "-" + clusterId);
                break;
            case "SLOG2":
                url = StrUtil.replace(url, LoginServerConstants.slog2_url_path,
                        LoginServerConstants.slog2_url_path + "-" + clusterId);
                break;
            case "D":
                url = StrUtil.replace(url, LoginServerConstants.sredis_url_path,
                        LoginServerConstants.sredis_url_path + "-" + clusterId);
                break;
            case "SEA_OSS":
                url = StrUtil.replace(url, LoginServerConstants.SEA_OSS_URL_PATH,
                        LoginServerConstants.SEA_OSS_URL_PATH + "-" + clusterId);
                break;
            case "SDT2":
                url = StrUtil.replace(url, LoginServerConstants.SDT2_URL_PATH,
                        LoginServerConstants.SDT2_URL_PATH + "-" + clusterId);
                break;
            case "SKAFKA":
                url = StrUtil.replace(url, LoginServerConstants.SKAFKA_URL_PATH,
                        LoginServerConstants.SKAFKA_URL_PATH + "-" + clusterId);
                break;
            default:
                break;
        }

        return url;
    }

}
