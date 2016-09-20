package org.okou.lippen.network.tool.ui.net;

import javax.swing.JOptionPane;

import org.okou.lippen.network.tool.listener.MessageReceivedListener;
import org.okou.lippen.network.tool.model.DataManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NetTcpClient extends AbstractNetTcp {
	private Bootstrap client;
	public NetTcpClient(DataManager data, MessageReceivedListener listener){
		super(data, listener);
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		client = new Bootstrap();
		client.group(bossGroup);
		client.channel(NioSocketChannel.class);
		client.handler(initializer);
	}
	@Override
	public boolean start(String ip, int port) {
		try {
			ChannelFuture f = client.connect(ip, port).sync();
			channel = f.channel();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "����ʧ��", "����ʧ��", JOptionPane.OK_OPTION);
			return false;
		}
		return true;
	}

	@Override
	public void sendMsg(String text) {
		if(channel == null) {
			JOptionPane.showMessageDialog(null, "δ��������", "�����쳣", JOptionPane.OK_OPTION);
			return;
		}
		byte[] bytes = msg2Bytes(text);
		if(bytes != null) {
			channel.writeAndFlush(bytes);
		}
	}
}
