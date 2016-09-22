package org.okou.lippen.network.tool.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import org.okou.lippen.network.tool.model.DataManager;
import org.okou.lippen.network.tool.model.DataManager.DataType;
import org.okou.lippen.network.tool.net.INet;
import org.okou.lippen.network.tool.ui.field.DataTextArea;
import org.okou.lippen.network.tool.ui.field.IPV4Field;
import org.okou.lippen.network.tool.ui.field.PortField;
import org.okou.lippen.network.tool.ui.menu.CharsetCheckBoxMenuItem;
import org.okou.lippen.network.tool.ui.select.NetSelect;
import org.okou.lippen.network.tool.ui.table.ReadOnlyTable;
import org.okou.lippen.network.tool.util.NetUtil;

@SuppressWarnings("serial")
public class Window extends JFrame {
	//�����ʽ
	private static final String[][] charsets = new String[][] {
		{"ISO-8859-1", "ISO-8859-1"},
		{"UTF-8", "UTF-8"},
		{"GBK", "GBK"},
		{"GB2312", "GB2312"},
		{"GB18030", "GB18030"},
		{"Big5", "Big5"},
		{"Big5-HKSCS", "Big5-HKSCS"},
		{"UTF-16", "UTF-16"},
		{"UTF-16BE", "UTF-16BE"},
		{"UTF-16LE", "UTF-16LE"},
		{"UTF-32", "UTF-32"},
		{"UTF-32BE", "UTF-32BE"},
		{"UTF-32LE", "UTF-32LE"},
	};
	//����ip
	private static final String LOCAL_IP = NetUtil.getLocalHostName();
	
	//��������
	private DataManager data;
	//��������ѡ���
	private NetSelect networkSelect;
	//ip�Ͷ˿���ʾ���
	private JLabel ipLabel;
	private JLabel portLabel;
	//ip�Ͷ˿������
	private IPV4Field ipInput;
	private PortField portInput;
	
	//������Ϣ�б���ʾ��ʽ
	private JCheckBox readHex;
	//������Ϣ����ʾ��ʽ
	private JCheckBox writeHex;
	//��������Ϣ֮�����
	private JCheckBox writeClear;
	//ʹ���б�ѡ���������
	private JCheckBox writeToListSelect;
	
	//�󶨰�ť
	private JButton bindButton;
	//��Ϣ���Ͱ�ť
	private JButton sendButton;
	//��Ϣ�����
	private DataTextArea writeArea;
	//������Ϣ��ʾ��
	private JTable table;
	
	//Ŀ��ip�Ͷ˿������
	private IPV4Field targetIp;
	private PortField targetPort;
	
