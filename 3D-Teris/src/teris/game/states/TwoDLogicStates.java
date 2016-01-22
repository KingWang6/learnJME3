package teris.game.states;

import teris.game.DIRECTION;
import teris.game.Main;
import teris.game.control.MoveControl;
import teris.game.control.RotateControl;
import teris.game.logic.TwoDLogic;
import teris.game.scene.BoxGeometry;
import static teris.game.logic.TwoDLogic.SIDE_X;
import static teris.game.logic.TwoDLogic.SIDE_Y;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;
import com.jme3.shadow.DirectionalLightShadowRenderer;

/**
 * 2D����˹����
 * @author yanmaoyuan
 *
 */
public class TwoDLogicStates extends AbstractAppState {

	// ����
	private final static String MOVE_R = "move_right";// �������ƶ�
	private final static String MOVE_L = "move_left";// ���������ƶ�
	private final static String MOVE_DOWN = "move_down";// ����(����)�����ƶ�
	private final static String ROTATE_R = "rotate_right";// �ܿؽڵ�����
	private final static String ROTATE_L = "rotate_left";// �ܿؽڵ�����
	private final static String PAUSE = "pause";// ��Ϸ��ͣ

	private String[] keys = { MOVE_R, MOVE_L, MOVE_DOWN, ROTATE_R, ROTATE_L, PAUSE };
	
	private Main game;
	private AssetManager assetManager;
	private InputManager inputManager;
	
	private Node rootNode = new Node("logicRoot");
	private Node guiNode = new Node("logicGui");
	
	private Node wellNode;// ��Ϸ�еġ������ڵ�
	private Node controlNode;// �ܿؽڵ�
	private Node previewNode;// Ԥ���ڵ�
	
	private Node axisNode;// Axis
	
	private BitmapText uiText;
	
	private MoveControl moveControl;// ���ڿ��Ʒ����ƶ��Ŀ�����
	private RotateControl rotateControl;// ���ڿ��Ʒ�����ת�Ŀ�����
	
	private TwoDLogic logic;

	// ����ڵ�(��)�Ĳ���
	private BoxGeometry[][] wells = null;

	// �ܿؽڵ�Ĳ���
	private BoxGeometry[][] controls = null;
	
	// Ԥ���ڵ�Ĳ���
	private BoxGeometry[][] previews = null;

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		// 3D����˹����ĺ����߼���
		logic = new TwoDLogic();
		
		game = (Main) app;
		assetManager = game.getAssetManager();
		inputManager = game.getInputManager();
		
		// ��ʼ������ͼ
		game.getRootNode().attachChild(rootNode);
		rootNode.attachChild(getWellNode());
		rootNode.attachChild(getPreviewNode());
		
		game.getViewPort().setBackgroundColor(new ColorRGBA(0.3f, 0.4f, 0.5f, 1));
		
		// ��ʼ��GUI
		game.getGuiNode().attachChild(guiNode);
		initGui();
		
		// ��ʼ�������
		Camera cam = game.getCamera();
		cam.setLocation(new Vector3f(0, 25, 10));
		cam.lookAt(new Vector3f(0, 8, 0), cam.getUp());
		
		// ��ʼ���ƹ�
		initLight();
		
		// ��ʼ���������
		initKeys();
		
