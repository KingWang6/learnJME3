package game.service;

import game.components.AoI;
import game.components.Model;
import game.components.Position;
import game.components.SpawnPoint;
import game.components.Velocity;
import game.components.BornPoint;
import game.core.Game;
import game.core.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;

/**
 * ˢ�ַ��񣬸���ˢ�����еĹ��
 * @author yanmaoyuan
 *
 */
public class SpawnService implements Service {

	private Logger log = LoggerFactory.getLogger(SpawnService.class);

	private Game game;
	private EntityData ed;
	private EntitySet spawnPoints;
	private EntitySet players;
	
	private EntitySet mobs;// ���еĹ���
	private EntitySet childMobs;// ����ĺ���
	private EntitySet movingMobs;// �˶��ŵ�����

	// ˢ�ֹ�ϵ
	private HashMap<EntityId, EntityId> mothers = new HashMap<EntityId, EntityId>();
		
	// ���ˢ������
	public final static int MAX_MOBS = 50;

	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		
		// ˢ�ֵ�
		spawnPoints = ed.getEntities(Position.class, SpawnPoint.class, AoI.class);
		
		players = ed.getEntities(
				Filters.fieldEquals(Model.class, "name", Model.PLAYER),
				Position.class, Model.class);

		mobs = ed.getEntities(
				Filters.fieldEquals(Model.class, "name", Model.BAD),
				Model.class);
		
		childMobs = ed.getEntities(BornPoint.class);
		movingMobs = ed.getEntities(BornPoint.class, Position.class, Velocity.class);
	}

	private int time = 0;

	@Override
	public void update(long timePerFrame) {

		if (childMobs.applyChanges()) {
			notifyDead(childMobs.getRemovedEntities());
		}
		
		if (movingMobs.applyChanges()) {
			goHome(movingMobs);
		}
		
		// ϵͳˢ����1��ʱ����
		time += timePerFrame;
		if (time >= 1000000000l) {
			time -= 1000000000;
			respawn();
		}

	}

	/**
	 * ����������Ҫ֪ͨˢ�µ�
	 * @param entities
	 */
	private void notifyDead(Set<Entity> entities) {
		for (Entity e : entities) {
			EntityId id = mothers.get(e.getId());
			
			if (id != null) {
				mothers.remove(e.getId());
				// ����һ�����ݣ�ֻ���µ�ǰ������
				SpawnPoint point = ed.getComponent(id, SpawnPoint.class);
				int max = point.getMaximumCount();
				int count = point.getCurrentCount() - 1;
				long start = point.getStartTime();
				long delta = point.getDeltaTime();
				
				ed.setComponent(id, new SpawnPoint(max, count, start, delta));
				log.info("����������ˢ�µ㣺" + id);
			}
		}
	}
	
	/**
	 * ˢ��
	 * 
	 */
	private void respawn() {

		mobs.applyChanges();// ���¹���
		int mobCount = mobs.size();
		if (mobCount >= MAX_MOBS)
			return;

		spawnPoints.applyChanges();// ����ˢ�ֵ�
		int spawnCount = spawnPoints.size();
		if (spawnCount <= 0)
			return;
		
		players.applyChanges();// �������
		int playerCount = players.size();
		if (playerCount <= 0)
			return;

		/**
		 * �������ˢ�ֲ��ԣ�������ARPG��Ϸ������ҿ���ˢ�ֵ��ʱ��Ż�ˢ�֡�
		 */
		List<Entity> activeSpawn = getActiveSpawnPoint();
		spawnCount = activeSpawn.size();
		if (spawnCount > 0) {
			// �����һ��ˢ�ֵ�
			int index = FastMath.rand.nextInt(spawnCount);
			Entity e = activeSpawn.get(index);
			spawn(e);
		}
	}
	
	/**
	 * ��ѡ�����ˢ�ֵ�
	 * @return
	 */
	protected List<Entity> getActiveSpawnPoint() {
		/**
		 * ���ｫҪ��2��ʵ�弯�Ͻ��б������㷨���Ӷ�O(N*N)��
		 * ʹ���������ʹ����ǿforѭ������Ϊ��ǿforѭ������ʱ�ᴴ��������Iterator���󣬵����ڴ汩�ǡ�
		 */
		Entity[] sAry = spawnPoints.toArray(new Entity[] {});
		Entity[] pAry = players.toArray(new Entity[] {});

		// �ж���Ҹ����Ƿ���ˢ�ֵ�
		List<Entity> activeSpawn = new ArrayList<Entity>();
		for (int j = 0; j < pAry.length; j++) {
			Entity player = pAry[j];
			
			// ��ҵ�λ��
			Vector3f pLoc = player.get(Position.class).getLocation();

			for (int i = 0; i < sAry.length; i++) {
				Entity spawnPoint = sAry[i];

				// ���ˢ�µ��Ѿ�����������ټ��㡣
				if (activeSpawn.contains(spawnPoint))
					continue;

				// ˢ�µ��λ��
				Vector3f sLoc = spawnPoint.get(Position.class).getLocation();

				// �ж���Ҿ���ˢ�ֵ�ľ����Ƿ��㹻��
				float distance = spawnPoint.get(AoI.class).getRadiusSquare();
				if (sLoc.distanceSquared(pLoc) <= distance) {
					// �������ˢ�ֵ�
					activeSpawn.add(spawnPoint);
				}
			}
		}
		
		return activeSpawn;
	}
	
	/**
	 * ˢ��һ������
	 * @param e
	 */
	private void spawn(Entity e) {
		SpawnPoint point = e.get(SpawnPoint.class);
		
		// ������������
		if (!point.isFull() && point.getPercent() > 1.0) {

			// ����ĳ�����
			Vector3f loc = e.get(Position.class).getLocation();
			float area = e.get(AoI.class).getRadius();// ������ε��뾶
			
			// ����λ���ڸ���
			float r = FastMath.rand.nextFloat() * area;
			float t = FastMath.rand.nextFloat() * FastMath.TWO_PI;
			float x = FastMath.sin(t) * r;
			float y = FastMath.cos(t) * r;
			
			EntityId id = game.getFactory().createBad(loc.x+x, loc.z+y);
			
			// ��¼��������ˢ�µ㣬��������ʧ��Ҫ֪ͨ���ˢ�ֵ㡣
			ed.setComponent(id, new BornPoint(loc, area));
			
			mothers.put(id, e.getId());

			// ����ˢ�ֵ�
			int current = point.getCurrentCount() + 1;
			int max = point.getMaximumCount();
			ed.setComponent(e.getId(), new SpawnPoint(max, current));
		}
	}
	
	/**
	 * �������̫Զ���ͻ���Ҫ�ؼҡ�
	 * @param entities
	 */
	private void goHome(Set<Entity> entities) {
		for(Entity e : movingMobs) {
			BornPoint mother = e.get(BornPoint.class);
			float radius = mother.getMaxRadius();
			
			Vector3f loc1 = mother.getLocation();
			Vector3f loc2 = e.get(Position.class).getLocation();
			
			if (loc1.distanceSquared(loc2) >= radius * radius) {
				Velocity v = e.get(Velocity.class);
				if (v != null) {
					Vector3f linear = loc1.subtract(loc2);
					linear.normalizeLocal().multLocal(15);

					ed.setComponent(e.getId(), new Velocity(linear));
				}
			}
		}
	}

	@Override
	public void terminate(Game game) {
		spawnPoints.release();
		spawnPoints = null;
		
		players.release();
		players = null;
		
		mobs.release();
		mobs = null;
		
		childMobs.release();
		childMobs = null;
		
		movingMobs.release();
		movingMobs = null;
	}

}
