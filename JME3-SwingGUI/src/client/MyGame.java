package client;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * JME3Ӧ������
 * 
 * @author yanmaoyuan
 * 
 */
public class MyGame extends SimpleApplication {

	public static void main(String[] args) {
		MyGame game = new MyGame();
		game.start();
	}

	/**
	 * ���췽��������SimpleApplication�Ĺ��췽������ʼ��������Ҫ��AppStates��
	 */
	public MyGame() {
		super(new StatsAppState(), new DebugKeysAppState());
	}

	@Override
	public void simpleInitApp() {
		assetManager.registerLocator("/", FileLocator.class);
	}

	/**
	 * ��ָ��λ�ô���һ����ɫ������
	 * Create a red box at specified location
	 * @param loc
	 */
	public void createBox(Vector3f loc) {
		Box b = new Box(1, 1, 1);

		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");

		mat.setColor("Color", ColorRGBA.Red);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		geom.move(loc);
	}
	
	/**
	 * ����ģ��
	 * load model
	 * @param path
	 */
	public void loadModel(String path) {
		Spatial model = assetManager.loadModel(path);
		rootNode.attachChild(model);
	}
}
