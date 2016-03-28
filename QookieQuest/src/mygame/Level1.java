package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;

/**
 *
 * @author Belcky
 */
public class Level1 extends Stage {
    
    //boolean if block is touched, then player may proceed to the door to exit
    boolean withKey=true;
    boolean withoutKey=false;
    boolean key = withoutKey;
    
    Node node;

    public Level1(Main m, ColorRGBA color, int plates, int blocks, int tramps) {
        super(m, color, plates, blocks, tramps);
        MyCustomControl cont = new MyCustomControl(m, this);
        
        setupPlatforms();
        setupBlocks();
        setupDoor();
        setBackground("Textures/cookies.jpg");
    }

    
    @Override
    public void setupPlatforms() {
        Box platform = new Box(3, .25f, 5);
        Material mat1 = m.makeMaterial("light", ColorRGBA.Red);
        Material mat2 = m.makeMaterial("light", ColorRGBA.Blue);

        Geometry[] platsG = new Geometry[NUMPLATFORMS];
        for (int i = 0; i < NUMPLATFORMS; i++) {
            platsG[i] = new Geometry("platform_" + (i + 1), platform);
            if (i % 2 == 0) {
                platsG[i].setMaterial(mat1);
            } else {
                platsG[i].setMaterial(mat2);
            }
            platformNodes[i] = new Node();
            platformNodes[i].attachChild(platsG[i]);
            platsG[i].setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            m.getRootNode().attachChild(platformNodes[i]);
        }

        Vector3f prevPos = new Vector3f(-20, 2, 0);
        for (int i = 0; i < NUMPLATFORMS; i++) {
            if (i > 0) {
                prevPos = platformNodes[i - 1].getLocalTranslation();
            }
            m.setPosition(platformNodes[i], prevPos.x + 10, prevPos.y + 5, 0);
            m.applyPhysics((Geometry) platformNodes[i].getChild(0), "rigid", 0f);
        }
    }

    @Override
    public void setupBlocks() {
        
            GhostControl ghost = new GhostControl(
            new BoxCollisionShape(new Vector3f(1,1,1)));  // a box-shaped ghost
            node = new Node("a ghost-controlled thing");
            node.addControl(ghost);
        
        Box block = new Box(1f, 1f, 1f);
        Geometry blockG = new Geometry("block_1", block);
        Material blockMat = m.makeMaterial("light", ColorRGBA.Cyan);
        blockG.setMaterial(blockMat);
        blockG.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        blockNodes[0] = new Node();
        blockNodes[0].attachChild(blockG);
        
            //node.attachChild(blockNodes[0]);
            //m.getRootNode().attachChild(node);
        
        m.getRootNode().attachChild(blockNodes[0]);
           m.bullet.getPhysicsSpace().add(ghost);
        Vector3f pos = platformNodes[1].getLocalTranslation();
        m.setPosition(blockNodes[0], pos.x, pos.y+.3f, pos.z);
        m.applyPhysics(blockG, "rigid", 10f);
    }
    
   
    
    

    @Override
    public void setupTrampolines() {
    }

    @Override
    public void setBackground(String imagePath) {
        sky = SkyFactory.createSky(
                m.getAssetManager(), imagePath, true);
        m.getRootNode().attachChild(sky);
    }

    @Override
    public void setupDoor() {
         GhostControl ghos = new GhostControl(
            new BoxCollisionShape(new Vector3f(1,1,1)));  // a box-shaped ghost
            node = new Node("a ghost-controlled thing");
            node.addControl(ghos);
            
        Box door = new Box(2f, 5f, 1f);
        Geometry doorG = new Geometry("door", door);
        Material blockMat = m.makeMaterial("light", ColorRGBA.Magenta);
        doorG.setMaterial(blockMat);
        doorG.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        doorNode.attachChild(doorG);
        m.getRootNode().attachChild(doorNode);
        m.setPosition(doorNode, 18f, 6f, 0);
        m.applyPhysics(doorG, "rigid", 0f);
    }
}
