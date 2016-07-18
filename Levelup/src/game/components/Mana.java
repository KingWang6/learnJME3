package game.components;

import com.simsilica.es.EntityComponent;

/**
 * ħ��ֵ
 * @author yanmaoyuan
 *
 */
public class Mana implements EntityComponent {

	float cur;// ��ǰֵ
	float max;// ���ֵ
	
	public Mana(float cur, final float max) {
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
		return "MP[" + cur + "/" + max + "]";
	}
}
