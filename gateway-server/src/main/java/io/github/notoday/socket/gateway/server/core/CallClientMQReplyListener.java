package io.github.notoday.socket.gateway.server.core;

import com.alibaba.fastjson.JSON;
import io.github.notoday.netty.remoting.RemotingServer;
import io.github.notoday.netty.remoting.common.RemotingSysResponseCode;
import io.github.notoday.netty.remoting.protocol.RemotingCommand;
import io.github.notoday.socket.gateway.server.config.ApplicationProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQReplyListener;
import org.springframework.stereotype.Service;

/**
 * @author no-today
 * @date 2022/07/26 21:47
 */
@Slf4j
@Service
@AllArgsConstructor
@RocketMQMessageListener(topic = "${application.call-client-topic}", consumerGroup = "GROUP_${application.call-client-topic}", messageModel = MessageModel.BROADCASTING)
public class CallClientMQReplyListener implements RocketMQReplyListener<RemotingCommand, RemotingCommand> {

    private final ApplicationProperties applicationProperties;
    private final RemotingServer remotingServer;

    @Override
    public RemotingCommand onMessage(RemotingCommand message) {
        long ttl = Long.parseLong(message.getExtFieldsOrDefault("ttl", String.valueOf(applicationProperties.getForwardTimeoutMillis())));
        String login = message.getExtFieldsOrDefault("login", null);

        if (StringUtils.isBlank(login)) {
            return RemotingCommand.failure(message.getReqId(), RemotingSysResponseCode.SYSTEM_ERROR, "[login] can not empty");
        }

        if (!remotingServer.isConnected(login)) {
            if (applicationProperties.isClusterMode()) {
                log.debug("[Call-Client] target peer is not connected to this node, login: {}", login);
                return null;
            } else {
                return RemotingCommand.failure(message.getReqId(), RemotingSysResponseCode.REQUEST_FAILED, "[login] connection not established");
            }
        }

        try {
            return remotingServer.invokeSync(login, message, ttl);
        } catch (Exception e) {
            log.error("[Call-Client] exception, login: {}, request: {}", JSON.toJSONString(login), message, e);
            return RemotingCommand.failure(message.getReqId(), RemotingSysResponseCode.SYSTEM_ERROR, RemotingHelper.exceptionSimpleDesc(e));
        }
    }
}
