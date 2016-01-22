package teris.game;
/**
 * <pre>
 * λ�Ƶķ���
 * ��X���򶫣���X��������
 * ��Y�����ϣ���Y�����£�
 * ��Z�����ϣ���Z���򱱡�
 * </pre>
 * 
 * @author yanmaoyuan
 *
 */
public enum DIRECTION {
	NORTH(0), WEST(1), SOUTH(2), EAST(3), UP(4), DOWN(5);
	
	private int value;
	private DIRECTION(int value) {
		this.value = value;
	}
	public int getValue() {return this.value;};
}