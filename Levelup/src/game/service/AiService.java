package game.service;

import game.components.AoI;
import game.components.CollisionShape;
import game.components.CoolDown;
import game.components.Damage;
import game.components.Model;
import game.components.Position;
import game.components.Velocity;
import game.core.Game;
import game.core.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;

public class AiService implements Service {

	private Logger log = LoggerFactory.getLogger(AiService.class);
	
	private EntitySet entities;// ����
	private EntitySet players;// ���
	private EntityData ed;

	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		
		entities = ed.getEntities(Filters.fieldEquals(Model.class, "name",
				Model.BAD), Model.class, Position.class, AoI.class, CollisionShape.class);
		
		players = ed.getEntities(Filters.fieldEquals(Model.class, "name",
				Model.PLAYER), Model.class, Position.class, CollisionShape.class);
	}

	@Override
	public void update(long time) {
		entities.applyChanges();
		players.applyChanges();
		
		for (Entity e : entities) {
			Vector3f loc = e.get(Position.class).getLocation();
			float aoiDist = e.get(AoI.class).getRadiusSquare();

			// Ѱ�����Լ�������������
			float minDist = Float.MAX_VALUE;
			Entity target = null;
			for(Entity p : players) {
				Vector3f pLoc = p.get(Position.class).getLocation();
				float dist = loc.distanceSquared(pLoc);
				if (dist > aoiDist) continue;

				// ��¼������������
				if (dist < minDist) {
					minDist = dist;
					target = p;
				}
			}
			
			// û���ҵ�����ȽϽ�����ң�����ԭ״��
			if (target != null) {
				
				// �ж��Ƿ��ڹ���������
				if (minDist >= 100) {
					// �����ƶ��ٶ�
					Vector3f v = target.get(Position.class).getLocation().subtract(loc);
					v.normalizeLocal().multLocal(20);
					e.set(new Velocity(v));
				} else {
					// ����׷����ң���ʼ����
					ed.removeComponent(e.getId(), Velocity.class);
					
					// ���㹥����ȴ
					CoolDown cd = ed.getComponent(e.getId(), CoolDown.class);
					if (cd == null || cd.getPercent() >= 1.0) {
						
						// TODO ����Ӧ��Ҫ���㹥�����˺�ֵ
						int delta = 1 + FastMath.rand.nextInt(5);
						
						target.set(new Damage(delta, e.getId()));
						e.set(new CoolDown(1000));
					}
				}
			}
		}
	}

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;

		players.release();
		players = null;
	}

}
