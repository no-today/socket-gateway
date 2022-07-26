## Socket Gateway

## Quick Start Demo

0. Run nacos and rocketmq

```shell
 docker-compose -f gateway-server/src/main/docker/nacos.yml up -d
 docker-compose -f gateway-server/src/main/docker/rocketmq.yml up -d
```

Wait for startup to complete

- [Nacos console](http://localhost:8848/nacos/)
- [RocketMQ console](http://localhost:8087/#/)

### Client Call Server

1. Run GatewayServerApplication
2. Run GetStringByLengthCommandProcessorTest / PingPongCommandProcessorTest

### Server Call Client

1. Run GatewayServerApplication
2. Run RequestClientControllerTest
3. [Call client](./requests.http)

## Use

- [netty-remoting](https://github.com/no-today/netty-remoting): netty remoting module