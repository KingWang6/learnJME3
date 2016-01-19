package com.ruanko.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.ruanko.model.Block;

/**
 * Ԥ��
 * @author yanmaoyuan
 *
 */
public class PreviewPanel extends JPanel {
	private int unitWidth = 24; // ���������� 24px
	private int unitHeight = 24; // ��������߶� 24px
	private int bound = 5; // �߽��� 5px
	private int screenWidth; // ��Ϸ����Ļ���
	private int screenHeight; // ��Ϸ����Ļ�߶�
	
	private int pattern = 0;
	private int state = 0;

	Color bgColor = new Color(255, 255, 200);
	
	public PreviewPanel() {
		screenWidth = 4 * unitWidth + bound * 2;
		screenHeight = 4 * unitHeight + bound * 2;

		this.setBackground(bgColor);
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
	}

	public void setNext(Block block) {
		this.pattern = block.getPattern();
		this.state = block.getState() + 1;
		repaint();
	}

	/**
	 * ���ƽ���
	 */
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.drawRect(0, 0, screenWidth - 1, screenHeight - 1);
		g.drawRect(2, 2, screenWidth - 5, screenHeight - 5);

		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern & k) != 0) {
					drawUnit(g, j, i);
				}
				k = k >> 1;
			}
		}
	}

	/**
	 * ���Ƶ�������
	 * 
	 * @param g
	 * @param logicX
	 * @param logicY
	 * @param state
	 */
	private void drawUnit(Graphics g, int logicX, int logicY) {
		// ������Ʒ������������
		int x = 5 + logicX * unitWidth;
		int y = 5 + logicY * unitHeight;
		int width = unitWidth;
		int height = unitHeight;

		Color blockColor = bgColor;
		Color lineColor = bgColor;

		switch (state) {
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
