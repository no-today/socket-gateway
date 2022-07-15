package io.github.notoday.socket.gateway.server;

import io.github.notoday.netty.remoting.config.NettyClientConfig;
import io.github.notoday.netty.remoting.core.ErrorInfo;
import io.github.notoday.netty.remoting.core.NettyRemotingClient;
import io.github.notoday.netty.remoting.core.ResultCallback;
import io.github.notoday.netty.remoting.protocol.RemotingCommand;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class GatewayServerApplicationTests {

    String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuby10b2RheSIsImF1dGgiOiJVU0VSIn0.NTleAm1L6H07JoElmUjWWG3h79drYOvVHMOy0jTAckKkwBbg98ViaqAAQD-fY8FXJWAXoDTm10BoXL8tRX6_qQ";

    @Test
    void login() throws Exception {
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
    }
}