	private JPanel connectPanel;
	private JList<Object> connectList;
	private JPopupMenu popup;

	
	private ActionListener listener;

	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private Dimension min = new Dimension(590, 590);
	private Dimension max = new Dimension(710, 590);
	public Window() {
		this.setTitle("Lippen Network Tool");
		this.setLayout(new BorderLayout());
		this.setSize(min);
		initComponent();
		addListener();
		initMenu();
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public void setSize(Dimension d){
		this.setMinimumSize(d);
		super.setSize(d);
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("����(O)");
		menu.setMnemonic('O');
		JMenu m = new JMenu("��������(E)");
		ButtonGroup group = new ButtonGroup();
		for(String[] charset:charsets) {
			if(charset.length == 0) {
				m.addSeparator();
				continue;
			}
			CharsetCheckBoxMenuItem item = new CharsetCheckBoxMenuItem(charset[0], charset[1]);
			if(charset[1].equals(DEFAULT_CHARSET.name())) {
				item.setSelected(true);
			}
			item.addActionListener(listener);
			group.add(item);
			m.add(item);
		}
		menu.add(m);
		
		JMenu about = new JMenu("����");

		menuBar.add(menu);
		menuBar.add(about);

		this.setJMenuBar(menuBar);
	}

	private void initComponent() {
		//��ʼ����������
		Object[] columnNames = new Object[]{"ʱ��", "����"};
		Object[][] rowData = new Object[][]{};
		data = new DataManager(rowData, columnNames); 
		data.setCharset(DEFAULT_CHARSET);
		
		//-------------��ʼ���ؼ�-------------
		JLabel networkTypeLabel = new JLabel("��1��Э������");
		//��������ѡ���
		networkSelect = new NetSelect(data);
		
		ipLabel = new JLabel("��2������ip��ַ");
		//ip��ַ�����
		ipInput = new IPV4Field(LOCAL_IP);
		
		portLabel = new JLabel("��3�����ض˿�");
		//�˿������
		portInput = new PortField(8080);
		
		bindButton = new JButton("����");
		
		readHex = new JCheckBox("ʮ��������ʾ");
		
		writeHex = new JCheckBox("ʮ��������ʾ");
		writeClear = new JCheckBox("������������");
		writeToListSelect = new JCheckBox("����ѡ�е�����");
		
		table = new ReadOnlyTable(data);
		JScrollPane tableScroll = new JScrollPane(table);
		
		JLabel targetHost = new JLabel("Ŀ������");
		targetIp = new IPV4Field(LOCAL_IP);
		JLabel targetPortLabel = new JLabel("Ŀ��˿�");
		targetPort = new PortField(7000);
		
		//��Ϣ���Ϳ�
		writeArea = new DataTextArea(data.getWriteType(), data.getCharset());
		sendButton = new JButton("����");
		
		DefaultListModel<Object> model = new DefaultListModel<>();
		connectList = new JList<>(model);
//		connectList = new JList<>();
//		org.okou.lippen.network.tool.ui.model.ChannelListModel model = new org.okou.lippen.network.tool.ui.model.ChannelListModel(connectList);
//		connectList.setModel(model);
		BiConsumer<Object, String> consumer = (connect, action) -> {
			switch(action) {
			case "clear" :
				model.removeAllElements();
				break;
			case "add" :
				boolean has = false;
				for(int i = 0; i < model.getSize(); i++) {
					has = connect.equals(model.getElementAt(i));
					if(has) {
						break;
					}
				}
				if(!has) {
					model.addElement(connect);
				}
				break;
			case "remove" :
				model.removeElement(connect);
				break;
			}
		};
		data.setConsumer(consumer);
		
		JPanel targetPanel = new JPanel();
//		targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.X_AXIS));
		targetPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		targetPanel.add(targetHost);
		targetPanel.add(targetIp);
		targetPanel.add(targetPortLabel);
		targetPanel.add(targetPort);
		
		JPanel writePanel = new JPanel();
		writePanel.setBorder(BorderFactory.createTitledBorder("������Ϣ"));
		writePanel.setLayout(new BorderLayout());
		writePanel.add(new JScrollPane(writeArea), BorderLayout.CENTER);
		writePanel.add(sendButton, BorderLayout.EAST);
		
		JPanel readPanel = new JPanel();
		readPanel.setBorder(BorderFactory.createTitledBorder("������Ϣ"));
		readPanel.setLayout(new BoxLayout(readPanel, BoxLayout.Y_AXIS));
		readPanel.add(tableScroll);
		readPanel.add(targetPanel);
		
		//�����������
		JPanel netSettingPanel = new JPanel();
		netSettingPanel.setBorder(BorderFactory.createTitledBorder("��������"));
		netSettingPanel.setLayout(new GridLayout(7, 1));
		netSettingPanel.add(networkTypeLabel);
		netSettingPanel.add(networkSelect);
		netSettingPanel.add(ipLabel);
		netSettingPanel.add(ipInput);
		netSettingPanel.add(portLabel);
		netSettingPanel.add(portInput);
		netSettingPanel.add(bindButton);
		
		JPanel readSettingPanel = new JPanel();
		readSettingPanel.setBorder(BorderFactory.createTitledBorder("��������"));
		readSettingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		readSettingPanel.add(readHex);
		
		
		JPanel leftUpPanel = new JPanel();
		leftUpPanel.setLayout(new BoxLayout(leftUpPanel, BoxLayout.Y_AXIS));
		leftUpPanel.add(netSettingPanel);
		leftUpPanel.add(readSettingPanel);

		JPanel writeSettingPanel = new JPanel();
		writeSettingPanel.setBorder(BorderFactory.createTitledBorder("��������"));
		writeSettingPanel.setLayout(new BoxLayout(writeSettingPanel, BoxLayout.Y_AXIS));
		writeSettingPanel.add(writeHex);
		writeSettingPanel.add(writeClear);
		writeSettingPanel.add(writeToListSelect);
		
		//�������
		JPanel leftPanel = new JPanel();
		//���ò���ΪBorderLayout�������ڵ��ڴ��ڴ�Сʱ���������䣬�ײ����䣬�м�Ϊ�հױ仯
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(leftUpPanel, BorderLayout.NORTH);
		leftPanel.add(writeSettingPanel, BorderLayout.SOUTH);
		//�ұ����
		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, readPanel, writePanel);
		//��ʾ�ָ������3/4λ��
		splitPanel.setDividerLocation((this.getHeight() >> 2) * 3);
		//ȡ���߿�
		splitPanel.setBorder(null);
		
		this.add(leftPanel, BorderLayout.WEST);
		this.add(splitPanel, BorderLayout.CENTER);
		
		
		connectPanel = new JPanel();
		connectPanel.setBorder(BorderFactory.createTitledBorder("�����б�"));
		connectPanel.setLayout(new GridLayout(1, 1));
		
		popup = new JPopupMenu();
		JMenuItem removeItem = new JMenuItem("ɾ��");
		JMenuItem clearItem = new JMenuItem("���");
		JMenuItem testItem = new JMenuItem("����");
		testItem.addActionListener((e) -> {
			connectList.repaint();
//			connectList.setSelectedIndex(0);
			Object o = connectList.getSelectedValue();
			System.out.println(o);
//			System.out.println("num:" + connectList.getModel().getSize());
		});
		removeItem.addActionListener((e) -> {
			Object obj = connectList.getSelectedValue();
			model.removeElement(obj);
//			data.removeConnect(obj);
			
		});
		clearItem.addActionListener((e) -> {
			data.clear();
		});
		popup.add(removeItem);
		popup.add(clearItem);
		popup.addSeparator();
		popup.add(testItem);
		JScrollPane listScroll = new JScrollPane(connectList);
		Dimension old = listScroll.getPreferredSize();
		listScroll.setPreferredSize(new Dimension(108, old.height));
		connectPanel.add(listScroll);
	}
	
