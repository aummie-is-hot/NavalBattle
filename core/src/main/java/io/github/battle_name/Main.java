package io.github.battle_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.math.Matrix4;
/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    
    class CannonBall {
        float x, y;
        float angle; // degrees
        float speed = 900;
        Texture tex;
        Rectangle rect;
        
        public CannonBall(float x, float y, float angle, Texture tex) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.tex = tex;
            
            this.rect = new Rectangle(x, y, tex.getWidth(), tex.getHeight());
        }

        public void update(float dt) {
            float rad = (float) Math.toRadians(angle);
            this.x += Math.cos(rad) * speed * dt;
            this.y += Math.sin(rad) * speed * dt;

        }

        public void draw(SpriteBatch batch) {
            batch.draw(tex, this.x - tex.getWidth() / 2f, this.y - tex.getHeight() / 2f);
      
            this.rect.x = this.x;
            this.rect.y= this.y;
            // cannonballhit = new Rectangle(x,y,tex.getWidth(), tex.getHeight());
        }
    }
    public enum EnemyType {
    JET(10, 290, "jet.png"),
    FAST(20, 250, "speedboat.png"),
    TANK(150, 100, "tank.png"),
    NORMAL(80, 150, "ship.png");

    public final float health;
    public final float speed;
    public final String texture;

    EnemyType(float health, float speed, String texture) {
        this.health = health;
        this.speed = speed;
        this.texture = texture;
    }
}
    public class Enemy {

    public float x, y;
    public float health;
    public float speed;
    public Sprite sprite;
    public Rectangle rect;
    public EnemyType type;

    public Enemy(float x, float y, EnemyType type) {
        this.x = x;
        this.y = y;
        this.type = type;

        this.health = type.health;
        this.speed = type.speed;

        Texture t = new Texture(type.texture);
        this.sprite = new Sprite(t);
        this.sprite.setOriginCenter();
        this.sprite.setPosition(x - sprite.getOriginX(), y - sprite.getOriginY());

        this.rect = new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
    }

    public void update(float dt, float playerX, float playerY) {
        // Direction vector
        float dx = playerX - x;
        float dy = playerY - y;

        // Normalize
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length != 0) {
            dx /= length;
            dy /= length;
        }

        // Move toward player
        x += dx * speed * dt;
        y += dy * speed * dt;

        // Update rectangle for collisions
        rect.x = x - sprite.getOriginX();
        rect.y = y - sprite.getOriginY();

        // Calculate rotation angle in degrees
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));

        // Rotate sprite
        sprite.setRotation(angle);

        // Update sprite position
        sprite.setPosition(x - sprite.getOriginX(), y - sprite.getOriginY());
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }
}

    public void spawnEnemy() {
    float randX = 100 + random.nextFloat() * (WORLD_WIDTH - 200);
float randY = 100 + random.nextFloat() * (WORLD_HEIGHT - 200);
// Pick a random type
    EnemyType type = EnemyType.values()[random.nextInt(EnemyType.values().length)];

    enemies.add(new Enemy(randX, randY, type));
}    
class Island {
    float x, y;
    Texture tex;
    float supplyvalue;

    public Island(float x, float y, Texture tex, float supplyvalue) {
        this.x = x;
        this.y = y;
        this.tex = tex;
        this.supplyvalue = supplyvalue;
        // 400x400 detection zone (change this for range)
    }

    public void draw(SpriteBatch batch) {
        batch.draw(tex, x - tex.getWidth()/2f, y - tex.getHeight()/2f);
    }

   public boolean isPlayerNear(float px, float py) {
    float dx = px - x;
    float dy = py - y;
    float distance = (float)Math.sqrt(dx*dx + dy*dy);
    return distance <= 500; // 1000 units detection radius
}
}

public class Radar {
    private float radarX, radarY;
    private float radarRadius;
    private float radarRange;
    private ShapeRenderer sr;

    public Main.Enemy lockedEnemy = null; // currently locked-on enemy

    public Radar(float radarX, float radarY, float radarRadius, float radarRange) {
        this.radarX = radarX;
        this.radarY = radarY;
        this.radarRadius = radarRadius;
        this.radarRange = radarRange;
        sr = new ShapeRenderer();
    }

