package yan.mazegame.states;

import yan.mazegame.TestMazeCreator;
import yan.mazegame.logic.BlockCreator;
import yan.mazegame.logic.MazeCreator;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;

public class TestMazeState extends AbstractAppState {

	private TestMazeCreator game;
	private Node rootNode;

	private AssetManager assetManager;
	private Camera cam;

	private Node maze;

	/* �Թ����� */
	private int col = 14; // ��
	private int row = 15; // ��
	private long seed = 47;// ����
	private boolean isRand = true;// �Ƿ����
	private int roadSize = 4;// ��·���

	private boolean mazeChanged = false;

	// �Թ�������
	private MazeCreator mc = new MazeCreator(col, row, seed, isRand);
	// ����������
	private BlockCreator bc = new BlockCreator(roadSize);

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		if (game == null) {
			game = (TestMazeCreator) app;
			rootNode = game.getRootNode();
		}
		assetManager = app.getAssetManager();
		cam = app.getCamera();

		maze = new Node("maze");
		rootNode.attachChild(maze);
		initMaze();

		initLight();

		// load sky
		rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
		
		cam.setLocation(new Vector3f(roadSize/2, 3, roadSize/2+1));
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);

		if (mazeChanged) {
			int[][] map = bc.getMap();
			int row = bc.getBlockRow();
			int col = bc.getBlockCol();

			// ���������ƫ����
			Vector3f postion = new Vector3f();
			Vector3f offset = new Vector3f(0f, 0.5f, 0);

			maze.detachAllChildren();

			Node wall = new Node("wall");
			maze.attachChild(wall);

			Box mesh = new Box(0.5f, 0.5f, 0.5f);
			Geometry geom = new Geometry("box", mesh);
			geom.setMaterial(getBrickWall());

			for (int y = 0; y < row; y++) {
				for (int x = 0; x < col; x++) {

					if (map[y][x] > 0) {
						// ����ʵ������
						postion.set(offset.add(x, 0, y));
						Geometry brick = geom.clone();
						brick.setLocalTranslation(postion);
						wall.attachChild(brick);
					}
				}
			}

			maze.attachChild(wall.clone().move(0, 1, 0));
			maze.attachChild(wall.clone().move(0, 2, 0));

		   // �����ذ�
			Material mat2 = assetManager.loadMaterial("Textures/Terrain/BrickWall/BrickWall.j3m");
			mat2.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
			mat2.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);
			mat2.getTextureParam("ParallaxMap").getTextureValue().setWrap(WrapMode.Repeat);
			Box floor = new Box(col/2+1, 0.1f, row/2+1);
			TangentBinormalGenerator.generate(floor);
			floor.scaleTextureCoordinates(new Vector2f(col/2, row/2));
			Geometry floorGeom = new Geometry("Floor", floor);
			floorGeom.setMaterial(mat2);
			floorGeom.setShadowMode(ShadowMode.Receive);
			floorGeom.move(col/2, -0.1f, row/2);
			maze.attachChild(floorGeom);

		    
			mazeChanged = false;
		}
	}

	private Material getBrickWall() {
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		Texture diff = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
		diff.setWrap(Texture.WrapMode.Repeat);
		Texture norm = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall_normal.jpg");
		norm.setWrap(Texture.WrapMode.Repeat);
		mat.setTexture("DiffuseMap", diff);
		mat.setTexture("NormalMap", norm);
		mat.setFloat("Shininess", 2.0f);
		
		return mat;
	}
	
	/**
	 * ��ʼ������
	 */
	boolean lightInitialzed = false;

	private void initLight() {
		if (!lightInitialzed) {
			/**
			 * ����һ����ֱ���µķ����Դ������⽫�������Ӱ����������Ԥ֪���������λ�á�
			 */
			DirectionalLight light = new DirectionalLight();
			ColorRGBA color = new ColorRGBA(1, 1, 1, 1f);
			light.setColor(color);
			light.setDirection(new Vector3f(3, -10, 4).normalizeLocal());
			rootNode.addLight(light);

			/**
			 * �����һ�������⣬����Ϸ������΢��һЩ��
			 */
			AmbientLight ambient = new AmbientLight();
			rootNode.addLight(ambient);

			lightInitialzed = true;
		}
	}

	public void initMaze() {
		// ���ò���
		mc.config(row, col, seed, isRand);
		bc.setRoadSize(roadSize);

		// �����Թ�
		mc.create();

		// ���ɵ�ͼ
		bc.create(mc);

		mazeChanged = true;
	}
}
