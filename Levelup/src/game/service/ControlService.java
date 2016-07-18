package game.service;

import game.components.Model;
import game.components.Position;
import game.components.Potion;
import game.components.Velocity;
import game.core.Game;
import game.core.Service;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;

/**
 * �û����Ʒ���
 * 
 * @author yanmaoyuan
 * 
 */
public class ControlService implements KeyListener, MouseMotionListener, MouseListener, Service {

	private Logger log = LoggerFactory.getLogger(ControlService.class);
	
	private Game game;
	private EntityData ed;
	private EntitySet entities;
	
	private boolean xPressed = false;
	private boolean zPressed = false;

	private boolean lPressed = false;// ������
	private boolean rPressed = false;// ����Ҽ�
	// ���������������
	private int lX = 0;
	private int lY = 0;
	
	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		entities = ed.getEntities(Filters.fieldEquals(Model.class, "name", Model.PLAYER),
				Model.class, Position.class);
		
		ViewService view = game.getService(ViewService.class);
		if (view != null) {
			view.addKeyListener(this);
			view.addMouseListener(this);
			view.addMouseMotionListener(this);
		}
	}

	// ������ȴʱ��
	long cdTime = 0;
	@Override
	public void update(long time) {
		cdTime += time;
		if (zPressed) {
		}
		if (xPressed) {
		}
		
		if (lPressed) {
			// 1�빥�����
			// TODO �˴�Ӧ�ø��ݹ������ж�ʱ��
			if (cdTime >= 1000000000) {
				cdTime = 0;
				for(Entity e : entities) {
					Position p = e.get(Position.class);
					Vector3f loc = p.getLocation();
					Vector3f target = new Vector3f(lX, 0, lY);
					Vector3f v = target.subtract(loc).normalize();
					v.multLocal(60);
					
					game.getFactory().createBullet(loc, v);
				}
			}
		}
	}

	@Override
	public void terminate(Game game) {
		ViewService view = game.getService(ViewService.class);
		if (view != null) {
			view.removeKeyListener(this);
			view.removeMouseListener(this);
			view.removeMouseMotionListener(this);
		}
	}

	// ��������
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_X:
			xPressed = true;
			break;
		case KeyEvent.VK_Z:
			zPressed = true;
			break;
		}
		
		log.info("����:" + e.getKeyChar());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: 
			game.stop();
			break;
		case KeyEvent.VK_X:
			xPressed = false;
			break;
		case KeyEvent.VK_Z:
			zPressed = false;
			break;
		case KeyEvent.VK_1:
			// �Ժ�ҩ
			eatPotion();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	// ������¼�����
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1: {// ���
			lX = e.getX();
			lY = e.getY();
			lPressed = true;
			break;
		}
		case MouseEvent.BUTTON2: {// �м�
			break;
		}
		case MouseEvent.BUTTON3: {// �Ҽ�
			setTarget(e.getX(), e.getY());
			rPressed = true;
			break;
		}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1: {// ���
			lPressed = false;
			break;
		}
		case MouseEvent.BUTTON2: {// �м�
			break;
		}
		case MouseEvent.BUTTON3: {// �Ҽ�
			rPressed = false;
			entities.applyChanges();
			for(Entity player : entities) {
				ed.removeComponent(player.getId(), Velocity.class);
			}
			break;
		}
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (lPressed) {
			lX = e.getX();
			lY = e.getY();
		}
		if (rPressed) {
			setTarget(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	public void eatPotion() {
		entities.applyChanges();
		for(Entity e:entities) {
			e.set(new Potion(25));
		}
	}
	public void setTarget(int x, int z) {
		entities.applyChanges();
		for(Entity e : entities) {
			Vector3f target = new Vector3f(x, 0, z);
			Vector3f loc = e.get(Position.class).getLocation();
			
			Vector3f v = target.subtract(loc);
			v.normalizeLocal().multLocal(100);
			ed.setComponents(e.getId(), new Velocity(v));
		}
	}
}
