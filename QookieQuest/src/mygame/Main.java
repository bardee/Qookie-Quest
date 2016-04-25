package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;

/**
 *
 *
 * @author Belcky
 */
public class Main extends SimpleApplication {

    public static BulletAppState bullet;
    public static Player player;
    public Level1 lvl1;
    public Level2 level2Stage;
    private LinkedList<Control> physics; //Keeps track of physics applied
    public static MyCustomControl logic; //Controls the basic logic of the game
    public AudioNode bgMusic;
    public NiftyJmeDisplay niftyDisplay;
    public Nifty nifty;

    public static void main(String[] args) {
        Main app = new Main();
        app.setDisplayStatView(false);
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 1;
        screen.height *= 1;
        settings.setResolution(screen.width, screen.height);
 
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bullet = new BulletAppState(); //initializes bullet engine
        physics = new LinkedList<Control>();
        stateManager.attach(bullet);
        initGui();
        initLightandShadow();
        MainMenuState mm = new MainMenuState();
        stateManager.attach(mm);
       // initAudio();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /*initializes camera settings*/
    public void initCam(boolean enabled, float movSpeed, Vector3f pos, Vector3f look,
            Vector3f upVec) {
        flyCam.setEnabled(enabled);
        flyCam.setMoveSpeed(movSpeed);
        cam.setLocation(pos);
        cam.lookAt(look, upVec);
    }
    
    public void initGui(){
        niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
    }

    /*creates lighting sources*/
    public void initLightandShadow() {
        // Light1: white, directional
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.7f, -15.0f, 0f)).normalizeLocal());
        sun.setColor(ColorRGBA.Gray);
        rootNode.addLight(sun);

        // Light 2: Ambient, gray
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        rootNode.addLight(ambient);

        // SHADOW
        // the second parameter is the resolution. Experiment with it! (Must be a power of 2)
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 4096, 2);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
    }
    
    //Creates sounds
    public void initAudio(){
        bgMusic = new AudioNode(assetManager, "Sounds/ringydingy.ogg", false);
        bgMusic.setLooping(true);
        bgMusic.setPositional(false);
        bgMusic.setVolume(.5f);
        rootNode.attachChild(bgMusic);
        bgMusic.play();
    }

    /*generates basic physics control and adds to geometry*/
    public void applyPhysics(Spatial obj, String controlType, float mass) {
        Control con = null;
        if (obj != null) {
            if (controlType.equals("rigid")) {
                con = new RigidBodyControl(mass);
                obj.addControl(con);
                bullet.getPhysicsSpace().add(con);
            }
            physics.add(con);
        }
    }

    //adds provided physics control to the physics space
    public void applyPhysics(Control control) {
        Control con = null;
        if (control != null) {
            if (control instanceof RigidBodyControl) {
                con = control;
                bullet.getPhysicsSpace().add(con);
            }
            physics.add(con);
        }
    }

    /*removes a physical object*/
    public void removePhysicsOb(Geometry obj) {
        RigidBodyControl con = obj.getControl(RigidBodyControl.class);
        physics.remove(con);
        bullet.getPhysicsSpace().remove(con);
        obj.removeFromParent();
    }

    /*Clears out entire physics space*/
    public void clearPhysicsSpace() {
        if (physics.size() > 0) {
            System.out.println("Clearing out physics space");
            for (Control con : physics) {
                bullet.getPhysicsSpace().remove(con);
            }
            physics.clear();
        } else {
            System.out.println("Nothing in Physics space to clear out");
        }
    }

    /*generates basic material*/
    public Material makeMaterial(String matType, ColorRGBA color) {
        Material mat;
        if (matType.equals("unshaded")) {
            matType = "Common/MatDefs/Misc/Unshaded.j3md";
            mat = new Material(assetManager, matType);
            mat.setColor("Color", color);
        } else if (matType.equals("light")) {
            matType = "Common/MatDefs/Light/Lighting.j3md";
            mat = new Material(assetManager, matType);
            mat.setBoolean("UseMaterialColors", true);
            mat.setColor("Ambient", color);
            mat.setColor("Diffuse", ColorRGBA.White);
            mat.setColor("Specular", ColorRGBA.Yellow);
            mat.setFloat("Shininess", 10f);
        } else {
            return null;
        }
        return mat;
    }

    /*sets the position of a spatial*/
    public void setPosition(Spatial obj, float x, float y, float z) {
        obj.setLocalTranslation(x, y, z);
    }

    /*Deletes specified input mappings*/
    public void deleteInputMappings(String mappings[]) {
        for (String i : mappings) {
            inputManager.deleteMapping(i);
        }
    }
}