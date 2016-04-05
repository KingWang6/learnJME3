package yan.mazegame.logic;

import java.util.Random;

/**
 * �Թ�������
 * @author yan
 *
 */
public class MazeCreator {
	private final static int ROWS = 20;// Ĭ������
	private final static int COLS = 30;// Ĭ������
	
	private final static int DIRECTION_NUM = 4;
	private final static int DIRECTION_EAST = 0;
	private final static int DIRECTION_SOUTH = 1;
	private final static int DIRECTION_WEST = 2;
	private final static int DIRECTION_NORTH = 3;

	private int[] cells;// ��ʶÿ�������Ƿ���ͨ
	private Cell[][] maze;// �Թ�

	private int rows;
	private int cols;
	private int cellCnt;
	private Random rand;

	/**
	 * ʹ��Ĭ�ϲ����������
	 */
	public MazeCreator() {
		this(ROWS, COLS, 0, false);
	}

	/**
	 * ����һ���Թ�������
	 * @param rows ����
	 * @param cols ����
	 * @param seed ���������
	 * @param rand �Ƿ�ʹ�������������Թ���falseΪ��ȫ�����true��������������ɡ�
	 */
	public MazeCreator(int rows, int cols, long seed, boolean rand) {
		if (rand) {
			this.rand = new Random();
		} else {
			this.rand = new Random(1000);
		}
		
		this.rows = rows;
		this.cols = cols;

		this.maze = new Cell[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				maze[i][j] = new Cell();
			}
		}
		maze[0][0].door[DIRECTION_WEST] = true;// ���
		maze[rows - 1][cols - 1].door[DIRECTION_EAST] = true;// �յ�

		this.cellCnt = cols * rows;
		this.cells = new int[cellCnt];
		for (int i = 0; i < cellCnt; i++) {
			cells[i] = -1;
		}
	}
	
	public void config(int rows, int cols, long seed, boolean rand) {
		if (rand) {
			this.rand = new Random();
		} else {
			this.rand = new Random(seed);
		}
		
		this.rows = rows;
		this.cols = cols;

		this.maze = new Cell[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				maze[i][j] = new Cell();
			}
		}
		maze[0][0].door[DIRECTION_WEST] = true;// ���
		maze[rows - 1][cols - 1].door[DIRECTION_EAST] = true;// �յ�

		this.cellCnt = cols * rows;
		this.cells = new int[cellCnt];
		for (int i = 0; i < cellCnt; i++) {
			cells[i] = -1;
		}
	}
	
	public void setRandomSeed(long seed) {
		this.rand.setSeed(seed);
	}

	public int getRowCount() {
		return rows;
	}
	
	public int getColCount() {
		return cols;
	}

	public Cell[][] getMaze() {
		return maze;
	}
	/**
	 * �����Թ�
	 */
	public void create() {
		int direction = 0;
		int c1 = 0;
		int c2 = 0;
		// ���ѡ��һ��ǽ
		while (true) {
			c1 = rand.nextInt(cellCnt);
			direction = rand.nextInt(DIRECTION_NUM);
			switch (direction) {
			case DIRECTION_EAST:
				if (c1 % cols == cols - 1)
					c2 = -1;
				else
					c2 = c1 + 1;
				break;
			case DIRECTION_SOUTH:
				if ((rows - 1) == (c1 - c1 % cols) / cols)
					c2 = -1;
				else
					c2 = c1 + cols;
				break;
			case DIRECTION_WEST:
				if (c1 % cols == 0)
					c2 = -1;
				else
					c2 = c1 - 1;
				break;
			case DIRECTION_NORTH:
				if (0 == (c1 - c1 % cols) / cols)
					c2 = -1;
				else
					c2 = c1 - cols;
				break;
			default:
				System.exit(0);
				break;
			}
			if (c2 < 0)
				continue;
			// �ж������ѡ���������ڷ����Ƿ���ͨ��
			if (is_Connect(c1, c2))
				continue;
			else {
				// �Ƴ�����֮���ǽ��
				union_Cells(c1, c2);
				// �����Թ�
				maze[(c1 - c1 % cols) / cols][c1 % cols].door[direction] = true;
				maze[(c2 - c2 % cols) / cols][c2 % cols].door[(direction + 2) % DIRECTION_NUM] = true;
			}
			// ��������յ���ͨ�ˣ���˵���Թ����ɳɹ�
			if (is_Connect(0, cellCnt - 1) && all_Connect())
				break;
			
		}
	}

	/**
	 * �ж����������Ƿ���ͨ
	 * @param c1
	 * @param c2
	 * @return
	 */
	private boolean is_Connect(int c1, int c2) {
		while (cells[c1] >= 0)
			c1 = cells[c1];
		while (cells[c2] >= 0)
			c2 = cells[c2];
		if (c1 == c2)
			return true;
		else
			return false;
	}

	/**
	 * �ж��Ƿ����еķ��䶼��ͨ��
	 * @return
	 */
	private boolean all_Connect() {
		int i, count_root = 0;
		for (i = 0; i < rows * cols; i++) {
			if (cells[i] < 0)
				count_root++;
		}
		if (1 == count_root)
			return true;
		else
			return false;
	}

	/**
	 * if the two adjacent rooms are not connect, remove the wall between them(or fix a door)
	 * @param c1
	 * @param c2
	 */
	private void union_Cells(int c1, int c2) {
		while (cells[c1] >= 0)
			c1 = cells[c1];
		while (cells[c2] >= 0)
			c2 = cells[c2];

		// the depth of the tree with c2 is deepper than Tc1, Tc1 attach to Tc2
		if (cells[c1] > cells[c2]) {
			cells[c1] = c2;
		} else {
			if (cells[c1] == cells[c2])
				cells[c1]--;
			cells[c2] = c1;
		}
	}
}