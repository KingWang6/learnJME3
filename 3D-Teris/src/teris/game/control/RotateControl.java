package teris.game.control;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * ��Y����ת�ռ�
 * @author yanmaoyuan
 *
 */
public class RotateControl extends AbstractControl {

	/**
	 * ���1����ת�Ƕȵ����ڣ�Ĭ��Ϊ0.1�롣
	 */
	private float time;
	private float scale;

	/**
	 * �ܵ���ת�Ƕȣ�Ĭ��90�㡣
	 */
	private float angleToRotate;

	/**
	 * ��ת����true˳ʱ����ת��false��ʱ����ת��
	 */
	private boolean clockwise;
	
	/**
	 * Ŀǰ�Ѿ���ת�ĽǶ�
	 */
	private float angleAlreadyRotated = 0f;

	/**
	 * �Ƿ�������ת
	 */
	private boolean isRotating = false;
	
	public RotateControl() {
		this.time = 0.1f;
		this.angleToRotate = FastMath.HALF_PI;

		this.scale = 1 / time;
		this.clockwise = true;
		this.isRotating = false;
		this.angleAlreadyRotated = 0f;
	}
	
 	public RotateControl(float angleToRotate) {
		this.angleToRotate = angleToRotate;
		
		this.time = 0.1f;
		this.scale = 1 / time;
		this.clockwise = true;
		this.isRotating = false;
		this.angleAlreadyRotated = 0f;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!isRotating)
			return;

		// ��0.1��֮����ת90��
		float angle = angleToRotate * scale * tpf;
		angleAlreadyRotated += angle;

		// �ж��Ƿ��Ѿ������ת
		if (angleAlreadyRotated >= angleToRotate) {
			// ��ֹ��ת����
			angle -= angleAlreadyRotated - angleToRotate;

			// �Ѿ������һ�����ڵ���ת�����ؼ�������λ��
			angleAlreadyRotated = 0f;
			isRotating = false;
		}

		// ��Y����ת
		if (clockwise)
			spatial.rotate(0, angle, 0);
		else
			spatial.rotate(0, -angle, 0);
	}

	/**
	 * ��ת�ڵ�
	 * 
	 * @param clockwise
	 *            true˳ʱ����ת��false��ʱ����ת��
	 */
	public void rotate(boolean clockwise) {
		if (this.isRotating == false) {
			this.isRotating = true;
			this.clockwise = clockwise;
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
	}

}
