package game.components;

import com.jme3.math.ColorRGBA;
import com.simsilica.es.EntityComponent;

public class Model implements EntityComponent {
	private final String name;
	private final ColorRGBA color;
	
	public final static String PLAYER = "player";// ���
	public final static String BAD = "bad";// ����
	public static final String BULLET = "Bullet";// �ӵ�
	public final static String TARGET = "target";// ׷��Ŀ��
	public final static String RESPAWN_POINT = "respawn_point";// ˢ�ֵ�
	
	public Model(String name, ColorRGBA color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public ColorRGBA getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		return "Model[Name=" + name + ", Color=" + color + "]";
	}
}
