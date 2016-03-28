/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.bulletphysics.dynamics.RigidBody;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Belcky
 */
public class Player {

    public Node playerNode;
    public Main m;
    public Control playerCon;

    /*creates player model and its controls*/
    public Player(Main m) {
        //Makes player
        this.playerNode = new Node();
        this.m = m;
        this.playerCon = new PlayerControl();
        
               GhostControl ghost = new GhostControl(
            new BoxCollisionShape(new Vector3f(1,1,1)));  // a box-shaped ghost
            Node node = new Node("a ghost-controlled thing");
            node.addControl(ghost);
            
        Cylinder c = new Cylinder(32, 32, 1f, 2.5f, true);
        Material playMat = m.makeMaterial("light", ColorRGBA.Yellow);
        Geometry playerG = new Geometry("player", c);
        playerG.rotate(FastMath.HALF_PI, 0, 0);
        playerG.setMaterial(playMat);
        playerNode.attachChild(playerG);
        playerNode.setLocalTranslation(-18, 2.2f, 0);
        playerG.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
               node.attachChild(playerNode);
                m.getRootNode().attachChild(node);
            
        m.getRootNode().attachChild(playerNode);
        m.applyPhysics(playerG, "rigid", 1000f); //makes player physical
        setControls(); 
        playerNode.addControl(playerCon);
    }

    //Sets keyboard controls
    public void setControls() {
        m.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_J));
        m.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_L));
        m.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        m.getInputManager().addListener(analogListener, "Up", "Down", "Left", "Right");
        m.getInputManager().addListener(actionListener, "Jump");
    }
    
    //handles what to do for continuous key holds
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("Left")) {
                RigidBodyControl control = (RigidBodyControl) playerNode.getChild(0).getControl(0);
                control.setLinearVelocity(new Vector3f(-3f, 0, 0));
            }
            if (name.equals("Right")) {
                RigidBodyControl control = (RigidBodyControl) playerNode.getChild(0).getControl(0);
                control.setLinearVelocity(new Vector3f(3f, 0, 0));
            }
        }
    };
    
    //handles what to do for single key press
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Jump") && isPressed) {
                RigidBodyControl control = (RigidBodyControl) playerNode.getChild(0).getControl(0);
                control.setLinearVelocity(new Vector3f(0, 15f, 0));
            }
        }
    };

    //control for player. Used for updating/states
    class PlayerControl extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
        }
    }
}
