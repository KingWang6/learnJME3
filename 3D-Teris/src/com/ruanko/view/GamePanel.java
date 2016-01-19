package com.ruanko.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * ��Ϸ�����
 * @author yanmaoyuan
 *
 */
public class GamePanel extends JPanel {

	private int[][] screen; // ��Ļ����
	private int columnNum; // ����
	private int rowNum; // ����

	private int unitWidth = 24; // ���������� 24px
	private int unitHeight = 24; // ��������߶� 24px
	private int bound = 5; // �߽��� 5px
	private int screenWidth; // ��Ϸ����Ļ���
	private int screenHeight; // ��Ϸ����Ļ�߶�

	Color bgColor = new Color(255, 255, 200);

	public void setScreen(int[][] screen) {
		this.screen = screen;
		rowNum = screen.length;
		columnNum = screen[0].length;
		screenWidth = columnNum * unitWidth + bound * 2;
		screenHeight = rowNum * unitHeight + bound * 2;

		this.setBackground(bgColor);
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));

	}

	/**
	 * ���ƽ���
	 */
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.drawRect(0, 0, screenWidth - 1, screenHeight - 1);
		g.drawRect(2, 2, screenWidth - 5, screenHeight - 5);
		for (int y = 0; y < rowNum; y++) {
			g.setColor(Color.black);
			for (int x = 0; x < columnNum; x++) {
				drawUnit(g, x, y, screen[y][x]);
			}
		}
	}

	/**
	 * ˢ����Ļ ����˸
	 */
	public void update(Graphics g) {
		paint(g);
	}

	/**
	 * ���Ƶ�������
	 * 
	 * @param g
	 * @param logicX
	 * @param logicY
	 * @param state
	 */
	private void drawUnit(Graphics g, int logicX, int logicY, int state) {
		// ������Ʒ������������
		int x = 5 + logicX * unitWidth;
		int y = 5 + logicY * unitHeight;
		int width = unitWidth;
		int height = unitHeight;

		Color blockColor = Color.white;
		Color lineColor = Color.white;

		switch (state) {
		case 0: {// �������ĵķ���
			g.setColor(bgColor);
			g.fillRect(x, y, width, height);
			break;
		}
		case 1: {// ����ʵ�ĵķ���
			blockColor = new Color(255, 0, 0);
			lineColor = new Color(127, 0, 0);
			break;
		}
		case 2: {// ����ʵ�ĵķ���
			blockColor = new Color(255, 200, 0);
			lineColor = new Color(127, 100, 0);
			break;
		}
		case 3: {// ����ʵ�ĵķ���
			blockColor = new Color(255, 255, 0);
			lineColor = new Color(127, 127, 0);
			break;
		}
		case 4: {// ����ʵ�ĵķ���
			blockColor = new Color(0, 255, 0);
			lineColor = new Color(0, 127, 0);
			break;
		}
		case 5: {// ����ʵ�ĵķ���
			blockColor = new Color(0, 255, 255);
			lineColor = new Color(0, 127, 127);
			break;
		}
		case 6: {// ����ʵ�ĵķ���
			blockColor = new Color(0, 0, 255);
			lineColor = new Color(0, 0, 127);
			break;
		}
		case 7: {// ����ʵ�ĵķ���
			blockColor = new Color(127, 0, 255);
			lineColor = new Color(64, 0, 127);
			break;
		}
		}
		if (state != 0) {
			g.setColor(blockColor);
			g.fillRect(x + 1, y + 1, width - 2, height - 2);

			g.setColor(lineColor);
			// drawRect
			g.drawLine(x + 1, y, x + width - 2, y);
			g.drawLine(x + 1, y + height - 1, x + width - 2, y + height - 1);
			g.drawLine(x, y + 1, x, y + height - 2);
			g.drawLine(x + width - 1, y + 1, x + width - 1, y + height - 2);
			// inner line
			g.drawLine(x + 2, y + height - 3, x + width - 3, y + height - 3);
			g.drawLine(x + width - 3, y + 2, x + width - 3, y + height - 3);
		}
	}

}
