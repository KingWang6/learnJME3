package game.core;

import game.service.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

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
	private boolean enabled;
	
	private ScheduledExecutorService executor;
	private ServiceRunnable serviceRunner;
	private Timer timer;
	private JFrame frame;
	private List<Service> services = new ArrayList<Service>();
	private EntityFactory factory;
	
	public Game() {
		// �����Ϸ����
		services.add(new EntityDataService());
		services.add(new ControlService());
		services.add(new SinglePlayerService());
		services.add(new AiService());
		services.add(new MovementService());
		services.add(new CollisionService());
		services.add(new BoundaryService(20));
		services.add(new SpawnService());
		services.add(new DecayService());
		services.add(new ViewService());
		
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
		
		factory = new EntityFactory(getEntityData());
		
		// ��������
		createFrame();
		
		// �̶�ˢ����ÿ��16֡��ʱ����Ϊ62.5���롣
		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(serviceRunner, 0, 62, TimeUnit.MILLISECONDS);
		started = true;
		
		enabled = true;
		log.info("��ʼ��Ϸ");
	}

	/**
	 * ������Ϸ����
	 */
	private void createFrame() {
		// ��������
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stop();
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				enabled = true;
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				enabled = false;
			}
			
		});
		frame.addKeyListener(getService(ControlService.class));
		
		// ���ô��ڲ���
		frame.setTitle("My Game");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		
		// ���ViewService
		frame.add(getService(ViewService.class));
		frame.pack();
		
		// ���ھ���
		Dimension frameSize = frame.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int locX = (screenSize.width - frameSize.width)/2;
		int locY = (screenSize.height - frameSize.height)/2;
		frame.setLocation(locX, locY);
		
		// ��ʾ����
		frame.setVisible(true);
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
		enabled = false;
		log.info("��Ϸ����");
		
		System.exit(0);
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
    
    public JFrame getFrame() {
    	return frame;
    }
    
    public EntityFactory getFactory() {
    	return factory;
    }
    
	private class ServiceRunnable implements Runnable {
		public void run() {
			
			try {
				timer.update();
				if (!enabled)
					return;
				runServices(timer.getTimePerFrame());
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
