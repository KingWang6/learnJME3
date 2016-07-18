package game.components;

import com.simsilica.es.EntityComponent;

/**
 * ����ֵ
 * @author yanmaoyuan
 *
 */
public class Exp implements EntityComponent {

	float cur;// ��ǰֵ
	float max;// ���ֵ
	
	public Exp(float cur, final float max) {
		this.cur = cur;
		this.max = max;
		
	}
	
	public float getCurrent() {
		return cur;
	}
	
	public float getMax() {
		return max;
	}
	
	public float getPercent() {
		return cur / max;
	}
	
	public String toString() {
		return "EXP[" + cur + "/" + max + "]";
	}
}
