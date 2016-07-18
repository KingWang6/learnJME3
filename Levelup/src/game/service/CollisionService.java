package game.service;

import game.components.CollisionShape;
import game.components.Damage;
import game.components.Level;
import game.components.Model;
import game.components.Position;
import game.core.Game;
import game.core.Service;

import java.util.Set;

import com.jme3.util.SafeArrayList;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * ��ײ���
 * 
 * @author yanmaoyuan
 * 
 */
public class CollisionService implements Service {

	private Game game;
	private EntityData ed;
	private EntitySet entities;
	private SafeArrayList<Entity> colliders = new SafeArrayList<Entity>(Entity.class);

	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		entities = ed.getEntities(Model.class, Position.class, CollisionShape.class);

	}

	@Override
	public void update(long time) {
		if (entities.applyChanges()) {
			removeColliders(entities.getRemovedEntities());
			addColliders(entities.getAddedEntities());
		}

		Entity[] array = colliders.getArray();
		for (int i = 0; i < array.length; i++) {
			Entity e1 = array[i];
			for (int j = i + 1; j < array.length; j++) {
				Entity e2 = array[j];
				generateContacts(e1, e2);
			}
		}
	}

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;
	}

	protected void addColliders(Set<Entity> set) {
		colliders.addAll(set);
	}

	protected void removeColliders(Set<Entity> set) {
		colliders.removeAll(set);
	}

	protected void generateContacts(Entity e1, Entity e2) {
		// ͬһʵ�岻���
		if (e1 == e2)
			return;
		
		Model m1 = e1.get(Model.class);
		Model m2 = e2.get(Model.class);
		
		// e1�ǻ��� e2���ӵ�
		if (Model.BAD.equals(m1.getName()) && Model.BULLET.equals(m2.getName())) {
			Position p1 = e1.get(Position.class);
			Position p2 = e2.get(Position.class);
	
			CollisionShape s1 = e1.get(CollisionShape.class);
			float r1 = s1.getRadius();
			CollisionShape s2 = e2.get(CollisionShape.class);
			float r2 = s2.getRadius();
	
			float threshold = r1 + r2;
			threshold *= threshold;
	
			float distSq = p1.getLocation().distanceSquared(p2.getLocation());
			if (distSq > threshold) {
				return; // û�з�����ײ
			}

			// TODO ������ҵĹ����������˺�
			EntityId player = game.getService(SinglePlayerService.class).getPlayer();
			Level lv = ed.getComponent(player, Level.class);
			int lvl = lv.getLv();
			e1.set(new Damage(lvl*2, player));
			
			// �Ƴ�����ӵ�
			ed.removeEntity(e2.getId());
		}
	}

	protected void generateContacts() {

		Entity[] array = colliders.getArray();
		for (int i = 0; i < array.length; i++) {
			Entity e1 = array[i];
			for (int j = i + 1; j < array.length; j++) {
				Entity e2 = array[j];
				generateContacts(e1, e2);
			}
		}
	}
}
