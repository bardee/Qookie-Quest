package mygame;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Belcky
 */

/*Abstract for creating level stages. Sets up basic basic level features such as
 * ground, platforms, obstacles, and exit
 */
public abstract class Stage {

    Main m;
    protected int NUMPLATFORMS;
    protected int NUMBLOCKS;
    protected int NUMTRAMPOLINES;
    protected Node groundNode;
    protected Node doorNode;
    protected Node[] platformNodes;
    protected Node[] blockNodes;
    protected Node[] trampolineNodes;
    protected Spatial sky;

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
        makeGround(groundColor);
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
        makeGround(groundColor);
    }

    protected void makeGround(ColorRGBA color) {
        Box ground = new Box(20, 1, 5);
        Geometry groundG = new Geometry("ground", ground);
        Material groundMat = m.makeMaterial("light", color);
        groundG.setMaterial(groundMat);
        groundG.setShadowMode(RenderQueue.ShadowMode.Receive);
        groundNode.attachChild(groundG);
        m.getRootNode().attachChild(groundNode);
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

    public abstract void setupDoor();
    
    public abstract void setupPlatforms();

    public abstract void setupBlocks();

    public abstract void setupTrampolines();
    
    public abstract void setBackground(String image);
}
