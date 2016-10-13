package net.jmecn.state;

import strongdk.jme.appstate.console.CommandEvent;
import strongdk.jme.appstate.console.CommandListener;
import strongdk.jme.appstate.console.ConsoleAppState;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Action;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.event.MouseAppState;

/**
 * ������
 * 
 * @author yanmaoyuan
 * 
 */
public class MainAppState extends BaseAppState {

	private SimpleApplication simpleApp;
	private ConsoleAppState console;

	public final static String START_GAME = "start";
	public final static String QUIT_GAME = "quit";

	// 3D����
	private Node rootNode = new Node("mainScene");
	private Spatial model;

	// GUI
	private Node guiNode = new Node("mainGui");

	private Container menu;

	private TextField userField;
	private TextField hostField;
	private TextField portField;

	@Override
	public void initialize(Application app) {

		simpleApp = (SimpleApplication) app;
		console = simpleApp.getStateManager().getState(ConsoleAppState.class);

		init3DScene();

		initGUI();
	}

	@Override
	public void update(float tpf) {
		// �ó���������ת
		model.rotate(0, tpf * FastMath.DEG_TO_RAD, 0);
	}

	@Override
	protected void cleanup(Application app) {
	}

	@Override
	protected void onEnable() {
		simpleApp.getGuiNode().attachChild(guiNode);
		simpleApp.getRootNode().attachChild(rootNode);

		// �������
		MouseAppState mouseAppState = getStateManager().getState(
				MouseAppState.class);
		if (mouseAppState != null) {
			mouseAppState.setEnabled(true);
		}

		// ���ÿ���̨
		if (console != null) {
			console.registerCommand(START_GAME, commandListener);
			console.registerCommand(QUIT_GAME, commandListener);
		}

		// ����FlyCamera
		simpleApp.getFlyByCamera().setEnabled(false);
		
		Camera cam = simpleApp.getCamera();
		cam.setLocation(new Vector3f(200, 400, 200));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		
		simpleApp.getViewPort().setBackgroundColor(ColorRGBA.Black);
	}

	@Override
	protected void onDisable() {
		guiNode.removeFromParent();
		rootNode.removeFromParent();

		// �������
		MouseAppState mouseAppState = getStateManager().getState(
				MouseAppState.class);
		if (mouseAppState != null) {
			mouseAppState.setEnabled(false);
		}

		// ���ÿ���̨
		if (console != null) {
			console.appendConsole("MainAppState detached");
			console.unregisterCommands(commandListener);
		}

		simpleApp.getFlyByCamera().setEnabled(true);
	}

	/**
	 * ��ʼ��3D����
	 */
	private void init3DScene() {
		// ��������ı������һ��3D����
		model = simpleApp.getAssetManager().loadModel(
				"Models/Terrain/iceworld.j3o");
		rootNode.attachChild(model);
	}

	/**
	 * ��ʼ��GUI
	 */
	private void initGUI() {
		// ����һ������
		menu = new Container();
		guiNode.attachChild(menu);

		// Put it somewhere that we will see it.
		// Note: Lemur GUI elements grow down from the upper left corner.
		menu.setLocalTranslation(450, 440, 0);

		// ����
		Label title = new Label("�˵�");

		// ��ʼ��Ϸ
		ActionButton single = new ActionButton(new Action("������Ϸ") {
			@Override
			public void execute(Button source) {
				startGame();
			}
		});

		ActionButton multi = new ActionButton(new Action("������") {
			@Override
			public void execute(Button source) {
				// �򿪾���������
			}
		});

		// �˳���Ϸ
		ActionButton quit = new ActionButton(new Action("�˳���Ϸ") {
			@Override
			public void execute(Button source) {
				quitGame();
			}
		});
		
		menu.addChild(title);
		menu.addChild(single);
		menu.addChild(multi);
		menu.addChild(quit);

		// �����������λ�ã����˵����С�
		Camera cam = simpleApp.getCamera();
		float menuScale = cam.getHeight()/600f;

        Vector3f pref = menu.getPreferredSize();
        float bias = (cam.getHeight() - (pref.y*menuScale)) * 0.1f;
        menu.setLocalTranslation(cam.getWidth() * 0.5f - pref.x * 0.5f * menuScale,
                                 cam.getHeight() * 0.5f + pref.y * 0.5f * menuScale + bias,
                                 10);
        menu.setLocalScale(menuScale);
        
	}

	// ������ָ�������
	private CommandListener commandListener = new CommandListener() {
		@Override
		public void execute(CommandEvent evt) {
			String command = evt.getCommand();
			if (START_GAME.equalsIgnoreCase(command)) {
				startGame();
			} else if (QUIT_GAME.equalsIgnoreCase(command)) {
				quitGame();
			}
		}
	};

	/**
	 * ��ʼ��Ϸ
	 */
	private void startGame() {
		setEnabled(false);
		
		simpleApp.getStateManager().attach(new SingleGameState());
	}

	/**
	 * �˳���Ϸ
	 */
	private void quitGame() {
		simpleApp.stop();
	}
}
