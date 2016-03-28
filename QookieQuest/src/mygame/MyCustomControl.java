/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author mikey ha
 */
public class MyCustomControl extends RigidBodyControl
        
    implements PhysicsCollisionListener {
    
    Main handle;
    Level1 L;
    public MyCustomControl(Main m, Level1 here) {
        m.bullet.getPhysicsSpace().addCollisionListener(this);
        handle=m;
        L=here;
    }

    public void collision(PhysicsCollisionEvent event) {
        
           if ( event.getNodeA().getName().equals("block_1") ) {
             Spatial node = event.getNodeA();
             handle.getRootNode().detachChild(L.blockNodes[0]);
             handle.getRootNode().detachChild(node);
             L.key=L.withKey;
               System.out.println("Player has the key");
            handle.getRootNode().detachChild(node);
        } else if ( event.getNodeB().getName().equals("player") ) {
             Spatial node = event.getNodeB();
            handle.getRootNode().detachChild(node);
        }  
           if (event.getNodeA().getName().equals("door")){
               System.out.println("door is hit");
               
            if(L.key==L.withKey){
               Spatial node = event.getNodeA();
             handle.getRootNode().detachChild(L.doorNode);
             handle.getRootNode().detachChild(node);
               System.out.println("Player exit");
               System.exit(0);
            }
            
            
        }
    }
    
}
