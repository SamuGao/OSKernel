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
		
		this.srcOpBtn = new JButton("��");
		this.desOpBtn = new JButton("��");
		this.okCpyBtn = new JButton("ȷ��");
		this.quitBtn = new JButton("�˳�");
		
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
		
		// ��ʼ���ؼ�
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
					JOptionPane.showMessageDialog(ApplicationFrame.this, "��ѡ��Դ�ļ�·����");
					return;
				}
				
				if(desPathStr == null || "".equals(desPathStr.trim())){
					JOptionPane.showMessageDialog(ApplicationFrame.this, "��ѡ��Ŀ��·����");
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
						JOptionPane.showMessageDialog(ApplicationFrame.this, "��ȡ�ļ��쳣��");
					}catch(IOException ioe){
						JOptionPane.showMessageDialog(ApplicationFrame.this, "��������쳣��");
					}finally{
						try {
							if(input != null){
								input.close();
							}
							
							if(output != null){
								output.close();
							}
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(ApplicationFrame.this, "�ر��ļ��쳣��");
						}
					}
					
					JOptionPane.showMessageDialog(ApplicationFrame.this, "д�ļ��ɹ���");
				}else{
					JOptionPane.showMessageDialog(ApplicationFrame.this, "Դ�ļ������ڣ�");
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
		
		
		cons.gridx = 0;                          // �ؼ�������
		cons.gridy = 0;                          // �ؼ�������
		cons.gridwidth = 1;                      // �ؼ���ռ��λ���
		cons.gridheight = 1;                     // �ؼ���ռ��λ�߶�
		cons.fill = GridBagConstraints.NONE;     // �ؼ���С����������Сʱ����䷽ʽ
		cons.ipadx = 1;                          // ������
		cons.ipady = 1;                          // ������
		cons.insets = new Insets(1, 1, 1, 1);    // ���֮��ļ��
		cons.weightx = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.weighty = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.anchor = GridBagConstraints.EAST; // ����������ڿؼ�ʱ���ؼ��İڷ�λ��
		this.add(new JLabel("Դ����·��: "), cons);
		
		cons.gridx = GridBagConstraints.RELATIVE;// �ؼ�������
		cons.gridy = 0;                          // �ؼ�������
		cons.gridwidth = 1;                      // �ؼ���ռ��λ���
		cons.gridheight = 1;                     // �ؼ���ռ��λ�߶�
		cons.fill = GridBagConstraints.NONE;     // �ؼ���С����������Сʱ����䷽ʽ
		cons.ipadx = 1;                          // ������
		cons.ipady = 1;                          // ������
		cons.insets = new Insets(1, 1, 1, 1);    // ���֮��ļ��
		cons.weightx = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.weighty = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.anchor = GridBagConstraints.CENTER; // ����������ڿؼ�ʱ���ؼ��İڷ�λ��
		this.add(this.srcPath, cons);
		
		cons.gridx = GridBagConstraints.RELATIVE;// �ؼ�������
		cons.gridy = 0;                          // �ؼ�������
		cons.gridwidth = 1;                      // �ؼ���ռ��λ���
		cons.gridheight = 1;                     // �ؼ���ռ��λ�߶�
		cons.fill = GridBagConstraints.NONE;     // �ؼ���С����������Сʱ����䷽ʽ
		cons.ipadx = 1;                          // ������
		cons.ipady = 1;                          // ������
		cons.insets = new Insets(1, 1, 1, 1);    // ���֮��ļ��
		cons.weightx = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.weighty = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.anchor = GridBagConstraints.CENTER; // ����������ڿؼ�ʱ���ؼ��İڷ�λ��
		this.add(this.srcOpBtn, cons);
		
		cons.gridx = 0;                          // �ؼ�������
		cons.gridy = 1;                          // �ؼ�������
		cons.gridwidth = 1;                      // �ؼ���ռ��λ���
		cons.gridheight = 1;                     // �ؼ���ռ��λ�߶�
		cons.fill = GridBagConstraints.NONE;     // �ؼ���С����������Сʱ����䷽ʽ
		cons.ipadx = 1;                          // ������
		cons.ipady = 1;                          // ������
		cons.insets = new Insets(1, 1, 1, 1);    // ���֮��ļ��
		cons.weightx = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.weighty = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.anchor = GridBagConstraints.EAST; // ����������ڿؼ�ʱ���ؼ��İڷ�λ��
		this.add(new JLabel("Ŀ��·��: "), cons);
		
		cons.gridx = GridBagConstraints.RELATIVE;// �ؼ�������
		cons.gridy = 1;                          // �ؼ�������
		cons.gridwidth = 1;                      // �ؼ���ռ��λ���
		cons.gridheight = 1;                     // �ؼ���ռ��λ�߶�
		cons.fill = GridBagConstraints.NONE;     // �ؼ���С����������Сʱ����䷽ʽ
		cons.ipadx = 1;                          // ������
		cons.ipady = 1;                          // ������
		cons.insets = new Insets(1, 1, 1, 1);    // ���֮��ļ��
		cons.weightx = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.weighty = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.anchor = GridBagConstraints.CENTER; // ����������ڿؼ�ʱ���ؼ��İڷ�λ��
		this.add(this.desPath, cons);
		
		cons.gridx = GridBagConstraints.RELATIVE;// �ؼ�������
		cons.gridy = 1;                          // �ؼ�������
		cons.gridwidth = 1;                      // �ؼ���ռ��λ���
		cons.gridheight = 1;                     // �ؼ���ռ��λ�߶�
		cons.fill = GridBagConstraints.NONE;     // �ؼ���С����������Сʱ����䷽ʽ
		cons.ipadx = 1;                          // ������
		cons.ipady = 1;                          // ������
		cons.insets = new Insets(1, 1, 1, 1);    // ���֮��ļ��
		cons.weightx = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.weighty = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.anchor = GridBagConstraints.CENTER; // ����������ڿؼ�ʱ���ؼ��İڷ�λ��
		this.add(this.desOpBtn, cons);
		
		cons.gridx = GridBagConstraints.RELATIVE;// �ؼ�������
		cons.gridy = 2;                          // �ؼ�������
		cons.gridwidth = 1;                      // �ؼ���ռ��λ���
		cons.gridheight = 1;                     // �ؼ���ռ��λ�߶�
		cons.fill = GridBagConstraints.NONE;     // �ؼ���С����������Сʱ����䷽ʽ
		cons.ipadx = 1;                          // ������
		cons.ipady = 1;                          // ������
		cons.insets = new Insets(1, 1, 1, 1);    // ���֮��ļ��
		cons.weightx = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.weighty = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.anchor = GridBagConstraints.CENTER; // ����������ڿؼ�ʱ���ؼ��İڷ�λ��
		this.add(new JPanel(), cons);
		
		cons.gridx = 1;// �ؼ�������
		cons.gridy = 3;                          // �ؼ�������
		cons.gridwidth = 1;                      // �ؼ���ռ��λ���
		cons.gridheight = 1;                     // �ؼ���ռ��λ�߶�
		cons.fill = GridBagConstraints.NONE;     // �ؼ���С����������Сʱ����䷽ʽ
		cons.ipadx = 1;                          // ������
		cons.ipady = 1;                          // ������
		cons.insets = new Insets(1, 1, 1, 1);    // ���֮��ļ��
		cons.weightx = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.weighty = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.anchor = GridBagConstraints.WEST; // ����������ڿؼ�ʱ���ؼ��İڷ�λ��
		this.add(this.okCpyBtn, cons);
		
		cons.gridx = 1;// �ؼ�������
		cons.gridy = 3;                          // �ؼ�������
		cons.gridwidth = 1;                      // �ؼ���ռ��λ���
		cons.gridheight = 1;                     // �ؼ���ռ��λ�߶�
		cons.fill = GridBagConstraints.NONE;     // �ؼ���С����������Сʱ����䷽ʽ
		cons.ipadx = 1;                          // ������
		cons.ipady = 1;                          // ������
		cons.insets = new Insets(1, 1, 1, 1);    // ���֮��ļ��
		cons.weightx = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.weighty = 0;                        // ���ڴ�С�ı�ʱ���ؼ��ĸı����
		cons.anchor = GridBagConstraints.EAST; // ����������ڿؼ�ʱ���ؼ��İڷ�λ��
		this.add(this.quitBtn, cons);
		
		this.pack();
	}
}
