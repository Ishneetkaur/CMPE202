/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.ui.Picture;
import de.lessvoid.nifty.Nifty;


/**
 *
 * @author Ankit
 */
public class StartGame extends AbstractAppState implements AnimEventListener, ActionListener, PhysicsCollisionListener{
    SimpleApplication rootApp;
    AppStateManager stateManager;
    
     public int countHit=0;
    private AnimChannel ach;
    private AnimControl act;
    private Node player_model,enemy_model;
    private Spatial scene_model;
    private BulletAppState bulletAppState; // Nedeed to access collision functions
    private RigidBodyControl landscape; // The scene is a rigid landscape
    private CharacterControl player; // The player entity
    private Node player_node,enemy_node; // Used to fix the model Y offset
    private ChaseCamera chase_cam; // Third person camera
    private Vector3f walk_direction = new Vector3f();
    private float air_time = 0.0f;
    private boolean left = false, right = false, up = false, down = false,
    /* --------- */ attacking = false, capture_mouse = true, running = false,
    /* --------- */ jumping = false, jump_pressed = false, attack_pressed = false,
    /* --------- */ lock_movement = false;        
    public int intGoldCount=0;
    private boolean isCollide=false;
    private Box brick=null; 
    private Node rootNode;
      Geometry boxGeometry,sphereGeometry;
      
    public void initialize(AppStateManager stateManager, Application app){
        
        System.out.println("In Start Game 1");
        super.initialize(stateManager, app);
        rootApp = (SimpleApplication)app;        
      
        this.stateManager = stateManager;
        rootNode = rootApp.getRootNode();
         
        Picture pic = new Picture("HUD Picture");
        pic.setImage(rootApp.getAssetManager(), "Interface/score.png", true);
        pic.setWidth(100);
        pic.setHeight(100);
        pic.setPosition(10,675);
        rootApp.getGuiNode().attachChild(pic);
        
        

        
    }
    
    public void cleanup()
    {
        
    }
    
