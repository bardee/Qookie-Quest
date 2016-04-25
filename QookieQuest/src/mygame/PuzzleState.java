package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import maze3d.Maze3D;
import stagebuilder.Stage;

public class PuzzleState extends AbstractAppState implements ActionListener, AnalogListener {

    private Main main;
    private Stage prevState;
    private int rows;
    private int cols;
    private Maze3D maze;
    private String mappings[]; //Input mappings
    private Geometry player;
    private Vector3f exit;

    public PuzzleState(Stage prevState, int rows, int cols) {
        this.prevState = prevState;
        if (rows > 1 && cols > 1) {
            this.rows = rows;
            this.cols = cols;
        }
        exit = new Vector3f((cols * 2) - 1, 0, (rows * 2) - .5f);
    }

    public PuzzleState(Stage prevState, Maze3D maze) {
        this.prevState = prevState;
        this.maze = maze;
        if (maze != null) {
            this.rows = maze.getRows();
            this.cols = maze.getCols();
        }
        exit = new Vector3f((cols * 2) - 1, 0, (rows * 2) - .5f);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        //Creates maze
        super.initialize(stateManager, app);
        System.out.println("Entering Puzzle State...");
        this.main = (Main) app;
        Maze3D maze;
        if (this.maze != null) {
            maze = this.maze;
        } else {
            maze = new Maze3D(main, rows, cols, main.makeMaterial("light", new ColorRGBA[]{ColorRGBA.Magenta}),
                    main.makeMaterial("unshaded", new ColorRGBA[]{ColorRGBA.Gray}));
        }
        //Sets camera position
        Vector3f pos = maze.findCenter();
        float zoom = (Math.max(rows, cols)*3)/(float)(rows*cols);
        main.initCam(false, 10, new Vector3f(pos.x, (rows * cols) + rows + 1, pos.z+1), pos,
                Vector3f.UNIT_Y);
        main.getCamera().setLocation(new Vector3f(pos.x, ((rows*cols)+rows)*zoom, pos.z));
        //Creates player piece
        initPlayer();

        //Keys
        InputManager inputManager = main.getInputManager();
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(this, "Pause", "Up", "Down", "Left", "Right");
        mappings = new String[]{"Pause", "Up", "Down"};
    }

    private void initPlayer() {
        Box b = new Box(.3f, .3f, .3f);
        player = new Geometry("player", b);
        player.setMaterial(main.makeMaterial("unshaded", new ColorRGBA[]{ColorRGBA.Blue}));
        main.getRootNode().attachChild(player);
        player.setLocalTranslation(1, 0, -.5f);
        main.applyPhysics(player, "rigid", 1);
    }

    @Override
    public void update(float tpf) {
        if (player.getLocalTranslation().distance(exit) <= 1) {
            try {
                leave(true);
            } catch (Exception ex) {
            }
        }
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        //Returns Back to the the level in its last state
        if (name.equals("Pause") && isPressed) {
            try {
                leave(false);
            } catch (Exception ex) {
            }
        }
    }

    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("Up")) {
            RigidBodyControl con = player.getControl(RigidBodyControl.class);
            con.setLinearVelocity(Vector3f.UNIT_Z.negate());
        }
        if (name.equals("Down")) {
            RigidBodyControl con = player.getControl(RigidBodyControl.class);
            con.setLinearVelocity(Vector3f.UNIT_Z);
            System.out.println(player.getWorldTranslation());
        }
        if (name.equals("Left")) {
            RigidBodyControl con = player.getControl(RigidBodyControl.class);
            con.setLinearVelocity(Vector3f.UNIT_X.negate());
        }
        if (name.equals("Right")) {
            RigidBodyControl con = player.getControl(RigidBodyControl.class);
            con.setLinearVelocity(Vector3f.UNIT_X);
        }

    }

    private void leave(boolean cleared) throws Exception {
        if (!cleared) {
            System.out.println("Returning back to stage");
        }
        if (cleared) {
            System.out.println("Cleared maze. Opened door");
        }
        AppStateManager asm = main.getStateManager();
        if (prevState != null) {
            Class c = prevState.getClass();
            Stage load = (Stage) c.getConstructor(Stage.class, boolean.class).newInstance(prevState, cleared);
            main.logic = new MyCustomControl(main, load);
            asm.detach(this);
            asm.attach(load);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        System.out.println("Cleaning up puzzle...");
        main.clearPhysicsSpace();
        main.getRootNode().detachAllChildren();
        main.deleteInputMappings(mappings);
    }
}