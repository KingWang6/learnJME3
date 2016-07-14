package game.service;

import game.components.AoI;
import game.components.Model;
import game.components.Position;
import game.components.SpawnPoint;
import game.core.Game;
import game.core.Service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;

public class SpawnService implements Service {

	private Logger log = LoggerFactory.getLogger(SpawnService.class);
	
	Game game;
	EntityData ed;
	EntitySet spawnPoints;
	EntitySet players;
	EntitySet mobs;

	// ���ˢ������
	public final static int MAX_MOBS = 50;
	
	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		spawnPoints = ed.getEntities(
				Position.class, 
				SpawnPoint.class,
				AoI.class);
		players = ed.getEntities(Filters.fieldEquals(Model.class, "name", Model.PLAYER),
				Position.class,
				Model.class);
		
		mobs = ed.getEntities(Filters.fieldEquals(Model.class, "name", Model.BAD),
				Model.class);
	}

	private int time = 0;
	
	@Override
	public void update(long timePerFrame) {
		time += timePerFrame;
		if (time < 1000000000l) {// ϵͳˢ����1��ʱ����
			return;
		} else {
			time -= 1000000000;
		}
		
		spawnPoints.applyChanges();// ����ˢ�ֵ�
		players.applyChanges();// �������
		mobs.applyChanges();// ���¹���

		// �жϵ�ͼ�еĹ��������Ƿ񵽴�����
		int mob_count = mobs.size();
		if (mob_count >= MAX_MOBS) return;
		
		if (spawnPoints.size() == 0) return;
		
		Entity[] sAry = spawnPoints.toArray(new Entity[] {});
		Entity[] pAry = players.toArray(new Entity[] {});
		
		// �ж���Ҹ����Ƿ���ˢ�ֵ�
		int plen = pAry.length;
		int len = sAry.length;
		List<Entity> activeSpawn = new ArrayList<Entity>();
		for(int j=0; j<plen; j++) {
			Vector3f pLoc = pAry[j].get(Position.class).getLocation();
			for (int i = 0; i < len; i++) {
				Vector3f sLoc = sAry[i].get(Position.class).getLocation();
				float distance = sAry[i].get(AoI.class).getRadiusSquare();
				if (sLoc.distanceSquared(pLoc) <= distance) {
					activeSpawn.add(sAry[i]);
				}
			}
		}
		
		
		len = activeSpawn.size();
		// ����û�����
		if (len == 0) {
			return;
		}
		
		// �����һ��ˢ�ֵ�
		int index = FastMath.rand.nextInt(len);
			
		Entity spawn = activeSpawn.get(index);
		SpawnPoint point = spawn.get(SpawnPoint.class);
		Vector3f loc = spawn.get(Position.class).getLocation();

		// ������������
		if (!point.isFull() && point.getPercent() > 1.0) {
			
			EntityId id = game.getFactory().createBad(loc.x, loc.z);
			log.info(spawn + " ˢ���� " + id);
			
			// ����ˢ�ֵ�
			int current = point.getCurrentCount() + 1;
			int max = point.getMaximumCount();
			ed.setComponent(spawn.getId(), new SpawnPoint(max, current));
		}
				
	}
	
	protected void respawn() {

	}

	public void nofityDead(EntityId dead) {

	}

	@Override
	public void terminate(Game game) {
		// TODO Auto-generated method stub

	}

}
