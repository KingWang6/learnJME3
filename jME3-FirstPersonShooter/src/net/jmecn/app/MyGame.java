package net.jmecn.app;

import net.jmecn.state.MainAppState;
import strongdk.jme.appstate.console.ConsoleAppState;
import strongdk.jme.appstate.console.ConsoleDefaultCommandsAppState;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.system.AppSettings;

/**
 * ��Ϸ����
 * 
 * @author yanmaoyuan
 * 
 */
public class MyGame extends SimpleApplication {

	/**
	 * ���췽������ʼ����Ϸ��ʹ�õ�AppState���Լ�����������
	 */
	public MyGame() {
		// ��ʼ��AppState
		super(new DebugKeysAppState(), new FlyCamAppState(),
				new StatsAppState(), new ConsoleAppState(),
				new ConsoleDefaultCommandsAppState(), new MainAppState());

		// ��ʼ����Ϸ����
		AppSettings settings = new AppSettings(true);
		settings.setResolution(1024, 768);
		settings.setTitle("www.jmecn.net");

		setSettings(settings);
		setShowSettings(false);// ����ʾ���ô���
		setPauseOnLostFocus(false);// ����ʧȥ����ʱ����Ϸ����ͣ
	}

	@Override
	public void simpleInitApp() {
		// ɾ��ԭ�еİ�ESC�˳���Ϸ�Ĺ���
		inputManager.deleteMapping(INPUT_MAPPING_EXIT);
	}

	public static void main(String[] args) {
		MyGame game = new MyGame();
		game.start();
	}

}
