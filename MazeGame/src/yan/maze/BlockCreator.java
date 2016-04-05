package yan.maze;

public class BlockCreator {

	// ��·���
	// �����Թ��ĵ�·ʹ�÷�����������ʵ�ʿ��Ҫ+1��
	private int ROAD_SIZE;
	
	private int[][] blocks = null;// �Թ���ͼ
	
	private int blockCol;// ��������
	private int blockRow;// ��������
	private int blockCount;// ��������
	
	/**
	 * Ĭ�Ϲ��캯��
	 */
	public BlockCreator() {
		ROAD_SIZE = 3;// Ĭ�ϵ�·���2
	}

	/**
	 * ���ó�ʼ��·���
	 * 
	 * @param roadSize
	 */
	public BlockCreator(final int roadSize) {
		ROAD_SIZE = roadSize + 1;
	}

	/**
	 * ���õ�·���
	 * 
	 * @param size
	 */
	public void setRoadSize(final int size) {
		ROAD_SIZE = size + 1;
	}

	/**
	 * ���ɷ����Թ�
	 * @param mc
	 */
	public void create(MazeCreator mc) {
		create(mc.getMaze(), mc.getColCount(), mc.getRowCount());
	}
	
	/**
	 * ���ɷ����Թ�
	 * @param maze
	 * @param cols
	 * @param rows
	 */
	public void create(final Cell[][] maze, final int cols, final int rows) {
		// �����Թ���ռ�������������
		this.blockRow = rows * ROAD_SIZE + 1;
		this.blockCol = cols * ROAD_SIZE + 1;
		blocks = new int[blockRow][blockCol];
		
		// �����Թ����ݣ�������������ǽ��
		for (int col = 0; col < cols; col++) {
			for (int row = 0; row < rows; row++) {
				Cell cell = maze[row][col];
				int x = col * ROAD_SIZE;
				int y = row * ROAD_SIZE;
				// 0: east; 1: south; 2: west; 3 north; have door: true
				if (!cell.door[3]) {
					makeWall(x, y, x + ROAD_SIZE, y);
				}
				if (!cell.door[0]) {
					makeWall(x + ROAD_SIZE, y, x + ROAD_SIZE, y + ROAD_SIZE);
				}
				if (!cell.door[1]) {
					makeWall(x, y + ROAD_SIZE, x + ROAD_SIZE, y + ROAD_SIZE);
				}
				if (!cell.door[2]) {
					makeWall(x, y, x, y + ROAD_SIZE);
				}
			}
		}

		// ���㹹�������Թ�ʹ�õķ�������
		blockCount = 0;
		for (int y = 0; y < blockRow; y++) {
			for (int x = 0; x < blockCol; x++) {
				if (blocks[y][x] != 0) {
					blockCount++;
				}
			}
		}
	}

	/**
	 * ����һ��ǽ
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	private void makeWall(int x1, int y1, int x2, int y2) {
		for (int y = y1; y <= y2; y++) {
			for (int x = x1; x <= x2; x++) {
				blocks[y][x] = 1;
			}
		}
	}
	

	public int[][] getMap() {
		return blocks;
	}

	public int getBlockCol() {
		return blockCol;
	}

	public int getBlockRow() {
		return blockRow;
	}

	public int getBlockCount() {
		return blockCount;
	}

}
