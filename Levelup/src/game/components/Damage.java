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
	int delta;// �˺�ֵ
	
	public Damage(int delta, EntityId dealer) {
		this.dealer = dealer;
		this.delta = delta;
	}
	
	public String toString() {
		return "Damage[value=" +delta+ ", dealer=" + dealer.getId() + "]";
	}
}
