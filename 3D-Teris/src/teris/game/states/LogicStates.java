package teris.game.states;

import java.util.HashMap;
import java.util.Map.Entry;

import teris.game.DIRECTION;
import teris.game.Main;
import teris.game.control.MoveControl;
import teris.game.control.RotateControl;
import teris.game.scene.BoxGeometry;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;
import com.jme3.shadow.DirectionalLightShadowRenderer;

public class LogicStates extends AbstractAppState {

	private Main game;
	
	private Node rootNode = new Node("logicRoot");
	private Node guiNode = new Node("logicGui");
	
	private Node wellNode;// ��Ϸ�еġ������ڵ�
	private Node controlNode;// �ܿؽڵ�
	private Node previewNode;// Ԥ���ڵ�
	
	private Node axisNode;// Axis
	
	private BitmapText scoreTxt;
	
	private MoveControl moveControl;// ���ڿ��Ʒ����ƶ��Ŀ�����
	private RotateControl rotateControl;// ���ڿ��Ʒ�����ת�Ŀ�����
	
	// ����Ĳ���
	public final static int SIDE_X = 6;
	public final static int SIDE_Y = 16;
	public final static int SIDE_Z = 6;

	// ����ڵ�(��)�Ĳ���
	private int[][][] matrix = new int[SIDE_Y][SIDE_Z][SIDE_X];
	private BoxGeometry[][][] wells = null;
	private boolean matrixChanged = false;

	// �ܿؽڵ�Ĳ���
	private int[][] shape = new int[4][4];
	private BoxGeometry[][] controls = null;
	private boolean controlChanged = false;
	
	// Ԥ���ڵ�Ĳ���
	private int[][] previewShape = new int[4][4];
	private BoxGeometry[][] previews = null;
	private boolean previewChanged = false;

	// 7�ַ������״����
	private static int[][] pattern = {
		{ 0x0f00, 0x2222, 0x00f0, 0x4444 }, // 'I'�͵�����״̬
		{ 0x0644, 0x0e20, 0x2260, 0x0470 }, // 'J'�͵�����״̬
		{ 0x0622, 0x02e0, 0x4460, 0x0740 }, // 'L'�͵�����״̬
		{ 0x0660, 0x0660, 0x0660, 0x0660 }, // 'O'�͵�����״̬
		{ 0x4620, 0x0360, 0x0462, 0x06c0 }, // 'S'�͵�����״̬
		{ 0x0270, 0x0464, 0x0e40, 0x2620 }, // 'T'�͵�����״̬
		{ 0x2640, 0x0630, 0x0264, 0x0c60 }, // 'Z'�͵�����״̬
	};
	
	// ��ǰ�������
	private int blockType; // �������� 0-6
	private int turnState; // ����״̬ 0-3
	private int posX; // ������
	private int posY; // ������
	private int posZ; // ������

	// ��һ������Ĳ���
	private int nextBlockType; // �������� 0-6
	private int nextTurnState; // ����״̬ 0-3
	
	// ��Ϸ��ز���
	private int level; // ��Ϸ���� 0-9
	private int score; // ��Ϸ����
	private float rate;// ������������
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		AssetManager assetManager = app.getAssetManager();
		
		// ��ʼ����Ϸ����
		game = (Main) app;
		
		game.getRootNode().attachChild(rootNode);
		game.getGuiNode().attachChild(guiNode);
		
		game.getViewPort().setBackgroundColor(new ColorRGBA(0.3f, 0.4f, 0.5f, 1));
		
		initGui();
		initCamera();
		initLight();
		
		initScene();
		
		// ��ʼ�����ݽṹ
		if (wells == null) {
			wells = new BoxGeometry[SIDE_Y][SIDE_Z][SIDE_X];
			
			// ���������ƫ����
			Vector3f postion = new Vector3f();
			Vector3f offset = new Vector3f(-SIDE_X/2+0.5f, 0.5f, -SIDE_Z/2+0.5f);
			
			for(int y=0; y<SIDE_Y; y++) {
				for(int x=0; x<SIDE_X; x++) {
					for(int z=0; z<SIDE_Z; z++) {
						// ����ʵ������
						postion.set(offset.add(x, y, z));
						
						wells[y][z][x] = new BoxGeometry(assetManager, 0);
						wells[y][z][x].setLocalTranslation(postion);
					}
				}
			}
		}
		
