package com.samugao.imgwriter;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class MainApplication {
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				ApplicationFrame frame = new ApplicationFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}

class ApplicationFrame extends JFrame{
	private static final long serialVersionUID = 7807506733757053445L;
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 300;
	
	private JButton srcOpBtn = null;
	private JButton desOpBtn = null;
	private JButton okCpyBtn = null;
	private JButton quitBtn = null;
	
	private JTextField srcPath = null;
	private JTextField desPath = null;
	private JFileChooser chooser = new JFileChooser();
	
	private Preferences root = Preferences.userNodeForPackage(this.getClass());
	public ApplicationFrame(){
		this.setTitle("ImgWriter");
		this.setResizable(false);
//		this.setLocationByPlatform(true);
		
		this.srcOpBtn = new JButton("打开");
		this.desOpBtn = new JButton("打开");
		this.okCpyBtn = new JButton("确定");
		this.quitBtn = new JButton("退出");
		
		String preSrcPath = root.get("SRCPATH", "");
		String preDesPath = root.get("DESPATH", "");
		this.srcPath = new JTextField(20);
		this.srcPath.setEditable(false);
		this.srcPath.setText(preSrcPath);
		this.desPath = new JTextField(20);
		this.desPath.setEditable(false);
		this.desPath.setText(preDesPath);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				super.windowClosing(e);
				
				root.put("SRCPATH", srcPath.getText().trim());
				root.put("DESPATH", desPath.getText().trim());
			}
		});
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		this.setBounds((screenSize.width-DEFAULT_WIDTH)/2, (screenSize.height-DEFAULT_HEIGHT)/2, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		// 初始化控件
		initCtrl();
	}
	
	private void initCtrl(){
		this.srcOpBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String srcPathStr = srcPath.getText().trim();
				if(srcPathStr != null && !"".equals(srcPathStr)){
					chooser.setCurrentDirectory(new File(srcPathStr));
				}
				int result = chooser.showOpenDialog(ApplicationFrame.this);
				if(result == JFileChooser.APPROVE_OPTION){
					String filePath = chooser.getSelectedFile().getPath();
					srcPath.setText(filePath);
				}
			}
			
		});
		
		this.desOpBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String desPathStr = desPath.getText().trim();
				if(desPathStr != null && !"".equals(desPathStr)){
					chooser.setCurrentDirectory(new File(desPathStr));
				}
				int result = chooser.showSaveDialog(ApplicationFrame.this);
				if(result == JFileChooser.APPROVE_OPTION){
					String filePath = chooser.getSelectedFile().getPath();
					desPath.setText(filePath);
				}
			}
			
		});
		
		this.okCpyBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String srcPathStr = srcPath.getText().trim();
				String desPathStr = desPath.getText().trim();
				if(srcPathStr == null || "".equals(srcPathStr.trim())){
					JOptionPane.showMessageDialog(ApplicationFrame.this, "请选择源文件路径！");
					return;
				}
				
				if(desPathStr == null || "".equals(desPathStr.trim())){
					JOptionPane.showMessageDialog(ApplicationFrame.this, "请选择目标路径！");
					return;
				}
				
				File srcFile = new File(srcPathStr);
				if(srcFile.exists()){
					File desFile = new File(desPathStr);
					
					DataInputStream input = null;
					DataOutputStream output = null;
					try{
						if(!desFile.exists()){
							desFile.createNewFile();
						}
						input = new DataInputStream(new FileInputStream(srcFile));
						output = new DataOutputStream(new FileOutputStream(desFile));
						
						int imgLen = 1474560;
						byte[] data = new byte[imgLen];
						for(int i = 0; i < imgLen; ++i){
							data[i] = 0x00;
						}
						
						input.read(data, 0, (int)srcFile.length());
						output.write(data, 0, imgLen);
						
						if(input != null){
							input.close();
						}
						if(output != null){
							output.close();
						}
					}catch(FileNotFoundException fe){
						JOptionPane.showMessageDialog(ApplicationFrame.this, "读取文件异常！");
					}catch(IOException ioe){
						JOptionPane.showMessageDialog(ApplicationFrame.this, "输入输出异常！");
					}finally{
						try {
							if(input != null){
								input.close();
							}
							
							if(output != null){
								output.close();
							}
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(ApplicationFrame.this, "关闭文件异常！");
						}
					}
					
					JOptionPane.showMessageDialog(ApplicationFrame.this, "写文件成功！");
				}else{
					JOptionPane.showMessageDialog(ApplicationFrame.this, "源文件不存在！");
				}
				

			}
			
		});
		
		this.quitBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				root.put("SRCPATH", srcPath.getText().trim());
				root.put("DESPATH", desPath.getText().trim());
				System.exit(0);
			}
			
		});
		
		GridBagLayout grpLayout = new GridBagLayout();
		GridBagConstraints cons = new GridBagConstraints();
		this.setLayout(grpLayout);
		
		
		cons.gridx = 0;                          // 控件所在列
		cons.gridy = 0;                          // 控件所在行
		cons.gridwidth = 1;                      // 控件所占单位宽度
		cons.gridheight = 1;                     // 控件所占单位高度
		cons.fill = GridBagConstraints.NONE;     // 控件大小比所在区域小时的填充方式
		cons.ipadx = 1;                          // 组件间距
		cons.ipady = 1;                          // 组件间距
		cons.insets = new Insets(1, 1, 1, 1);    // 组件之间的间距
		cons.weightx = 0;                        // 窗口大小改变时，控件的改变比例
		cons.weighty = 0;                        // 窗口大小改变时，控件的改变比例
		cons.anchor = GridBagConstraints.EAST; // 所在区域大于控件时，控件的摆放位置
		this.add(new JLabel("源程序路径: "), cons);
		
		cons.gridx = GridBagConstraints.RELATIVE;// 控件所在列
		cons.gridy = 0;                          // 控件所在行
		cons.gridwidth = 1;                      // 控件所占单位宽度
		cons.gridheight = 1;                     // 控件所占单位高度
		cons.fill = GridBagConstraints.NONE;     // 控件大小比所在区域小时的填充方式
		cons.ipadx = 1;                          // 组件间距
		cons.ipady = 1;                          // 组件间距
		cons.insets = new Insets(1, 1, 1, 1);    // 组件之间的间距
		cons.weightx = 0;                        // 窗口大小改变时，控件的改变比例
		cons.weighty = 0;                        // 窗口大小改变时，控件的改变比例
		cons.anchor = GridBagConstraints.CENTER; // 所在区域大于控件时，控件的摆放位置
		this.add(this.srcPath, cons);
		
		cons.gridx = GridBagConstraints.RELATIVE;// 控件所在列
		cons.gridy = 0;                          // 控件所在行
		cons.gridwidth = 1;                      // 控件所占单位宽度
		cons.gridheight = 1;                     // 控件所占单位高度
		cons.fill = GridBagConstraints.NONE;     // 控件大小比所在区域小时的填充方式
		cons.ipadx = 1;                          // 组件间距
		cons.ipady = 1;                          // 组件间距
		cons.insets = new Insets(1, 1, 1, 1);    // 组件之间的间距
		cons.weightx = 0;                        // 窗口大小改变时，控件的改变比例
		cons.weighty = 0;                        // 窗口大小改变时，控件的改变比例
		cons.anchor = GridBagConstraints.CENTER; // 所在区域大于控件时，控件的摆放位置
		this.add(this.srcOpBtn, cons);
		
		cons.gridx = 0;                          // 控件所在列
		cons.gridy = 1;                          // 控件所在行
		cons.gridwidth = 1;                      // 控件所占单位宽度
		cons.gridheight = 1;                     // 控件所占单位高度
		cons.fill = GridBagConstraints.NONE;     // 控件大小比所在区域小时的填充方式
		cons.ipadx = 1;                          // 组件间距
		cons.ipady = 1;                          // 组件间距
		cons.insets = new Insets(1, 1, 1, 1);    // 组件之间的间距
		cons.weightx = 0;                        // 窗口大小改变时，控件的改变比例
		cons.weighty = 0;                        // 窗口大小改变时，控件的改变比例
		cons.anchor = GridBagConstraints.EAST; // 所在区域大于控件时，控件的摆放位置
		this.add(new JLabel("目标路径: "), cons);
		
		cons.gridx = GridBagConstraints.RELATIVE;// 控件所在列
		cons.gridy = 1;                          // 控件所在行
		cons.gridwidth = 1;                      // 控件所占单位宽度
		cons.gridheight = 1;                     // 控件所占单位高度
		cons.fill = GridBagConstraints.NONE;     // 控件大小比所在区域小时的填充方式
		cons.ipadx = 1;                          // 组件间距
		cons.ipady = 1;                          // 组件间距
		cons.insets = new Insets(1, 1, 1, 1);    // 组件之间的间距
		cons.weightx = 0;                        // 窗口大小改变时，控件的改变比例
		cons.weighty = 0;                        // 窗口大小改变时，控件的改变比例
		cons.anchor = GridBagConstraints.CENTER; // 所在区域大于控件时，控件的摆放位置
		this.add(this.desPath, cons);
		
		cons.gridx = GridBagConstraints.RELATIVE;// 控件所在列
		cons.gridy = 1;                          // 控件所在行
		cons.gridwidth = 1;                      // 控件所占单位宽度
		cons.gridheight = 1;                     // 控件所占单位高度
		cons.fill = GridBagConstraints.NONE;     // 控件大小比所在区域小时的填充方式
		cons.ipadx = 1;                          // 组件间距
		cons.ipady = 1;                          // 组件间距
		cons.insets = new Insets(1, 1, 1, 1);    // 组件之间的间距
		cons.weightx = 0;                        // 窗口大小改变时，控件的改变比例
		cons.weighty = 0;                        // 窗口大小改变时，控件的改变比例
		cons.anchor = GridBagConstraints.CENTER; // 所在区域大于控件时，控件的摆放位置
		this.add(this.desOpBtn, cons);
		
		cons.gridx = GridBagConstraints.RELATIVE;// 控件所在列
		cons.gridy = 2;                          // 控件所在行
		cons.gridwidth = 1;                      // 控件所占单位宽度
		cons.gridheight = 1;                     // 控件所占单位高度
		cons.fill = GridBagConstraints.NONE;     // 控件大小比所在区域小时的填充方式
		cons.ipadx = 1;                          // 组件间距
		cons.ipady = 1;                          // 组件间距
		cons.insets = new Insets(1, 1, 1, 1);    // 组件之间的间距
		cons.weightx = 0;                        // 窗口大小改变时，控件的改变比例
		cons.weighty = 0;                        // 窗口大小改变时，控件的改变比例
		cons.anchor = GridBagConstraints.CENTER; // 所在区域大于控件时，控件的摆放位置
		this.add(new JPanel(), cons);
		
		cons.gridx = 1;// 控件所在列
		cons.gridy = 3;                          // 控件所在行
		cons.gridwidth = 1;                      // 控件所占单位宽度
		cons.gridheight = 1;                     // 控件所占单位高度
		cons.fill = GridBagConstraints.NONE;     // 控件大小比所在区域小时的填充方式
		cons.ipadx = 1;                          // 组件间距
		cons.ipady = 1;                          // 组件间距
		cons.insets = new Insets(1, 1, 1, 1);    // 组件之间的间距
		cons.weightx = 0;                        // 窗口大小改变时，控件的改变比例
		cons.weighty = 0;                        // 窗口大小改变时，控件的改变比例
		cons.anchor = GridBagConstraints.WEST; // 所在区域大于控件时，控件的摆放位置
		this.add(this.okCpyBtn, cons);
		
		cons.gridx = 1;// 控件所在列
		cons.gridy = 3;                          // 控件所在行
		cons.gridwidth = 1;                      // 控件所占单位宽度
		cons.gridheight = 1;                     // 控件所占单位高度
		cons.fill = GridBagConstraints.NONE;     // 控件大小比所在区域小时的填充方式
		cons.ipadx = 1;                          // 组件间距
		cons.ipady = 1;                          // 组件间距
		cons.insets = new Insets(1, 1, 1, 1);    // 组件之间的间距
		cons.weightx = 0;                        // 窗口大小改变时，控件的改变比例
		cons.weighty = 0;                        // 窗口大小改变时，控件的改变比例
		cons.anchor = GridBagConstraints.EAST; // 所在区域大于控件时，控件的摆放位置
		this.add(this.quitBtn, cons);
		
		this.pack();
	}
}
