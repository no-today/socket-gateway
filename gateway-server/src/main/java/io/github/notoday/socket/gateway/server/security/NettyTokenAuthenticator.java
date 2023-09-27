package io.github.notoday.socket.gateway.server.security;

import io.github.notoday.netty.remoting.exception.AuthenticationException;
import io.github.notoday.netty.remoting.security.Authentication;
import io.github.notoday.netty.remoting.security.AuthenticationToken;
import io.github.notoday.netty.remoting.security.Authenticator;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

/**
 * @author no-today
 * @date 2022/06/27 22:18
 */
@Service
public class NettyTokenAuthenticator implements Authenticator {

    private final TokenProvider tokenProvider;

    public NettyTokenAuthenticator(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Authentication authenticate(Channel channel, AuthenticationToken authenticationToken) throws AuthenticationException {
        try {
            return tokenProvider.getAuthentication(authenticationToken.getToken());
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed", e);
        }
    }
}
