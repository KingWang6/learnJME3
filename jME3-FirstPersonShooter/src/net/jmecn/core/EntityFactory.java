package net.jmecn.core;

import org.apache.log4j.Logger;

import com.simsilica.es.EntityData;

/**
 * ʵ�幤�������������������͵�ʵ��
 * @author yanmaoyuan
 *
 */
public class EntityFactory {
	static Logger log = Logger.getLogger(EntityFactory.class);
	
	private EntityData ed;
	
	public EntityFactory(EntityData ed) {
		this.ed = ed;
	}
	
	public void createPlayer() {
		
	}
	
}
