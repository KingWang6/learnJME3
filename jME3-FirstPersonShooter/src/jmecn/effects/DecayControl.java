package jmecn.effects;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 * һ��ʱ�����Զ���ʧ
 * 
 * @author yanmaoyuan
 * 
 */
public class DecayControl extends AbstractControl {

	private float totalTime = 5f;
	private float time = 0;

	private AudioNode boom;
	private Explosion explosion;
	private boolean used = false;
	
	private SimpleApplication simpleApp;
	
	public DecayControl(AudioNode boom, SimpleApplication simpleApp) {
		this.simpleApp = simpleApp;
		
		totalTime = 5f;
		time = 0f;
		this.boom = boom;
		
		explosion = new Explosion(simpleApp.getAssetManager(),
				simpleApp.getRenderManager());
	}

	@Override
	protected void controlUpdate(float tpf) {
		time += tpf;

		if (time >= totalTime && !used) {
			time = 0;
			// �Ӹ��ڵ����Ƴ�������
			explosionNow();
		}

	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {}

	public void explosionNow() {
		used = true;
		
		// �Ӹ��ڵ����Ƴ�������
		Node node = spatial.getParent();
		node.detachChild(spatial);
	
		// ע�⣡������������Ƴ�������ʱ����ײ��״��δ��BulletAppState���Ƴ���
		BulletAppState bulletApppState = simpleApp.getStateManager().getState(BulletAppState.class);
		if (bulletApppState != null) {
			bulletApppState.getPhysicsSpace().removeAll(spatial);
		}
		
		// ���ñ�ը��Ч
		Vector3f location = spatial.getLocalTranslation();
		spatial.removeControl(this);
	
		boom.playInstance();
		
		explosion.setLocalTranslation(location);
		node.attachChild(explosion);
	}

}
