package teris.game.states;

import teris.game.Game;
import teris.game.control.MoveControl;
import teris.game.control.MoveControl.DIRECTION;
import teris.game.control.RotateControl;
import teris.game.scene.BoxGeometry;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class LogicStates extends AbstractAppState {

	private Game game;
	private AssetManager assetManager;
	
	// ���ڿ��Ʒ����ƶ������εĿ�����
	private MoveControl mc;
	private RotateControl rc;
	
	// ����Ĳ���
	public final static int SIDE_X = 6;
	public final static int SIDE_Y = 16;
	public final static int SIDE_Z = 6;

	// ����ڵ�(��)�Ĳ���
	private int[][][] matrix = new int[SIDE_Y][SIDE_Z][SIDE_X];
	private BoxGeometry[][][] wells = new BoxGeometry[SIDE_Y][SIDE_Z][SIDE_X];
	private boolean matrixChanged = false;

	// �ܿؽڵ�Ĳ���
	private int[][] shape = new int[4][4];
	private BoxGeometry[][] controls = new BoxGeometry[4][4];
	private boolean controlChanged = false;

	// 7�ַ������״����
	private static int[][] pattern = {
		{ 0x0f00, 0x2222, 0x00f0, 0x4444 }, // �����͵�����״̬
		{ 0x0270, 0x0464, 0x0e40, 0x2620 }, // 'T'�͵�����״̬
		{ 0x4620, 0x0360, 0x0462, 0x06c0 }, // 'S'�͵�����״̬
		{ 0x2640, 0x0630, 0x0264, 0x0c60 }, // 'Z'�͵�����״̬
		{ 0x0622, 0x02e0, 0x4460, 0x0740 }, // 'L'�͵�����״̬
		{ 0x0644, 0x0e20, 0x2260, 0x0470 }, // 'J'�͵�����״̬
		{ 0x0660, 0x0660, 0x0660, 0x0660 }, // 'O'�͵�����״̬
	};
	
	// ��ǰ�������
	private int blockType; // �������� 0-6
	private int turnState; // ����״̬ 0-3
	private int posX; // ������
	private int posY; // ������
	private int posZ; // ������
	private boolean reachButton; // ����״̬

	// ��һ������Ĳ���
	private int nextBlockType; // �������� 0-6
	private int nextTurnState; // ����״̬ 0-3
	
	// ��Ϸ��ز���
	private int level; // ��Ϸ���� 0-9
	private int score; // ��Ϸ����
	private float rate = 0.5f;// ������������ 1��
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		game = (Game) app;
		assetManager = game.getAssetManager();

		mc = game.getControlNode().getControl(MoveControl.class);
		rc = game.getControlNode().getControl(RotateControl.class);
		
		for(int y=0; y<SIDE_Y; y++) {
			for(int x=0; x<SIDE_X; x++) {
				for(int z=0; z<SIDE_Z; z++) {
					wells[y][z][x] = new BoxGeometry(assetManager, 0);
					wells[y][z][x].setLocalTranslation(x - 2.5f, y, z - 2.5f);
				}
			}
		}
		
		for(int x=0; x<4; x++) {
			for(int y=0; y<4; y++) {
				controls[y][x] = new BoxGeometry(assetManager, 0);
				controls[y][x].setLocalTranslation(x - 1.5f, 0, y - 1.5f);
			}
		}
		
		newGame();
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

			// ���濪ʼд�߼�
			if (!moveDown()) {// ��������
				
				// ���ܿؽڵ���ӵ�����
				addToWell();
				
				// 
				game.getControlNode().setLocalTranslation(new Vector3f(0, SIDE_Y - 1, 0));
				game.getControlNode().setLocalRotation(new Quaternion());
				
				deleteFullLine();// ������������

				if (isGameEnd()) {
					setEnabled(false);
				} else {
					// ����ǰ���ƽڵ�����һ�����齻��
					createNewBlock();

					// �����µķ��飬������Ԥ�������С�
					getNextBlock();
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
		
		game.getControlNode().setLocalTranslation(0, SIDE_Y-1, 0);

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
		matrixChanged = true;
		
		for(int x = 0; x<4; x++) {
			for(int y=0; y<4; y++) {
				shape[y][x] = 0;
			}
		}
		controlChanged = true;
	}
	
	private void createNewBlock() {
		reachButton = false;
		posY = SIDE_Y - 1;
		posX = SIDE_X / 2 - 2;
		posZ = SIDE_Z / 2 - 2;
		
		blockType = nextBlockType;
		turnState = nextTurnState;
		
		updateControlNode(blockType + 1);
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
	 * ����һ��
	 * 
	 * @param line
	 */
	protected void newLine(int line) {
		// ���һ��
		matrix[posY][line] = new int[] { 0, 0, 0, 0, 0, 0};

//		// ����Ļ�ϵķ�������
//		for (int i = line; i > 0; i--) {
//			matrix[posY][i] = matrix[posY][i - 1];
//		}
//		matrix[posY][0] = new int[] { 0, 0, 0, 0, 0, 0};
		
		matrixChanged = true;
	}

	private void refresh() {
		// �������˸ı�
		if (matrixChanged) {
			matrixChanged = false;
			Node wellNode = game.getWellNode();
			for (int y = 0; y < SIDE_Y; y++) {
				for (int x = 0; x < SIDE_X; x++) {
					for (int z = 0; z < SIDE_Z; z++) {
						int index = matrix[y][z][x];
						if (index > 0) {
							wells[y][z][x].setColor(index - 1);
							wellNode.attachChild(wells[y][z][x]);
						} else {
							wellNode.detachChild(wells[y][z][x]);
						}
					}
				}
			}
		}
		
		// �ܿؽڵ㷢���˸ı�
		if (controlChanged) {
			controlChanged = false;
			Node controlNode = game.getControlNode();
			for(int y=0; y<4; y++) {
				for(int x=0; x<4; x++) {
					int index = shape[y][x];
					if (index > 0) {
						controls[y][x].setColor(index - 1);
						controlNode.attachChild(controls[y][x]);
					} else {
						controlNode.detachChild(controls[y][x]);
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
	 * ����˳ʱ����ת
	 */
	public void rotateRight() {
		if (assertValid((turnState + 1) % 4, posX, posY, posZ)) {
			if (!rc.isRotating()) {
				turnState = (turnState + 1) % 4;
				rc.rotate(true);
			}
		}
	}
	/**
	 * ������ʱ����ת
	 */
	public void rotateLeft() {
		if (assertValid((turnState + 7) % 4, posX, posY, posZ)) {
			if (!rc.isRotating()) {
				turnState = (turnState + 7) % 4;
				game.getControlNode().getControl(RotateControl.class).rotate(false);
			}
		}
	}

	/**
	 * ��������
	 * 
	 * @return
	 */
	public boolean moveDown() {
		boolean result = false;
		if (reachButton)
			return result;

		if (assertValid(turnState, posX, posY - 1, posZ)) {
			posY--;
			result = true;
			mc.move(DIRECTION.DOWN);
		} else {
			reachButton = true;
		}
		return result;
	}
	
	/**
	 * �������ƶ�
	 */
	public void moveNorth() {
		if (assertValid(turnState, posX, posY, posZ-1)) {
			if (!mc.isMoving()) {
				posZ--;
				mc.move(DIRECTION.NORTH);
			}
		}
	}
	/**
	 * ���������ƶ�
	 */
	public void moveSouth() {
		if (assertValid(turnState, posX, posY, posZ+1)) {
			if (!mc.isMoving()) {
				posZ++;
				mc.move(DIRECTION.SOUTH);
			}
		}
	}
	/**
	 * ���������ƶ�
	 */
	public void moveWest() {
		if (assertValid(turnState, posX - 1, posY, posZ)) {
			if (!mc.isMoving()) {
				posX--;
				mc.move(DIRECTION.WEST);
			}
		}
	}

	/**
	 * ʵ�ֿ������
	 */
	public void moveEast() {
		if (assertValid(turnState, posX + 1, posY, posZ)) {
			if (!mc.isMoving()) {
				posX++;
				mc.move(DIRECTION.EAST);
			}
		}
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
					if (posY < 0 || posY >= SIDE_Y || posZ + i < 0  || posZ + i >= SIDE_Z || posX + j < 0 || posX + j >= SIDE_X) {
						return false;
					}
					if (matrix[posY][posZ + i][posX + j] > 0)
						return false;
				}
				k = k >> 1;
			}
		}
		return result;
	}

	/**
	 * ˢ�¾���
	 * @param s
	 */
	private void addToWell() {
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					matrix[posY][posZ + i][posX + j] = blockType + 1;
				}
				k = k >> 1;
			}
		}
		
		matrixChanged = true;
	}
	
	/**
	 * ˢ�¿��ƽڵ�
	 * @param s
	 */
	private void updateControlNode(int s) {
		assert s > 0;
		
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					shape[i][j] = s;
				} else {
					shape[i][j] = 0;
				}
				k = k >> 1;
			}
		}
		
		controlChanged = true;
	}

}
