package org.okou.lippen.network.tool.ui.select;

import javax.swing.JComboBox;

import org.okou.lippen.network.tool.listener.MessageReceivedListener;
import org.okou.lippen.network.tool.model.DataManager;
import org.okou.lippen.network.tool.net.INet;
import org.okou.lippen.network.tool.net.NetTCPClient;
import org.okou.lippen.network.tool.net.NetTCPServer;
import org.okou.lippen.network.tool.net.NetUDPServer;

@SuppressWarnings("serial")
public class NetSelect extends JComboBox<INet> {
	public NetSelect(DataManager data) {
		//���ݼ����������յ�����֮����ӵ���������
		MessageReceivedListener messageReceived = (bytes) -> {
			data.addMessage(bytes);;
		};
		// �������ͼ���
		addItem(new NetTCPServer(data, messageReceived));
		addItem(new NetTCPClient(data, messageReceived));
		addItem(new NetUDPServer(data, messageReceived));
	}
	
	@Override
	public INet getSelectedItem() {
		return (INet) super.getSelectedItem();
	}
}
