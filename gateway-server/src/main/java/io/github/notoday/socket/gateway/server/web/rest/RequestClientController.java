package io.github.notoday.socket.gateway.server.web.rest;

import io.github.notoday.netty.remoting.RemotingServer;
import io.github.notoday.netty.remoting.common.RemotingSysResponseCode;
import io.github.notoday.netty.remoting.protocol.RemotingCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author no-today
 * @date 2022/07/01 23:59
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class RequestClientController {

    @Resource
    private RemotingServer remotingServer;

    /**
     * 生产环境考虑仅允许微服务内部调用并进行安全校验
     */
    @PostMapping("/services/invoke/{client}")
    public ResponseEntity<RemotingCommand> invoke(@PathVariable("client") String client, @RequestBody RemotingCommand command, @RequestParam(defaultValue = "3000") Long timeout) {
        try {
            return ResponseEntity.ok(remotingServer.invokeSync(client, command, timeout));
        } catch (Exception e) {
            return ResponseEntity.ok(RemotingCommand.failure(command.getReqId(), RemotingSysResponseCode.SYSTEM_ERROR, e.getMessage()));
        }
    }
}
