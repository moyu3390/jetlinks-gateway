package org.jetlinks.gateway.vertx.tcp;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.Getter;
import lombok.Setter;
import org.jetlinks.gateway.session.DeviceSession;
import org.jetlinks.protocol.ProtocolSupport;
import org.jetlinks.protocol.device.DeviceOperation;
import org.jetlinks.protocol.message.codec.EncodedMessage;
import org.jetlinks.protocol.message.codec.Transport;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class TcpDeviceSession implements DeviceSession {
    private String id;

    private String deviceId;

    private volatile long lastPingTime = System.currentTimeMillis();

    private long connectTime = System.currentTimeMillis();

    private DeviceOperation operation;

    private ProtocolSupport protocolSupport;

    private NetSocket socket;

    private long keepAliveInterval = TimeUnit.MINUTES.toMillis(10);

    @Override
    public long lastPingTime() {
        return lastPingTime;
    }

    @Override
    public long connectTime() {
        return connectTime;
    }

    @Override
    public void send(EncodedMessage encodedMessage) {
        socket.write(Buffer.buffer(encodedMessage.getByteBuf()));
    }

    @Override
    public Transport getTransport() {
        return Transport.TCP;
    }

    @Override
    public void close() {
        socket.close();
    }

    @Override
    public void ping() {
        lastPingTime = System.currentTimeMillis();
    }

    @Override
    public boolean isAlive() {
        //10分钟未发送ping
        return System.currentTimeMillis() - lastPingTime > keepAliveInterval;
    }

    @Override
    public String toString() {
        return deviceId + "[" + socket.remoteAddress() + "]";
    }
}