package io.github.notoday.command.processor.examples.processor;

import io.github.notoday.netty.remoting.protocol.RemotingCommand;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQReplyListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "callServer", selectorExpression = Constant.CMD_RETURN_REQUEST_TAG, consumerGroup = "GROUP_RETURN_REQUEST")
public class ReturnRequestCommandProcessor implements RocketMQReplyListener<RemotingCommand, RemotingCommand> {

    @Override
    public RemotingCommand onMessage(RemotingCommand message) {
        return RemotingCommand.success(message.getReqId(), message.getBody(), message.getExtFields());
    }
}