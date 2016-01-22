package teris.game;

import teris.game.states.InputStates;
import teris.game.states.LogicStates;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

	public Main() {
		super( new StatsAppState(), new DebugKeysAppState(),
			new LogicStates(),// �߼�
			new InputStates(),// ����
			new ScreenshotAppState("")// ��ͼ
		);
	}
	@Override
	public void simpleInitApp() {
		// �������˹������Ϸ����ҪFlyCamera��������Ƴ�������
		FlyCamAppState fcs = stateManager.getState(FlyCamAppState.class);
		if (fcs != null) stateManager.detach(fcs);
	}
	
	public static void main(String[] args) {
		Main app = new Main();
		
		AppSettings settings = new AppSettings(true);
		settings.setTitle("3D Teris");
		settings.setResolution(480, 640);
		
		app.setSettings(settings);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false);
		
		app.start();
	}
}
