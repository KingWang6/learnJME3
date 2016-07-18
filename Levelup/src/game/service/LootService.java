package game.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.components.Dead;
import game.components.Decay;
import game.components.Exp;
import game.components.Health;
import game.components.Model;
import game.components.Potion;
import game.core.Game;
import game.core.Service;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * ����������ĵ���
 * 
 * @author yanmaoyuan
 *
 */
public class LootService implements Service {

	static Logger log = LoggerFactory.getLogger(LootService.class);

	private EntityData ed;
	private EntitySet entities;
	private Game game;

	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		entities = ed.getEntities(Dead.class);
	}

	@Override
	public void update(long time) {
		entities.applyChanges();

		for (Entity e : entities) {
			// ����ģ�����;�������
			Model model = ed.getComponent(e.getId(), Model.class);
			log.info(e.getId().getId() + " ������.");
			
			// ��������
			if (Model.BAD.equals(model.getName())) {
				// ��һ�þ���ֵ
				Exp mobExp = ed.getComponent(e.getId(), Exp.class);
				EntityId player = game.getService(SinglePlayerService.class)
						.getPlayer();
				Exp exp = ed.getComponent(player, Exp.class);
				ed.setComponent(
						player,
						new Exp(exp.getCurrent() + mobExp.getCurrent(), exp
								.getMax()));

				// ���չ���5%���������ֵ��Ϊ����
				float hp = ed.getComponent(e.getId(), Health.class).getMaxHp();
				hp *= 0.05f;
				ed.setComponent(player, new Potion(hp));

				log.info("��һ��" + (int)mobExp.getMax() + "�㾭��ֵ!");
			}

			ed.removeComponent(e.getId(), Dead.class);
			e.set(new Decay(0));
		}
	}

	@Override
	public void terminate(Game game) {
		// TODO Auto-generated method stub

	}

}
