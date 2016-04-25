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
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import stagebuilder.Stage;

/**
 *
 * @author Swati
 * 
 * This is the third level of the game. The outline of code in this class is very similar to the second level of the game, 
 * but I added some extra additions to this level. As this is the third level of the game and is meant to be the hardest, 
 * besides moving the platforms at a unform pace left to right, I also made the even numbered platform nodes move along
 * Y-Coordinate system simultaneously. I achieved this by overriding update() and adding an additional if statement to 
 * check if the platform node was an even numbered. If the platform was even numbered, I move it along the Y-Coordinate 
 * system simutaneously at a uniform pace. 
 * 
 * As in Level 2, I made the platform kinematic by adding mass initially and setting the kinematic property to true for 
 * the platforms later to move them. I was able to synchronize the movement by calling move() on individual platformNodes 
 * and assigned same speed rate to all the nodes. To move the platforms in both direction on X-Coordinate and Y-Coordinate 
 * (for even numbered nodes), I used a boolean value to decide whether the platform has to move left or right. 
 * 
 */
public class Level3 extends Stage {

    private int factor = 1;
    private float time = 0;
    private boolean movePlatformRight = true;
    private Control myPlatformControl; // platform control used to move the platform
    private int MAX_COOKIES;  // Maximum number of cookies on this level.
    private final float PLATFORM_MOVE_HORIZONTAL_FACTOR = 0.5f;  // The platform moves at this pace in the X direction
    private final float PLATFORM_MOVE_VERTICAL_FACTOR = 0.6f; // The platform moves at this pace in the Y direction
    
    public Level3(Main m, ColorRGBA color, int plates, int blocks, int tramps) {
        super(m, color, plates, blocks, tramps);
        setupPlatforms();  // Sets up the platforms
        MAX_COOKIES = plates;  
        setupBlocks();  // Setsup the cookies
        setupDoor();  // Setsup the door
        setBackground("Textures/cookies.jpg");
        m.initCam(true, 10, new Vector3f(0, 15f, 100f), Vector3f.ZERO, Vector3f.UNIT_Y);
        setMazeSize(3, 3);
    }

    public Level3(Stage lvl, boolean cleared) {
        super(lvl, cleared);
        m.bgMusic.stop();
        m.initBGM("Sounds/ringydingy.ogg");
        m.initCam(true, 10, new Vector3f(0, 15f, 100f), Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        load();
        System.out.println("Entering level 3...");
    }
    
    // This method sets up the platforms for this level.
    @Override
    public void setupPlatforms() {
        Box platform = new Box(3, .25f, 5);
        Material mat1 = m.makeMaterial("light", new ColorRGBA[]{ColorRGBA.Red});
        Material mat2 = m.makeMaterial("light", new ColorRGBA[]{ColorRGBA.Blue});
        myPlatformControl = new PlatformControl();
        //Creates and attaches the platform geometries
        Geometry[] platsG = new Geometry[NUMPLATFORMS];
        for (int i = 0; i < NUMPLATFORMS; i++) {
            platsG[i] = new Geometry("platform_" + (i + 1), platform);
            myPlatformControl = new PlatformControl();  // Creating an object of inner custom class of control.
            // Assign different colors to the platforms of this level. 
            if (i % 2 == 0) {
                platsG[i].setMaterial(mat1);
            } else {
                platsG[i].setMaterial(mat2);
            }
            platformNodes[i] = new Node(); // Creating a new node and initializing the node for this index in the array of nodes.
            platformNodes[i].setName("platformNode" + (i + 1)); // Set the name of the node. 
            platformNodes[i].addControl(myPlatformControl); // Add the control to the platform.
            platformNodes[i].attachChild(platsG[i]);  // Attach teh geometry to the platform node.
            platsG[i].setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            m.applyPhysics(platformNodes[i], "rigid", 1f);  // Apply pphysics to the platform nodes.
            platformNodes[i].getControl(RigidBodyControl.class).setKinematic(true); // Make the platforms kinematic so that they can move.
            m.getRootNode().attachChild(platformNodes[i]); // Attach the platform nodes to the root node.
        }

        //Positions the platforms
        Vector3f prevPos = new Vector3f(-20, 2, 0);
        for (int i = 0; i < NUMPLATFORMS; i++) {
            if (i > 0) {
                prevPos = platformNodes[i - 1].getLocalTranslation();
            }
            m.setPosition(platformNodes[i], prevPos.x + 10, prevPos.y + 5, 0); // Set the position of the platforms.
            //Applies the physics
            m.applyPhysics((Geometry) platformNodes[i].getChild(0), "rigid", 0f);
        }
    }

    // This is the update loop which move the platforms in the X and Y directions simultaneously.
    @Override
    public void update(float tpf) {
        for (int counter = 0; counter < platformNodes.length; counter++) {
            if (movePlatformRight) {
                // Move the platform in the X direction
                platformNodes[counter].move(PLATFORM_MOVE_HORIZONTAL_FACTOR, PLATFORM_MOVE_HORIZONTAL_FACTOR / 10, 0);
                if(counter % 2 == 0)
                // Move the platform in the Y direction if the counter is even.
                    platformNodes[counter].move(0, PLATFORM_MOVE_HORIZONTAL_FACTOR / 6, 0);
            } else {
                // Move the platform in the X direction
                platformNodes[counter].move(-PLATFORM_MOVE_HORIZONTAL_FACTOR, -PLATFORM_MOVE_HORIZONTAL_FACTOR / 10, 0);
                if(counter % 2 == 0)
                // Move the platform in the Y direction if the counter is even.
                    platformNodes[counter].move(0, -PLATFORM_MOVE_HORIZONTAL_FACTOR/6 , 0);
            }
            // Set the position of the cookies so that they are on the platforms when the platforms are moving. 
            Vector3f pos = platformNodes[counter].getLocalTranslation();
            m.setPosition(blockNodes[counter], pos.x, pos.y + .3f, pos.z);
        }
        if (time > factor * 2) {
            factor++;
            movePlatformRight = !movePlatformRight;
        }
        
        time += tpf;
    }

    // Blocks here are reffered to cookies. These cookies are the objects which the player has to collect to attain 
    // the key and get to the next level of the game. 
    @Override
    public void setupBlocks() {
        //Creates and attaches cookie geometries
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

        //For positioning the cookies exactly on teh platforms so that the cookies move with
        Vector3f prevPos = new Vector3f(-20, 3.25f, 0);
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

    // Set the door for this level of the game.
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
        m.deleteInputMappings(newMappings);
        System.out.println("Cleaning up level 3...");
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