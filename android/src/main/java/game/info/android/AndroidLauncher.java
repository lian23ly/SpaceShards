package game.info.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import game.info.MyGames;
import android.view.View;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true; // Recommended, but not required.

        // Установите размеры окна, умноженные на масштаб
        int windowWidth = MyGames.WIDTH * MyGames.SCALE;
        int windowHeight = MyGames.HEIGHT * MyGames.SCALE;

        // Устанавливаем размеры окна
      /*  config.width = windowWidth;   //Deprecated: не используйте
        config.height = windowHeight;  // Не используйте
*/
        // Инициализируем игру
        initialize(new MyGames(), config);

        // Получаем View, которое отображает игру
        View view = getWindow().getDecorView();

        // Устанавливаем размеры View, чтобы соответствовать масштабу игры
        view.setMinimumWidth(windowWidth);
        view.setMinimumHeight(windowHeight);

    }
}
