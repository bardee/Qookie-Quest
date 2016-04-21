package maze3d;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import java.awt.Dimension;
import com.jme3.texture.Texture;
import java.awt.Toolkit;
import java.util.LinkedList;
import mygame.Main;

public class Maze3D {

    protected Main m;
    protected Maze mazeData; //The 2d maze data
    protected Node mazeNode; //The 3d maze;
    protected int rows, cols;
    protected Material wallMat, floorMat;
    protected Geometry geomLarge, geomSmall, geomGround;

    //Empty 3D Maze constructor. Used for wanting to build new maze manually
    public Maze3D(Main m) {
        this.m = m;
        this.mazeNode = new Node();
        this.rows = 0;
        this.cols = 0;
    }

    //Randomly constructs a 3D maze of the specified size
    public Maze3D(Main m, int rows, int cols, Material wallMat, Material floorMat) {
        this.m = m;
        this.mazeNode = new Node();
        this.wallMat = wallMat;
        this.floorMat = floorMat;

        if (rows >= 2) {
            this.rows = rows;
        }
        if (cols >= 2) {
            this.cols = cols;
        }
        this.mazeData = new Maze(rows, cols, false);
        buildMaze(rows, cols);
    }

    //Constructs 3D Maze from user's provided 2D maze
    public Maze3D(Main m, Maze maze, Material wallMat, Material floorMat) {
        this.m = m;
        this.mazeNode = new Node();
        this.mazeData = maze;
        this.wallMat = wallMat;
        this.floorMat = floorMat;
        mazeNode.setName("mazeNode");

        String data = maze.toString();
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == ' ') {
                cols++;
            }
            if (data.charAt(i) == '\n') {
                rows++;
            }
        }
        cols /= rows;

        buildMaze(rows, cols);
        mazeNode.move(0f, 4f, 0f);
    }

    //Actually makes the maze
    public void buildMaze(int rows, int cols) {
        Box eastBorder, southBorder, f;
        Geometry eastb, southb, floor;
        MazeCell cell;
        int r = 0, c = 0;

        for (r = 0; r < rows; r++) {
            for (c = 0; c < cols; c++) {
                cell = new MazeCell(mazeData, wallMat, r, c);
                if (!cell.getChildren().isEmpty()) {
                    mazeNode.attachChild(cell);
                    for (Spatial walls : cell.getChildren()) {
                        m.applyPhysics((Geometry) walls, "rigid", 0); // Applies the physics
                    }
                }
            }
        }
        //Creates remaining borders
        eastBorder = new Box(.5f, 1f, rows + .5f);
        eastb = new Geometry("Eastwall", eastBorder);
        eastb.setMaterial(wallMat);
        eastb.setLocalTranslation(cols * 2f, 0f, rows - .5f);
        mazeNode.attachChild(eastb);
        m.applyPhysics(eastb, "rigid", 0);

        southBorder = new Box((cols - 1) + .5f, 1f, .5f);
        southb = new Geometry("Southwall", southBorder);
        southb.setMaterial(wallMat);
        southb.setLocalTranslation(cols - 1, 0f, (rows * 2f) - .5f);
        mazeNode.attachChild(southb);
        m.applyPhysics(southb, "rigid", 0);

        f = new Box(cols + .5f, .25f, rows + .5f);
        floor = new Geometry("floor", f);
        floor.setMaterial(floorMat);
        floor.setLocalTranslation(cols, -1.25f, rows - .5f);
        mazeNode.attachChild(floor);
        m.applyPhysics(floor, "rigid", 0);

        m.getRootNode().attachChild(mazeNode);
    }

    public Node getMazeNode() {
        return mazeNode;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setWallMat(Material wallMat) {
        this.wallMat = wallMat;
    }

    public void setFloorMat(Material floorMat) {
        this.floorMat = floorMat;
    }

    public void move(Vector3f position) {
        RigidBodyControl con = null;
        for (Spatial child : mazeNode.getChildren()) {
            if (child instanceof Node) {
                Node childNode = (Node) child;
                for (Spatial ances : childNode.getChildren()) {
                    con = ances.getControl(RigidBodyControl.class);
                    con.setPhysicsLocation(ances.getWorldTranslation().add(position));
                }
            } else {
                con = child.getControl(RigidBodyControl.class);
                con.setPhysicsLocation(child.getWorldTranslation().add(position));
            }
        }
        mazeNode.move(position);
    }
}
