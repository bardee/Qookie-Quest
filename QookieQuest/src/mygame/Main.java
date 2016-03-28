package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import java.util.LinkedList;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    BulletAppState bullet;
    public Player player;
    public Level1 lvl1;

    public static void main(String[] args) {
        Main app = new Main();
        app.setDisplayStatView(false);
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        //app.setSettings(settings);
        
         settings.setResolution(1914,1040);
         app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bullet = new BulletAppState(); //initializes bullet engine
        stateManager.attach(bullet);
        lvl1 = new Level1(this, ColorRGBA.Green, 3, 1, 0); //sets up stage
        player = new Player(this); //creates player
        initCam();
        initLightandShadow();
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
    public void initCam() {
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(10);
        cam.setLocation(new Vector3f(0, 15f, 100f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    /*creates lighting sources*/
    private void initLightandShadow() {
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
    
    /*generates basic physics control*/
    public void applyPhysics(Geometry obj, String controlType, float mass){
        if(controlType.equals("rigid")){
            RigidBodyControl rigidCon = new RigidBodyControl(mass);
            obj.addControl(rigidCon);
            bullet.getPhysicsSpace().add(rigidCon);
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

    /*sets the position of a node*/
    public void setPosition(Node obj, float x, float y, float z) {
        obj.setLocalTranslation(x, y, z);
    }
}
