package org.okou.lippen.network.tool.main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.okou.lippen.network.tool.ui.Window;

public class NetWorkTool {
	
	public static void main(String[] args) {
		//�������ĵ�ʱ�򣬲�����ʾ�����������Ҫ
//		System.setProperty("java.awt.im.style","on-the-spot");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new Window();
	}
}
