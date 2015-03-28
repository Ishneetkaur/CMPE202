package jm3test.helloworld;
 
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
 
/** Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys. */
public class HelloAnimation2 extends SimpleApplication {
 
    /**
     *
     * @param args
     */
    public static void main(String[] args){
        HelloAnimation2 app = new HelloAnimation2();
        app.start(); // start the game
    }
 protected Geometry player1,player2;
 Node pivot = new Node("pivot");
    @Override
    public void simpleInitApp() {
      
        Box b1 = new Box(1, 1, 1);
        player1 = new Geometry("red cube", b1);
        Material mat1 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", 
                assetManager.loadTexture("Models/Images/alien.png"));
        player1.setMaterial(mat1);
        
        
        Box b2 = new Box(1, 1, 3);
        player2 = new Geometry("red cube", b2);
        player2.setLocalTranslation(new Vector3f(3,1,1));
        Material mat2 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setTexture("ColorMap", 
                assetManager.loadTexture("Models/Images/a.jpg"));
        
        player2.setMaterial(mat2);
        
        
        rootNode.attachChild(pivot); // put this node in the scene

        /** Attach the two boxes to the *pivot* node. (And transitively to the root node.) */
        pivot.attachChild(player1);
        pivot.attachChild(player2);
        /** Rotate the pivot node: Note that both boxes have rotated! */
       
    }

    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate:
        pivot.rotate(0, 2*tpf, 0); 
        
    }
    }

    

    