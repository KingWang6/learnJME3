package net.jmecn.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * ����BulletAppState����ײ���
 * @author yanmaoyuan
 */
public class CollisionAppState extends AbstractAppState {

	private SimpleApplication simpleApp;
	private Camera cam;
	
	private final BulletAppState bulletAppState;
	private RigidBodyControl terrain;
	private CharacterControl player;
	
	private EntityData ed;
	private EntitySet entities;
	private EntitySet movingPlayer;

	private Map<EntityId, RigidBodyControl> objects;
	
	public CollisionAppState() {
		bulletAppState = new BulletAppState();
		
		objects = new HashMap<EntityId, RigidBodyControl>();
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		
		this.simpleApp = (SimpleApplication) app;
		stateManager.attach(bulletAppState);
		bulletAppState.setDebugEnabled(true);

		ed = this.simpleApp.getStateManager().getState(EntityDataState.class).getEntityData();
        entities = ed.getEntities(Collision.class, Model.class, Position.class);
        
        movingPlayer = ed.getEntities(Player.class, Movement.class);
        
        cam = app.getCamera();
	}

	@Override
	public void update(float tpf) {
		if (entities.applyChanges()) {
            removeCollision(entities.getRemovedEntities());
            addCollision(entities.getAddedEntities());
        }
		
		// ���������λ��
		for(Entry<EntityId, RigidBodyControl> entry : objects.entrySet()) {
			EntityId id = entry.getKey();
			RigidBodyControl obj = entry.getValue();
			
			if (obj.isActive()) {
				ed.setComponent(id, new Position(obj.getPhysicsLocation()));
			}
		}
		
		if (player != null) {
			if (movingPlayer.applyChanges()) {
				Entity e = movingPlayer.iterator().next();
				Movement move = e.get(Movement.class);
				player.setWalkDirection(move.getDirection().mult(move.getSpeed()));
			}
			
			cam.setLocation(player.getPhysicsLocation());
		}
	}

	private void removeCollision(Set<Entity> entities) {
        for (Entity e : entities) {
        	RigidBodyControl object = objects.remove(e.getId());
        	bulletAppState.getPhysicsSpace().removeCollisionObject(object);
        }
		
	}
	
	private void addCollision(Set<Entity> entities) {
		for (Entity e : entities) {
			Collision collision = e.get(Collision.class);
			Position position = e.get(Position.class);
			Model model = e.get(Model.class);
			
			String name = model.getName();
			if (name.equals(Model.BOMB)) {
				
				// ����һ������
				RigidBodyControl rigidBody = new RigidBodyControl(collision.getMass());
				objects.put(e.getId(), rigidBody);

				// ���ģ��
				Spatial bombModel = simpleApp.getStateManager().getState(VisualAppState.class).getModel(e.getId());
				bombModel.addControl(rigidBody);

				//  ���뵽����ռ�
				bulletAppState.getPhysicsSpace().add(bombModel);

				// �����������
				rigidBody.setLinearVelocity(collision.getLinearVelocity());
				rigidBody.setAngularVelocity(collision.getAnglurVelocity());
				rigidBody.setGravity(collision.getGravity());
				rigidBody.setPhysicsLocation(position.getLocation());
			}
			
			if (name.equals(Model.ICEWORLD)) {
				// ����
				terrain = new RigidBodyControl(0);
				Spatial terrainModel = simpleApp.getStateManager().getState(VisualAppState.class).getModel(e.getId());
				terrainModel.addControl(terrain);
				bulletAppState.getPhysicsSpace().add(terrainModel);
				
			}
			
			if (name.equals(Model.OTO)) {
				// ���
				CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(10f, 40f, 1);
				player = new CharacterControl(capsuleShape, collision.getMass());
				player.setJumpSpeed(60);
				player.setFallSpeed(60);
				player.setGravity(98);
				player.setPhysicsLocation(position.getLocation());

				bulletAppState.getPhysicsSpace().add(player);
			}
        }
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		entities.release();
        entities = null;
        
        movingPlayer.release();
        movingPlayer = null;
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		super.stateDetached(stateManager);
		
		stateManager.detach(bulletAppState);
	}

}