	public void addListener(){
		//����¼������Ҽ�����ѡ����굱ǰ��
		MouseListener mouseListener = new MouseAdapter() {  
		    @Override  
		    public void mouseClicked(MouseEvent e) {  
		        int index = connectList.locationToIndex(e.getPoint());  
		        connectList.setSelectedIndex(index);  
		    }  
		};
		connectList.addMouseListener(mouseListener);
		
		
		Supplier<InetSocketAddress> addressSource = () -> {
			String udpIP = targetIp.getText();
			if(udpIP == null) {
				JOptionPane.showMessageDialog(null, "UDP�ͻ�����Ϣδ����", "UDP", JOptionPane.OK_OPTION);
				return null;
			}
			int udpPort = this.targetPort.getNumber();
			return new InetSocketAddress(udpIP, udpPort);
		};
		data.setAddressSource(addressSource);
		writeToListSelect.addActionListener((e) -> {
			if(writeToListSelect.isSelected()) {
				data.setAddressSource(null);
			} else {
				data.setAddressSource(addressSource);
			}
		});
		data.setChannelSource(() -> {
			return connectList.getSelectedValuesList();
		});
		sendButton.addActionListener((e) -> {
			String str = writeArea.getText();
			INet n = networkSelect.getSelectedItem();
			if(n == null) {
				return;
			}
			n.sendMsg(str);
			if(writeClear.isSelected()) {
				writeArea.setText("");
			}
		});
		networkSelect.addActionListener((e) -> {
			INet option = networkSelect.getItemAt(networkSelect.getSelectedIndex());
			if (!option.isServer()) {
				ipLabel.setText("��2��������IP��ַ");
				portLabel.setText("��3���������˿�");
			} else {
				ipLabel.setText("��2������ip��ַ");
				portLabel.setText("��3�����ض˿�");
			}
		});
		bindButton.addActionListener((e) -> {
			String ipStr = ipInput.getText();
			int port = portInput.getNumber();
			INet n = networkSelect.getSelectedItem();
			if(n == null) {
				return;
			}
			Dimension d = null;
			if(bindButton.getText().equals("����")) {
				if(n.start(ipStr, port)){
					if(n.isServer()) {
						d = max;
						this.add(connectPanel, BorderLayout.EAST);
						connectPanel.repaint();
						if(n.canRemoveClient()) {
							this.connectList.setComponentPopupMenu(popup);
						}
					}
					bindButton.setText("�Ͽ�");
					networkSelect.setEnabled(false);
					ipInput.setEnabled(false);
					portInput.setEnabled(false);
				}
			} else {
				if(n.stop()) {
					if(n.isServer()) {
						if(n.canRemoveClient()) {
							this.connectList.setComponentPopupMenu(null);
						}
						d = min;
						this.remove(connectPanel);
					}
					bindButton.setText("����");
					networkSelect.setEnabled(true);
					ipInput.setEnabled(true);
					portInput.setEnabled(true);
					data.clear();
				}
			}
			if(d != null) {
				this.setSize(d);
			}
		});
		readHex.addActionListener((e) -> {
			if(readHex.isSelected()) {
				data.setReadType(DataType.HEX);
			} else {
				data.setReadType(DataType.STRING);
			}
			table.repaint();
		});
		writeHex.addActionListener((e) -> {
			if(writeHex.isSelected()) {
				data.setWriteType(DataType.HEX);
			} else {
				data.setWriteType(DataType.STRING);
			}
			writeArea.setType(data.getWriteType());
		});
		listener = (e) -> {
			Object obj = e.getSource();
			if(obj instanceof CharsetCheckBoxMenuItem) {
				CharsetCheckBoxMenuItem i = (CharsetCheckBoxMenuItem) obj;
				Charset c = i.getCharset();
				data.setCharset(c);
				table.repaint();
				writeArea.setCharset(c);
			}
		};
	}
	
	
}
