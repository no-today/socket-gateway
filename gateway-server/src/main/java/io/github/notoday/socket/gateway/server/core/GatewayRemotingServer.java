package io.github.notoday.socket.gateway.server.core;

import io.github.notoday.netty.remoting.RemotingServer;
import io.github.notoday.socket.gateway.server.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

/**
 * @author no-today
 * @date 2022/06/20 16:42
 */
@Service
@RequiredArgsConstructor
public class GatewayRemotingServer implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationProperties applicationProperties;
    private final RemotingServer remotingServer;
    private final NettyRequestForwardProcessor nettyRequestForwardProcessor;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        remotingServer.registerDefaultProcessor(nettyRequestForwardProcessor, Executors.newFixedThreadPool(applicationProperties.getForwardThreads()));
        remotingServer.start();
    }
}
