package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import model.Musteri;
import service.ServisYonetimSistemi;

import static ui.UiFactory.*;

/** Giriş ekranı: personel girişi, müşteri koduyla giriş ve yeni kayıt bağlantısı. */
class LoginScreen {

    private final AppContext ctx;
    private final ServisYonetimSistemi servis;

    LoginScreen(AppContext ctx) {
        this.ctx = ctx;
        this.servis = ctx.servis();
    }

    void show(Stage stage) {
        Label lblTitle = new Label("VivaCar Oto Servis");
        lblTitle.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 24;" +
                        "-fx-font-weight: bold;"
        );

        Label lblSubtitle = new Label("Lütfen giriş türünü seçin");
        lblSubtitle.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #9ca3af;" +
                        "-fx-font-size: 14;"
        );

        // Personel
        Label lblUser = createLabel("Kullanıcı Adı");
        Label lblPass = createLabel("Parola");

        TextField txtUser = createTextField();
        txtUser.setMaxWidth(220);

        PasswordField txtPass = new PasswordField();
        txtPass.setMaxWidth(220);
        txtPass.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-background-color: #020617;" +
                        "-fx-border-color: #1f2937;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-prompt-text-fill: #6b7280;"
        );

        Button btnLoginStaff = createColoredButton("Personel Girişi", "#2563eb");

        VBox personelForm = new VBox(10, lblUser, txtUser, lblPass, txtPass, btnLoginStaff);
        personelForm.setPadding(new Insets(20));
        personelForm.setAlignment(Pos.CENTER);
        personelForm.setMaxWidth(260);
        personelForm.setStyle(
                "-fx-background-color: #020617;" +
                        "-fx-border-color: #1f2937;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;"
        );

        Label lblPersonelTitle = new Label("Personel Girişi");
        lblPersonelTitle.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-weight: bold;"
        );

        VBox personelCard = new VBox(8, lblPersonelTitle, personelForm);
        personelCard.setAlignment(Pos.TOP_CENTER);

        // Müşteri (Kod)
        Label lblCodeTitle = new Label("Müşteri Girişi");
        lblCodeTitle.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-weight: bold;"
        );

        Label lblCode = createLabel("Müşteri Kodu");
        TextField txtCode = createTextField();
        txtCode.setMaxWidth(220);

        Button btnLoginCustomer = createColoredButton("Müşteri Olarak Devam Et", "#22c55e");

        Label lblNot = new Label("Müşteri değil misiniz?");
        lblNot.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #9ca3af;" +
                        "-fx-font-size: 11;"
        );

        Hyperlink linkRegister = new Hyperlink("Hemen kaydolun");
        linkRegister.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #38bdf8;" +
                        "-fx-font-size: 12;"
        );

        VBox musteriForm = new VBox(10, lblCode, txtCode, btnLoginCustomer, lblNot, linkRegister);
        musteriForm.setPadding(new Insets(20));
        musteriForm.setAlignment(Pos.CENTER);
        musteriForm.setMaxWidth(260);
        musteriForm.setStyle(
                "-fx-background-color: #020617;" +
                        "-fx-border-color: #1f2937;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;"
        );

        VBox musteriCard = new VBox(8, lblCodeTitle, musteriForm);
        musteriCard.setAlignment(Pos.TOP_CENTER);

        HBox forms = new HBox(30, personelCard, musteriCard);
        forms.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(16, lblTitle, lblSubtitle, forms);
        centerBox.setAlignment(Pos.CENTER);

        StackPane bg = new StackPane(centerBox);
        bg.setPadding(new Insets(32));
        bg.setStyle("-fx-background-color: linear-gradient(to bottom right, #020617, #0b1120);");

        BorderPane root = createWindowChrome(stage, bg, "VivaCar Oto Servis");

        Scene scene = new Scene(root, 1000, 550);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        btnLoginStaff.setOnAction(e -> {
            String u = txtUser.getText().trim();
            String p = txtPass.getText().trim();

            boolean dogru =
                    (u.equals("emre") && p.equals("4435")) ||
                            (u.equals("mert") && p.equals("4646"));

            if (dogru) ctx.showStaffScreen(stage);
            else showAlert("Giriş Hatası", "Kullanıcı adı veya parola hatalı.");
        });

        btnLoginCustomer.setOnAction(e -> {
            String kod = txtCode.getText().trim();
            if (kod.isEmpty()) {
                showAlert("Eksik Bilgi", "Müşteri kodu boş olamaz.");
                return;
            }

            Musteri m = servis.musteriKodlaBul(kod);
            if (m == null) {
                showAlert("Kayıt Bulunamadı",
                        "Bu müşteri kodu ile kayıtlı bir müşteri bulunamadı.\n" +
                                "Lütfen kodunuzu kontrol edin veya yeni kayıt oluşturun.");
            } else {
                ctx.showCustomerPortalScreen(stage, m);
            }
        });

        linkRegister.setOnAction(e -> ctx.showCustomerRegisterScreen(stage));
    }
}
