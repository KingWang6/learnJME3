package game.core;

import game.components.AoI;
import game.components.CollisionShape;
import game.components.Decay;
import game.components.Exp;
import game.components.Health;
import game.components.Level;
import game.components.Mana;
import game.components.Model;
import game.components.Position;
import game.components.SpawnPoint;
import game.components.Stamina;
import game.components.Velocity;
import game.service.LevelService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.Name;

/**
 * ʵ�幤�������������������͵�ʵ��
 * @author yanmaoyuan
 *
 */
public class EntityFactory {
	private Logger log = LoggerFactory.getLogger(EntityFactory.class);
	
	private EntityData ed;
	
	public EntityFactory(EntityData ed) {
		this.ed = ed;
	}
	
	public EntityId createPlayer() {
		EntityId player = ed.createEntity();
		resetPlayer(player);
		
		log.info("�������");
		
		return player;
	}
	
	/**
	 * ��ʼ���������
	 */
	public void resetPlayer(EntityId player) {
		ed.setComponents(player,
				new Model(Model.PLAYER, ColorRGBA.Green),
				new Name("���"),
				new Position(new Vector3f(540, 0, 360), null),
				new CollisionShape(10),
				// RPG��ֵ
				new Level(1),
				new Health(100, 100),
				new Mana(50, 50),
				new Stamina(50, 50),
				new Exp(0, LevelService.expTable[0]));
	}
	
	/**
	 * �����ӵ�ʵ��
	 * @param location �ӵ��ĳ�ʼλ��
	 * @param velocity �˶��ٶ�
	 * @return
	 */
	public EntityId createBullet(Vector3f location, Vector3f velocity) {
		EntityId id = ed.createEntity();
		ed.setComponents(id,
				new Model(Model.BULLET, ColorRGBA.Black),
				new Position(location, null),
				new Velocity(velocity),
				new CollisionShape(2),
				new Decay(5000));
		
		return id;
	}
	
	/**
	 * ���������
	 */
	static String names[] = {"ʷ��ķ", "�粼��", "������"};

	/**
	 * ��������ʵ��
	 * @param x
	 * @param z
	 * @return
	 */
	public EntityId createBad(float x, float z) {
		EntityId id = ed.createEntity();
		float rand = FastMath.rand.nextFloat()*10;
		
		int n = FastMath.rand.nextInt(3);
		
		ed.setComponents(id,
				new Model(Model.BAD, ColorRGBA.randomColor()),
				new Position(new Vector3f(x, 0, z), null),
				new Name(names[n] + id.getId()),
				new AoI(100),
				new CollisionShape(10+FastMath.rand.nextFloat()*10),
				new Health(10+rand, 10+rand),
				new Exp(10+rand, 10+rand));
		
		return id;
	}
	
	public void createTarget(float x, float z) {
		EntityId target = ed.createEntity();
		ed.setComponents(target,
				new Model(Model.TARGET, ColorRGBA.Blue),
				new Position(new Vector3f(x, 0, z), null),
				new Decay(100));// Ŀ�������Ļ�ϳ���100���룬Ȼ����ʧ��
	}
	
	public EntityId createSpawnPoint(float x, float z) {
		EntityId respawn = ed.createEntity();
		ed.setComponents(respawn, 
				new Model(Model.RESPAWN_POINT, ColorRGBA.DarkGray),
				new Position(new Vector3f(x, 0, z), null),
				new SpawnPoint(10), 
				new AoI(250));
		
		return respawn;
	}
	
	/**
	 * ���һ���������ĳ��ٶ�
	 * @return
	 */
	protected Vector3f randomDirection() {
		float theta = FastMath.rand.nextFloat() * FastMath.TWO_PI;
		float x = FastMath.sin(theta);
		float z = FastMath.cos(theta);
		Vector3f dir = new Vector3f(x, 0, z);
		dir.normalizeLocal();
		return dir;
	}

}
