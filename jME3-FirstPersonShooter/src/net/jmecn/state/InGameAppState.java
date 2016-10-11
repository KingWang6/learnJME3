package net.jmecn.state;

import net.jmecn.app.ModelFactory;
import net.jmecn.core.Model;
import net.jmecn.effects.DecayControl;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.ui.Picture;

/**
 * ��Ϸ�ڵĳ���
 * @author yanmaoyuan
 *
 */
public class InGameAppState extends AbstractAppState {

	private final static String LEFT = "left";
	private final static String RIGHT = "right";
	private final static String FORWARD = "forward";
	private final static String BACKWARD = "backward";
	private final static String JUMP = "jump";
	private final static String TRIGGER = "trigger";// ����۶����
	private final static String BOMB = "bomb";// �Ҽ�������
	private final static String ESC = "esc";// �˳���Ϸ

	private SimpleApplication simpleApp;
	private Camera cam;
	private ModelFactory modelFactory;

	// ��Ϸ�ڳ����Ľ��
	private Node rootNode = new Node("InGameScene");
	private Node guiNode = new Node("InGameGUI");
	private Node shootable = new Node("Shootable");

	// ��Ч
	private AudioNode audio_bomb;
	private AudioNode audio_ak47;
	private AudioNode audio_beep;

	// ����
	private BulletAppState bulletAppState;
	private CharacterControl player;
	private Node terrainModel;

	// �˶��߼�
	private boolean left = false, right = false, forward = false,
			backward = false, trigger = false, bomb = false;
	private Vector3f camLoc = new Vector3f();
	private Vector3f camDir = new Vector3f();
	private Vector3f camLeft = new Vector3f();
	private Vector3f walkDirection = new Vector3f();
	private float moveSpeed = 2f;
	
	// DEBUG
	private AxisAppState axisAppState = new AxisAppState();//�ο�����ϵ

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		// TODO for debug ���һ���ο�����ϵ������������ꡣ
		stateManager.attach(axisAppState);
		
		this.simpleApp = (SimpleApplication) app;
		this.modelFactory = new ModelFactory(simpleApp.getAssetManager());
		
		
		// ��ʼ�������λ��
		this.cam = simpleApp.getCamera();
		cam.lookAtDirection(new Vector3f(-1, 0, 0), Vector3f.UNIT_Y);

		// ��ʼ������
		simpleApp.getViewPort().setBackgroundColor(new ColorRGBA(0.75f, 0.875f, 1f, 1f));
		simpleApp.getRootNode().attachChild(rootNode);
		simpleApp.getGuiNode().attachChild(guiNode);
		rootNode.attachChild(shootable);

		// ���ص���
		terrainModel = modelFactory.getIceWorld();
		shootable.attachChild(terrainModel);

		// ���
		Spatial sky = modelFactory.getSky();
		rootNode.attachChild(sky);
		
