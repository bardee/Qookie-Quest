package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.ui.Picture;
import java.util.logging.Level;
import java.util.logging.Logger;
import stagebuilder.Stage;

public class PauseState extends AbstractAppState implements ActionListener {

    private Main main;
    private Picture logoPic;
    private Stage prevState;
    private String newMappings[];

    public PauseState(Stage prev) {
        this.prevState = prev;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.main = (Main) app;
        //Attach pause text
        logoPic = new Picture("startScreenLogo");
        logoPic.setImage(app.getAssetManager(), "Interface/pauseText.png", true);
        logoPic.setWidth(main.getAppSettings().getWidth() * .7f);
        logoPic.setHeight(main.getAppSettings().getHeight() * .5f);
        logoPic.setPosition(
                main.getAppSettings().getWidth() * .15f,
                main.getAppSettings().getHeight() * .30f);
        main.getGuiNode().attachChild(logoPic);
        //Keys
        InputManager inputManager = main.getInputManager();
        inputManager.addMapping("Unpause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(this, newMappings = new String[]{"Unpause"});
    }

    @Override
    public void update(float tpf) {
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        //Pausing game during round
        if (name.equals("Unpause") && isPressed) {
            System.out.println("Unpaused!");
            //transition to initial break screen
            if (prevState != null) {
                Class c = prevState.getClass();
                AppStateManager asm = main.getStateManager();
                Stage load = null;
                try {
                    load = (Stage) c.getConstructor(Stage.class, boolean.class).newInstance(prevState, false);
                } catch (Exception ex) {
                }
                asm.detach(this);
                asm.attach(load);
            }
        }
    }

    @Override
    public void cleanup() {
        main.getGuiNode().detachChild(logoPic);
        main.deleteInputMappings(newMappings);
    }
}