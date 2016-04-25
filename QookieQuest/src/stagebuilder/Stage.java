package stagebuilder;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;
import java.util.List;
import mygame.Main;
import mygame.PauseState;

/**
 *
 * @author Belcky
 */

/*Abstract for creating level stages. Sets up basic basic level features such as
 * ground, platforms, obstacles, and exit
 */
public abstract class Stage extends AbstractAppState implements ActionListener {

    protected Main m;
    protected int NUMPLATFORMS;
    protected int NUMBLOCKS;
    protected int NUMTRAMPOLINES;
    protected Node groundNode;
    protected Node doorNode;
    protected Node[] platformNodes;
    protected Node[] blockNodes;
    protected Node[] trampolineNodes;
    protected Spatial sky;
    protected Spatial[] lastState;
    protected boolean cleared;
    public static final boolean withKey = true;
    public static final boolean withoutKey = false;
    public boolean key = withoutKey; //Checks if player collected keys
    protected int mazeSize[] = new int[2];
    protected Cookie c;
    protected String newMappings[];

    public Stage(Main m, ColorRGBA groundColor) {
        this.m = m;
        NUMPLATFORMS = 0;
        NUMBLOCKS = 0;
        NUMTRAMPOLINES = 0;
        groundNode = new Node();
        doorNode = new Node();
        platformNodes = new Node[1];
        blockNodes = new Node[1];
        trampolineNodes = new Node[1];
        cleared = false;
        makeGround(groundColor);
        initKeys();
    }

    public Stage(Main m, ColorRGBA groundColor, int numPlats, int numBlocks, int numTrampos) {
        this.m = m;
        if (numPlats >= 0) {
            NUMPLATFORMS = numPlats;
            platformNodes = new Node[NUMPLATFORMS];
        }
        if (numBlocks >= 0) {
            NUMBLOCKS = numBlocks;
            blockNodes = new Node[NUMBLOCKS];
        }
        if (numTrampos >= 0) {
            NUMTRAMPOLINES = numTrampos;
            trampolineNodes = new Node[NUMTRAMPOLINES];
        }
        groundNode = new Node();
        doorNode = new Node();
        cleared = false;
        makeGround(groundColor);
        this.initKeys();
        setcook();
    }

    //Used for loading up previous state of the stage
    public Stage(Stage lvl, boolean clear) {
        this.m = lvl.m;
        this.NUMPLATFORMS = lvl.NUMPLATFORMS;
        this.NUMBLOCKS = lvl.NUMBLOCKS;
        this.NUMTRAMPOLINES = lvl.NUMTRAMPOLINES;
        this.platformNodes = lvl.platformNodes;
        this.blockNodes = lvl.blockNodes;
        this.trampolineNodes = lvl.trampolineNodes;
        this.sky = lvl.sky;
        this.groundNode = lvl.groundNode;
        this.doorNode = lvl.doorNode;
        this.mazeSize = lvl.mazeSize;
        this.lastState = lvl.lastState;
        this.cleared = clear;
        this.initKeys();
    }

    protected void makeGround(ColorRGBA color) {
        //Creates and attaches the geometry for the ground
        Box ground = new Box(20, 1, 5);
        Geometry groundG = new Geometry("ground", ground);
        Material groundMat = m.makeMaterial("light", new ColorRGBA[]{color});
        groundG.setMaterial(groundMat);
        groundG.setShadowMode(RenderQueue.ShadowMode.Receive);
        groundNode.setName("groundNode");
        groundNode.attachChild(groundG);
        m.getRootNode().attachChild(groundNode);
        //Applies the physics
        m.applyPhysics(groundG, "rigid", 0f);
    }

    public int getNumPlatforms() {
        return NUMPLATFORMS;
    }

    public void setNumPlatforms(int amount) {
        NUMPLATFORMS = amount;
    }

    public int getNumBlocks() {
        return NUMBLOCKS;
    }

    public void setNumBlocks(int amount) {
        NUMBLOCKS = amount;
    }

    public int getNumTrampolines() {
        return NUMTRAMPOLINES;
    }

    public void setNumTrampolines(int amount) {
        NUMTRAMPOLINES = amount;
    }

    public Node getGroundNode() {
        return groundNode;
    }

    public Node getDoorNode() {
        return doorNode;
    }

    public Node[] getPlatformNodes() {
        return platformNodes;
    }

    public Node[] getBlockNodes() {
        return blockNodes;
    }

    public Node[] getTrampolineNodes() {
        return trampolineNodes;
    }

    public Spatial getSky() {
        return sky;
    }

    public boolean getClear() {
        return cleared;
    }

    public int[] getMazeSize() {
        return mazeSize;
    }

    public void setBackground(String image) {
        //Creates and attaches background image
        sky = SkyFactory.createSky(
                m.getAssetManager(), image, true);
        m.getRootNode().attachChild(sky);
    }

    public void setMazeSize(int rows, int cols) {
        mazeSize[0] = rows;
        mazeSize[1] = cols;
    }

    //Saves the current state of the stage
    protected void saveState() {
        int numObjs = m.getRootNode().getChildren().size();
        int i = 0;
        this.lastState = new Spatial[numObjs];
        for (Spatial ob : m.getRootNode().getChildren()) {
            lastState[i] = ob;
            i++;
        }
    }

    protected void load() {
        if (lastState != null && lastState.length > 0) {
            for (Spatial obj : lastState) {
                if (obj instanceof Node) {
                    Node obNode = (Node) obj;
                    for (Spatial child : obNode.getChildren()) {
                        if (child.getControl(RigidBodyControl.class) != null) {
                            m.applyPhysics(child.getControl(RigidBodyControl.class));
                        }
                    }
                }
                if (obj.getControl(RigidBodyControl.class) != null) {
                    m.applyPhysics(obj.getControl(RigidBodyControl.class));
                }
                m.getRootNode().attachChild(obj);
            }
        }
        lastState = null;
    }

    protected void setKeys() {
    }

    protected void setcook() {
        c = new Cookie(m);
        c.setLocalTranslation(0, 5, 0);
        m.applyPhysics(c, "ghost", 5);
    }

    public void initKeys() {
        m.getInputManager().addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        m.getInputManager().addListener(this, newMappings =new String[]{"Pause"});
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Pause") && isPressed) {
            PauseState pause = new PauseState(this);
            AppStateManager asm = m.getStateManager();
            asm.detach(this);
            asm.attach(pause);
        }
    }

    public abstract void setupDoor();

    public abstract void setupPlatforms();

    public abstract void setupBlocks();

    public abstract void setupTrampolines();
}