		initAudio();
		initSunLight();
		initCrossHairs();
		initBullet();
		initInput();
	}

	/* gun shot sound is to be triggered by a mouse click. */
	private void initAudio() {
		audio_bomb = new AudioNode(simpleApp.getAssetManager(),
				"Sound/weapons/explode3.wav", DataType.Buffer);
		audio_bomb.setPositional(false);
		audio_bomb.setLooping(false);
		audio_bomb.setVolume(2);
		rootNode.attachChild(audio_bomb);

		audio_ak47 = new AudioNode(simpleApp.getAssetManager(),
				"Sound/weapons/ak47-1.wav", DataType.Buffer);
		audio_ak47.setPositional(false);
		audio_ak47.setLooping(false);
		audio_ak47.setVolume(3);
		rootNode.attachChild(audio_ak47);

		audio_beep = new AudioNode(simpleApp.getAssetManager(),
				"Sound/Effects/Beep.ogg", DataType.Buffer);
		audio_beep.setPositional(true);
		audio_beep.setLooping(false);
		audio_beep.setVolume(3);
		rootNode.attachChild(audio_beep);

	}

	/**
	 * ��ʼ������
	 */
	protected void initSunLight() {

		DirectionalLight dl = new DirectionalLight();
		dl.setDirection(new Vector3f(-3, -5, -3).normalize());
		dl.setColor(new ColorRGBA(.6f, .6f, .6f, 0.8f));
		rootNode.addLight(dl);

		dl = new DirectionalLight();
		dl.setDirection(new Vector3f(3, -5, 3).normalize());
		dl.setColor(new ColorRGBA(.4f, .4f, .4f, 0.2f));
		rootNode.addLight(dl);
	}

	/**
	 * ׼��
	 */
	protected void initCrossHairs() {
		Picture pic = new Picture("cross");
		pic.setImage(simpleApp.getAssetManager(), "Interface/Images/cross.png", true);
		pic.setWidth(1024);
		pic.setHeight(768);
		guiNode.attachChild(pic);
	}

	protected void initBullet() {
		bulletAppState = new BulletAppState();
		simpleApp.getStateManager().attach(bulletAppState);

		// ����
		RigidBodyControl rigidBodyControl = new RigidBodyControl(0);
		terrainModel.addControl(rigidBodyControl);

		// ���
		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(10f, 40f, 1);
		player = new CharacterControl(capsuleShape, 50f);
		player.setJumpSpeed(60);
		player.setFallSpeed(60);
		player.setGravity(98f);
		player.setPhysicsLocation(new Vector3f(200, 20, 80));

		bulletAppState.getPhysicsSpace().add(terrainModel);
		bulletAppState.getPhysicsSpace().add(player);
	}

	protected void initInput() {
		InputManager inputManager = simpleApp.getInputManager();

		inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping(BACKWARD, new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping(TRIGGER, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping(BOMB, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping(ESC, new KeyTrigger(KeyInput.KEY_ESCAPE));

		inputManager.addListener(myListener, LEFT, RIGHT, FORWARD, BACKWARD,
				JUMP, TRIGGER, BOMB, ESC);
	}

	/**
	 * �����¼�������
	 */
	private ActionListener myListener = new ActionListener() {
		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (name.equals(LEFT)) {
				left = isPressed;
			} else if (name.equals(RIGHT)) {
				right = isPressed;
			} else if (name.equals(FORWARD)) {
				forward = isPressed;
			} else if (name.equals(BACKWARD)) {
				backward = isPressed;
			} else if (name.equals(JUMP)) {
				player.jump();
			} else if (name.equals(TRIGGER)) {
				trigger = isPressed;
			} else if (name.equals(BOMB)) {
				bomb = isPressed;
			} else if (name.equals(ESC) && isPressed) {
				// �˳���Ϸ
				quitGame();
			}
		}
	};

	private float gunTime = 0f;
	public final static float GUN_COOLDOWN_TIME = 0.2f;// ǹ����ȴʱ��
	private float bombTime = 0f;
	public final static float BOMB_COOLDOWN_TIME = 1f;// ը����ȴʱ��

	@Override
	public void update(float tpf) {
		// ��������
		camDir.set(cam.getDirection()).multLocal(0.6f);
		camLeft.set(cam.getLeft()).multLocal(0.4f);
		walkDirection.set(0, 0, 0);
		if (left) {
			walkDirection.addLocal(camLeft);
		}
		if (right) {
			walkDirection.addLocal(camLeft.negate());
		}
		if (forward) {
			walkDirection.addLocal(camDir);
		}
		if (backward) {
			walkDirection.addLocal(camDir.negate());
		}
		walkDirection.y = 0;
		walkDirection.normalizeLocal().multLocal(moveSpeed);
		player.setWalkDirection(walkDirection);
		cam.setLocation(player.getPhysicsLocation());

		// ������ǹ
		gunTime += tpf;
		if (trigger) {
			if (gunTime >= GUN_COOLDOWN_TIME) {
				gunTime = 0f;
				shoot();
			}
		}

		bombTime += tpf;
		if (bomb) {
			if (bombTime >= BOMB_COOLDOWN_TIME) {
				bombTime = 0;
				bomb();
			}
		}
	}

	/**
	 * ��ǹ
	 */
	private void shoot() {
		audio_ak47.playInstance();

		// ���߼��
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(cam.getLocation(), cam.getDirection());
		shootable.collideWith(ray, results);
		if (results.size() > 0) {
			CollisionResult closest = results.getClosestCollision();

			Quaternion rotation = new Quaternion();
			rotation.lookAt(cam.getDirection(), cam.getUp());
			// ���۱��
			Geometry mark = modelFactory.createCylinder(ColorRGBA.Yellow);
			mark.scale(0.2f);
			mark.setLocalTranslation(closest.getContactPoint());
			mark.setLocalRotation(rotation);
			
			rootNode.attachChild(mark);
			
			// ����Ŀ����������ж��Ƿ���ը��
			Geometry target = closest.getGeometry();
			String name = target.getName();
			if (name.equals("BombGeom1")) {
				// ˲�䱬ը
				Node parent = target.getParent().getParent().getParent();
				DecayControl control = parent.getControl(DecayControl.class);
				control.explosionNow();
			}
		}
	}

	/**
	 * ����
	 */
	private void bomb() {
		camLoc.set(cam.getLocation());
		camLoc.addLocal(cam.getDirection().mult(10));

		// ����ը��ģ��
		Spatial bomb = modelFactory.create(Model.BOMB);
		
		// ��������5�����ʧ��Ȼ��ըBOOM!!
		DecayControl decayContorl = new DecayControl(audio_bomb, simpleApp);
		bomb.addControl(decayContorl);
		
		shootable.attachChild(bomb);
		bomb.setLocalTranslation(camLoc);
		RigidBodyControl ball_phy = new RigidBodyControl(0.5f);
		bomb.addControl(ball_phy);
		bulletAppState.getPhysicsSpace().add(ball_phy);
		ball_phy.setLinearVelocity(cam.getDirection().mult(100).add(walkDirection));
		ball_phy.setGravity(new Vector3f(0, -98f, 0));
		
	}

	/**
	 * �˳���Ϸ
	 */
	private void quitGame() {
		simpleApp.getStateManager().detach(InGameAppState.this);
		simpleApp.getStateManager().attach(new MainAppState());
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		// �˳���ϷʱҪ��������
		simpleApp.getRootNode().detachChild(rootNode);
		simpleApp.getGuiNode().detachChild(guiNode);
		simpleApp.getViewPort().setBackgroundColor(ColorRGBA.Black);

		// �������ϵ
		stateManager.detach(axisAppState);
		
		// �����������
		bulletAppState.getPhysicsSpace().removeAll(rootNode);
		bulletAppState.getPhysicsSpace().remove(player);
		stateManager.detach(bulletAppState);
		
		// �����������
		InputManager inputManager = simpleApp.getInputManager();
		inputManager.removeListener(myListener);
		
	}

}
