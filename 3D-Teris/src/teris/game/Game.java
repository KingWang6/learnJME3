package teris.game;

import teris.game.states.InputStates;
import teris.game.states.LogicStates;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.system.AppSettings;

public class Game extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		
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
	
	public static void main(String[] args) {
		Game app = new Game();
		AppSettings settings = new AppSettings(true);
		settings.setTitle("3D Teris");
		settings.setResolution(480, 640);
		settings.setFrequency(75);
		app.setSettings(settings);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false);
		
		app.start();
	}

}
