package io.github.notoday.socket.gateway.server.core;

import io.github.notoday.netty.remoting.NettyRequestProcessor;
import io.github.notoday.netty.remoting.RemotingServer;
import io.github.notoday.netty.remoting.core.ErrorInfo;
import io.github.notoday.netty.remoting.core.ResultCallback;
import io.github.notoday.netty.remoting.protocol.RemotingCommand;
import io.github.notoday.netty.remoting.security.RemotingSecurityUtils;
import io.github.notoday.socket.gateway.server.config.ApplicationProperties;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalRequestCallback;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;

/**
 * @author no-today
 * @date 2022/07/11 09:46
 */
@Slf4j
@Service
@AllArgsConstructor
public class NettyRequestForwardProcessor implements NettyRequestProcessor {

    private final ApplicationProperties applicationProperties;
    private final RocketMQTemplate rocketMQTemplate;
    private final RemotingServer remotingServer;

    @Override
    public boolean rejectRequest() {
        return applicationProperties.isStopForward();
    }

    @PostConstruct
    public void init() {
        rocketMQTemplate.setAsyncSenderExecutor(Executors.newFixedThreadPool(applicationProperties.getForwardThreads()));
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand request) throws Exception {
        String login = RemotingSecurityUtils.getCurrentLogin(channelHandlerContext.channel());
        int reqId = request.getReqId();

        rocketMQTemplate.sendAndReceive(applicationProperties.getCallServerTopic() + ":" + request.getCode(), request, new RocketMQLocalRequestCallback<RemotingCommand>() {

            @Override
            public void onSuccess(RemotingCommand response) {
                response.setReqId(reqId);
                response.markResponseType();

                remotingServer.invokeOneway(login, response, applicationProperties.getForwardReplyTimeoutMillis(), new ResultCallback<>() {
                    @Override
                    public void onSuccess(Void unused) {
                        log.debug("response success: {}:{}", login, response.getReqId());
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        log.error("response failure: {}:{} -> {}", login, response.getReqId(), errorInfo);
                    }
                });
            }

            @Override
            public void onException(Throwable e) {
                log.error("command process failure: {}:{} ", login, reqId, e);
            }
        }, applicationProperties.getForwardTimeoutMillis());

        return null;
    }
}
