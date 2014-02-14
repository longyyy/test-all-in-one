package com.taobao.teaey.lostrpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

@ChannelHandler.Sharable
public class TightLoggingHandler extends ChannelDuplexHandler {
    private final static Logger logger = LoggerFactory.getLogger(TightLoggingHandler.class);

    private static final char[] BYTE2CHAR = new char[256];

    static {
        // Generate the lookup table for byte-to-char conversion
        for (int i = 0; i < BYTE2CHAR.length; i++) {
            if (i <= 0x1f || i >= 0x7f) {
                BYTE2CHAR[i] = i == '\n' ? '\n' : '.'; // 1 is newline hold for log
                // view replacing.
            } else {
                BYTE2CHAR[i] = (char) i;
            }
        }
    }

    protected String format(ChannelHandlerContext ctx, String message) {
        String chStr = ctx.channel().toString();
        return chStr + ' ' + message;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        /* 提前判断日志级别, 避免过多计算, 下同 */
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "REGISTERED"));
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "UNREGISTERED"));
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "ACTIVE"));
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "INACTIVE"));
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "EXCEPTION: " + cause), cause);
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "USER_EVENT: " + evt));
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "BIND(" + localAddress + ')'));
        }
        super.bind(ctx, localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
                        ChannelPromise promise) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "CONNECT(" + remoteAddress + ", " + localAddress + ')'));
        }
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "DISCONNECT()"));
        }
        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "CLOSE()"));
        }
        super.close(ctx, promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "DEREGISTER()"));
        }
        super.deregister(ctx, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (logger.isDebugEnabled()) {
            logMessage(ctx, "RECEIVED", msg);
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (logger.isDebugEnabled()) {
            logMessage(ctx, "WRITE", msg);
        }
        ctx.write(msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(format(ctx, "FLUSH"));
        }
        ctx.flush();
    }

    private void logMessage(ChannelHandlerContext ctx, String eventName, Object msg) {
        logger.debug(format(ctx, formatMessage(eventName, msg)));
    }

    protected String formatMessage(String eventName, Object msg) {
        if (msg instanceof ByteBuf) {
            return formatByteBuf(eventName, (ByteBuf) msg);
        } else if (msg instanceof ByteBufHolder) {
            return formatByteBufHolder(eventName, (ByteBufHolder) msg);
        } else {
            return formatNonByteBuf(eventName, msg);
        }
    }

    /**
     * Returns a String which contains all details to log the {@link io.netty.buffer.ByteBuf}
     */
    protected String formatByteBuf(String eventName, ByteBuf buf) {
        int length = buf.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder dump = new StringBuilder(rows * 80 + eventName.length() + 16);

        dump.append(eventName).append('(').append(length).append('B').append(')');

        dump.append('\n');

        final int startIndex = buf.readerIndex();
        final int endIndex = buf.writerIndex();

        for (int i = startIndex; i < endIndex; i++) {
            dump.append(BYTE2CHAR[buf.getUnsignedByte(i)]);
        }
        return dump.toString();
    }

    /**
     * Returns a String which contains all details to log the {@link Object}
     */
    protected String formatNonByteBuf(String eventName, Object msg) {
        return eventName + ":\n" + msg;
    }

    /**
     * Returns a String which contains all details to log the
     * {@link io.netty.buffer.ByteBufHolder}.
     * <p/>
     * By default this method just delegates to
     * {@link #formatByteBuf(String, io.netty.buffer.ByteBuf)}, using the content of the
     * {@link io.netty.buffer.ByteBufHolder}. Sub-classes may override this.
     */
    protected String formatByteBufHolder(String eventName, ByteBufHolder msg) {
        return formatByteBuf(eventName, msg.content());
    }

}