     public void startGame()
    {
          System.out.println("In Start Game 3");
         init_models();
        init_physics();
        init_camera();
        init_keys();
        init_light();
        init_anim();
        init_bricks();
          bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    
    
    
    
    
    // Light ---------------------------------------------------------
    private void init_light() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f)); // mult makes it brighter
        rootNode.addLight(al);
 
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White); 
        sun.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(sun);       
             
    }
    
    // Models --------------------------------------------------------
    private void init_models() 
    {
        player_node = new Node("Player Node");
        enemy_node=new Node("Enemy Node");
        
       player_model = (Node)rootApp.getAssetManager().loadModel("Models/yomario/mario.mesh.xml");
       enemy_model=(Node)rootApp.getAssetManager().loadModel("Models/yomario/mario.mesh.xml");
         enemy_model.setLocalScale(0.035f);
        enemy_model.getLocalTranslation().addLocal(5, -3.6f, 5); // model offset fix
        enemy_node.attachChild(enemy_model);
       
       
      //player_model = (Node)assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        player_model.setLocalScale(0.035f);
        player_model.getLocalTranslation().addLocal(0, -3.6f, 0); // model offset fix
        player_node.attachChild(player_model);
        
        
       
        
        
        
        
        
        
         rootApp.getAssetManager().registerLocator("town.zip", ZipLocator.class);
    scene_model = rootApp.getAssetManager().loadModel("main.scene");
       // scene_model = assetManager.loadModel("Scenes/town/main.scene");
        scene_model.setLocalScale(2f);
    }
    
    // Physics -------------------------------------------------------
    private void init_physics() {
       // Initialize physics engine
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // Our rigid scene
        CollisionShape scene_shape =
            CollisionShapeFactory.createMeshShape((Node)scene_model);
        landscape = new RigidBodyControl(scene_shape, 0); // Static physics node with mass 0
        scene_model.addControl(landscape);
        
        // Third parameter of CapsuleCollisionShape is the axis, 1 = Y
        CapsuleCollisionShape capsule_shape =
            new CapsuleCollisionShape(2.3f, 2.6f, 1);
        player = new CharacterControl(capsule_shape, 0.05f);
            // 0.05f is the highest step you can climb without jumping
        player.setJumpSpeed(30);
        player.setFallSpeed(60);
        player.setGravity(70);
        player_node.addControl(player);
        player.setPhysicsLocation(new Vector3f(0, 10, 0));
        player_node.setLocalTranslation(new Vector3f(0, 10, 0));
        
        rootNode.attachChild(player_node);
        rootNode.attachChild(scene_model);
        
        // Physic nodes are attached to the physics space
        // instead of the root node
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);
    }
    
    // Camera --------------------------------------------------------
    void init_camera() {
        rootApp.getViewPort().setBackgroundColor(new ColorRGBA(0.7f,0.8f,1f,1));
        rootApp.getFlyByCamera().setEnabled(false);
        chase_cam = new ChaseCamera(rootApp.getCamera(), player_model, rootApp.getInputManager());
        //chase_cam.setDragToRotate(false);
      //  chase_cam.setInvertVerticalAxis(true);
    //   chase_cam.setSmoothMotion(true);
       // chase_cam.setLookAtOffset(new Vector3f(5f, 10f, 5f ));
        
       //  flyCam.setEnabled(false);

    // Enable a chase cam
         
   // chase_cam = new ChaseCamera(cam, player_model, inputManager);
  chase_cam.setInvertVerticalAxis(true);
   // chase_cam.setLookAtOffset(new Vector3f(5f, 10f, 5f ));
    //Uncomment this to invert the camera's vertical rotation Axis 
    //chaseCam.setInvertVerticalAxis(true);

    //Uncomment this to invert the camera's horizontal rotation Axis
    //chaseCam.setInvertHorizontalAxis(true);

    //Comment this to disable smooth camera motion
    chase_cam.setSmoothMotion(true);

    //Uncomment this to disable trailing of the camera 
    //WARNING, trailing only works with smooth motion enabled. It is true by default.
    //chaseCam.setTrailingEnabled(false);

    //Uncomment this to look 3 world units above the target
    //chaseCam.setLookAtOffset(Vector3f.UNIT_Y.mult(3));

    //Uncomment this to enable rotation when the middle mouse button is pressed (like Blender)
    //WARNING : setting this trigger disable the rotation on right and left mouse button click
    //chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));

    //Uncomment this to set mutiple triggers to enable rotation of the cam
    //Here spade bar and middle mouse button
    //chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE),new KeyTrigger(KeyInput.KEY_SPACE));

    //registering inputs for target's movement
//    registerInput();
        
    }
    
    // Controls ------------------------------------------------------
    private void init_keys() {
        rootApp.getInputManager().addMapping("Left",  new KeyTrigger(KeyInput.KEY_LEFT));
        rootApp.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        rootApp.getInputManager().addMapping("Up",    new KeyTrigger(KeyInput.KEY_UP));
        rootApp.getInputManager().addMapping("Down",  new KeyTrigger(KeyInput.KEY_DOWN));
        rootApp.getInputManager().addMapping("Jump",  new KeyTrigger(KeyInput.KEY_SPACE));
        //inputManager.addMapping("CatchM", new KeyTrigger(KeyInput.KEY_Q));
      //  inputManager.addMapping("Run",    new KeyTrigger(KeyInput.KEY_LSHIFT));
        //inputManager.addMapping("Attack", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        rootApp.getInputManager().addListener(this, "Left");
        rootApp.getInputManager().addListener(this, "Right");
        rootApp.getInputManager().addListener(this, "Up");
        rootApp.getInputManager().addListener(this, "Down");
        rootApp.getInputManager().addListener(this, "Jump");
      //  inputManager.addListener(this, "CatchM");
       // inputManager.addListener(this, "Run");
        //inputManager.addListener(this, "Attack");
    }
    
    public void onAction(String name, boolean pressed, float k) {
        if (name.equals("Left"))
            left = pressed;
        else if (name.equals("Right"))
            right = pressed;
        else if (name.equals("Up"))
            up = pressed;
        else if (name.equals("Down"))
            down = pressed;
        else if (name.equals("Jump"))
            jump_pressed = true;
        /*else if (name.equals("CatchM") && !pressed) {
            capture_mouse ^= true;
            Mouse.setGrabbed(capture_mouse);
            chase_cam.setDragToRotate(!capture_mouse);
        }*/
       /* else if (name.equals("Run"))
            running = pressed;
        else if (name.equals("Attack")){
            if(capture_mouse && !jumping) {
                attack_pressed = pressed;
                if(pressed && !attacking)
                    attacking = true;
            }
        }*/
    }
    
    // Animations ----------------------------------------------------
    private void init_anim() {
       System.out.println(player_model.getControl(0));
      act = player_model.getControl(AnimControl.class);
       for (String anim : act.getAnimationNames()) { System.out.println(anim); }
        ach = act.createChannel();
        ach.setAnim("my_animation");
       // ach.setLoopMode(LoopMode.Loop);
         act.addListener(this);
       
    }
    
    private void init_bricks()
    {
         for (int i = 0; i < 5; i++) {
            Box box = new Box(2f, 2f, 2f);
            Sphere s= new Sphere(3,3,1f);
      
              boxGeometry = new Geometry("Box", box);
                 //   PhysicsNode pn = new PhysicsNode(boxGeometry, box, 0);
                 sphereGeometry = new Geometry("Sphere", s);
             Material material = new Material(rootApp.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
             Material material2 = new Material(rootApp.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
             material.setTexture("ColorMap", rootApp.getAssetManager().loadTexture("Interface/Logo/Monkey.jpg"));
            material2.setColor("Color", ColorRGBA.Blue);
             boxGeometry.setMaterial(material);
             sphereGeometry.setMaterial(material2);
            boxGeometry.setLocalTranslation(i*4+2, 11f, -15f);
            sphereGeometry.setLocalTranslation(5f, 11f,3*i+5f+2);
            //RigidBodyControl automatically uses Sphere collision shapes when attached to single geometry with sphere mesh
            boxGeometry.addControl(new RigidBodyControl(.000f));
            boxGeometry.getControl(RigidBodyControl.class).setRestitution(1);
            rootNode.attachChild(boxGeometry);
            bulletAppState.getPhysicsSpace().add(boxGeometry);
            
            sphereGeometry.addControl(new RigidBodyControl(.000f));
            sphereGeometry.getControl(RigidBodyControl.class).setRestitution(1);
            rootNode.attachChild(sphereGeometry);
            bulletAppState.getPhysicsSpace().add(sphereGeometry);
          
        }
         
           for (int i = 0; i < 5; i++) {
            Box box = new Box(2f, 2f, 2f);
            Sphere s= new Sphere(3,3,1f);
      
              boxGeometry = new Geometry("Box", box);
                 //   PhysicsNode pn = new PhysicsNode(boxGeometry, box, 0);
                 sphereGeometry = new Geometry("Sphere", s);
             Material material = new Material(rootApp.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
             Material material2 = new Material(rootApp.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
             material.setTexture("ColorMap", rootApp.getAssetManager().loadTexture("Interface/Logo/Monkey.jpg"));
            material2.setColor("Color", ColorRGBA.Blue);
             boxGeometry.setMaterial(material);
             sphereGeometry.setMaterial(material2);
            boxGeometry.setLocalTranslation(i*(4)+50, 11f, -50f);
            sphereGeometry.setLocalTranslation(-15f, 11f,3*i-50f);
            //RigidBodyControl automatically uses Sphere collision shapes when attached to single geometry with sphere mesh
            boxGeometry.addControl(new RigidBodyControl(.000f));
            boxGeometry.getControl(RigidBodyControl.class).setRestitution(1);
            rootNode.attachChild(boxGeometry);
            bulletAppState.getPhysicsSpace().add(boxGeometry);
            
            sphereGeometry.addControl(new RigidBodyControl(.000f));
            sphereGeometry.getControl(RigidBodyControl.class).setRestitution(1);
            rootNode.attachChild(sphereGeometry);
            bulletAppState.getPhysicsSpace().add(sphereGeometry);
          
        }
         
         /*brick=new Box(1,1,1);
        Geometry player = new Geometry("blue cube", brick);
        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat);
        player.setLocalTranslation(1, 10, 1);
        rootNode.attachChild(player);
        */
    }
    
    public void onAnimCycleDone(AnimControl ctrl, AnimChannel ch, String name) {
      /*  if(name.equals("Attack2") && attacking && !attack_pressed) {
            if (!ch.getAnimationName().equals("Idle2")) {
                ch.setAnim("Idle2", 0f);
                ch.setLoopMode(LoopMode.Loop);
                ch.setSpeed(1f);
                attacking = false;
                lock_movement = false;
            }
        }
        else if(name.equals("JumpNoHeight"))
            jump_pressed = false;*/
    }
    
    public void onAnimChange(AnimControl ctrl, AnimChannel ch, String name) {
        // Blah, unused but I still need to implement it D:
    }
    
    // Update Loop ---------------------------------------------------
    public void simpleUpdate(float k) {
        float movement_amount = 0.3f;
        if(running) movement_amount *= 1.75;
       
        // Gets forward direction and moves it forward
        Vector3f direction = rootApp.getCamera().getDirection().clone().multLocal(movement_amount);
        // Gets left direction and moves it to the left
        Vector3f left_direction =  rootApp.getCamera().getLeft().clone().multLocal(movement_amount * 0.75f);
        
        // We don't want to fly or go underground
        direction.y = 0;
        left_direction.y = 0;
        
        walk_direction.set(0, 0, 0); // The walk direction is initially null

        if(left) walk_direction.addLocal(left_direction);
        if(right) walk_direction.addLocal(left_direction.negate());
        if(up) walk_direction.addLocal(direction);
        if(down) walk_direction.addLocal(direction.negate());
        
        if(!player.onGround()) air_time += k;
        else {
           // System.out.println("1st if");
            air_time = 0;
            jumping = false;
        }
        //air_time > 0.1f || 
        if ((jump_pressed)) {
            jumping = true;
            player.jump();
            jump_pressed=false;
            // Stop movement if jumping while walking
            if(jump_pressed && ach.getAnimationName().equals("Walk"))
                lock_movement = true;
          /*  if (!ach.getAnimationName().equals("JumpNoHeight")) {
                ach.setAnim("JumpNoHeight");
                ach.setSpeed(1f);
                ach.setLoopMode(LoopMode.DontLoop);
            }*/
          /*  if(ach.getTime() >= 0.32f) { // Delay jump to make the animation look decent
                System.out.println("2nd if");
                player.jump();
                lock_movement = false;
            }*/
        } else if(attacking) {
           /* lock_movement = true;
            if (!ach.getAnimationName().equals("Attack2")) {
                ach.setAnim("Attack2");
                ach.setSpeed(1f);
                ach.setLoopMode(LoopMode.Loop);
            
             }*/
        } else {
            // If we're not walking, set standing animation if not jumping
          /*  if (walk_direction.length() == 0) {
                 ach.setLoopMode(LoopMode.Loop);
                 if (!ach.getAnimationName().equals("Idle2")) {
                    ach.setAnim("Idle2", 0f);
                    ach.setSpeed(1f);
                 }
            } else {
                // ... otherwise, set the walking animation
                ach.setLoopMode(LoopMode.Loop);
                if(!ach.getAnimationName().equals("Walk"))
                    ach.setAnim("Walk", 0.5f);
                if(running) ach.setSpeed(1.75f);
                else ach.setSpeed(1f);
            }*/
        }
        if(!lock_movement)
            player.setWalkDirection(walk_direction);
        else
            player.setWalkDirection(Vector3f.ZERO);
        
        // Rotate model to point walk direction if moving
        if(walk_direction.length() != 0)
            player.setViewDirection(walk_direction.negate());
            // negating cause the model is flipped
        
        // Rotate model to point camera direction if attacking
        if(attacking)
            player.setViewDirection(direction.negate());
        
        //collision with brick
         updatescore();
       
    }
    
    public void collision(PhysicsCollisionEvent event) {
        System.out.println(event.getNodeA().getName());
        System.out.println(event.getNodeB().getName());
         PhysicsCollisionObject objectToRemove=null;
         if("Box".equals(event.getNodeB().getName()) || "Sphere".equals(event.getNodeB().getName()))
         {
             objectToRemove=event.getObjectB();
             Spatial n=event.getNodeB();
             n.removeFromParent();
               //fpsText.setColor(ColorRGBA.Blue);
             /// fpsText.setText("You hit the box!");
            
                //PhysicsCollisionObject object=event.getObjectA();
                bulletAppState.getPhysicsSpace().removeCollisionObject(objectToRemove);
                //rootNode.removeControl((Controlobject);
                rootNode.updateGeometricState();
                System.out.println(event.getNodeA().getName()+" "+event.getNodeB().getName());
             
             
              scoreupdated=true;
               
         }
      //   else if()
        //     objectToRemove=event.getObjectB();
                   /*  else if("Sphere".equals(event.getNodeA().getName()))
                           objectToRemove=  event.getObjectA();
                             else if("Sphere".equals(event.getNodeB().getName()))
                                objectToRemove= event.getObjectB();*/
             
   //     if ( || "Box".equals(event.getNodeB().getName()) || "Sphere".equals(event.getNodeB().getName()) || "Sphere".equals(event.getNodeA().getName())) {
           // if ("bullet".equals(event.getNodeA().getName()) || "bullet".equals(event.getNodeB().getName())) {
              
               // boxGeometry.removeControl(event.getNodeB());
                
            //}
       // }
        //system.out.println(countHit);
    }
    public static boolean scoreupdated=false;
    public void updatescore()
    {
        if(scoreupdated)
        {
             countHit++;
               intGoldCount++;
               scoreupdated=false;
        }
          // System.out.println("hit"+intGoldCount);
               // fpsText.setText("Your Score :"+intGoldCount );
    }
}