    public void render(float playerX, float playerY, ArrayList<Main.Enemy> enemies) {
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Radar background
        sr.setColor(0, 0, 0, 0.5f);
        sr.circle(radarX, radarY, radarRadius);

        // Player at center
        sr.setColor(0, 1, 0, 1);
        sr.circle(radarX, radarY, 5);

        // Enemies
        for (Main.Enemy e : enemies) {
            float dx = e.x - playerX;
            float dy = e.y - playerY;
            float distance = (float)Math.sqrt(dx*dx + dy*dy);

            if (distance <= radarRange) {
                float blipX = radarX + (dx / radarRange) * radarRadius;
                float blipY = radarY + (dy / radarRange) * radarRadius;

                if (e == lockedEnemy) {
                    // Locked-on: green square
                    sr.setColor(0, 1, 0, 1);
                    float size = 6;
                    sr.rect(blipX - size/2f, blipY - size/2f, size, size);
                } else {
                    // Normal: red circle
                    sr.setColor(1, 0, 0, 1);
                    sr.circle(blipX, blipY, 3);
                }
            }
        }

        sr.end();
    }

    public void dispose() {
        sr.dispose();
    }
} class Missile {
    float x, y;
    float speed = 1500;
    float angle;
    Enemy target;
    Texture tex;
    boolean hit = false;
    Rectangle rect;

    public Missile(float x, float y, Enemy target, Texture tex) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.tex = tex;
        this.rect = new Rectangle(x - tex.getWidth() / 2f, y - tex.getHeight() / 2f, tex.getWidth(), tex.getHeight());
    }

    public void update(float dt) {
        if (hit) return;

        // Check if target is dead
        if (target == null || target.isDead()) {
            // Auto-lock to next radar-locked enemy if available
            target = radar.lockedEnemy;
            if (target == null) {
                hit = true; // No valid target, missile will disappear
                return;
            }
        }

        // Move toward target
        float dx = target.x - x;
        float dy = target.y - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            dx /= dist;
            dy /= dist;

            x += dx * speed * dt;
            y += dy * speed * dt;

            // Update rotation
            angle = (float) Math.toDegrees(Math.atan2(dy, dx));

            // Update collision rectangle
            rect.setPosition(x - tex.getWidth() / 2f, y - tex.getHeight() / 2f);

            // Check hit
            if (rect.overlaps(target.rect)) {
                hit = true;
                target.health -= 10000; // instant kill

                // Remove enemy from list
                Main.this.enemies.remove(target);

                // Clear radar lock if necessary
                if (radar.lockedEnemy == target) radar.lockedEnemy = null;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(
            tex,
            x - tex.getWidth() / 2f,
            y - tex.getHeight() / 2f,
            tex.getWidth() / 2f,
            tex.getHeight() / 2f,
            tex.getWidth(),
            tex.getHeight(),
            1f,
            1f,
            angle,
            0,
            0,
            tex.getWidth(),
            tex.getHeight(),
            false,
            false
        );
    }
}

TextButton mainButton;
TextButton option1;
TextButton option2;
TextButton option3;

boolean open = false;
int maxMissileAmmo =5;
    int missles = maxMissileAmmo;
   int maxcannonammo = 150;
    int ammo = maxcannonammo;
    ArrayList<CannonBall> cannonballs = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();
    private SpriteBatch batch;
    private Texture image;
    private Texture image2;
    private Sprite shipSprite;
    private Sprite bgSprite;
    int bulletsR;
    int bulletsL;
    private Label label;
    private Stage stage;
    private Sprite enemySprite;
    private Texture background;
    private Texture cannonball;
    public static final float WORLD_WIDTH = 5000;
    public static final float WORLD_HEIGHT = 5000;
    private TextureRegion bgRegion;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture sheet;
    private Animation<TextureRegion> animation;
    private float stateTime;
    ParticleEffect wakeEffect;
    ArrayList<Integer> bulletsLeft = new ArrayList();
    ArrayList<Integer> bulletsRight = new ArrayList();
    float playerRotation = 0;
    float enemyX = 1100;
    float enemyY = 1000;
    float rotation = 0;
    int enemyhealth = 5;
    float x = 1000;
    float y = 100;
    float speed = 300;
    boolean enemyhit = false;
    boolean shoot = true;
    Radar radar;
    Random random = new Random();
    ArrayList<Island> islands = new ArrayList<>();
    Texture islandTex;
    private BitmapFont font;
    ArrayList<Missile> missiles = new ArrayList<>();
    Texture missleTex; // your missile texture
    Texture buttonTexture;
    Skin skin;
    boolean optionsVisible = false;
    private Stage hudStage;
    @Override
    public void create() {
        
        Gdx.input.setInputProcessor(stage);
        missleTex = new Texture("missle2.png");
        font = new BitmapFont(); // default font
font.getData().setScale(2f); // optional: make it bigger
        batch = new SpriteBatch();
         radar = new Radar(
        Gdx.graphics.getWidth() - 60,  // radar top-right corner X
        Gdx.graphics.getHeight() - 60, // radar top-right corner Y
        60,                            // radar radius on screen
        2000                            // radar detection range in world units
    );
        islandTex = new Texture("island.png");
        buttonTexture = new Texture("island.png");
    TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));   // normal
buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTexture)); // pressed
buttonStyle.font = font;
buttonStyle.fontColor = Color.WHITE; // optional
    Random random = new Random();

    int NUM_ISLANDS = 20;   // generate 20 islands
    int WORLD_SIZE = 10000; // change to your world size

    for (int i = 0; i < NUM_ISLANDS; i++) {
        float ix = random.nextFloat() * WORLD_SIZE;
        float iy = random.nextFloat() * WORLD_SIZE;

        islands.add(new Island(ix, iy, islandTex, random.nextFloat(0,3)));
    }

        background = new Texture("water_tile.png");
        cannonball = new Texture("cannon.png");

        image2 = new Texture(Gdx.files.internal("ship.png"));
        enemySprite = new Sprite(image2);
        
        enemySprite.setOriginCenter();

        image = new Texture(Gdx.files.internal("ship.png"));
        shipSprite = new Sprite(image);

        // set origin to center so rotation is around the ship center
        shipSprite.setOriginCenter();
        camera = new OrthographicCamera();
        viewport = new FitViewport(1700, 864, camera); // visible screen
        stage = new Stage(viewport);

        // Start camera centered on player
        camera.position.set(x + image.getWidth() / 2f, y + image.getHeight() / 2f, 0);
        camera.update();
        BitmapFont font = new BitmapFont();
     stage = new Stage(viewport); // still can be used for world-based UI if needed

    // HUD stage (fixed on screen)
    hudStage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(hudStage);

    // --- Add buttons to hudStage instead ---
    mainButton = new TextButton("Click for options", buttonStyle);
    mainButton.setPosition(50, 400);
    mainButton.setSize(300,60);
    hudStage.addActor(mainButton);

    option1 = new TextButton("Option 1", buttonStyle);
    option1.setPosition(50, 360);
    option1.setSize(200, 40);
    option1.setVisible(false);
    hudStage.addActor(option1);

    option2 = new TextButton("Option 2", buttonStyle);
    option2.setPosition(50, 320);
    option2.setSize(200, 40);
    option2.setVisible(false);
    hudStage.addActor(option2);

    option3 = new TextButton("Option 3", buttonStyle);
    option3.setPosition(50, 280);
    option3.setSize(200, 40);
    option3.setVisible(false);
    hudStage.addActor(option3);

    mainButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            boolean visible = !option1.isVisible();
            option1.setVisible(visible);
            option2.setVisible(visible);
            option3.setVisible(visible);
        }
    });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
           hudStage.getViewport().update(width, height, true); // HUD viewport
    }

    @Override
    public void render() {
         // CLICK HANDLING
    
        
        // charged shot + refresh radar with space bar + better enemy types + stealth boat and jet+damaged ship models for diffreant levels of health + player health+missles+islands to restock bullets and missles
        float dt = Gdx.graphics.getDeltaTime();
        bulletsL = bulletsLeft.size();
        bulletsR = bulletsRight.size();
        radar.lockedEnemy = null;
float nearestDist = Float.MAX_VALUE;
for (Enemy e : enemies) {
    float dx = e.x - x;
    float dy = e.y - y;
    float dist = (float)Math.sqrt(dx*dx + dy*dy);
    if (dist < nearestDist && dist <= radar.radarRange) {
        nearestDist = dist;
        radar.lockedEnemy = e;
    }
}
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)&&ammo>0) {
            float rad = (float) Math.toRadians(playerRotation + 90); // left side
            float spawnX = x + MathUtils.cos(rad) * 60;
            float spawnY = y + MathUtils.sin(rad) * 60;
            ammo = ammo-1;
            cannonballs.add(new CannonBall(spawnX, spawnY, playerRotation + 90, cannonball));

        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)&&ammo>0) {
            float rad = (float) Math.toRadians(playerRotation - 90); // right side
            float spawnX = x + MathUtils.cos(rad) * 60;
            float spawnY = y + MathUtils.sin(rad) * 60;
            ammo = ammo-1;
            cannonballs.add(new CannonBall(spawnX, spawnY, playerRotation - 90, cannonball));

        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
    if(radar.lockedEnemy != null && missles > 0) {
        missiles.add(new Missile(x, y, radar.lockedEnemy, missleTex));
        missles--;
    }
}
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerRotation += 150 * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerRotation -= 150 * dt;
        }
        for (int i = 0; i < cannonballs.size(); i++) {
            cannonballs.get(i).update(dt);
        }
        float angleRad = (float) Math.toRadians(playerRotation);
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
        float camX = x + image.getWidth() / 2f;
        float camY = y + image.getHeight() / 2f;

        // Clamp camera so it never goes outside world bounds
        float halfViewportWidth = viewport.getWorldWidth() / 2f;
        float halfViewportHeight = viewport.getWorldHeight() / 2f;

        camX = MathUtils.clamp(camX, halfViewportWidth, WORLD_WIDTH - halfViewportWidth);
        camY = MathUtils.clamp(camY, halfViewportHeight, WORLD_HEIGHT - halfViewportHeight);

        camera.position.set(camX, camY, 0);
        camera.update();

        // --- Draw ---
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.setProjectionMatrix(camera.combined); // must be AFTER camera.update
        batch.begin();
        int tileW = background.getWidth();
        int tileH = background.getHeight();
         if (Math.random() < 0.001) {
            spawnEnemy();
        }

        // update enemies
       

        // Draw enough tiles to cover the camera view
        for (int x = (int) (camera.position.x - camera.viewportWidth / 2) / tileW * tileW; x < camera.position.x
                + camera.viewportWidth / 2; x += tileW) {
            for (int y = (int) (camera.position.y - camera.viewportHeight / 2) / tileH * tileH; y < camera.position.y
                    + camera.viewportHeight / 2; y += tileH) {

                batch.draw(background, x, y);
            }
        }
        
        if (enemyhealth>0){
        
        enemySprite.draw(batch);
        }
        for (CannonBall b : cannonballs) {
            b.draw(batch);
          //  System.out.println(b.rect);
        }
        shipSprite.draw(batch);
        for (Enemy e : enemies) {
    e.draw(batch);
    e.update(dt, x, y);
    
    

}

