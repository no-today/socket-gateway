package io.github.notoday.command.processor.examples.processor;

import io.github.notoday.netty.remoting.config.NettyClientConfig;
import io.github.notoday.netty.remoting.core.ErrorInfo;
import io.github.notoday.netty.remoting.core.NettyRemotingClient;
import io.github.notoday.netty.remoting.core.ResultCallback;
import io.github.notoday.netty.remoting.exception.RemotingException;
import io.github.notoday.netty.remoting.protocol.RemotingCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author no-today
 * @date 2022/07/14 17:14
 */
@Slf4j
@SpringBootTest
class ReturnRequestCommandProcessorTest {

    String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuby10b2RheSIsImF1dGgiOiJVU0VSIn0.NTleAm1L6H07JoElmUjWWG3h79drYOvVHMOy0jTAckKkwBbg98ViaqAAQD-fY8FXJWAXoDTm10BoXL8tRX6_qQ";
    int cmd = Constant.CMD_RETURN_REQUEST;

    @Test
    void callServer() throws Exception {
        NettyRemotingClient client = new NettyRemotingClient(new NettyClientConfig(), null);

        client.login(token, "no-today", new ResultCallback<>() {
            @Override
            public void onSuccess(RemotingCommand command) {
                log.info(command.toString());
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                log.error(errorInfo.toString());
                fail();
            }
        });

        AtomicInteger successCounter = new AtomicInteger();
        AtomicInteger errorCounter = new AtomicInteger();
        Runnable runnable = () -> {
            while (true) {
                try {
                    int value = RandomUtils.nextInt(1000, 99999);

                    RemotingCommand response = client.invokeSync(RemotingCommand.request(cmd, ByteBuffer.allocate(4).putInt(value).array(), Map.of("length", String.valueOf(value))), 3000);
                    assertTrue(response.success());
                    assertFalse(response.getExtFields().isEmpty());
                    assertEquals(value, ByteBuffer.wrap(response.getBody()).getInt());

                    successCounter.incrementAndGet();
                } catch (RemotingException | InterruptedException ignored) {
                    errorCounter.incrementAndGet();
                }
            }
        };

        int threads = 2;
        Stream.iterate(0, i -> i + 1)
                .limit(threads).map(e -> new Thread(runnable))
                .forEach(Thread::start);

        int minutes = 1;
        TimeUnit.MINUTES.sleep(minutes);
        client.shutdown();
        log.info("UnitTest: {} minutes {}/{}", minutes, errorCounter.get(), successCounter.get());
    }
}