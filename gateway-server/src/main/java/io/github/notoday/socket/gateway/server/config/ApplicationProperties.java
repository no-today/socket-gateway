package io.github.notoday.socket.gateway.server.config;

import io.github.notoday.netty.remoting.RemotingServer;
import io.github.notoday.netty.remoting.config.NettyServerConfig;
import io.github.notoday.netty.remoting.core.NettyRemotingServer;
import io.github.notoday.socket.gateway.server.security.NettyTokenAuthenticator;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * @author no-today
 * @date 2022/06/20 16:44
 */
@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    /**
     * 停止转发 `runtime`
     */
    private boolean stopForward;

    /**
     * 转发处理程序线程数 `only startup`
     */
    private int forwardThreads = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 转发处理超时时间 `runtime`
     */
    private long forwardTimeoutMillis = TimeUnit.SECONDS.toMillis(5);

    /**
     * 转发应答超时时间 `runtime`
     */
    private long forwardReplyTimeoutMillis = TimeUnit.SECONDS.toMillis(5);

    /**
     * 通过该 Topic 调用命令处理器
     * <p>
     * 终端 -> 网关 -> 微服务
     */
    private String callServerTopic;

    /**
     * 通过该 Topic 调用客户端的命令处理器
     * <p>
     * 微服务 -> 网关 -> 终端
     */
    private String callClientTopic;

    /**
     * 集群模式
     */
    private boolean clusterMode = false;

    private NettyServerConfig nettyServerConfig = new NettyServerConfig();
    private SecurityConfig securityConfig = new SecurityConfig();

    @Bean
    public RemotingServer remotingServer(NettyTokenAuthenticator nettyTokenAuthenticator) {
        return new NettyRemotingServer(nettyServerConfig, null, nettyTokenAuthenticator);
    }

    @Data
    public static class SecurityConfig {
        private String base64Secret;
    }
}
