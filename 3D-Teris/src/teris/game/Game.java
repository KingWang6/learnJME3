package teris.game;

import teris.game.states.InputStates;
import teris.game.states.LogicStates;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.shadow.DirectionalLightShadowRenderer;

public class Game extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		
		viewPort.setBackgroundColor(new ColorRGBA(0.3f, 0.4f, 0.5f, 1));
		initGui();
		initCamera();
		initLight();
		
		// �������˹������Ϸ����ҪFlyCamera��������Ƴ�������
		FlyCamAppState fcs = stateManager.getState(FlyCamAppState.class);
		if (fcs != null) stateManager.detach(fcs);
		
		// ��Ҳ����Ҫ��ʾ������Ϣ
		StatsAppState sas = stateManager.getState(StatsAppState.class);
		if (sas != null) stateManager.detach(sas);
		
		// DebugҲ�Ƴ���
		DebugKeysAppState dkas = stateManager.getState(DebugKeysAppState.class);
		if (dkas != null) stateManager.detach(dkas);
		
		// TODO ����������
		// ��û����
		
		// ������Ϸ�����߼�
		LogicStates logic = new LogicStates();
		// �Ȳ��������ȳ�ʼ����������������
		logic.setEnabled(false);
		stateManager.attach(logic);
		
		// �����������
		InputStates input = new InputStates();
		stateManager.attach(input);
	}
	
	@Override
	public void simpleUpdate(float tpf) {

	}
	
	private void initGui() {
		String txtB = "KeyPress:\n[A][W][S][D]: move cubes.\n[E][C]: rotate cubes.\n[Q][Z]: rotate camera.\n[P]: pause.\n[F2]: turn on/off axis.";
		BitmapText txt;
		BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
		txt = new BitmapText(fnt, false);
		txt.setBox(new Rectangle(0, 0, settings.getWidth(), settings.getHeight()));
		txt.setText(txtB);
		txt.setLocalTranslation(0, txt.getHeight(), 0);
		guiNode.attachChild(txt);

	}

	private void initCamera() {
		cam.setLocation(new Vector3f(10, 25, 10));
		cam.lookAt(new Vector3f(3, 13, 3), cam.getUp());
		this.flyCam.setEnabled(false);
	}
	
	/**
	 * Initialize the light
	 */
	private void initLight() {
		// Sun
		DirectionalLight sun = new DirectionalLight();
		ColorRGBA color = new ColorRGBA(0.5f, 0.5f, 0.5f, 1f);
		sun.setColor(color);
		sun.setDirection(new Vector3f(0, -1f, 0).normalizeLocal());
		rootNode.addLight(sun);
		
		/* Drop shadows */
		DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 4);
		dlsr.setLight(sun);
		viewPort.addProcessor(dlsr);
		rootNode.setShadowMode(ShadowMode.CastAndReceive);
	}
	
	public static void main(String[] args) {
		Game app = new Game();
		app.start();
	}

}
