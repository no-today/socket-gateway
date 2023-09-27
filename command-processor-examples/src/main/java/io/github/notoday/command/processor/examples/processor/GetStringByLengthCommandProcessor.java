package io.github.notoday.command.processor.examples.processor;

import io.github.notoday.netty.remoting.common.RemotingSystemCode;
import io.github.notoday.netty.remoting.protocol.RemotingCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQReplyListener;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Slf4j
@Service
@RocketMQMessageListener(topic = "callServer", selectorExpression = Constant.CMD_GET_STRING_BY_LENGTH_TAG, consumerGroup = "GROUP_GET_STRING_BY_LENGTH")
public class GetStringByLengthCommandProcessor implements RocketMQReplyListener<RemotingCommand, RemotingCommand> {

    @Override
    public RemotingCommand onMessage(RemotingCommand message) {
        try {
            return RemotingCommand.success(message.getReqId(), RandomStringUtils.random(ByteBuffer.wrap(message.getBody()).getInt()).getBytes(), message.getExtFields());
        } catch (Exception e) {
            return RemotingCommand.failure(message.getReqId(), RemotingSystemCode.SYSTEM_ERROR, RemotingHelper.exceptionSimpleDesc(e));
        }
    }
}