		if (controls == null) {
			controls = new BoxGeometry[4][4];
			
			// ���������ƫ����
			Vector3f postion = new Vector3f();
			Vector3f offset = new Vector3f(-2+0.5f, 0.5f, -2+0.5f);
						
			for(int x=0; x<4; x++) {
				for(int y=0; y<4; y++) {
					// ����ʵ������
					postion.set(offset.add(x, 0, y));
					
					controls[y][x] = new BoxGeometry(assetManager, 0);
					controls[y][x].setLocalTranslation(postion);
				}
			}
		}
		
		if (previews == null) {
			previews = new BoxGeometry[4][4];
			
			// ���������ƫ����
			Vector3f postion = new Vector3f();
			Vector3f offset = new Vector3f(-2+0.5f, 0.5f, -2+0.5f);
						
			for(int x=0; x<4; x++) {
				for(int y=0; y<4; y++) {
					// ����ʵ������
					postion.set(offset.add(x, 0, y));
					
					previews[y][x] = new BoxGeometry(assetManager, 0);
					previews[y][x].setLocalTranslation(postion);
				}
			}
		}
		
		newGame();
	}
	
	private void initGui() {
		BitmapFont fnt = game.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		String txtA = "Score: 0\nLevel: 0";
		scoreTxt = new BitmapText(fnt, false);
		scoreTxt.setText(txtA);
		scoreTxt.setLocalTranslation(0, 640, 0);
		guiNode.attachChild(scoreTxt);

	}

	private void initCamera() {
		game.getCamera().setLocation(new Vector3f(0, 25, 10));
		game.getCamera().lookAt(new Vector3f(0, 8, 0), game.getCamera().getUp());
	}
	
	/**
	 * ��ʼ������
	 */
	private void initLight() {
		
		/**
		 * ����һ����ֱ���µķ����Դ������⽫�������Ӱ����������Ԥ֪���������λ�á�
		 */
		DirectionalLight light = new DirectionalLight();
		ColorRGBA color = new ColorRGBA(0.5f, 0.5f, 0.5f, 1f);
		light.setColor(color);
		light.setDirection(new Vector3f(0, -1f, 0).normalizeLocal());
		rootNode.addLight(light);
		
		// ������Ӱ
		DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(game.getAssetManager(), 1024, 4);
		dlsr.setLight(light);
		game.getViewPort().addProcessor(dlsr);
		rootNode.setShadowMode(ShadowMode.CastAndReceive);
		
		/**
		 * �����һ�������Դ���ó���������ķ�λ��һ�㡣
		 */
		light = new DirectionalLight();
		color = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);
		light.setColor(color);
		light.setDirection(new Vector3f(0, -1, -1).normalize());
		rootNode.addLight(light);
		
		/**
		 * �����һ�������⣬����Ϸ������΢��һЩ��
		 */
		AmbientLight ambient = new AmbientLight();
		rootNode.addLight(ambient);
	}
	
	/**
	 * ��ʼ������
	 */
	private void initScene() {
		rootNode.attachChild(getWellNode());
		
		rootNode.attachChild(getPreviewNode());
		
	}
	
	private Node getWellNode() {
		if (wellNode == null) {
			wellNode = new Node("well");
			
			// �����ת������
			wellNode.addControl(new RotateControl());
			
			// ���ܿؽڵ���ӵ�"��"�ڵ��У�������ת"��"��ʱ���ܿؽڵ�Ҳ��һ����ת��
			wellNode.attachChild(getControlNode());
			
			wellNode.setShadowMode(ShadowMode.Receive);
			
			// ��Ӳο�����ϵ
			axisNode = getAxisNode();
			wellNode.attachChild(getAxisNode());
		}
		
		return wellNode;
	}
	private Node getControlNode() {
		if (controlNode == null) {
			
			controlNode = new Node("control");
			
			// �����ת������
			rotateControl = new RotateControl();
			controlNode.addControl(rotateControl);
			
			// ����ƶ�������
			moveControl = new MoveControl();
			controlNode.addControl(moveControl);
			
			// �ܿؽڵ�ֻ������Ӱ����������Ӱ
			controlNode.setShadowMode(ShadowMode.Cast);
		}
		return controlNode;
	}
	private Node getPreviewNode() {
		if (previewNode == null) {
			previewNode = new Node("preview");
			previewNode.scale(0.5f);
			previewNode.rotate(FastMath.QUARTER_PI/3, 0, 0);
			previewNode.move(0, 0, 5);
			previewNode.setShadowMode(ShadowMode.Off);
		}
		
		return previewNode;
	}

	private Node getAxisNode() {
		if (axisNode == null) {
			axisNode = new Node("AxisNode");
			Geometry grid = new Geometry("Axis", new Grid(7, 7, 1f));
			
			AssetManager assetManager = game.getAssetManager();
			Material gm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			gm.setColor("Color", ColorRGBA.White);
			gm.getAdditionalRenderState().setWireframe(true);
			grid.setMaterial(gm);
			
			grid.center().move(0, 0, 0);

			axisNode.attachChild(grid);
		}

		return axisNode;
	}
	
	/**
	 * ��ǰ�Ѿ�����ʱ��
	 */
	private float timeInSecond = 0f;
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
				
				// ���ܿؽڵ㸴λ
				resetControlNode();
				
				deleteFullLine();// ������������

				if (isGameEnd()) {
					setEnabled(false);
					scoreTxt.setText("Your Final Score: " + score);
				} else {
					// ����ǰ���ƽڵ�����һ�����齻��
					createNewBlock();

					// �����µķ��飬������Ԥ�������С�
					getNextBlock();
				}
			} else {
				moveControl.move(DIRECTION.DOWN);
			}

			// ˢ�½���
			refresh();
		}
	}

	/**
	 * ��ʼ����Ϸ
	 */
	private void newGame() {

		// ��վ�������
		for(int y=0; y<SIDE_Y; y++) {
			for(int x=0; x<SIDE_X; x++) {
				for(int z=0; z<SIDE_Z; z++) {
					matrix[y][z][x] = 0;
				}
			}
		}
		matrixChanged = true;
		
		// ����ܿؽڵ���״����
		for(int x = 0; x<4; x++) {
			for(int y=0; y<4; y++) {
				shape[y][x] = 0;
			}
		}
		controlChanged = true;
		
		
		// ��ʼ����Ϸ�ȼ������֡���������
		level = 0;
		score = 0;
		rate = 1f;
		
		// ��ʼ���ܿؽڵ��λ��
		resetControlNode();

		// ��ʼ���ܿط����λ��
		createNewBlock();

		// ����Ԥ������
		getNextBlock();
		
		// ������Ϸ
		setEnabled(true);
	}

	private void createNewBlock() {
		posY = SIDE_Y - 1;
		posX = SIDE_X / 2 - 2;
		posZ = SIDE_Z / 2 - 2;
		
		blockType = nextBlockType;
		turnState = nextTurnState;
		
		updateControl();
	}

	private void getNextBlock() {
		nextBlockType = FastMath.rand.nextInt(7);
		nextTurnState = FastMath.rand.nextInt(4);
		
		updatePreview();
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

	HashMap<Integer, Vector3f> recordMap = new HashMap<Integer, Vector3f>();
	private void deleteFullLine() {
		recordMap.clear();
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
				for(int j=0; j<SIDE_X; j++) {
					recordIt(j, posY, i);
				}
				full_line_num++;
			}
		}
		
		for (int j = 0; j < SIDE_X; j++) {
			boolean isfull = true;

			for (int i = 0; i < SIDE_Z; i++) {
				if (matrix[posY][i][j] == 0) {
					isfull = false;
					continue;
				}
			}
			if (isfull) {
				// record it
				for(int i=0; i<SIDE_Z; i++) {
					recordIt(j, posY, i);
				}
				full_line_num++;
			}
		}
		
		// �������÷�������
		if (full_line_num > 0) {
			for(Entry<Integer, Vector3f> entry : recordMap.entrySet()) {
				Vector3f pos = entry.getValue();
				int x= (int) pos.x;
				int y= (int) pos.y;
				int z= (int) pos.z;
				
				// �ø÷����Ϸ������з�������
				for(int i=y; i<SIDE_Y-1; i++) {
					matrix[i][z][x] = matrix[i+1][z][x];
				}
				matrix[SIDE_Y-1][z][x] = 0;
			}
			
			matrixChanged = true;
		}
		addScore(full_line_num);
	}

	/**
	 * ��¼��Щ�㱻�����ˡ�
	 * @param x
	 * @param y
	 * @param z
	 */
	private void recordIt(int x, int y, int z) {
		int hashCode = x*1000 + y*10 + z;
		if (!recordMap.containsKey(hashCode)) {
			recordMap.put(hashCode, new Vector3f(x, y, z));
		}
	}
	/**
	 * ����һ��
	 * 
	 * @param line
	 */
	protected void newLine(int line) {
		// ���һ��
		//matrix[posY][line] = new int[] { 0, 0, 0, 0, 0, 0};

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
		
		// Ԥ���ڵ㷢���˸ı�
		if (previewChanged) {
			previewChanged = false;
			
			for(int y=0; y<4; y++) {
				for(int x=0; x<4; x++) {
					int index = previewShape[y][x];
					if (index > 0) {
						previews[y][x].setColor(index - 1);
						previewNode.attachChild(previews[y][x]);
					} else {
						previewNode.detachChild(previews[y][x]);
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
		score = score + lineNum * lineNum;
		// ÿ1000����һ�������9����
		if (score / 100 > level && level < 9) {
			level++;
			rate -= 0.1f;
		}
		
		scoreTxt.setText("Score: " + score + "\nLevel: " + level);
	}
	
	public void rotateWellRight() {
		wellNode.getControl(RotateControl.class).rotate(true);
	}
	
	public void rotateWellLeft() {
		wellNode.getControl(RotateControl.class).rotate(false);
	}
	/**
	 * ����˳ʱ����ת
	 */
	public void rotateRight() {
		if (assertValid((turnState + 1) % 4, posX, posY, posZ)) {
			if (!rotateControl.isRotating()) {
				turnState = (turnState + 1) % 4;
				rotateControl.rotate(true);
			}
		}
		
		// Ҳ����Լ�����ת��ǽ����wallkick
	}
	/**
	 * ������ʱ����ת
	 */
	public void rotateLeft() {
		if (assertValid((turnState + 7) % 4, posX, posY, posZ)) {
			if (!rotateControl.isRotating()) {
				turnState = (turnState + 7) % 4;
				rotateControl.rotate(false);
			}
		}
	}

	/**
	 * ��������
	 * @return
	 */
	public void quickDown() {
		while(moveDown());
	}
	/**
	 * ��������
	 * 
	 * @return
	 */
	public boolean moveDown() {
		boolean result = false;

		if (assertValid(turnState, posX, posY - 1, posZ)) {
			posY--;
			result = true;
		}
		return result;
	}
	
	/**
	 * ���ݾ���ת�ĽǶȣ�������ȷ�Ķ����ϱ�����
	 * @param dir
	 */
	public void move(DIRECTION dir) {
		int offset = wellNode.getControl(RotateControl.class).getOffset();
		offset += dir.getValue();
		
		while (offset < 0) {
			offset += 4;
		}
		if (offset > 3) offset %= 4;
		
		switch (offset) {
		case 0: moveNorth();break;
		case 1: moveWest();break;
		case 2: moveSouth();break;
		case 3: moveEast();break;
		}
		
	}
	/**
	 * �������ƶ�
	 */
	public void moveNorth() {
		if (assertValid(turnState, posX, posY, posZ-1)) {
			if (!moveControl.isMoving()) {
				posZ--;
				moveControl.move(DIRECTION.NORTH);
			}
		}
	}
	/**
	 * ���������ƶ�
	 */
	public void moveSouth() {
		if (assertValid(turnState, posX, posY, posZ+1)) {
			if (!moveControl.isMoving()) {
				posZ++;
				moveControl.move(DIRECTION.SOUTH);
			}
		}
	}
	/**
	 * ���������ƶ�
	 */
	public void moveWest() {
		if (assertValid(turnState, posX - 1, posY, posZ)) {
			if (!moveControl.isMoving()) {
				posX--;
				moveControl.move(DIRECTION.WEST);
			}
		}
	}

	/**
	 * ʵ�ֿ������
	 */
	public void moveEast() {
		if (assertValid(turnState, posX + 1, posY, posZ)) {
			if (!moveControl.isMoving()) {
				posX++;
				moveControl.move(DIRECTION.EAST);
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
	 */
	private void updateControl() {
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					shape[i][j] = blockType + 1;
				} else {
					shape[i][j] = 0;
				}
				k = k >> 1;
			}
		}
		
		controlChanged = true;
	}
	
	/**
	 * ˢ��Ԥ���ڵ�
	 */
	private void updatePreview() {
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[nextBlockType][nextTurnState] & k) != 0) {
					previewShape[i][j] = nextBlockType + 1;
				} else {
					previewShape[i][j] = 0;
				}
				k = k >> 1;
			}
		}
		
		previewChanged = true;
	}
	
	/**
	 * ��λ�ܿؽڵ��λ��
	 */
	private void resetControlNode() {
		controlNode.setLocalTranslation(new Vector3f(0, SIDE_Y - 1, 0));
		controlNode.setLocalRotation(new Quaternion());
	}

	/**
	 * ��ʾ�ο�����ϵ
	 */
	public void showAxis() {
		if (wellNode.hasChild(axisNode)) {
			wellNode.detachChild(axisNode);
		} else {
			wellNode.attachChild(axisNode);
		}
	}
	/**
	 * �л���Ϸ����ͣ/����״̬
	 */
	public void pause() {
		if (isEnabled()) {
			setEnabled(false);
		} else {
			setEnabled(true);
		}
	}

}
