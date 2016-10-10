package net.jmecn.core;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

public class PlayerInputAppState extends AbstractAppState {

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
	private EntityData ed;
	private EntitySet player;
	
	// �˶��߼�
	private boolean left = false, right = false, forward = false,
			backward = false, trigger = false, bomb = false;
	private Vector3f camLoc = new Vector3f();
	private Vector3f camDir = new Vector3f();
	private Vector3f camLeft = new Vector3f();
	private Vector3f walkDirection = new Vector3f();
	private float moveSpeed = 2f;

	private float gunTime = 0f;
	public final static float GUN_COOLDOWN_TIME = 0.1f;// ǹ����ȴʱ��
	private float bombTime = 0f;
	public final static float BOMB_COOLDOWN_TIME = 1f;// ը����ȴʱ��
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {

		super.initialize(stateManager, app);
		this.simpleApp = (SimpleApplication) app;
		this.cam = app.getCamera();

		ed = this.simpleApp.getStateManager().getState(EntityDataState.class)
				.getEntityData();
		player = ed.getEntities(Player.class, Position.class);

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
				//player.jump();
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
		walkDirection.normalizeLocal();
		
		Movement movement = new Movement(walkDirection, moveSpeed);
		
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
		
		player.applyChanges();
		for (Entity e : player) {
			e.set(movement);
		}
	}
	
	/**
	 * ��ǹ
	 */
	private void shoot() {
		// ���߼��
	}

	/**
	 * ����
	 */
	private void bomb() {
		camLoc.set(cam.getLocation());
		camLoc.addLocal(cam.getDirection().mult(10));
		Vector3f linearVelocity = cam.getDirection().mult(100);
		Vector3f gravity = new Vector3f(0, -98f, 0);

		EntityId bomb = ed.createEntity();
		ed.setComponents(bomb, 
			new Model(Model.BOMB),
			new Collision(0.5f, linearVelocity, new Vector3f(0, 0, 0), gravity),
			new Position(camLoc));
	}

	@Override
	public void cleanup() {
		super.cleanup();
		player.release();
		player = null;
	}
	
	/**
	 * �˳���Ϸ
	 */
	private void quitGame() {
		simpleApp.stop();
	}
}