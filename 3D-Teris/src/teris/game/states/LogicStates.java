package teris.game.states;

import teris.game.Game;
import teris.game.scene.BoxGeometry;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class LogicStates extends AbstractAppState {

	private Game game;
	private AssetManager assetManager;

	// ����Ĳ���
	public final static int SIDE_X = 6;
	public final static int SIDE_Y = 16;
	public final static int SIDE_Z = 6;
	
	private int[][][] matrix = new int[SIDE_Y][SIDE_Z][SIDE_X];

	private boolean isChanged = false;

	// 7�ַ������״����
	private String[] color = { "red", "blue", "orange", "cyan", "green", "purple", "yellow" };
	private static int[][] pattern = {
		{ 0x0f00, 0x4444, 0x0f00, 0x4444 }, // �����͵�����״̬
		{ 0x04e0, 0x0464, 0x00e4, 0x04c4 }, // 'T'�͵�����״̬
		{ 0x4620, 0x6c00, 0x4620, 0x6c00 }, // 'S'�͵�����״̬
		{ 0x2640, 0xc600, 0x2640, 0xc600 }, // 'Z'�͵�����״̬
		{ 0x6220, 0x1700, 0x2230, 0x0740 }, // 'L'�͵�����״̬
		{ 0x6440, 0x0e20, 0x44c0, 0x8e00 }, // 'J'�͵�����״̬
		{ 0x0660, 0x0660, 0x0660, 0x0660 }, // 'O'�͵�����״̬
	};
	
	// ��ǰ�������
	private int blockType; // �������� 0-6
	private int turnState; // ����״̬ 0-3
	private int posX; // ������
	private int posY; // ������
	private int posZ; // ������
	private int blockState; // ����״̬

	// ��һ������Ĳ���
	private int nextBlockType; // �������� 0-6
	private int nextTurnState; // ����״̬ 0-3
	
	// ��Ϸ��ز���
	private int level; // ��Ϸ���� 0-9
	private int score; // ��Ϸ����
	private float rate = 1f;// ������������ 1��
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		game = (Game) app;
		assetManager = game.getAssetManager();

		newGame();

		Node controlNode = game.getControlNode();
		BoxGeometry _0 = new BoxGeometry(assetManager, "yellow");
		BoxGeometry _1 = new BoxGeometry(assetManager, "yellow");
		BoxGeometry _2 = new BoxGeometry(assetManager, "yellow");
		BoxGeometry _3 = new BoxGeometry(assetManager, "yellow");
		controlNode.attachChild(_0);
		controlNode.attachChild(_1);
		controlNode.attachChild(_2);
		controlNode.attachChild(_3);
		_0.move(0, 0, 0);
		_1.move(1, 0, 0);
		_2.move(-1, 0, 0);
		_3.move(0, 0, 1);

		controlNode.move(0.5f, 13f, 0.5f);

	}



	private float timeInSecond = 0f;// ��ǰ�Ѿ�����ʱ��
	@Override
	public void update(float tpf) {
		if (!isEnabled()) {
			return;
		}

		timeInSecond += tpf;
		if (timeInSecond >= rate) {
			timeInSecond -= rate;

			System.out.println("����");

			// ���濪ʼд�߼�

			if (!fallDown()) {// ��������
				deleteFullLine();// ������������

				if (isGameEnd()) {
					setEnabled(false);
				} else {
					// ����ǰ���ƽڵ�����һ�����齻��
					getNextBlock();

					// �����µķ��飬������Ԥ�������С�
					createNewBlock();
				}
			}

			// ˢ�½���
			refresh();
		}
	}

	/**
	 * ��ʼ����Ϸ
	 */
	private void newGame() {

		// ��վ���
		clear();

		// ���ɷ���
		getNextBlock();

		createNewBlock();
		
		getNextBlock();

		// ������Ϸ
		setEnabled(true);
	}

	public void clear() {
		// ��վ���
		for(int y=0; y<SIDE_Y; y++) {
			for(int x=0; x<SIDE_X; x++) {
				for(int z=0; z<SIDE_Z; z++) {
					matrix[y][z][x] = 0;
				}
			}
		}
		isChanged = true;
	}
	
	private void createNewBlock() {
		blockState = 1;
		posY = SIDE_Y - 1;
		posX = SIDE_X / 2 - 2;
		posZ = SIDE_Z / 2 - 2;
		
		blockType = nextBlockType;
		turnState = nextTurnState;
	}

	private void getNextBlock() {
		nextBlockType = FastMath.rand.nextInt(7);
		nextTurnState = FastMath.rand.nextInt(4);
	}

	private boolean isGameEnd() {
		boolean result = false;

		for (int col = 0; col < SIDE_X; col++) {
			for(int row = 0; row<SIDE_Z; row++) {
				if (matrix[SIDE_Y-1][row][col] != 0) {
					result = true;
				}
			}
		}

		return result;
	}

	private void deleteFullLine() {
		int full_line_num = 0;
		for (int i = 0; i < SIDE_Z; i++) {
			boolean isfull = true;

			for (int j = 0; j < SIDE_X; j++) {
				if (matrix[posY][i][j] == 0) {
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
	}

	/**
	 * �жϷ����Ƿ��������
	 * 
	 * @return
	 */
	private boolean fallDown() {
		int full_line_num = 0;
		for (int i = 0; i < SIDE_Z; i++) {
			boolean isfull = true;
			for (int j = 0; j < SIDE_X; j++) {
				if (matrix[posY][i][j] == 0) {
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
		return false;
	}
	
	/**
	 * ʵ�ֿ����µĲ����ķ���
	 * 
	 * @return
	 */
	public boolean moveDown() {
		boolean result = false;
		if (blockState == 2)
			return result;

		dispBlock(0);
		if (assertValid(turnState, posX, posY - 1, posZ)) {
			posY--;
			result = true;
		} else {
			blockState = 2;
		}
		dispBlock(blockType + 1);
		return result;
	}

	/**
	 * ����һ��
	 * 
	 * @param line
	 */
	protected void newLine(int line) {
		// ���һ��
		matrix[posY][line] = new int[] { 0, 0, 0, 0, 0, 0};

		// ����Ļ�ϵķ�������
		for (int i = line; i > 0; i--) {
			matrix[posY][i] = matrix[posY][i - 1];
		}
		matrix[posY][0] = new int[] { 0, 0, 0, 0, 0, 0};
	}

	private void refresh() {
		if (isChanged) {
			isChanged = false;
			Node wellNode = game.getWellNode();
			for (int y = 0; y < SIDE_Y; y++) {
				for (int x = 0; x < SIDE_X; x++) {
					for (int z = 0; z < SIDE_Z; z++) {
						int index = matrix[y][z][x];
						if (index > 0) {
							Geometry box = new BoxGeometry(assetManager, color[index - 1]);
							box.setLocalTranslation(x - 2.5f, y, z - 2.5f);
							wellNode.attachChild(box);
						}
					}
				}
			}
		}
	}

	/**
	 * ���ӷ���
	 * 
	 * @param lineNum
	 *            ������������
	 */
	public void addScore(int lineNum) {
		score = score + (level + 1) * lineNum;
		// ÿ1000����һ�������9����
		if (score / 1000 > level && level < 9) {
			level++;
		}
	}
	
	/**
	 * ʵ�֡��顱��ת�ķ���
	 */
	public void leftTurn() {
		dispBlock(0);
		if (assertValid((turnState + 1) % 4, posX, posY, posZ)) {
			turnState = (turnState + 1) % 4;
		}
		dispBlock(blockType + 1);
	}

	/**
	 * ʵ�֡��顱�����Ƶķ���
	 */
	public void leftMove() {
		dispBlock(0);
		if (assertValid(turnState, posX - 1, posY, posZ)) {
			posX--;
		}
		dispBlock(blockType + 1);
	}

	/**
	 * ʵ�ֿ������
	 */
	public void rightMove() {
		System.out.println("right move");
		dispBlock(0);
		if (assertValid(turnState, posX + 1, posY, posZ)) {
			posX++;
		}
		dispBlock(blockType + 1);
	}

	/**
	 * ��֤����λ����Ч��
	 * 
	 * @param turnState
	 * @param posX
	 * @param posY
	 * @return
	 */
	protected boolean assertValid(int turnState, int posX, int posY, int posZ) {
		boolean result = true;
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if ((int) (pattern[blockType][turnState] & k) != 0) {
					if (posZ + i < 0  || posZ + i >= SIDE_Z || posX + j < 0 || posX + j >= SIDE_X) {
						return false;
					}
					if (matrix[posY][posZ + i][posX + j] > 1)
						return false;
				}
				k = k >> 1;
			}
		}
		return result;
	}

	// ͬ����ʾ�ķ���
	public synchronized void dispBlock(int s) {
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					matrix[posY][posZ + i][posX + j] = s;
				}
				k = k >> 1;
			}
		}
	}

}
