package game.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.components.Damage;
import game.components.Dead;
import game.components.Health;
import game.core.Game;
import game.core.Service;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Name;

public class DamageService implements Service {

	private Logger log = LoggerFactory.getLogger(DamageService.class);
	
	private EntityData ed;
	private EntitySet entities;

	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		entities = ed.getEntities(Health.class, Damage.class, Name.class);
	}

	@Override
	public void update(long time) {
		if (entities.applyChanges()) {
			for (Entity e : entities) {
				String name = e.get(Name.class).getName();
				
				Damage damage = e.get(Damage.class);
				EntityId dealer = damage.getDealer();
				float delta = damage.getDelta();
				Health hp = e.get(Health.class);
				
				String dealerName = ed.getComponent(dealer, Name.class).getName();
				
				log.info(dealerName + "������" + name + ", �����" + (int)delta + "���˺�.");
				
				// ����
				if (delta > hp.getCurrentHp()) {
					e.set(new Health(0, hp.getMaxHp()));
					e.set(new Dead());
				} else {
					e.set(new Health(hp.getCurrentHp() - delta, hp.getMaxHp()));
				}
				
				// �Ƴ����˺�
				ed.removeComponent(e.getId(), Damage.class);
			}
		}
	}

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;
	}

}
