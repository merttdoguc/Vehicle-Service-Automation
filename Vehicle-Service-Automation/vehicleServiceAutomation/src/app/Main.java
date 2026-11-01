package app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.AppContext;

/** VivaCar Oto Servis - uygulama giriş noktası. */
public class Main extends Application {

    private final AppContext ctx = new AppContext();

    @Override
    public void start(Stage primaryStage) {
        ctx.idSayaclariniGuncelle();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        ctx.showLoginScreen(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
