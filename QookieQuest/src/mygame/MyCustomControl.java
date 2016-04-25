/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import javax.swing.JOptionPane;
import stagebuilder.Stage;

/**
 *
 * @author mikey ha
 */
public class MyCustomControl implements PhysicsCollisionListener, ActionListener {

    private Main main;
    private Stage L;
    private String mappings[];
    private int doAction = IDLE; // Specifies ENTER trigger action
    private static final int IDLE = 0, ENTER = 1;

    public MyCustomControl(Main m, Stage here) {
        m.bullet.getPhysicsSpace().addCollisionListener(this);
        main = m;
        L = here;
        main.getInputManager().addMapping("Action", new KeyTrigger(KeyInput.KEY_RETURN));
        main.getInputManager().addListener(this, mappings = new String[]{"Action"});
    }

    public void collision(PhysicsCollisionEvent event) {
        if (checkNodeCollide("player", "block", event)) {
            obtainKey(event);
        }
        if (compareDistance(main.player.playerNode.getChild(0),
                L.getDoorNode().getChild(0), 5)) {
            doAction = ENTER;
        } else {
            doAction = IDLE;
        }
        if(L.getClear()){
            System.out.println("Moving onto next Stage!");
            JOptionPane.showMessageDialog(null, "The total score for "+L.toString()+" is "+L.getMaxUserScore());
            System.exit(0);
        }
    }

    /*Checks to see if two nodes containing the specified strings are colliding 
    with each other*/
    public boolean checkNodeCollide(String node1, String node2, PhysicsCollisionEvent event) {
        if (event.getNodeA().getName().contains(node1)
                && event.getNodeB().getName().contains(node2)) {
            return true;
        } else if (event.getNodeB().getName().contains(node1)
                && event.getNodeA().getName().contains(node2)) {
            return true;
        }
        return false;
    }

    //Checks if two spatials are within a certain distance of each other
    public boolean compareDistance(Spatial a, Spatial b, float distance) {
        if (a != null && b != null) {
            Vector3f vA = a.getWorldTranslation();
            Vector3f vB = b.getWorldTranslation();

            if (vA.distance(vB) <= distance) {
                return true;
            }
        }
        return false;
    }

    //Checks if a single node is currently colliding
    public Spatial checkSingleNodeCollide(String node, PhysicsCollisionEvent event) {
        if (event.getNodeA().getName().contains(node)) {
            return event.getNodeA();
        } else if (event.getNodeB().getName().contains(node)) {
            return event.getNodeB();
        }
        return null;
    }

    //attempts to gain the keys
    private void obtainKey(PhysicsCollisionEvent event) {
        Spatial node = checkSingleNodeCollide("block", event);
        main.getRootNode().detachChild(L.getBlockNodes()[0]);
        L.key = L.withKey;
        System.out.println("Player has the key");
    }

    //Attempts to unlock the door
    private void tryOpenDoor() {
        doAction = IDLE;
        if (L.key == L.withKey) {
            System.out.println("Player exit");
            System.exit(0);
        } else {
            AppStateManager asm = main.getStateManager();
            main.deleteInputMappings(mappings);
            asm.detach(L);
            int maze[] = L.getMazeSize();
            asm.attach(new PuzzleState(L, maze[0],maze[1]));
        }
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Action") && isPressed) {
            if (doAction == ENTER) {
                tryOpenDoor();
            }
        }
    }
}