		// ��ʼ��Ϸ
		newGame();
	}
	
	/**
	 * ��ǰ�Ѿ�����ʱ��
	 */
	private float timeInSecond = 0f;
	
	/**
	 * ��Ϸ��ѭ��
	 */
	@Override
	public void update(float tpf) {
		if (!isEnabled()) {
			return;
		}

		timeInSecond += tpf;
		if (timeInSecond >= logic.getRate()) {
			timeInSecond -= logic.getRate();

			// ���濪ʼд�߼�
			
			if (!logic.moveDown()) {// ��������
				
				// ���ܿؽڵ���ӵ�����
				logic.addToWell();
				
				// ���ܿؽڵ㸴λ
				resetControlNode();
				
				logic.deleteFullLine();// ������������

				if (logic.isGameEnd()) {
					uiText.setText("Your Final Score: " + logic.getScore());
					setEnabled(false);
				} else {
					logic.createNewBlock();// �ı��ܿط���
					logic.getNextBlock();// �����µ�Ԥ������
					
					// ���·���
					uiText.setText("Score: " + logic.getScore() + "\nLevel: " + logic.getLevel());
				}
			} else {
				// ���������½�
				moveControl.move(DIRECTION.DOWN);
			}

			// ˢ�½���
			refresh();
		}
	}
	
	@Override
	public void cleanup() {
		// �Ƴ�����ͼ
		game.getRootNode().detachChild(rootNode);
		game.getGuiNode().detachChild(guiNode);
		game.getViewPort().setBackgroundColor(new ColorRGBA(0, 0, 0, 1));
		
		// ��հ���ӳ��
		inputManager.removeListener(listener);
		for (String key : keys) {
			inputManager.deleteMapping(key);
		}
		
		super.cleanup();
	}
	
	/**
	 * ��ʼ��GUI
	 */
	private void initGui() {
		BitmapFont fnt = game.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		String txtA = "Score: 0\nLevel: 0";
		uiText = new BitmapText(fnt, false);
		uiText.setText(txtA);
		uiText.setLocalTranslation(0, 640, 0);
		
		guiNode.attachChild(uiText);
	}

	/**
	 * ��ֹ��γ�ʼ�����ա�
	 */
	private boolean lightInitialzed = false;
	/**
	 * ��ʼ������
	 */
	private void initLight() {
		if (!lightInitialzed) {
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
			
			lightInitialzed = true;
		}
	}
	
	/**
	 * ��ʼ���������
	 */
	private void initKeys() {
		// ��ʼ������
		inputManager = game.getInputManager();
		inputManager.addMapping(MOVE_R, new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping(MOVE_L, new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addMapping(MOVE_DOWN, new KeyTrigger(KeyInput.KEY_X));
		inputManager.addMapping(ROTATE_R, new KeyTrigger(KeyInput.KEY_E));
		inputManager.addMapping(ROTATE_L, new KeyTrigger(KeyInput.KEY_C));
		inputManager.addMapping(PAUSE, new KeyTrigger(KeyInput.KEY_P));

		inputManager.addListener(listener, keys);
	}
	
	private ActionListener listener = new ActionListener() {

		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (isPressed) {
				switch (name) {
				case MOVE_R:
					move(DIRECTION.EAST);
					break;
				case MOVE_L:
					move(DIRECTION.WEST);
					break;
				case MOVE_DOWN:
					quickDown();
					break;
				case ROTATE_R:
					rotateRight();
					break;
				case ROTATE_L:
					rotateLeft();
					break;
				case PAUSE:
					/**
					 * �л���Ϸ����ͣ/����״̬
					 */
					if (isEnabled()) {
						setEnabled(false);
					} else {
						setEnabled(true);
					}
					break;
				default:
					break;
				}
			}
			
		}
		
	};
	
	/**
	 * ��á������ڵ㡣���ڵ㲻�����򴴽�����
	 * @return
	 */
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
			
			// ��ʼ��"��"�ڵ��еķ������ݽṹ
			if (wells == null) {
				wells = new BoxGeometry[SIDE_Y][SIDE_X];
				
				// ���������ƫ����
				Vector3f postion = new Vector3f();
				Vector3f offset = new Vector3f(-SIDE_X/2+0.5f, 0.5f, -SIDE_Y/2+0.5f);
				
				for(int y=0; y<SIDE_Y; y++) {
					for(int x=0; x<SIDE_X; x++) {
							// ����ʵ������
							postion.set(offset.add(x, y, 0));
							
							wells[y][x] = new BoxGeometry(assetManager, 0);
							wells[y][x].setLocalTranslation(postion);
					}
				}
			}
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
			
			// ��ʼ���ܿؽڵ��еķ������ݽṹ
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
			
			// ��ʼ��Ԥ���ڵ��еķ������ݽṹ
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
	 * ��λ�ܿؽڵ��λ��
	 */
	private void resetControlNode() {
		controlNode.setLocalTranslation(new Vector3f(0, SIDE_Y - 1, 0));
		controlNode.setLocalRotation(new Quaternion());
	}

	private void refresh() {
		// �������˸ı�
		if (logic.isMatrixChanged()) {
			logic.setMatrixChanged(false);
			
			for (int y = 0; y < SIDE_Y; y++) {
				for (int x = 0; x < SIDE_X; x++) {
					int index = logic.getMatrix(x, y);
					if (index > 0) {
						wells[y][x].setColor(index - 1);
						wellNode.attachChild(wells[y][x]);
					} else {
						wellNode.detachChild(wells[y][x]);
					}
				}
			}
		}
		
		// �ܿؽڵ㷢���˸ı�
		if (logic.isControlChanged()) {
			logic.setControlChanged(false);
			
			for(int y=0; y<4; y++) {
				for(int x=0; x<4; x++) {
					int index = logic.getControl(x, y);
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
		if (logic.isPreviewChanged()) {
			logic.setPreviewChanged(false);
			
			for(int y=0; y<4; y++) {
				for(int x=0; x<4; x++) {
					int index = logic.getPreview(x, y);
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
	 * ��ʼ����Ϸ
	 */
	public void newGame() {

		logic.newGame();
		
		
		// ��ʼ���ܿؽڵ��λ��
		resetControlNode();

		// ��ʼ���ܿط����λ��
		logic.createNewBlock();
		
		// ����Ԥ������
		logic.getNextBlock();
		
		// ������Ϸ
		setEnabled(true);
	}

	/**
	 * ����˳ʱ����ת
	 */
	private void rotateRight() {
		if (!rotateControl.isRotating() && logic.rotateRight()) {
			rotateControl.rotate(true);
		}
		// Ҳ����Լ�����ת��ǽ����wallkick
	}
	/**
	 * ������ʱ����ת
	 */
	private void rotateLeft() {
		if (!rotateControl.isRotating() && logic.rotateLeft()) {
			rotateControl.rotate(false);
		}
	}

	/**
	 * ��������
	 * @return
	 */
	private void quickDown() {
		while(logic.moveDown());
	}
	
	/**
	 * ƽ�Ʒ���
	 * 
	 * @param dir
	 */
	private void move(DIRECTION dir) {
		// ���������ƶ��У������ƶ�����
		if (moveControl.isMoving()) {
			return;
		}

		int direction = dir.getValue();
		
		// ��������˹��ܣ��ͻ���ݾ���ת�ĽǶȣ�������ȷ�Ķ����ϱ�����
		{
			direction += wellNode.getControl(RotateControl.class).getOffset();
	
			while (direction < 0) {
				direction += 4;
			}
			if (direction > 3) {
				direction %= 4;
			}
		}

		switch (direction) {
		case 0:
			break;
		case 1:
			if (logic.moveLeft()) {
				// ���������ƶ�
				moveControl.move(DIRECTION.WEST);
			}
			break;
		case 2:
			break;
		case 3:
			if (logic.moveRight()) {
				// �������ƶ�
				moveControl.move(DIRECTION.EAST);
			}
			break;
		}
	}
}
