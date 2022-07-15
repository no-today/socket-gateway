package io.github.notoday.socket.gateway.server.web.rest;

import io.github.notoday.netty.remoting.NettyRequestProcessor;
import io.github.notoday.netty.remoting.config.NettyClientConfig;
import io.github.notoday.netty.remoting.core.ErrorInfo;
import io.github.notoday.netty.remoting.core.NettyRemotingClient;
import io.github.notoday.netty.remoting.core.ResultCallback;
import io.github.notoday.netty.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author no-today
 * @date 2022/07/14 17:48
 */
@Slf4j
class RequestClientControllerTest {

    String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuby10b2RheSIsImF1dGgiOiJVU0VSIn0.NTleAm1L6H07JoElmUjWWG3h79drYOvVHMOy0jTAckKkwBbg98ViaqAAQD-fY8FXJWAXoDTm10BoXL8tRX6_qQ";

    @Test
    void loginAndPending() throws Exception {
        NettyRemotingClient client = new NettyRemotingClient(new NettyClientConfig(), null);

        client.login(token, "no-today", new ResultCallback<>() {
            @Override
            public void onSuccess(RemotingCommand command) {
                System.out.println(command);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                System.err.println(errorInfo);
                fail();
            }
        });

        client.registerDefaultProcessor(new NettyRequestProcessor() {
            @Override
            public boolean rejectRequest() {
                return false;
            }

            @Override
            public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws Exception {
                log.info("Receive cmd: {}", remotingCommand);
                return remotingCommand;
            }
        }, Executors.newFixedThreadPool(5));

        TimeUnit.MINUTES.sleep(5);
    }
}