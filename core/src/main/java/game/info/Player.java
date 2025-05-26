package game.info;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Texture[] walkTextures;
    private Texture idleTexture;
    private final Vector2 position;
    private final float speed;
    private final Vector2 size;
    private Polygon collisionPolygon;
    private float animationTimer = 0;
    private boolean isMoving = false;
    private float frameDuration = 0.2f;
    private boolean isFacingRight = true;
    private Vector2 velocity = new Vector2();

    public Player(Texture idleTexture, Texture[] walkTextures, float x, float y, float speed) {
        this.idleTexture = idleTexture;
        this.walkTextures = walkTextures;
        this.position = new Vector2(x, y);
        this.speed = speed;
        this.size = new Vector2(idleTexture.getWidth(), idleTexture.getHeight());
        collisionPolygon = createCollisionPolygon();
    }

    private Polygon createCollisionPolygon() {
        float[] vertices = {
            -size.x / 2, -size.y / 2,
            -size.x / 2, size.y / 2,
            size.x / 2, size.y / 2,
            size.x / 2, -size.y / 2
        };
        Polygon polygon = new Polygon(vertices);
        polygon.setPosition(position.x, position.y);
        return polygon;
    }

    public void update(Vector2 direction, float delta, Polygon islandBounds) {
        isMoving = direction.len2() > 0;

        if (isMoving) {
            if (Math.abs(direction.x) > 0.1f) {
                if (direction.x > 0) isFacingRight = true;
                else if (direction.x < 0) isFacingRight = false;
            }

            animationTimer += delta;

            Vector2 newPosition = new Vector2(position);
            newPosition.mulAdd(direction, speed * delta);

            Polygon tempPolygon = createCollisionPolygon();
            tempPolygon.setPosition(newPosition.x, newPosition.y);

            if (islandBounds == null || Intersector.overlapConvexPolygons(tempPolygon, islandBounds)) {
                position.set(newPosition);
                collisionPolygon.setPosition(newPosition.x, newPosition.y);
                velocity.set(direction).scl(speed);
            } else {
                newPosition.set(position.x + direction.x * speed * delta, position.y);
                tempPolygon = createCollisionPolygon();
                tempPolygon.setPosition(newPosition.x, position.y);

                if (islandBounds == null || Intersector.overlapConvexPolygons(tempPolygon, islandBounds)) {
                    position.x = newPosition.x;
                    collisionPolygon.setPosition(newPosition.x, position.y);
                    velocity.set(direction.x, 0).scl(speed);
                } else {
                    newPosition.set(position.x, position.y + direction.y * speed * delta);
                    tempPolygon = createCollisionPolygon();
                    tempPolygon.setPosition(position.x, newPosition.y);
                    if (islandBounds == null || Intersector.overlapConvexPolygons(tempPolygon, islandBounds)) {
                        position.y = newPosition.y;
                        collisionPolygon.setPosition(position.x, newPosition.y);
                        velocity.set(0, direction.y).scl(speed);
                    } else {
                        velocity.set(0, 0);
                    }
                }
            }
        } else {
            animationTimer = 0;
            velocity.set(0, 0);
        }
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(
            position.x - size.x / 2,
            position.y - size.y / 2,
            size.x,
            size.y
        );
    }

    public void draw(SpriteBatch batch) {
        Texture currentTexture = isMoving ?
            walkTextures[(int) (animationTimer / frameDuration) % walkTextures.length] :
            idleTexture;

        batch.draw(currentTexture,
            isFacingRight ? position.x - size.x / 2 : position.x + size.x / 2,
            position.y - size.y / 2,
            isFacingRight ? size.x : -size.x,
            size.y);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getVelocityX() {
        return velocity.x;
    }

    public float getVelocityY() {
        return velocity.y;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void dispose() {

    }

    public Polygon getCollisionPolygon() {
        return collisionPolygon;
    }
}
