package game.core;

import game.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simsilica.es.EntityData;

/**
 * ��Ϸ����
 * @author yanmaoyuan
 *
 */
public class Game {
	
	private Logger log = LoggerFactory.getLogger(Game.class);
	
	private boolean started;
	private ScheduledExecutorService executor;
	private ServiceRunnable serviceRunner;
	private Timer timer;
	private List<Service> services = new ArrayList<Service>();

	public Game() {
		// �����Ϸ����
		services.add(new EntityDataService());
		services.add(new DecayService());
		services.add(new FpsService());
		
		// ��ʼ����ʱ��
		timer = new Timer();
		// ��ʼ����Ϸ���߳�
		serviceRunner = new ServiceRunnable();
	}

    public EntityData getEntityData() {
        return getService(EntityDataService.class).getEntityData(); 
    }
    
    /**
     * ��ӷ���
     * @param s
     * @return
     */
    public <T extends Service> T addService( T s ) {
        if( started ) {
            throw new IllegalStateException( "��Ϸ������." );
        }
        services.add(s);
        return s;
    }
    
    /**
     * ��ѯ����
     * @param type
     * @return
     */
	@SuppressWarnings("unchecked")
	public <T extends Service> T getService(Class<T> type) {
		int len = services.size();
		for (int i = 0; i < len; i++) {
			Service s = services.get(i);
			if (type.isInstance(s)) {
				return (T) s;
			}
		}
		return null;
	}

	/**
	 * ��ʼ��Ϸ
	 */
	public void start() {
		if (started) {
			return;
		}
		
		// ˳���ʼ�����з���
		for (Service s : services) {
			s.initialize(this);
		}
		
		executor = Executors.newScheduledThreadPool(1);
		// �̶�ˢ����ÿ��16֡��ʱ����Ϊ62.5���롣
		executor.scheduleAtFixedRate(serviceRunner, 0, 62, TimeUnit.MILLISECONDS);
		started = true;
		
		log.info("��ʼ��Ϸ");
	}

	/**
	 * ������Ϸ
	 */
	public void stop() {
		if (!started) {
			return;
		}
		executor.shutdown();

		// �����������еķ���
		for (int i = services.size() - 1; i >= 0; i--) {
			Service s = services.get(i);
			s.terminate(this);
		}
		started = false;
		
		log.info("��Ϸ����");
	}

	/**
	 * �������з���
	 * @param gameTime
	 */
	protected void runServices(long gameTime) {
		int len = services.size();
		for (int i = 0; i < len; i++) {
			Service s = services.get(i);
			s.update(gameTime);
		}
	}

    public long getGameTime() {
        return timer.getTime(); 
    }
    
    public Timer getTimer() {
    	return timer;
    }
    
	private class ServiceRunnable implements Runnable {
		public void run() {
			try {
				timer.update();
				runServices(getGameTime());
			} catch (RuntimeException e) {
				log.error("�������з����쳣", e);
			}
		}
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
}
