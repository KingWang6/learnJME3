package game.service;

import game.components.Decay;
import game.components.Position;
import game.core.Game;
import game.core.Service;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * �������ʵ���Ƿ�����Ļ�߽硣
 * 
 * @author yanmaoyuan
 * 
 */
public class BoundaryService implements Service {

	private EntityData ed;
	private EntitySet entities;
	private float margin;// ��߾�
	private Vector3f min;
	private Vector3f max;

	public BoundaryService(float margin) {
		this.margin = margin;
	}

	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		entities = ed.getEntities(Position.class);

		float width = ViewService.WIDTH;
		float height = ViewService.HEIGHT;

		// ����߽�����ֵ����Сֵ�����ڼ��ʵ����Ƿ񳬳��˱߽硣
		Vector3f worldMin = new Vector3f(0, 0, 0);
		Vector3f worldMax = new Vector3f(width, 0, height);
		min = worldMin.addLocal(-margin, -margin, -margin);
		max = worldMax.addLocal(margin, margin, margin);
	}

	@Override
	public void update(long time) {
		entities.applyChanges();
		for (Entity e : entities) {
			// ���ʵ�崥���˱߽磬����������ʧ��
			Position pos = e.get(Position.class);
			Vector3f loc = pos.getLocation();

			boolean changed = false;
			if (loc.x < min.x) {
				changed = true;
			} else if (loc.x > max.x) {
				changed = true;
			}
			
			if (loc.y < min.y) {
				changed = true;
			} else if (loc.y > max.y) {
				changed = true;
			}

			if (loc.z < min.z) {
				changed = true;
			} else if (loc.z > max.z) {
				changed = true;
			}

			// �����߽������
			if (changed) {
				e.set(new Decay(0));
			}
		}
	}

	@Override
	public void terminate(Game game) {
		// �ͷ�ʵ��
		entities.release();
		entities = null;
	}

}
