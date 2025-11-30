package org.n1es.gui;


import org.json.JSONObject;
import org.n1es.commons.ConfigConstant;
import org.n1es.dnslog.DNSLogHandler;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingConstants.CENTER;

public class CausewayGui extends JFrame {

	// 1. 组件声明
	private JTextField textField;

	private JTextField textField1;

	private JTextArea resultTextArea;

	private JButton button;

	public CausewayGui() {
		// 初始化组件
		initComponents();

		// 添加事件监听器
		addEventListener();
	}

	private void initComponents() {
		JFrame frame = new JFrame();
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setTitle("Causeway GUI");


		JPanel mainPanel   = new JPanel(new BorderLayout());
		JPanel topPanel    = new JPanel(new GridBagLayout());
		JPanel bottomPanel = new JPanel();
		JLabel urlLabel    = new JLabel("请求地址：");
		button = new JButton("确认");
		JLabel cookieLabel = new JLabel("Cookie：");
		textField = new JTextField(20);
		textField1 = new JTextField(20);

		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(bottomPanel, BorderLayout.CENTER);

		frame.setContentPane(mainPanel);
		frame.setLocationRelativeTo(null);

		GridBagConstraints gbc = new GridBagConstraints();

		// 设置panel整体布局在最左侧
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.gridx = 0;
		gbc.gridy = 0;
		topPanel.add(urlLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		topPanel.add(textField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		topPanel.add(cookieLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		topPanel.add(textField1, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		topPanel.add(button, gbc);

		bottomPanel.add(new JLabel("结果：", CENTER), BorderLayout.CENTER);

		resultTextArea = new JTextArea();
		resultTextArea.setLineWrap(true);
		resultTextArea.setWrapStyleWord(true);
		resultTextArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(resultTextArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(400, 300));

		bottomPanel.add(scrollPane, BorderLayout.CENTER);

		frame.setVisible(true);
	}

	private void addEventListener() {
		button.addActionListener(e -> {
			JSONObject result = DNSLogHandler.dnslogOperator.apply(new JSONObject() {{
				put("dnslogURL", ConfigConstant.DEFAULT_DNSLOG);
				put("targetURL", textField.getText());
				put("Cookie", textField1.getText());
			}});

			resultTextArea.setText(result.toString());
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(CausewayGui::new);
	}
}
