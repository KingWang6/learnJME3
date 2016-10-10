package net.jmecn.state;

import strongdk.jme.appstate.console.CommandEvent;
import strongdk.jme.appstate.console.CommandListener;
import strongdk.jme.appstate.console.ConsoleAppState;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.light.DirectionalLight;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.event.MouseAppState;
import com.simsilica.lemur.style.BaseStyles;

/**
 * ������
 * @author yanmaoyuan
 *
 */
public class MainAppState extends AbstractAppState {

	private SimpleApplication simpleApp;
	private ConsoleAppState console;
	
	public final static String START_GAME = "start";
	public final static String QUIT_GAME = "quit";
	
	private Node guiNode = new Node("mainGui");
	private Node rootNode = new Node("mainScene");
	private Spatial model;
	private DirectionalLight sun;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		simpleApp = (SimpleApplication)app;
		
		simpleApp.getGuiNode().attachChild(guiNode);
		simpleApp.getRootNode().attachChild(rootNode);
		
		// ��������ı������һ��3D����
		model = simpleApp.getAssetManager().loadModel("Models/Terrain/iceworld.blend");
		rootNode.attachChild(model);
		
		sun = new DirectionalLight();
		sun.setDirection(new Vector3f(-5, -6, 4).normalize());
		rootNode.addLight(sun);
		
		// ��ʼ������̨
		console = stateManager.getState(ConsoleAppState.class);
		if (console != null) {
			console.registerCommand(START_GAME, commandListener);
			console.registerCommand(QUIT_GAME, commandListener);
		}
		
		// �������
		MouseAppState mouseAppState = stateManager.getState(MouseAppState.class);
    	if (mouseAppState != null) {
    		mouseAppState.setEnabled(true);
    	}
		
    	// ��ʼ�������λ��
    	Camera cam = simpleApp.getCamera();
    	cam.setLocation(new Vector3f(30, 50, 30));
		cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
		simpleApp.getFlyByCamera().setEnabled(false);
		
	    // ��ʼ��Lemur GUI
		GuiGlobals.initialize(app);
		
		initGUI();
		
	}
	
	@Override
	public void update(float tpf) {
		// �ó���������ת
		model.rotate(0, tpf * FastMath.DEG_TO_RAD, 0);
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
		simpleApp.getStateManager().detach(MainAppState.this);
    	simpleApp.getStateManager().attach(new InGameAppState());
	}
	
	/**
	 * �˳���Ϸ
	 */
	private void quitGame() {
		simpleApp.stop();
	}

	@SuppressWarnings("unchecked")
	private void initGUI() {
		// Load the 'glass' style
		BaseStyles.loadStyleResources("Interface/Style/style.groovy");
		
		// Set 'glass' as the default style when not specified
		GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
		
		BitmapFont font = simpleApp.getAssetManager().loadFont("Interface/Font/font.fnt");
		GuiGlobals.getInstance().getStyles().setDefault(font);
		
		// ����һ������
		Container myWindow = new Container();
		guiNode.attachChild(myWindow);
		
		// Put it somewhere that we will see it.
		// Note: Lemur GUI elements grow down from the upper left corner.
		myWindow.setLocalTranslation(450, 440, 0);

		// ����
		myWindow.addChild(new Label("�˵�"));
		
		// ��ʼ��Ϸ
		Button startBtn = myWindow.addChild(new Button("��ʼ��Ϸ"));
		startBtn.addClickCommands(new Command<Button>() {
		        @Override
		        public void execute(Button source) {
		        	startGame();
		        }
		    });
		
		// �˳���Ϸ
		Button quitBtn = myWindow.addChild(new Button("�˳���Ϸ"));
		quitBtn.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				quitGame();
			}
		});
		
	}
	
	@Override
	public void stateDetached(AppStateManager stateManager) {
		// �������
		MouseAppState mouseAppState = stateManager.getState(MouseAppState.class);
    	if (mouseAppState != null) {
    		mouseAppState.setEnabled(false);
    	}
		
    	// ����̨
    	if (console != null) {
    		console.appendConsole("MainAppState detached");
    		console.unregisterCommands(commandListener);
    	}
    
    	// �Ƴ��ƹ�
    	rootNode.removeLight(sun);
    	sun = null;
    	
    	// �Ƴ�3D����
    	rootNode.detachChild(model);
    	model = null;

    	// �Ƴ�GUIԪ��
		guiNode.detachAllChildren();
		
		simpleApp.getGuiNode().detachChild(guiNode);
		simpleApp.getRootNode().detachChild(rootNode);
		simpleApp.getFlyByCamera().setEnabled(true);
	}
	
}
