package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/** Tüm ekranlarda ortak kullanılan koyu tema bileşenleri ve diyaloglar. */
final class UiFactory {

    private UiFactory() {
    }

    static Label createLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 12;"
        );
        return l;
    }

    static TextField createTextField() {
        TextField t = new TextField();
        t.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-background-color: #020617;" +
                        "-fx-border-color: #1f2937;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-prompt-text-fill: #6b7280;"
        );
        return t;
    }

    static Button createColoredButton(String text, String bgColor) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 999;" +
                        "-fx-text-fill: #f9fafb;" +
                        "-fx-background-color: " + bgColor + ";" +
                        "-fx-padding: 8 18 8 18;" +
                        "-fx-border-color: transparent;"
        );
        b.setEffect(new DropShadow(12, Color.web("#00000080")));
        return b;
    }

    static VBox createCard(String titleText, Node contentNode) {
        Label title = new Label(titleText);
        title.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #f9fafb;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-weight: bold;"
        );

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1f2937; -fx-opacity: 0.6;");

        VBox box = new VBox(6, title, sep, contentNode);
        box.setPadding(new Insets(10));
        box.setStyle(
                "-fx-background-color: #020617;" +
                        "-fx-border-color: #1f2937;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );
        return box;
    }

    /** Formlarda kullanılan standart koyu temalı ızgara. */
    static GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(8));
        grid.setStyle("-fx-background-color: #020617;");
        return grid;
    }

    /** Izgaraya "etiket + alan" satırı ekler. */
    static void addFormRow(GridPane grid, int row, String labelText, Node field) {
        grid.add(createLabel(labelText), 0, row);
        grid.add(field, 1, row);
    }

    /** Servis türü seçim kutusu (ilk eleman seçili gelir). */
    static ComboBox<String> createServisTuruCombo() {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(
                "Periyodik Bakım",
                "Yağ Değişimi",
                "Fren Bakımı",
                "Lastik Değişimi",
                "Genel Kontrol"
        );
        combo.getSelectionModel().selectFirst();
        combo.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-background-color: #020617;" +
                        "-fx-border-color: #1f2937;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;"
        );
        return combo;
    }

    /** "Oturumu kapat" onay penceresi; kullanıcı onaylarsa true döner. */
    static boolean confirmLogout(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Oturumu Kapat");
        alert.setHeaderText("Güvenli Çıkış");
        alert.setContentText(message);

        DialogPane dp = alert.getDialogPane();
        dp.setStyle("-fx-background-color: #020617; -fx-font-family: 'Montserrat Medium';");
        dp.lookupAll(".label").forEach(node -> {
            if (node instanceof Label) ((Label) node).setTextFill(Color.web("#f9fafb"));
        });

        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    static void lockAsReadOnly(TextField... fields) {
        for (TextField f : fields) {
            f.setEditable(false);
            f.setFocusTraversable(false);
        }
    }

    static void showAlert(String baslik, String mesaj) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);

        DialogPane dp = alert.getDialogPane();
        dp.setStyle(
                "-fx-background-color: #020617;" +
                        "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #f9fafb;"
        );

        Node contentLabel = dp.lookup(".content.label");
        if (contentLabel instanceof Label) ((Label) contentLabel).setTextFill(Color.web("#f9fafb"));

        alert.showAndWait();
    }

    static void applyDarkHeader(TableView<?> tableView) {
        Runnable styler = () -> {
            for (Node n : tableView.lookupAll(".column-header-background")) {
                n.setStyle("-fx-background-color: #020617;");
            }
            for (Node n : tableView.lookupAll(".column-header")) {
                n.setStyle("-fx-background-color: #020617; -fx-border-color: #1f2937; -fx-border-width: 0 1 1 0;");
            }
            for (Node n : tableView.lookupAll(".filler")) {
                n.setStyle("-fx-background-color: #020617; -fx-border-color: #1f2937; -fx-border-width: 0 0 1 0;");
            }
            for (Node n : tableView.lookupAll(".column-header .label")) {
                if (n instanceof Label lbl) {
                    lbl.setTextFill(Color.web("#e5e7eb"));
                    lbl.setStyle("-fx-font-family: 'Montserrat Medium'; -fx-font-weight: bold;");
                }
            }
        };

        if (tableView.getSkin() != null) Platform.runLater(styler);
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) Platform.runLater(styler);
        });
    }

    static BorderPane createWindowChrome(Stage stage, Region content, String titleText) {
        Label icon = new Label("🛠");
        icon.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #38bdf8;" +
                        "-fx-font-size: 18;"
        );

        Label title = new Label(titleText);
        title.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;"
        );

        HBox left = new HBox(8, icon, title);
        left.setAlignment(Pos.CENTER_LEFT);

        Button btnMin = new Button("—");
        Button btnMax = new Button("⬜");
        Button btnClose = new Button("✕");
        styleTitleBarButton(btnMin, false);
        styleTitleBarButton(btnMax, false);
        styleTitleBarButton(btnClose, true);
        btnMin.setOnAction(e -> stage.setIconified(true));
        btnMax.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
        btnClose.setOnAction(e -> stage.close());

        HBox right = new HBox(6, btnMin, btnMax, btnClose);
        right.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox titleBar = new HBox(10, left, spacer, right);
        titleBar.setPadding(new Insets(6, 12, 6, 12));
        titleBar.setStyle(
                "-fx-background-color: #020617;" +
                        "-fx-border-color: #1f2937;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        final double[] offset = new double[2];
        titleBar.setOnMousePressed(e -> {
            offset[0] = e.getSceneX();
            offset[1] = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            if (!stage.isMaximized()) {
                stage.setX(e.getScreenX() - offset[0]);
                stage.setY(e.getScreenY() - offset[1]);
            }
        });

        BorderPane root = new BorderPane();
        root.setTop(titleBar);
        root.setCenter(content);
        root.setStyle("-fx-background-color: #020617;");

        return root;
    }

    private static void styleTitleBarButton(Button b, boolean isClose) {
        String baseColor = isClose ? "#ef4444" : "transparent";
        String textColor = isClose ? "#f9fafb" : "#e5e7eb";

        b.setPrefSize(30, 22);
        b.setFocusTraversable(false);
        b.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-background-color: " + baseColor + ";" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-font-size: 11;" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-color: transparent;"
        );
    }
}
