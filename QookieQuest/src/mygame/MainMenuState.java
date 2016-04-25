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

public class MainMenuState extends AbstractAppState implements ActionListener, ScreenController {

    Main main;
    private String newMappings[];

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.main = (Main) app;

        //Keys
        InputManager inputManager = main.getInputManager();
        inputManager.addMapping("StartRound", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, newMappings = new String[]{"StartRound"});
        main.nifty.fromXml("Interface/Menus.xml", "Main", this);
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
            startGame();
        }
    }

    public void startGame() {
        //transitiong to the round state
        System.out.println("Beginning game...");
        Level1 lvl1 = new Level1(main, ColorRGBA.Green, 3, 1, 0); //sets up stage
//        Level2 myLevelObject = new Level2(main, ColorRGBA.Red, 3, 1, 0);
        main.player = new Player(main);
        MyCustomControl logic = new MyCustomControl(main, lvl1);
//        MyCustomControl logic = new MyCustomControl(main, myLevelObject);
        main.getFlyByCamera().setDragToRotate(false);
        AppStateManager asm = main.getStateManager();
        asm.detach(this);
        asm.attach(lvl1);
//        asm.attach(myLevelObject);
    }

    public void selectLvl() {
        //transitiong to the level select screen
        LevelSelectState sel = new LevelSelectState();
        AppStateManager asm = main.getStateManager();
        asm.detach(this);
        asm.attach(sel);
    }

    public void quitGame() {
        //closes game
        System.exit(0);
    }

    @Override
    public void cleanup() {
        System.out.println("cleaning up main menu screen...");
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