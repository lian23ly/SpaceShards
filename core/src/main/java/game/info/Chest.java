package game.info;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Chest {
    private final Texture texture;
    private final Rectangle bounds;
    private final float x;
    private final float y;

    public Chest(float x, float y) {
        this.texture = new Texture(Gdx.files.internal("shop.png"));
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void dispose() {
        texture.dispose();
    }
}
