/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stagebuilder;

import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import mygame.Main;

/**
 *
 * @author Belcky
 */
public class Cookie extends Node {

    public Cookie(Main m) {
        Node body = (Node) m.getAssetManager().loadModel("Models/Cookie/body.mesh.j3o");
        Node chips = (Node) m.getAssetManager().loadModel("Models/Cookie/chips.mesh.j3o");
        body.setMaterial(m.getAssetManager().loadMaterial("Materials/cookie.j3m"));
        chips.setMaterial(m.getAssetManager().loadMaterial("Materials/chips.j3m"));
        this.attachChild(body);
        this.attachChild(chips);
        this.setModelBound(new BoundingBox());
        this.updateModelBound();
        this.setShadowMode(RenderQueue.ShadowMode.Cast);
        this.setLocalScale(0.04f,0.04f,0.04f);
        this.setName("cookie");
        m.getRootNode().attachChild(this);
    }
}
