package yan.maze;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import yan.mazegame.logic.BlockCreator;
import yan.mazegame.logic.MazeCreator;

/**
 * ����������
 * 
 * @author yan
 * 
 */
public class UI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6014389155626784508L;
	/* ���ڿؼ� */
	private JLabel statsText;// ״̬��
	// ״̬����ʾ��Ϣ
	private String format = "��:%d, ��:%d, ��Ҫ����:%d(%d��).";

	/* �Թ����� */
	
	// �Թ�������
	private int col; // ��
	private int row; // ��
	private long seed;// ����
	private boolean isRand;// �Ƿ����
	private MazeCreator mc;

	// ����������
	private int roadSize;// ��·���
	private BlockCreator bc;
	
	// ��ͼ��
	private int pixel;// ÿ������Ŀ��
	private Canvas canvas;

	/**
	 * ���캯������ʼ��������
	 */
	public UI() {
		// �����Թ�������
		col = 12;
		row = 9;
		seed = md5("yan");
		isRand = false;
		mc = new MazeCreator(col, row, seed, isRand);
		
		// ��������������
		roadSize = 2;
		bc = new BlockCreator(roadSize);
		
		// ��������
		pixel = 12;
		canvas = new Canvas(pixel);

		this.setTitle("YAN���Թ�������");
		this.setSize(1024, 768);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getJMenuBar());// ���ò˵�
		this.setContentPane(getContentPanel());// �����沼��
		
		// �����Թ�
		this.updateMaze();

		// ��ʾ����
		this.setVisible(true);

	}

	/**
	 * �����沼��
	 * 
	 * @return
	 */
	private JPanel getContentPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// ����
		JScrollPane pane = new JScrollPane();
		pane.setViewportView(canvas);
		panel.add(pane, BorderLayout.CENTER);

		// ������
		panel.add(getJToolBar(), BorderLayout.EAST);

		// ״̬��
		JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(status, BorderLayout.SOUTH);
		statsText = new JLabel();
		status.add(statsText);

		return panel;
	}

	/**
	 * ˢ���Թ�
	 */
	private void updateMaze() {
		// �����Թ�
		mc.config(row, col, seed, isRand);
		mc.create();

		// ���ɷ���
		updateBlocks();

		// ����ͼ��
		updateCanvas();
	}

	void updateBlocks() {
		bc.setRoadSize(roadSize);
		bc.create(mc);
		
		int bRow = bc.getBlockRow();
		int bCol = bc.getBlockCol();
		int bCnt = bc.getBlockCount();
		int bStack = bCnt / 64;
		if (bCnt % 64 != 0)
			bStack++;
		String str = String.format(format, bRow, bCol, bCnt, bStack);
		statsText.setText(str);
	}
	
	void updateCanvas() {
		canvas.setPixel(pixel);
		canvas.setMap(bc);
		
		// ˢ��
		canvas.updateUI();
	}
	
	/**
	 * �˵�
	 */
	public JMenuBar getJMenuBar() {
		JMenuBar bar = new JMenuBar();

		JMenu fMenu = new JMenu("�ļ�(F)");
		bar.add(fMenu);

		JMenuItem exItem = new JMenuItem("����pngͼƬ(E)");
		exItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ImageIO.write(canvas.getImage(), "png", new File("map.png"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		fMenu.add(exItem);

		return bar;
	}

	/**
	 * ������
	 * 
	 * @return
	 */
	public JToolBar getJToolBar() {
		JToolBar toolBar = new JToolBar("������");
		toolBar.setOrientation(JToolBar.VERTICAL);
		toolBar.setAlignmentY(5);

		final JLabel l1 = new JLabel("����: " + row);
		addTool(toolBar, l1);

		final JSlider rowSlider = new JSlider(JSlider.HORIZONTAL, 5, 40, row);
		rowSlider.setMajorTickSpacing(10);
		rowSlider.setPaintLabels(true);
		rowSlider.setPaintTicks(true);
		rowSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				row = rowSlider.getValue();
				l1.setText("����: " + row);
				updateMaze();
			}
		});
		addTool(toolBar, rowSlider);

		final JLabel l2 = new JLabel("����: " + col);
		addTool(toolBar, l2);

		final JSlider colSlider = new JSlider(JSlider.HORIZONTAL, 5, 60, col);
		colSlider.setMajorTickSpacing(10);
		colSlider.setPaintLabels(true);
		colSlider.setPaintTicks(true);
		colSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				col = colSlider.getValue();
				l2.setText("����:" + col);
				updateMaze();
			}
		});
		addTool(toolBar, colSlider);

		final JLabel l4 = new JLabel("��·���:" + roadSize);
		addTool(toolBar, l4);

		final JSlider roadSlider = new JSlider(JSlider.HORIZONTAL, 1, 3,
				roadSize);
		roadSlider.setMajorTickSpacing(1);
		roadSlider.setPaintLabels(true);
		roadSlider.setPaintTicks(true);
		roadSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				roadSize = roadSlider.getValue();
				l4.setText("��·���:" + roadSize);

				// ���ɷ���
				updateBlocks();

				// ����ͼ��
				updateCanvas();
			}

		});
		addTool(toolBar, roadSlider);

		final JLabel l5 = new JLabel("��������:" + pixel);
		addTool(toolBar, l5);

		final JSlider pixelSlider = new JSlider(JSlider.HORIZONTAL, 8, 32,
				pixel);
		pixelSlider.setPaintLabels(true);
		pixelSlider.setMajorTickSpacing(8);
		pixelSlider.setPaintTicks(true);
		pixelSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				pixel = pixelSlider.getValue();
				l5.setText("��������:" + pixel);
				
				updateCanvas();
			}
		});
		addTool(toolBar, pixelSlider);

		final JTextField seedText = new JTextField(10);
		final JCheckBox isRandCheck = new JCheckBox("��������Թ�");
		isRandCheck.setSelected(isRand);
		isRandCheck.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				isRand = isRandCheck.isSelected();
				seedText.setEnabled(!isRand);
			}
		});
		addTool(toolBar, isRandCheck);

		JLabel l3 = new JLabel("ʹ���Թ�����:");
		addTool(toolBar, l3);

		seedText.setText("yan");
		addTool(toolBar, seedText);

		JButton refreshBtn = new JButton("ˢ�µ�ͼ");
		refreshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isRand) {
					String seeds = seedText.getText();
					seed = md5(seeds);
				}
				updateMaze();
			}
		});
		addTool(toolBar, refreshBtn);
		
		JButton astarBtn = new JButton("A��Ѱ·");
		astarBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new Thread() {
					public void run() {
						bc.pathfinding();
						updateCanvas();
					}
				}.start();
			}
		});
		addTool(toolBar, astarBtn);

		return toolBar;
	}

	/**
	 * ������������ӿؼ�
	 * @param toolBar
	 * @param comp
	 */
	private void addTool(JToolBar toolBar, Component comp) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(comp);
		toolBar.add(panel);
	}
	
	private long md5(String seeds) {
		long value = seed;
		try {
			// ʹ��MD5�㷨������������ӡ�
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(seeds.getBytes("UTF-8"));
	
			byte byteData[] = md.digest();
	
			// convert the byte to hex format method 2
			StringBuffer hexString = new StringBuffer();
			hexString.append("0x");
			for (int i = 0; i < 7; i++) {
				String hex = Integer.toHexString(0xff & byteData[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			value = Long.decode(hexString.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return value;
	}

	public static void main(String[] args) {
		new UI();
	}
}
