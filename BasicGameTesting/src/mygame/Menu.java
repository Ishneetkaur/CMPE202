/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Ankit
 */
public class Menu extends AbstractAppState implements ScreenController {

    SimpleApplication rootApp;
    NiftyJmeDisplay startScreen;
    Nifty nifty;

    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        rootApp = (SimpleApplication) app;

        startScreen = new NiftyJmeDisplay(app.getAssetManager(),
                app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());

        nifty = startScreen.getNifty();
        nifty.fromXml("Interface/mainMenu.xml", "start", this);
        app.getGuiViewPort().addProcessor(startScreen);

    }

    public void bind(Nifty nifty, Screen screen) {
    }

    @Override
    public void cleanup() {
        nifty.exit();
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    public void startGame() {
        System.out.println("In Start Game");
        this.rootApp.getStateManager().attach(new StartGame());
        this.rootApp.getStateManager().detach(this);
    }

    public void exitGame() {
        rootApp.stop();

    }

    public void gameCredits() {
        nifty.fromXml("Interface/credits.xml", "credit", this);
        rootApp.getGuiViewPort().addProcessor(startScreen);
    }

    public void backToMenu() {
        nifty.fromXml("Interface/mainMenu.xml", "start", this);
        rootApp.getGuiViewPort().addProcessor(startScreen);

    }

    public void gameOptions() {
        System.out.println("In Game Options");
        nifty.fromXml("Interface/options.xml", "option", this);



        rootApp.getGuiViewPort().addProcessor(startScreen);




    }

    public void set2Click() {
        Element niftyElement = nifty.getCurrentScreen().findElementByName("Panel_tick1");
        // Element niftyElement = nifty.getCurrentScreen().findElementByName("panel_tick1");
        if (niftyElement != null) {
            nifty.removeElement(nifty.getCurrentScreen(), niftyElement);
        }

        Element tabButtonPanel = nifty.getCurrentScreen().findElementByName("panel_mid");
       

        Element panel_tick2 = nifty.getCurrentScreen().findElementByName("Panel_tick2");
       
        
        
        
        if(panel_tick2 == null)
        {
        new PanelBuilder("Panel_tick2") {
            {
                childLayoutCenter(); // panel properties, add more...               
                height("30%");
                alignLeft();
                width("20%");
                margin("25");
                // GUI elements
                image(new ImageBuilder("tick2") {
                    {
                        alignLeft();
                        filename("Interface/tick.png");
                        height("100%");
                        width("75%");
                    }
                });
            }
        }.build(nifty, nifty.getCurrentScreen(), tabButtonPanel);
        }

    }

    public void set1Clicked() {
        Element niftyElement = nifty.getCurrentScreen().findElementByName("Panel_tick2");

        if (niftyElement != null) {
            nifty.removeElement(nifty.getCurrentScreen(), niftyElement);
        }

        Element tabButtonPanel = nifty.getCurrentScreen().findElementByName("panel_top3");

         Element panel_tick1 = nifty.getCurrentScreen().findElementByName("Panel_tick1");
                    
        
        if(panel_tick1 == null)
        {

        new PanelBuilder("Panel_tick1") {
            {
                childLayoutCenter(); // panel properties, add more...               
                height("30%");
                alignLeft();
                width("20%");
                margin("25");
                // GUI elements
                image(new ImageBuilder("tick1") {
                    {
                        alignLeft();
                        filename("Interface/tick.png");
                        height("100%");
                        width("75%");


                    }
                });


            }
        }.build(nifty, nifty.getCurrentScreen(), tabButtonPanel);
        }
    }
}
