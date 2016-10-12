package net.jmecn.app;

import net.jmecn.effects.DecayControl;
import net.jmecn.state.AxisAppState;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;

/**
 * �����õ�App�������������ײ����ʱ��Ϊʲô���뿪��ͼ��
 * ������ʹ�� jbullet����bullet-native�����׶���һ�����ʴ���ģ�͡������Ǵӷ�϶����ȥ�ˣ�
 * @author yanmaoyuan
 *
 */
public class TestModels extends SimpleApplication {
	
	private final static String TRIGGER = "trigger";// ����۶����
	
	// ����
	private BulletAppState bulletAppState;
	private CharacterControl player;
	private Node terrainModel;
	
	@Override
	public void simpleInitApp() {
		// ��ʼ��AppState
		stateManager.attach(new AxisAppState());
		
		// ���ص���
		terrainModel = (Node) assetManager.loadModel(
				"Models/Terrain/iceworld.blend");
		terrainModel.scale(10);
		rootNode.attachChild(terrainModel);

		terrainModel.breadthFirstTraversal(new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				// BlenderLoader�����ģ�ͷ���ȶ�̫���ˣ�����һ�㡣
				if (spatial instanceof Geometry) {
					Geometry geom = (Geometry) spatial;
					Material mat = geom.getMaterial();
					mat.setFloat("Shininess", 0);
				}
			}
		});

		// ���
		Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/sky.jpg", EnvMapType.SphereMap);
		rootNode.attachChild(sky);
		
		initCamera();
		initSunLight();
		initBullet();
		initInput();
		initAudio();
	}
	
	private void initInput() {
		/**
		 * �����¼�������
		 */
		ActionListener myListener = new ActionListener() {
			@Override
			public void onAction(String name, boolean isPressed, float tpf) {
				if (name.equals(TRIGGER) && isPressed) {
					bomb();
				}
			}
		};
		
		inputManager.addMapping(TRIGGER, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addListener(myListener, TRIGGER);
	}
	
	private AudioNode audio_gun;
	private void initAudio() {
		audio_gun = new AudioNode(assetManager,
				"Sound/Effects/Gun.wav", DataType.Buffer);
		audio_gun.setPositional(false);
		audio_gun.setLooping(false);
		audio_gun.setVolume(2);
		rootNode.attachChild(audio_gun);
	}
	/**
	 * ��ʼ�������
	 */
	private void initCamera() {
		cam.setLocation(new Vector3f(200, 200, 80));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		flyCam.setMoveSpeed(100);
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
	
	protected void initBullet() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);

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
		
		// ���������������״̬�����Կ�����ײ��λ������
		bulletAppState.setDebugEnabled(true);
	}

	/**
	 * ����
	 */
	private void bomb() {
		Vector3f loc = cam.getLocation();
		loc.addLocal(cam.getDirection().mult(3));

		// ����ը��ģ��
		Node bomb = (Node) assetManager.loadModel("Models/Bomb/bomb.blend");
		
		// ��������5�����ʧ��Ȼ��ըBOOM!!
		DecayControl decayContorl = new DecayControl(audio_gun, this);
		bomb.addControl(decayContorl);
		
		rootNode.attachChild(bomb);
		bomb.setLocalTranslation(loc);
		RigidBodyControl ball_phy = new RigidBodyControl(0.5f);
		bomb.addControl(ball_phy);
		bulletAppState.getPhysicsSpace().add(ball_phy);
		ball_phy.setLinearVelocity(cam.getDirection().mult(50));
		ball_phy.setGravity(new Vector3f(0, -98f, 0));
		
	}

	public static void main(String[] args) {

		TestModels app = new TestModels();
		app.start();
	}

}
