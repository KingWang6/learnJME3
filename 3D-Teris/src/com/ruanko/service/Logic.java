package com.ruanko.service;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.ruanko.model.Block;
import com.ruanko.view.GamePanel;
import com.ruanko.view.MainFrame;
import com.ruanko.view.PreviewPanel;

public class Logic implements KeyListener {
	private boolean isPlay; // �Ƿ�������Ϸ��
	private boolean running = false;
	private int level; // ��Ϸ���� 0-9
	private int score; // ��Ϸ����
	private Block currentBlock; // ��ǰ����
	private Block nextBlock; // ��ǰ����

//	private int[][] screen = new int[][]{ // ��Ļ����
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	private int[][] screen = new int[20][10]; // ��Ļ����

	int rowNum = screen.length;
	int columnNum = screen[0].length;

	public int[][] getScreen() {
		return screen;
	}

	GamePanel gp;
	PreviewPanel pp;
	MainFrame mf;

	/**
	 * ���췽��
	 * @param gp
	 * @param pp
	 * @param mf
	 */
	public Logic(GamePanel gp, PreviewPanel pp, MainFrame mf) {
		this.gp = gp;
		this.pp = pp;
		this.mf = mf;
		gp.setScreen(screen);
		gp.addKeyListener(this);
		level = 0;
		score = 0;
		currentBlock = new Block(this);
		nextBlock = new Block(this);
		pp.setNext(nextBlock);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * ������Ϸ
	 */
	public void start() {
		for (int y = 0; y < rowNum; y++) {
			for (int x = 0; x < columnNum; x++) {
				screen[y][x] = 0;
			}
		}
		
		currentBlock = nextBlock;
		nextBlock = new Block(this);
		pp.setNext(nextBlock);

		isPlay = true;
		running = true;
		
		gp.repaint();
	}

	/**
	 * ��������ͣ
	 */
	public void stop() {
		running = (running == false);
	}

	/**
	 * ���ӷ���
	 * 
	 * @param lineNum
	 *            ������������
	 */
	public void addScore(int lineNum) {
		score = score + (level + 1) * lineNum;
		mf.setScore(score);
		// ÿ1000����һ�������9����
		if (score / 1000 > level && level < 9) {
			level++;
			mf.setLevel(level);
		}
	}

	/**
	 * ����ɾ������
	 */
	public void deleteFullLine() {
		int full_line_num = 0;
		for (int i = 0; i < rowNum; i++) {
			boolean isfull = true;

			for (int j = 0; j < columnNum; j++) {
				if (screen[i][j] == 0) {
					isfull = false;
					continue;
				}
			}
			if (isfull) {
				newLine(i);// ����һ��
				full_line_num++;
			}
		}
		addScore(full_line_num);
		System.out.println("Score : " + score);
	}

	/**
	 * ����һ��
	 * 
	 * @param line
	 */
	protected void newLine(int line) {
		// ���һ��
		screen[line] = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gp.repaint();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// ����Ļ�ϵķ�������
		for (int i = line; i > 0; i--) {
			screen[i] = screen[i - 1];
		}
		screen[0] = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gp.repaint();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ж���Ϸ�Ƿ��������
	 * @return
	 */
	private boolean isGameEnd() {
		boolean result = false;
		
		for (int col = 0; col < screen[0].length; col++) {
			if (screen[0][col] != 0)
				result = true;
		}

		return result;
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	// �����������ķ���
	public void keyPressed(KeyEvent e) {
		if (!isPlay)
			return;
		System.out.println("Key Pressed : " + e.getKeyCode());
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			currentBlock.leftTurn();
			gp.repaint();
			break;
		case KeyEvent.VK_DOWN:
			currentBlock.fallDown();
			gp.repaint();
			break;
		case KeyEvent.VK_LEFT:
			currentBlock.leftMove();
			gp.repaint();
			break;
		case KeyEvent.VK_RIGHT:
			currentBlock.rightMove();
			gp.repaint();
			break;
		case KeyEvent.VK_SPACE:
			currentBlock.leftTurn();
			gp.repaint();
			break;
		}
	}

	Thread thread = new Thread() {
		public void run() {
			while (true) {

				if (!currentBlock.fallDown()) {
					deleteFullLine();
					if (isGameEnd()) {
						isPlay = false;
						running = false;
					} else {
						currentBlock = nextBlock;
						gp.repaint();
						nextBlock = new Block(Logic.this);
						pp.setNext(nextBlock);
					}
				}
				gp.repaint();

				try {
					sleep((10 - level) * 110);
				} catch (InterruptedException e) {
				}
				while (!running) {
					System.out.println("pause..");
				}
			}
		}
	};
}
