package io.github.notoday.socket.gateway.server.security;

import io.github.notoday.netty.remoting.security.Authentication;
import io.github.notoday.socket.gateway.server.config.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

/**
 * @author no-today
 * @date 2022/06/27 22:35
 */
@Slf4j
@Service
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private final Key key;
    private final JwtParser jwtParser;

    public TokenProvider(ApplicationProperties applicationProperties) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(applicationProperties.getSecurityConfig().getBase64Secret()));
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.key).build();
    }

    @PostConstruct
    public void init() {
        log.info("TEST-TOKEN: {}", generateToken(new Authentication("no-today", "", List.of("USER"))));
    }

    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getPrincipal())
                .claim(AUTHORITIES_KEY, String.join(",", authentication.getAuthorities()))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        List<String> authorities = Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(","));
        return new Authentication(claims.getSubject(), "", authorities);
    }
}
