package io.github.battle_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
     private Texture image2;
    private Sprite shipSprite;
    private Sprite bgSprite;
    private Label label;
    private Stage stage;
     private Sprite enemySprite;
     private Texture background;
      public static final float WORLD_WIDTH = 5000;
      public static final float WORLD_HEIGHT =5000;
    private TextureRegion bgRegion;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture sheet;
    private Animation<TextureRegion> animation;
    private float stateTime;
    ParticleEffect wakeEffect;
    float playerRotation = 0;
    float enemyX = 1100;
float enemyY = 200;
    float rotation = 0;
    int enemydead = 0;
    float x = 1000;
    float y = 100;
    float speed = 300;
    @Override
    public void create() {
        batch = new SpriteBatch();
        
       Texture backgroundTexture = new Texture(Gdx.files.internal("water_tile.png"));
backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

// Create a region that repeats over the world
 bgRegion = new TextureRegion(backgroundTexture);
bgRegion.setRegion(0, 0, (int)WORLD_WIDTH, (int)WORLD_HEIGHT);


        image2 = new Texture(Gdx.files.internal("ship.png"));
        enemySprite = new Sprite(image2);    
        enemySprite.setOriginCenter();
        
        
        image = new Texture(Gdx.files.internal("ship.png"));
    shipSprite  = new Sprite(image);

    // set origin to center so rotation is around the ship center
    shipSprite.setOriginCenter();
        camera = new OrthographicCamera();
        viewport = new FitViewport(1700, 864, camera); // visible screen
        stage = new Stage(viewport);

        // Start camera centered on player
        camera.position.set(x + image.getWidth()/2f, y + image.getHeight()/2f, 0);
        camera.update();
        
    


    }
    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }



    @Override
    public void render() {
       
        // make start screen and when you get hit restart screen with an if statement using a varibale
        //  to decide if the game is
        //  running like if( running = 1) 
        // put all the code in the normal game, if (running=0) restart the timer and show the restart screen
        // make it have hearts so when the enemy doesnt die and hit the bottom you lose a heart as its your base. 
        // And add a boost meter that when pressing shift allows you to move alot faster but its limited 
        // and the boost regens a bit slow like 1 every second
       
        float dt = Gdx.graphics.getDeltaTime();
        
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
           playerRotation += 150 * dt;
        }  
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
           playerRotation -= 150 * dt;
        } 
        
        float angleRad = (float)Math.toRadians(playerRotation);
float dirX = MathUtils.cos(angleRad);
float dirY = MathUtils.sin(angleRad);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
    x += dirX * speed * dt;
    y += dirY * speed * dt;
}
if (Gdx.input.isKeyPressed(Input.Keys.S)) {
    x -= dirX * speed * dt;
    y -= dirY * speed * dt;
}
        shipSprite.setRotation(playerRotation);
        shipSprite.setPosition(x - shipSprite.getOriginX(), y - shipSprite.getOriginY());
        enemySprite.setRotation(0); // For now static
        enemySprite.setPosition(enemyX - enemySprite.getOriginX(), enemyY - enemySprite.getOriginY());
  // Clamp player normally
x = MathUtils.clamp(x, 0, WORLD_WIDTH - image.getWidth());
y = MathUtils.clamp(y, 0, WORLD_HEIGHT - image.getHeight());

// Camera follows player
float camX = x + image.getWidth()/2f;
float camY = y + image.getHeight()/2f;

// Clamp camera so it never goes outside world bounds
float halfViewportWidth = viewport.getWorldWidth()/2f;
float halfViewportHeight = viewport.getWorldHeight()/2f;

camX = MathUtils.clamp(camX, halfViewportWidth, WORLD_WIDTH - halfViewportWidth);
camY = MathUtils.clamp(camY, halfViewportHeight, WORLD_HEIGHT - halfViewportHeight);

camera.position.set(camX, camY, 0);
camera.update();

        // --- Draw ---
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.setProjectionMatrix(camera.combined); // must be AFTER camera.update
        batch.begin();
       batch.draw(bgRegion, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        enemySprite.draw(batch);
        shipSprite.draw(batch);
        
        batch.end();

        // --- Stage ---
        stage.act(dt);
        stage.draw();
        
        Rectangle player = new Rectangle(x, y, image.getWidth(), image.getHeight());
       
       
       
        
        
        

        
        
       

    
       






    }

    @Override
    public void dispose() {
        
        batch.dispose();
        image.dispose();
        image2.dispose();
    }
}
