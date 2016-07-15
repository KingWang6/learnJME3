package game.service;

import game.components.CollisionShape;
import game.components.Model;
import game.components.Position;
import game.core.Game;
import game.core.Service;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * ��ʾ���� �̳�Canvas�࣬���ڻ���UI��
 * 
 * @author yanmaoyuan
 * 
 */
public class ViewService extends Canvas implements Service {

	private static final long serialVersionUID = 1L;
	
	private Logger log = LoggerFactory.getLogger(ViewService.class);

	public final static int WIDTH = 1080;
	public final static int HEIGHT = 720;

	private Image mBuffer;// ������
	private Graphics gBuffer;

	private EntityData ed;
	
	// ģ��
	private EntitySet models;
	
	public ViewService() {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		models = ed.getEntities(Position.class, Model.class, CollisionShape.class);
		
		log.info("Canvas׼�����");
	}

	@Override
	public void update(long time) {
		// ��ʼ��������
		if (mBuffer == null) {
			mBuffer = createImage(WIDTH, HEIGHT);
			gBuffer = mBuffer.getGraphics();
		}
		
		// ����
		gBuffer.setColor(Color.white);
		gBuffer.fillRect(0, 0, WIDTH, HEIGHT);
		
		// ����data
		models.applyChanges();
		Entity[] ary = models.toArray(new Entity[]{});
		for (Entity e : ary) {
			Position p = e.get(Position.class);
			Vector3f loc = p.getLocation();
			Model model = e.get(Model.class);
			float radius = e.get(CollisionShape.class).getRadius();

			ColorRGBA mc = model.getColor();
			Color c = new Color(mc.r, mc.g, mc.b, mc.a);
			gBuffer.setColor(c);
			
			gBuffer.fillOval(
					(int)(loc.x-radius), 
					(int)(loc.z-radius), 
					(int)radius * 2, 
					(int)radius * 2);
			
			gBuffer.setColor(Color.black);
			
			gBuffer.drawOval(
					(int)(loc.x-radius), 
					(int)(loc.z-radius), 
					(int)radius * 2, 
					(int)radius * 2);
		}
		
		int count = models.size();
		gBuffer.setColor(Color.black);
		gBuffer.drawString("ʵ������:" + count, 10, 23);
		// �ػ�
		paintText();
		//paintPlayerStatus();
		repaint();
	}

	@Override
	public void terminate(Game game) {
		models.release();
		models = null;
	}

	/**
	 * ��������
	 */
	private void paintText() {
		gBuffer.setColor(Color.black);
		gBuffer.drawString("����˵��:", 0, 613);
		gBuffer.drawString("������:��ָ��λ�ô���ˢ�ֵ�", 0, 626);
		gBuffer.drawString("����Ҽ�:��������ƶ�", 0, 639);
		gBuffer.drawString("ESC:�˳�����", 0, 652);
	}
	
	private void paintPlayerStatus() {
		// �ֱ�Ѫ�����������������������ı߿�
		float hpPct = 1.0f;
		float mpPct = 1.0f;
		float spPct = 1.0f;
		float expPct = 0.35f;
		
		// HP
		gBuffer.setColor(Color.black);
		gBuffer.drawString("����:", 10, 17);
		gBuffer.drawRoundRect(40, 4, 200, 17, 3, 3);
		gBuffer.setColor(Color.white);
		gBuffer.fillRect(41, 5, 199, 16);
		gBuffer.setColor(Color.red);
		gBuffer.fillRect(42, 6, (int)(hpPct * 197), 14);
		
		// MP
		gBuffer.setColor(Color.black);
		gBuffer.drawString("ħ��:", 10, 36);
		gBuffer.drawRoundRect(40, 24, 200, 17, 3, 3);
		gBuffer.setColor(Color.white);
		gBuffer.fillRect(41, 25, 199, 16);
		gBuffer.setColor(Color.blue);
		gBuffer.fillRect(42, 26, (int)(mpPct * 197), 14);
		
		// SP ����
		gBuffer.setColor(Color.black);
		gBuffer.drawString("����:", 10, 55);
		gBuffer.drawRoundRect(40, 44, 150, 15, 3, 3);
		gBuffer.setColor(Color.white);
		gBuffer.fillRect(41, 45, 149, 14);
		gBuffer.setColor(Color.green);
		gBuffer.fillRect(42, 46, (int)(spPct * 147), 12);
		
		// EXP ����ֵ
		gBuffer.setColor(Color.black);
		gBuffer.drawRoundRect(10, 707, 1060, 8, 3, 3);
		gBuffer.setColor(Color.white);
		gBuffer.fillRect(11, 708, 1059, 7);
		gBuffer.setColor(Color.cyan);
		gBuffer.fillRect(12, 709, (int)(expPct * 1057), 5);

	}
	/**
	 * ����Canvas�е�paint����������������ͼ����Ƶ���Ļ�ϡ�
	 */
	@Override
	public void paint(Graphics g) {
		g.drawImage(mBuffer, 0, 0, this);
	}

	/**
	 * ����Canvas�е�update������ֱ�ӵ���paint(g)��������Ļ��˸��
	 */
	@Override
	public void update(Graphics g) {
		paint(g);
	}

}
