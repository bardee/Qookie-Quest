/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import stagebuilder.Stage;

/**
 *
 * @author Swati
 * 
 * This is the second level of the game. The outline of code in this class is very similar to the first level of the game, 
 * but I added some extra additions to this level. As this is the second level of the game and is meant to be harder, 
 * I moved the platforms at a unform pace from left to right. I achieved this by overriding update(). Unlike the previous 
 * level, I also added multiple cookies to the platforms to this stage. 
 * 
 * To move the platforms move, I made them kinematic by adding mass initially and setting the kinematic property to true for 
 * the platforms. I was able to synchronize their movement by calling move() on individual platformNodes and assigned same 
 * speed rate to all the nodes. To move the platforms in both direction on X-Coordinate, I used a boolean value and 
 * used it to decide whether the platform moved left or right. 
 * 
 */

public class Level2 extends Stage {

    private boolean movePlatformRight = true;
    private Control myPlatformControl; // platform control used to move the platform
    private float time = 0;
    private int factor = 1;
    private int MAX_COOKIES ; // Count of maximum number of cookies
    private final float PLATFORM_MOVE_FACTOR = 0.25f; // Each platform in this level moves by this constant value
    
    public Level2(Main m, ColorRGBA color, int platformCount, int cookieCount, int tramps) {
        super(m, color, platformCount, cookieCount, tramps);
        setupPlatforms();
        MAX_COOKIES = cookieCount;
        setupBlocks(); // Set up the cookies on the stage
        setupDoor();  // Set up the door for this particular level
        setBackground("Textures/cookies.jpg");
        m.initCam(true, 10, new Vector3f(0, 15f, 100f), Vector3f.ZERO, Vector3f.UNIT_Y);
        setMazeSize(3, 3);
    }

    public Level2(Stage lvl, boolean cleared) {
        super(lvl, cleared);
        m.bgMusic.stop();
        m.initBGM("Sounds/ringydingy.ogg");
        m.initCam(true, 10, new Vector3f(0, 15f, 100f), Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        load();
        System.out.println("Entering level 2...");
    }

    // This method sets up the platform for level 2. I used controls and made the platforms kinematic in this method.  
    @Override
    public void setupPlatforms() {
        Box platform = new Box(3, .25f, 5);
        Material mat1 = m.makeMaterial("light", new ColorRGBA[]{ColorRGBA.Red}); 
        Material mat2 = m.makeMaterial("light", new ColorRGBA[]{ColorRGBA.Blue});

        //Creates and attaches the platform geometries
        Geometry[] platsG = new Geometry[NUMPLATFORMS];
        for (int i = 0; i < NUMPLATFORMS; i++) {
            platsG[i] = new Geometry("platform_" + (i + 1), platform);
            myPlatformControl = new PlatformControl(); // Creating an object of inner custom class of control.
            platsG[i].addControl(myPlatformControl); // Add my own inner custom class custom to the platform.
            // To diversify the colors of the platform.
            if (i % 2 == 0) {
                platsG[i].setMaterial(mat1); 
            } else {
                platsG[i].setMaterial(mat2);
            }
            platformNodes[i] = new Node();  // Creating a new node and initializing the node for this index in the array of nodes.
            platformNodes[i].setName("platformNode" + (i + 1)); // Set the name of the node. 
            platformNodes[i].attachChild(platsG[i]);  // Attach the geometry to the node.
            platsG[i].setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            m.applyPhysics(platformNodes[i], "rigid", 1f); // Apply Physics to the node.
            platformNodes[i].getControl(RigidBodyControl.class).setKinematic(true); // Make the platform kinematic so that it can move.
            m.getRootNode().attachChild(platformNodes[i]);  // Attach teh platform node to root node. 
        }

        //Positions the platforms
        Vector3f prevPos = new Vector3f(-20, 2, 0);
        for (int i = 0; i < NUMPLATFORMS; i++) {
            if (i > 0) {
                prevPos = platformNodes[i - 1].getLocalTranslation();
            }
            m.setPosition(platformNodes[i], prevPos.x + 10, prevPos.y + 5, 0); // Set position of the platforms. 
            //Applies the physics
            m.applyPhysics((Geometry) platformNodes[i].getChild(0), "rigid", 0f);
        }
    }

    // This method updates the movement of the platforms. 
    @Override
    public void update(float tpf) {

        for (int counter = 0; counter < platformNodes.length; counter++) {
            if (movePlatformRight) {
                platformNodes[counter].move(-PLATFORM_MOVE_FACTOR, 0, 0);
            } else {
                platformNodes[counter].move(PLATFORM_MOVE_FACTOR, 0, 0);
            }
            // Set the cookies on the platform so that the cookies move with the platform. 
            Vector3f pos = platformNodes[counter].getLocalTranslation(); 
            m.setPosition(blockNodes[counter], pos.x, pos.y + .3f, pos.z); // Set the position of cookie at new position.
        }
        if (time > factor * 2) {
            factor++;
            movePlatformRight = !movePlatformRight;
        }
        time += tpf;
    }

    // Setsup the cookies for this level.
    @Override
    public void setupBlocks() {
        //Creates and attaches block geometries
        Box cookie = new Box(1f, 1f, 1f);
        Geometry[] cookiesGeometry = new Geometry[MAX_COOKIES];

        for (int counter = 0; counter < MAX_COOKIES; counter++) {
            Material cookieMaterial = m.makeMaterial("light", new ColorRGBA[]{ColorRGBA.randomColor()});
            cookiesGeometry[counter] = new Geometry("cookie_" + (counter + 1), cookie);
            cookiesGeometry[counter].setMaterial(cookieMaterial);
            blockNodes[counter] = new Node();
            blockNodes[counter].setName("cookieNode_" + counter);
            blockNodes[counter].attachChild(cookiesGeometry[counter]);
            cookiesGeometry[counter].setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            m.getRootNode().attachChild(blockNodes[counter]);
        }

        //Positions the cookies
        Vector3f prevPos = new Vector3f(-20, 3, 0);
        for (int counter = 0; counter < MAX_COOKIES; counter++) {
            if (counter > 0) {
                prevPos = blockNodes[counter - 1].getLocalTranslation();
            }
            m.setPosition(blockNodes[counter], prevPos.x + 10, prevPos.y + 5, 0);
            //Applies the physics
            m.applyPhysics((Geometry) blockNodes[counter].getChild(0), "rigid", 0f);
        }
    }

    @Override
    public void setupTrampolines() {
    }

    // Sets the position of the door
    @Override
    public void setupDoor() {
        //Creates door geometry and attaches it
        Box door = new Box(2f, 5f, 1f);
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
        System.out.println("Cleaning up level 2...");
    }

    class PlatformControl extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
        }
    }
}