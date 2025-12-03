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
import java.util.ArrayList;
import java.util.Random;
/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    // Rectangle cannonballhit;
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
    public class Enemy {

    public float x, y;
    public float health;
    public Sprite sprite;
    public Rectangle rect;
    public float speed;

    public Enemy(float x, float y, float health, float speed, Texture texture) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.speed = speed;

        this.sprite = new Sprite(texture);
        this.sprite.setOriginCenter(); // Important for rotation
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
float randHealth = 10 + random.nextInt(20);
float speed = 100 + random.nextFloat() * 100; // 100–200 speed
Texture enemyTexture = new Texture("ship.png");

enemies.add(new Enemy(randX, randY, randHealth, speed, enemyTexture));
}    

public class Radar {

    private float radarX, radarY;
    private float radarRadius;
    private float radarRange;
    private ShapeRenderer sr;

    public Radar(float radarX, float radarY, float radarRadius, float radarRange) {
        this.radarX = radarX;
        this.radarY = radarY;
        this.radarRadius = radarRadius;
        this.radarRange = radarRange;
        sr = new ShapeRenderer();
    }

    public void render(float playerX, float playerY, ArrayList<Enemy> enemies) {
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Radar background
        sr.setColor(0, 0, 0, 0.5f);
        sr.circle(radarX, radarY, radarRadius);

        // Player at center
        sr.setColor(0, 1, 0, 1);
        sr.circle(radarX, radarY, 5);

        // Enemies
        sr.setColor(1, 0, 0, 1);
        for (Enemy e : enemies) {
            float dx = e.x - playerX;
            float dy = e.y - playerY;
            float distance = (float)Math.sqrt(dx*dx + dy*dy);

            if (distance <= radarRange) {
                float blipX = radarX + (dx / radarRange) * radarRadius;
                float blipY = radarY + (dy / radarRange) * radarRadius;
                sr.circle(blipX, blipY, 3);
            }
        }

        sr.end();
    }

    public void dispose() {
        sr.dispose();
    }
}
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

    @Override
    public void create() {
        batch = new SpriteBatch();
         radar = new Radar(
        Gdx.graphics.getWidth() - 50,  // radar top-right corner X
        Gdx.graphics.getHeight() - 50, // radar top-right corner Y
        50,                            // radar radius on screen
        2000                            // radar detection range in world units
    );
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

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {

        
        // radar
        float dt = Gdx.graphics.getDeltaTime();
        bulletsL = bulletsLeft.size();
        bulletsR = bulletsRight.size();

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            float rad = (float) Math.toRadians(playerRotation + 90); // left side
            float spawnX = x + MathUtils.cos(rad) * 60;
            float spawnY = y + MathUtils.sin(rad) * 60;

            cannonballs.add(new CannonBall(spawnX, spawnY, playerRotation + 90, cannonball));

        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            float rad = (float) Math.toRadians(playerRotation - 90); // right side
            float spawnX = x + MathUtils.cos(rad) * 60;
            float spawnY = y + MathUtils.sin(rad) * 60;

            cannonballs.add(new CannonBall(spawnX, spawnY, playerRotation - 90, cannonball));

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
        batch.end();
          radar.render(x, y, enemies);
        // --- Stage ---
        stage.act(dt);
        stage.draw();
     
       
        
        for (int i = cannonballs.size() - 1; i >= 0; i--) {
    CannonBall b = cannonballs.get(i);

    for (int j = enemies.size() - 1; j >= 0; j--) {
        Enemy e = enemies.get(j);

        if (e.rect.overlaps(b.rect)) {

            

            e.health -= 10;
            cannonballs.remove(i);

            // remove enemy if dead
            if (e.health <= 0) {
                enemies.remove(j);
            }

            break; // stop checking this cannonball
        }
    }
}
        

    }

    @Override
    public void dispose() {

        batch.dispose();
        image.dispose();
        image2.dispose();
    }
}
