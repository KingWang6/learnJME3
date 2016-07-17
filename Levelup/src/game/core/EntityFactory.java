package game.core;

import game.components.AoI;
import game.components.CollisionShape;
import game.components.Decay;
import game.components.Model;
import game.components.Position;
import game.components.SpawnPoint;
import game.components.Velocity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

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
	
	public EntityId createPlayer(float x, float z) {
		EntityId player = ed.createEntity();
		ed.setComponents(player,
				new Model(Model.PLAYER, ColorRGBA.Green),
				new Position(new Vector3f(540, 0, 360), null),
				new CollisionShape(10));
		
		log.info("�������ʵ��:" + x + ", " + z);
		return player;
	}

	public EntityId createBad(float x, float z) {
		EntityId id = ed.createEntity();
		ed.setComponents(id,
				new Model(Model.BAD, ColorRGBA.randomColor()),
				new Position(new Vector3f(x, 0, z), null),
				new Velocity(randomDirection().mult(15)),
				new AoI(100),
				new CollisionShape(1+FastMath.rand.nextFloat()*20));
		
		log.info("��������ʵ��:" + x + ", " + z);
		return id;
	}
	
	public void createTarget(float x, float z) {
		EntityId target = ed.createEntity();
		ed.setComponents(target,
				new Model(Model.TARGET, ColorRGBA.Blue),
				new Position(new Vector3f(x, 0, z), null),
				new Decay(100));// Ŀ�������Ļ�ϳ���100���룬Ȼ����ʧ��
		
		log.info("����һ��Ŀ��ʵ��:" + x + ", " + z);
	}
	
	public EntityId createSpawnPoint(float x, float z) {
		EntityId respawn = ed.createEntity();
		ed.setComponents(respawn, 
				new Model(Model.RESPAWN_POINT, ColorRGBA.DarkGray),
				new Position(new Vector3f(x, 0, z), null),
				new SpawnPoint(10), 
				new AoI(250));
		
		log.info("����һ��ˢ�ֵ�:" + x + ", " + z);
		
		return respawn;
	}
	
	/**
	 * ���һ���������ĳ��ٶ�
	 * @return
	 */
	private Vector3f randomDirection() {
		float theta = FastMath.rand.nextFloat() * FastMath.TWO_PI;
		float x = FastMath.sin(theta);
		float z = FastMath.cos(theta);
		Vector3f dir = new Vector3f(x, 0, z);
		dir.normalizeLocal();
		return dir;
	}

}
