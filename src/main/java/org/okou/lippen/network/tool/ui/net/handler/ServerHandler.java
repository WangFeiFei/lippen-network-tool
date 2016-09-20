package org.okou.lippen.network.tool.ui.net.handler;

import javax.swing.JOptionPane;

import org.okou.lippen.network.tool.listener.MessageReceivedListener;
import org.okou.lippen.network.tool.model.DataManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf>{
	private DataManager data;
	private MessageReceivedListener listener;
	public ServerHandler(DataManager data, MessageReceivedListener listener){
		this.data = data;
		this.listener = listener;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		data.addConnect(ctx.channel());
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		byte[] data = new byte[msg.readableBytes()];
		msg.readBytes(data);
		listener.messageReceived(data);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		JOptionPane.showMessageDialog(data.getComponent(), "�������쳣", "�������쳣", JOptionPane.OK_OPTION);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		data.removeConnect(ctx.channel());
	}
}
