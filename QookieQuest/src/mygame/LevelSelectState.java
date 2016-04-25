package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import stagebuilder.Stage;

public class LevelSelectState extends AbstractAppState implements ActionListener, ScreenController {

    Main main;
    private String newMappings[];

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.main = (Main) app;

        //Keys
        InputManager inputManager = main.getInputManager();
        inputManager.addMapping("StartRound", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, newMappings = new String[]{"StartRound"});
        main.nifty.fromXml("Interface/Menus.xml", "LevelSelect", this);
// nifty.fromXml("Interface/helloworld.xml", "start", new MySettingsScreen(data));
// attach the Nifty display to the gui view port as a processor
        main.getGuiViewPort().addProcessor(main.niftyDisplay);
        main.getFlyByCamera().setDragToRotate(true);

    }

    @Override
    public void update(float tpf) {
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("StartRound") && isPressed) {
            //transition to the round state
        }
    }

    public void selectLvl(String lvl) {
        //transitioning to the the selected level
        int level = Integer.parseInt(lvl);
        System.out.println("Level no is " + level);
        Stage currentSelectedStage = null;

        if (level == 1) {
            currentSelectedStage = new Level1(main, ColorRGBA.Green, level + 1, level + 1, 0); //sets up stage
        }
        if (level == 2) {
            currentSelectedStage = new Level2(main, ColorRGBA.Green, level + 1, level + 1, 0); //sets up stage
        }
        if (level == 3) {
            currentSelectedStage = new Level3(main, ColorRGBA.Green, level + 1, level + 1, 0); //sets up stage
        }
        main.player = new Player(main);
        MyCustomControl logic = new MyCustomControl(main, currentSelectedStage);
        AppStateManager asm = main.getStateManager();
        asm.detach(this);
        asm.attach(currentSelectedStage);

    }

    public void menuReturn() {
        MainMenuState menu = new MainMenuState();
        AppStateManager asm = main.getStateManager();
        asm.detach(this);
        asm.attach(menu);
    }

    public void quitGame() {
        //closes game
        System.exit(0);
    }

    @Override
    public void cleanup() {
        System.out.println("cleaning up level select screen...");
        main.getGuiViewPort().removeProcessor(main.niftyDisplay);
        main.deleteInputMappings(newMappings);
    }

    public void bind(Nifty nifty, Screen screen) {
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }
}