for (int i = missiles.size() - 1; i >= 0; i--) {
    Missile m = missiles.get(i);
    m.update(dt);
    m.draw(batch);

    if (m.hit) missiles.remove(i);
}
for (Island isl : islands) {
    if(isl.supplyvalue>0){
        // instead of dissapear when out of supplys make it have a cooldown for supply
     isl.draw(batch);
      if (isl.isPlayerNear(x, y)) {

        // show UI prompt
        font.draw(batch, "Press G to Resupply", 
            camera.position.x - 50, camera.position.y + 100);

        // if E pressed -> refill
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            isl.supplyvalue=isl.supplyvalue-1;
            ammo = maxcannonammo;
            missles = maxMissileAmmo;
            
            //playerHealth = maxPlayerHealth;

            // OPTIONAL: feedback
            System.out.println("Resupplied at island!");
        }
    }
    }
   
}
font.draw(batch, "Ammo: "+ammo, 
            camera.position.x -800, camera.position.y + 400);
       
font.draw(batch, "Missiles: " + missles, camera.position.x - 800, camera.position.y + 350);


        batch.end();
          radar.render(x, y, enemies);
        // --- Stage ---
        stage.act(dt);
        
        stage.draw();
        hudStage.act(dt);
hudStage.draw();
       
        
        for (int i = cannonballs.size() - 1; i >= 0; i--) {
    CannonBall b = cannonballs.get(i);

    for (int j = enemies.size() - 1; j >= 0; j--) {
        Enemy e = enemies.get(j);

        if (e.rect.overlaps(b.rect)) {

            
            int crit = random.nextInt(10);
            if (crit ==9){
                e.health-=30;
                System.out.println("CRITCAL HIT!");
            }
            else{
                e.health -= 10;
            }
            
            cannonballs.remove(i);

            // remove enemy if dead
            if (e.health <= 0) {
                enemies.remove(j);
            }

            break; // stop checking this cannonball
        }
    }
}       for (Enemy e : enemies) {

    if(e.rect.overlaps(shipSprite.getBoundingRectangle())){
       // System.exit(0);
        }
}
        
    //handleHUDInput();  // process clicks first
//drawHUD();         // draw buttons on top
    }

    @Override
    public void dispose() {
        font.dispose(); // add this
        batch.dispose();
        image.dispose();
        image2.dispose();
    }
}
