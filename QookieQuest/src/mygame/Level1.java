package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import stagebuilder.Stage;
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

    public Level1(Main m, ColorRGBA color, int plates, int blocks, int tramps) {
        super(m, color, plates, blocks, tramps);
        setupPlatforms();
        setupBlocks();
        setupDoor();
        setBackground("Textures/cookies.jpg");
        m.initCam(true, 10, new Vector3f(0, 15f, 100f), Vector3f.ZERO, Vector3f.UNIT_Y);
        setMazeSize(3, 3); // Creates the maze
    }

    public Level1(Stage lvl, boolean cleared) {
        super(lvl, cleared);
        m.initCam(true, 10, new Vector3f(0, 15f, 100f), Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        load();
        m.bgMusic.stop();
        m.initBGM("Sounds/ringydingy.ogg");
        System.out.println("Entering level 1...");
    }

    @Override
    public void setupPlatforms() {
        Box platform = new Box(3, .25f, 5);
        Material mat1 = m.makeMaterial("light", new ColorRGBA[]{ColorRGBA.Red});
        Material mat2 = m.makeMaterial("light", new ColorRGBA[]{ColorRGBA.Blue});

        //Creates and attaches the platform geometries
        Geometry[] platsG = new Geometry[NUMPLATFORMS];
        for (int i = 0; i < NUMPLATFORMS; i++) {
            platsG[i] = new Geometry("platform_" + (i + 1), platform);
            if (i % 2 == 0) {
                platsG[i].setMaterial(mat1);
            } else {
                platsG[i].setMaterial(mat2);
            }
            platformNodes[i] = new Node();
            platformNodes[i].setName("platformNode" + (i + 1));
            platformNodes[i].attachChild(platsG[i]);
            platsG[i].setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            m.getRootNode().attachChild(platformNodes[i]);
        }

        //Positions the platforms
        Vector3f prevPos = new Vector3f(-20, 2, 0);
        for (int i = 0; i < NUMPLATFORMS; i++) {
            if (i > 0) {
                prevPos = platformNodes[i - 1].getLocalTranslation();
            }
            m.setPosition(platformNodes[i], prevPos.x + 10, prevPos.y + 5, 0);
            //Applies the physics
            m.applyPhysics((Geometry) platformNodes[i].getChild(0), "rigid", 0f);
        }
    }

    @Override
    public void setupBlocks() {
        //Creates and attaches block geometries
        Box block = new Box(1f, 1f, 1f);
        Geometry blockG = new Geometry("block_1", block);
        Material blockMat = m.makeMaterial("light", new ColorRGBA[]{ColorRGBA.Cyan});
        blockG.setMaterial(blockMat);
        blockG.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        blockNodes[0] = new Node();
        blockNodes[0].setName("blockNode1");
        blockNodes[0].attachChild(blockG);
        m.getRootNode().attachChild(blockNodes[0]);
        //Positions blocks
        Vector3f pos = platformNodes[1].getLocalTranslation();
        m.setPosition(blockNodes[0], pos.x, pos.y + .3f, pos.z);
        //APplies the physics
        m.applyPhysics(blockG, "rigid", 10f);
    }

    @Override
    public void setupTrampolines() {
    }

    @Override
    public void setupDoor() {
        //Creates door geometry and attaches it
        Box door = new Box(2f, 5f, 2.5f);
        Geometry doorG = new Geometry("door", door);
        Material blockMat = m.makeMaterial("light", new ColorRGBA[]{ColorRGBA.Magenta});
        doorG.setMaterial(blockMat);
        doorG.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        doorNode.setName("doorNode");
        doorNode.attachChild(doorG);
        m.getRootNode().attachChild(doorNode);
        //Positions door
        m.setPosition(doorNode, 18f, 6f, 0);
        //Applies physics
        m.applyPhysics(doorG, "rigid", 0f);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        saveState(); //Stores the current state
        m.clearPhysicsSpace(); //Removes all current physics
        m.getRootNode().detachAllChildren();
        m.deleteInputMappings(newMappings);
        System.out.println("Cleaning up level 1...");
    }
}
