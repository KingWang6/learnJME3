package yan.maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import yan.mazegame.logic.BlockCreator;

/**
 * ����
 * @author yan
 *
 */
public class Canvas extends JPanel {
	private BufferedImage image;// ͼ�񻺳���

	private int SIZE = 12;
	
	public Canvas() {
		SIZE = 12;// Ĭ��12����
	}
	
	/**
	 * �����û����õ�����ֵ��ʼ�����塣
	 * @param px
	 */
	public Canvas(int px) {
		setPixel(px);
	}
	
	/**
	 * �������ؿ��
	 * ����7 px
	 * @param px
	 */
	public void setPixel(int px) {
		if (px < 7) px = 7;
		SIZE = px;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3767183299577116646L;

	int[][] map;
	int col = 0;
	int row = 0;

	int width = 0;
	int height = 0;

	/**
	 * �����Թ���ͼ
	 * 
	 * @param map
	 */
	public void setMap(BlockCreator bc) {
		this.map = bc.getMap();
		this.row = bc.getBlockRow();
		this.col = bc.getBlockCol();

		width = col * SIZE + 9;
		height = row * SIZE + 9;

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		drawImage();
		this.setPreferredSize(new Dimension(width, height));
	}

	/**
	 * ���Թ���ͼ�����������С�
	 */
	private void drawImage() {
		// ����
		image.flush();

		Graphics g = image.getGraphics();
		
		// ����ɫ
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		
		// ѭ�����ƽ���
		for (int y = 0; y < row; y++) {
			g.setColor(Color.BLACK);
			for (int x = 0; x < col; x++) {
				// ���ƽ��淽��
				switch (map[y][x]) {
				case 1: {
					drawBlock(g, x, y);
					break;
				}
				case 2: {
					drawPath(g, x, y);
					break;
				}
				}
			}
		}
	}

	/**
	 * ���Ƶ�������
	 * 
	 * @param g
	 *            ����
	 * @param posX
	 *            ������
	 * @param posY
	 *            ������
	 */
	private void drawBlock(Graphics g, int posX, int posY) {
		// ���÷�������
		int x = 5 + posX * SIZE;
		int y = 5 + posY * SIZE;

		// ���Ʒ����ɫ
		g.setColor(new Color(0xEE, 0xEE, 0xFF));
		g.fillRect(x + 1, y + 1 , SIZE - 2, SIZE - 2);
		
		// �����߶���ɫ
		g.setColor(Color.BLACK);
		// �����߶�
		g.drawLine(x + 1, y, x + SIZE - 2, y);
		g.drawLine(x + 1, y + SIZE - 1, x + SIZE - 2, y + SIZE - 1);
		g.drawLine(x, y + 1, x, y + SIZE - 2);
		g.drawLine(x + SIZE - 1, y + 1, x + SIZE - 1, y + SIZE - 2);
		// ����������߶�
		g.drawLine(x + 2, y + SIZE - 3, x + SIZE - 3, y + SIZE - 3);
		g.drawLine(x + SIZE - 3, y + 2, x + SIZE - 3, y + SIZE - 3);
	}

	/**
	 * ����·����
	 * 
	 * @param g
	 *            ����
	 * @param posX
	 *            ������
	 * @param posY
	 *            ������
	 */
	private void drawPath(Graphics g, int posX, int posY) {
		// ���÷�������
		int x = 5 + posX * SIZE;
		int y = 5 + posY * SIZE;

		// ���Ʒ����ɫ
		g.setColor(Color.RED);
		g.fillRect(x + 2, y + 2 , SIZE - 5, SIZE - 5);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(image, 0, 0, null);
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	public BufferedImage getImage() {
		return image;
	}
}
