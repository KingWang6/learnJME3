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
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	JLabel statsText;// ״̬��

	/* �Թ����� */
	private int col = 12; // ��
	private int row = 9; // ��
	private long seed = 47;// ����
	private boolean isRand = true;// �Ƿ����
	private int roadSize = 1;// ��·���
	private int pixel = 24;// ÿ������Ŀ��

	// �Թ�������
	private MazeCreator mc = new MazeCreator(col, row, seed, isRand);
	// ����������
	private BlockCreator bc = new BlockCreator(roadSize);
	// ��ͼ��
	private Canvas canvas = new Canvas();

	/**
	 * ���캯������ʼ��������
	 */
	public UI() {
		this.setTitle("YAN���Թ�������");
		this.setSize(1024, 768);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// ���ò˵�
		this.setJMenuBar(getJMenuBar());

		// �����沼��
		this.setContentPane(getContentPanel());

		// ˢ���Թ�
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

	private void updateMaze() {
		// ���ò���
		mc.config(row, col, seed, isRand);
		bc.setRoadSize(roadSize);
		canvas.setPixel(pixel);

		// �����Թ�
		mc.create();

		// ���ɵ�ͼ
		bc.create(mc);

		// ����ͼ��
		canvas.setMap(bc);

		// ˢ��
		canvas.updateUI();

		int bRow = bc.getBlockRow();
		int bCol = bc.getBlockCol();
		int bCnt = bc.getBlockCount();
		int bStack = bCnt / 64;
		if (bCnt % 64 != 0)
			bStack++;
		String str = String.format("��:%d, ��:%d, ��Ҫ����:%d(%d��).", bRow, bCol,
				bCnt, bStack);
		statsText.setText(str);
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
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);// ��ֹ����
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

				bc.setRoadSize(roadSize);
				bc.create(mc);

				// ����ͼ��
				canvas.setMap(bc);

				// ˢ��
				canvas.updateUI();

				int bRow = bc.getBlockRow();
				int bCol = bc.getBlockCol();
				int bCnt = bc.getBlockCount();
				int bStack = bCnt / 64;
				if (bCnt % 64 != 0)
					bStack++;
				String str = String.format("��:%d, ��:%d, ��Ҫ����:%d(%d��).", bRow,
						bCol, bCnt, bStack);
				statsText.setText(str);
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
				canvas.setPixel(pixel);
				canvas.setMap(bc);
				canvas.updateUI();
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
				if (isRand) {
					seedText.setEnabled(false);
				} else {
					seedText.setEnabled(true);
				}
			}
		});
		addTool(toolBar, isRandCheck);

		JLabel l3 = new JLabel("ʹ���Թ�����:");
		addTool(toolBar, l3);

		seedText.setText("" + this.seed);
		addTool(toolBar, seedText);

		JButton button = new JButton("ˢ�µ�ͼ");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (!isRand)
					try {
						// ʹ��MD5�㷨������������ӡ�
						
						String seeds = seedText.getText();
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
						seed = Long.decode(hexString.toString());
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				updateMaze();
			}
		});
		addTool(toolBar, button);

		return toolBar;
	}

	private void addTool(JToolBar toolBar, Component comp) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(comp);
		toolBar.add(panel);
	}

	public static void main(String[] args) {
		new UI();
	}
}
