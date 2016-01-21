package teris.game.control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * �ƶ�
 * @author yanmaoyuan
 *
 */
public class MoveControl extends AbstractControl {

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
		NORTH, SOUTH, EAST, WEST, UP, DOWN
	}
	
	private boolean isMoving;
	private DIRECTION dir;
	
	/**
	 * ���1���ƶ������ڣ�Ĭ��Ϊ0.1�롣
	 */
	private float time;
	private float scale;
	
	/**
	 * �ܵ��ƶ����룬Ĭ��1��
	 */
	private float unit;
	/**
	 * �Ѿ��ƶ��ľ���
	 */
	private float distAlreadyMoved;
	
	public MoveControl() {
		time = 0.1f;
		scale = 1 / time;
		unit = 1f;
		distAlreadyMoved = 0f;
		isMoving = false;
		dir = DIRECTION.DOWN;
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		if (isMoving) {
			float dist = unit * scale * tpf;
			distAlreadyMoved += dist;
	
			// �ж��Ƿ��Ѿ����λ��
			if (distAlreadyMoved >= unit) {
				// ��ֹ�ƶ���Զ
				dist -= distAlreadyMoved - unit;
	
				// �Ѿ�����ƶ������ؼ�������λ��
				distAlreadyMoved = 0f;
				isMoving = false;
			}
	
			// �ƶ�
			switch (dir) {
			case NORTH:
				spatial.move(0, 0, -dist);
				break;
			case SOUTH:
				spatial.move(0, 0, dist);
				break;
			case EAST:
				spatial.move(dist, 0, 0);
				break;
			case WEST:
				spatial.move(-dist, 0, 0);
				break;
			case UP:
				spatial.move(0, dist, 0);
				break;
			case DOWN:
				spatial.move(0, -dist, 0);
				break;
			}
		}
	}
	
	public void move(DIRECTION dir) {
		if (!isMoving) {
			isMoving = true;
			this.dir = dir;
		}
		
	}

	public boolean isMoving() {
		return isMoving;
	}
	
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {}

}
