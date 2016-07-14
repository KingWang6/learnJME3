package game.service;

import game.components.AoI;
import game.components.Model;
import game.components.Position;
import game.components.SpawnPoint;
import game.components.Velocity;
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
	
	// �ٶ�
	private EntitySet velocities;
	
	private EntitySet aois;

	public ViewService() {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		models = ed.getEntities(Position.class, Model.class);
		velocities = ed.getEntities(Position.class, Velocity.class);
		aois = ed.getEntities(Position.class, AoI.class, SpawnPoint.class);
		
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
		
		// ��������
		velocities.applyChanges();
		for (Entity e : velocities) {
			Vector3f loc = e.get(Position.class).getLocation();
			Vector3f inear = e.get(Velocity.class).getLinear();
			
			Vector3f point = loc.add(inear);
			
			gBuffer.setColor(Color.BLACK);
			gBuffer.drawLine((int)loc.x, (int)loc.z, (int)point.x, (int)point.z);
		}
		
		// ����data
		models.applyChanges();
		Entity[] ary = models.toArray(new Entity[]{});
		for (Entity e : ary) {
			Position p = e.get(Position.class);
			Vector3f loc = p.getLocation();
			Model model = e.get(Model.class);

			ColorRGBA mc = model.getColor();
			Color c = new Color(mc.r, mc.g, mc.b, mc.a);
			gBuffer.setColor(c);
			
			model.getShape().draw(gBuffer, loc);
		}
		
		// ���Ʊ߿�
		aois.applyChanges();
		for (Entity e : aois) {
			Vector3f loc = e.get(Position.class).getLocation();
			float radius = e.get(AoI.class).getRadius();
			
			gBuffer.setColor(Color.gray);
			gBuffer.drawOval(
					(int)(loc.x-radius), 
					(int)(loc.z-radius), 
					(int)radius * 2, 
					(int)radius * 2);
		}
		
		// �ػ�
		paintText();
		paintPlayerStatus();
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
		
		// HP
		gBuffer.setColor(Color.black);
		gBuffer.drawString("����:", 10, 17);
		gBuffer.drawRoundRect(40, 4, 200, 17, 3, 3);
		gBuffer.setColor(Color.red);
		gBuffer.fillRect(42, 6, 197, 14);
		
		// MP
		gBuffer.setColor(Color.black);
		gBuffer.drawString("ħ��:", 10, 36);
		gBuffer.drawRoundRect(40, 24, 200, 17, 3, 3);
		gBuffer.setColor(Color.blue);
		gBuffer.fillRect(42, 26, 197, 14);
		
		// SP ����
		gBuffer.setColor(Color.black);
		gBuffer.drawString("����:", 10, 55);
		gBuffer.drawRoundRect(40, 44, 150, 15, 3, 3);
		gBuffer.setColor(Color.green);
		gBuffer.fillRect(42, 46, 147, 12);
		
		// EXP ����ֵ
		gBuffer.setColor(Color.black);
		gBuffer.drawRoundRect(10, 707, 1060, 8, 3, 3);
		gBuffer.setColor(Color.cyan);
		float pct = 0.57f;
		int length = (int)(pct * 1057);
		gBuffer.fillRect(12, 709, length, 5);

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
