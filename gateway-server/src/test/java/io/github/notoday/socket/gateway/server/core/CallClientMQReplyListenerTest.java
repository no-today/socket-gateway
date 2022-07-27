package io.github.notoday.socket.gateway.server.core;

import io.github.notoday.netty.remoting.protocol.RemotingCommand;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author no-today
 * @date 2022/07/27 11:04
 */
@SpringBootTest
class CallClientMQReplyListenerTest {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void test() {
        RemotingCommand request = RemotingCommand.request(1005, "hello,world".getBytes(), Map.of("login", "no-today"));
        RemotingCommand response = rocketMQTemplate.sendAndReceive("callClient", request, RemotingCommand.class, 100000);

        System.out.println(response);
        assertArrayEquals(request.getBody(), response.getBody());
    }
}