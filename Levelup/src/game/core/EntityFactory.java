package game.core;

import game.components.Decay;
import game.components.Model;
import game.components.Position;
import game.components.Velocity;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public void createPlayer(int x, int z) {
		EntityId player = ed.createEntity();
		ed.setComponents(player,
				new Model(Model.PLAYER, Color.GREEN),
				new Position(new Vector3f(x, 0, z), null),
				new Velocity(randomDirection().mult(60)));
		
		log.info("�������ʵ��:" + x + ", " + z);
	}

	public void createBad(int x, int z) {
		EntityId player = ed.createEntity();
		ed.setComponents(player,
				new Model(Model.BAD, Color.RED),
				new Position(new Vector3f(x, 0, z), null),
				new Velocity(randomDirection().mult(60)));
		
		log.info("��������ʵ��:" + x + ", " + z);
	}
	
	public void createTarget(int x, int z) {
		
		EntityId target = ed.createEntity();
		ed.setComponents(target,
				new Model(Model.TARGET, Color.BLUE),
				new Position(new Vector3f(x, 0, z), null),
				new Decay(12000));// Ŀ�������Ļ�ϳ���12�룬Ȼ����ʧ��
		
		log.info("����һ��Ŀ��ʵ��:" + x + ", " + z);
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
