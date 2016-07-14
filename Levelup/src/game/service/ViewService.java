package game.service;

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
	private EntitySet entities;

	public ViewService() {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		entities = ed.getEntities(Model.class, Position.class);
		
		log.info("Canvas׼�����");
	}

	@Override
	public void update(long time) {
		// ��ʼ��������
		if (mBuffer == null) {
			mBuffer = createImage(WIDTH, HEIGHT);
			gBuffer = mBuffer.getGraphics();
			// ����
			gBuffer.setColor(Color.white);
			gBuffer.fillRect(0, 0, WIDTH, HEIGHT);
			
			paintText();
			
			repaint();
			
		}
		
		// ����data
		if (entities.applyChanges()) {
			// ����
			gBuffer.setColor(Color.white);
			gBuffer.fillRect(0, 0, WIDTH, HEIGHT);
			int size = entities.size();
			
			Entity[] ary = entities.toArray(new Entity[]{});
			for (Entity e : ary) {
				Position p = e.get(Position.class);
				Vector3f loc = p.getLocation();
				Model model = e.get(Model.class);

				ColorRGBA mc = model.getColor();
				Color c = new Color(mc.r, mc.g, mc.b, mc.a);
				gBuffer.setColor(c);
				
				model.getShape().draw(gBuffer, loc);
			}

			// �ػ�
			paintText();
			
			repaint();
		}
	}

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;
	}

	/**
	 * ��������
	 */
	private void paintText() {
		gBuffer.setColor(Color.black);
		gBuffer.drawString("����˵��:", 0, 13);
		gBuffer.drawString("Z��:�����̵�", 0, 26);
		gBuffer.drawString("X��:�������", 0, 39);
		gBuffer.drawString("������:��ָ��λ�ô����̵�", 0, 52);
		gBuffer.drawString("����Ҽ�:��ָ��λ�ô������", 0, 65);
		gBuffer.drawString("����м�:��ָ���㴴��һ��Ŀ�꣬��ǰ��Ļ�����е㶼��׷����", 0, 78);
		gBuffer.drawString("ESC:�˳�����", 0, 91);
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
