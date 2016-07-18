package game.components;

import com.simsilica.es.EntityComponent;

/**
 * ����ֵ
 * @author yanmaoyuan
 *
 */
public class Health implements EntityComponent {

	float cur;// ��ǰ����ֵ
	float max;// �������ֵ
	
	public Health(float cur, final float max) {
		if (cur > max) { cur = max;}
		
		this.cur = cur;
		this.max = max;
		
	}
	
	public float getCurrentHp() {
		return cur;
	}
	
	public float getMaxHp() {
		return max;
	}
	
	public float getPercent() {
		return cur / max;
	}
	
	public String toString() {
		return "HP[" + cur + "/" + max + "]";
	}
}
