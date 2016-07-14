package game.components;

import com.simsilica.es.EntityComponent;

/**
 * ��ײ��״
 * 
 * @author yanmaoyuan
 * 
 */
public class CollisionShape implements EntityComponent {
	private float radius;

	public CollisionShape(float radius) {
		this.radius = radius;
	}

	public float getRadius() {
		return radius;
	}
}
