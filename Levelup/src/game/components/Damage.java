package game.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 * �˺�ֵ
 * @author yanmaoyuan
 *
 */
public class Damage implements EntityComponent {

	EntityId dealer;// �˺�������
	float delta;// �˺�ֵ
	
	public Damage(float delta, EntityId dealer) {
		this.dealer = dealer;
		this.delta = delta;
	}
	
	public float getDelta() {
		return delta;
	}
	
	public EntityId getDealer() {
		return dealer;
	}
	
	public String toString() {
		return "Damage[value=" +delta+ ", dealer=" + dealer.getId() + "]";
	}
}
