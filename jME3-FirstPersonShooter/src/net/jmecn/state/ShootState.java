package net.jmecn.state;

import java.util.Set;

import net.jmecn.components.Decay;
import net.jmecn.components.Model;
import net.jmecn.components.Position;
import net.jmecn.components.Shoot;
import net.jmecn.components.Shootable;

import org.apache.log4j.Logger;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

public class ShootState extends BaseAppState {
	
	static Logger log = Logger.getLogger(ShootState.class);
	
	// ʵ��ϵͳ
	private EntityData ed;
	private EntitySet shootings;
	private EntitySet shootables;
	
	ModelState visual;
	@Override
	protected void initialize(Application app) {
		ed = getStateManager().getState(EntityDataState.class).getEntityData();
		visual = getStateManager().getState(ModelState.class);
		
		shootings = ed.getEntities(Shoot.class);
		shootables = ed.getEntities(Model.class, Shootable.class, Position.class);
	}
	
	@Override
	protected void cleanup(Application app) {
		shootings.release();
		shootings = null;
		
		shootables.release();
		shootables = null;
	}

	@Override
	protected void onEnable() {
	}

	@Override
	protected void onDisable() {
	}

	@Override
	public void update(float tpf) {
		// �������
		shootables.applyChanges();
		
		if (shootings.applyChanges()) {
			shoot(shootings.getAddedEntities());
			shoot(shootings.getChangedEntities());
		}
	}
	
	private void shoot(Set<Entity> entities) {
		for(Entity e : entities) {
			Shoot shoot = e.get(Shoot.class);
			
			// ���߼��
			CollisionResults results = new CollisionResults();
			CollisionResult closest = null;
			Ray ray = new Ray(shoot.getLocation(), shoot.getDirection());
			
			EntityId closestId = null;// �����е�Entity
			float distance = Float.MAX_VALUE;// ���о���
			
			for(Entity shootable : shootables) {
				Spatial model = visual.getModel(shootable.getId());
				if (model == null) continue;
				
				model.collideWith(ray, results);
				closest = results.getClosestCollision();
				if (closest != null) {
					// �ҵ������һ��ʵ��
					if (closest.getDistance() < distance) {
						distance = closest.getDistance();
						closestId = shootable.getId();
					}
				}
			}
			
			// �����е�Ŀ��
			if (closestId != null) {
				Quaternion rotation = new Quaternion();
				rotation.lookAt(shoot.getDirection(), Vector3f.UNIT_Y);
				
				// ���۱��
				EntityId bulletMark = ed.createEntity();
				ed.setComponents(bulletMark,
						new Position(closest.getContactPoint(), rotation),
						new Model(Model.BULLET),
						new Decay(10000));
				
				// �жϱ����е���������
				Model model = ed.getComponent(closestId, Model.class);
				
				if (model.getName().equals(Model.BOMB)) {
					ed.setComponent(closestId, new Decay(0));
					//ed.removeEntity(closestId);
				}
			}
			
		}
	}

}
