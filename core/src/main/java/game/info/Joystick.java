package game.info;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;

public class Joystick {
    private final Texture knobTexture;
    private final Texture baseTexture;
    private final Vector2 basePosition;
    private final Vector2 knobPosition;
    private final float radius;
    private boolean active;


    public Joystick(Texture knobTexture, Texture baseTexture, float x, float y, float radius) {
        this.knobTexture = knobTexture;
        this.baseTexture = baseTexture;
        this.basePosition = new Vector2(x + baseTexture.getWidth()/2f, y + baseTexture.getHeight()/2f);
        this.knobPosition = new Vector2(basePosition);
        this.radius = radius;
    }

    public boolean handleTouch(float x, float y, boolean isTouchDown) {
        if (isTouchDown) {
            if (Vector2.dst(x, y, basePosition.x, basePosition.y) <= radius * 1.5f) {
                active = true;
                updateKnobPosition(x, y);
                return true;
            }
            return false;
        } else if (active) {
            updateKnobPosition(x, y);
            return true;
        }
        return false;
    }

    private void updateKnobPosition(float x, float y) {
        Vector2 direction = new Vector2(x - basePosition.x, y - basePosition.y);
        float distance = direction.len();

        if (distance > radius) {
            direction.scl(radius / distance);
        }

        knobPosition.set(basePosition.x + direction.x, basePosition.y + direction.y);
    }

    public Vector2 getDirection() {
        if (!active) return Vector2.Zero;

        Vector2 direction = new Vector2(
            (knobPosition.x - basePosition.x) / radius,
            (knobPosition.y - basePosition.y) / radius
        );

        return direction.len() > 0.1f ? direction : Vector2.Zero;
    }

    public void draw(SpriteBatch batch) {
        Color oldColor = batch.getColor().cpy();

        batch.setColor(1, 1, 1, 0.5f);

        float baseWidth = baseTexture.getWidth() * 1.5f;
        float baseHeight = baseTexture.getHeight() * 1.5f;
        batch.draw(baseTexture,
            basePosition.x - baseWidth / 2f,
            basePosition.y - baseHeight / 2f,
            baseWidth, baseHeight);

        batch.setColor(oldColor);

        float knobWidth = knobTexture.getWidth() * 1.5f;
        float knobHeight = knobTexture.getHeight() * 1.5f;
        batch.draw(knobTexture,
            knobPosition.x - knobWidth / 2f,
            knobPosition.y - knobHeight / 2f,
            knobWidth, knobHeight);
    }

    public void reset() {
        active = false;
        knobPosition.set(basePosition);
    }
    public boolean isTouchInJoystickArea(float x, float y) {
        return Vector2.dst(x, y, basePosition.x, basePosition.y) <= radius * 1.5f;
    }
    public void setPosition(float x, float y) {
        basePosition.set(x + baseTexture.getWidth()/2f, y + baseTexture.getHeight()/2f);
        knobPosition.set(basePosition);
    }
    public Vector2 getBasePosition() {
        return basePosition;
    }

    public void dispose() {
        knobTexture.dispose();
        baseTexture.dispose();
    }
}
