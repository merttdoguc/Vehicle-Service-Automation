import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Main extends Application {

    private ServisYonetimSistemi servis = new ServisYonetimSistemi();

    // Personel ekranı: Müşteri alanları (ID otomatik)
    private TextField txtAd;
    private TextField txtSoyad;
    private TextField txtTelefon;
    private TextField txtEmail;

    // Personel ekranı: Araç alanları
    private TextField txtPlaka;
    private TextField txtMarka;
    private TextField txtModel;
    private TextField txtUretimYili;
    private TextField txtKilometre;
    private TextField txtSonServis;
    private TextField txtSasiNo;
    private TextField txtYakitTipi;

    // Personel ekranı: Randevu alanları
    private TextField txtTarih;   // gg.MM.yyyy
    private TextField txtSaat;    // HH:mm
    private ComboBox<String> cmbServisTuru;

    // Tablo
    private TableView<Randevu> table;
    private ObservableList<Randevu> randevuData;

    private final DateTimeFormatter TARIH_SAAT_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final DateTimeFormatter TARIH_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter SAAT_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    // Otomatik ID sayaçları
    private int nextMusteriID = 1;
    private int nextRandevuID = 1;

    // Alt sol köşe tarih-saat
    private Label lblDateTime;

    @Override
    public void start(Stage primaryStage) {
        // SİSTEMİ BAŞLATIRKEN ID'LERİ DE GÜNCELLE
        idSayaclariniGuncelle();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        showLoginScreen(primaryStage);
    }

    // BU YENİ METODU DA Main.java İÇİNDE UYGUN BİR YERE EKLE
    private void idSayaclariniGuncelle() {
        // Müşteri ID'lerini tara
        for (Musteri m : servis.getMusteriListesi()) {
            if (m.getMusteriID() >= nextMusteriID) {
                nextMusteriID = m.getMusteriID() + 1;
            }
        }
        // Randevu ID'lerini tara
        for (Randevu r : servis.getRandevuListesi()) {
            if (r.getRandevuID() >= nextRandevuID) {
                nextRandevuID = r.getRandevuID() + 1;
            }
        }
    }

    // ===================== LOGIN EKRANI =========================

    private void showLoginScreen(Stage stage) {
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

        // =========== PERSONEL GİRİŞİ ===========

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

        VBox personelForm = new VBox(10,
                lblUser, txtUser,
                lblPass, txtPass,
                btnLoginStaff
        );
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

        // =========== MÜŞTERİ GİRİŞİ (MÜŞTERİ KODU) ===========

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

        VBox musteriForm = new VBox(10,
                lblCode, txtCode,
                btnLoginCustomer,
                lblNot,
                linkRegister
        );
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

        // İki kart yan yana
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

        // =========== EVENTLER ===========

        // Personel girişi
        btnLoginStaff.setOnAction(e -> {
            String u = txtUser.getText().trim();
            String p = txtPass.getText().trim();

            boolean dogru =
                    (u.equals("emre") && p.equals("4435")) ||
                            (u.equals("mert") && p.equals("4646"));

            if (dogru) {
                showMainScreen(stage);
            } else {
                showAlert("Giriş Hatası", "Kullanıcı adı veya parola hatalı.");
            }
        });

        // Müşteri girişi (müşteri kodu)
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
                showCustomerPortalScreen(stage, m);
            }
        });

        // Kayıt ekranına geçiş
        linkRegister.setOnAction(e -> showCustomerRegisterScreen(stage));
    }

    // ===================== YENİ MÜŞTERİ KAYIT EKRANI =========================

    private void showCustomerRegisterScreen(Stage stage) {
        Label lblHeader = new Label("Yeni Müşteri Kaydı");
        lblHeader.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;"
        );

        // ---- MÜŞTERİ FORMU ----
        TextField cAd = createTextField();
        TextField cSoyad = createTextField();
        TextField cTel = createTextField();
        TextField cEmail = createTextField();

        GridPane musteriGrid = new GridPane();
        musteriGrid.setHgap(8);
        musteriGrid.setVgap(6);
        musteriGrid.setPadding(new Insets(8));
        musteriGrid.setStyle("-fx-background-color: #020617;");

        int row = 0;
        musteriGrid.add(createLabel("Ad"), 0, row);
        musteriGrid.add(cAd, 1, row++);

        musteriGrid.add(createLabel("Soyad"), 0, row);
        musteriGrid.add(cSoyad, 1, row++);

        musteriGrid.add(createLabel("Telefon (11 hane)"), 0, row);
        musteriGrid.add(cTel, 1, row++);

        musteriGrid.add(createLabel("E-posta"), 0, row);
        musteriGrid.add(cEmail, 1, row++);

        VBox musteriCard = createCard("Müşteri Bilgileri", musteriGrid);

        // ---- ARAÇ FORMU ----
        TextField cPlaka = createTextField();
        TextField cMarka = createTextField();
        TextField cModel = createTextField();
        TextField cUretimYili = createTextField();
        TextField cKilometre = createTextField();
        TextField cSonServis = createTextField();
        TextField cSasiNo = createTextField();
        TextField cYakitTipi = createTextField();

        GridPane aracGrid = new GridPane();
        aracGrid.setHgap(8);
        aracGrid.setVgap(6);
        aracGrid.setPadding(new Insets(8));
        aracGrid.setStyle("-fx-background-color: #020617;");

        row = 0;
        aracGrid.add(createLabel("Plaka"), 0, row);
        aracGrid.add(cPlaka, 1, row++);

        aracGrid.add(createLabel("Marka"), 0, row);
        aracGrid.add(cMarka, 1, row++);

        aracGrid.add(createLabel("Model"), 0, row);
        aracGrid.add(cModel, 1, row++);

        aracGrid.add(createLabel("Üretim Yılı"), 0, row);
        aracGrid.add(cUretimYili, 1, row++);

        aracGrid.add(createLabel("Kilometre"), 0, row);
        aracGrid.add(cKilometre, 1, row++);

        aracGrid.add(createLabel("Son Servis Tarihi (gg.MM.yyyy)"), 0, row);
        aracGrid.add(cSonServis, 1, row++);

        aracGrid.add(createLabel("Şasi No"), 0, row);
        aracGrid.add(cSasiNo, 1, row++);

        aracGrid.add(createLabel("Yakıt Tipi"), 0, row);
        aracGrid.add(cYakitTipi, 1, row++);

        VBox aracCard = createCard("Araç Bilgileri", aracGrid);

        HBox centerForms = new HBox(20, musteriCard, aracCard);
        centerForms.setPadding(new Insets(16));
        centerForms.setAlignment(Pos.TOP_CENTER);

        Button btnKaydet = createColoredButton("Kaydı Tamamla", "#22c55e");
        Button btnGeri = createColoredButton("Giriş Ekranına Dön", "#6b7280");

        HBox buttonBar = new HBox(12, btnKaydet, btnGeri);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(12));

        VBox center = new VBox(10, lblHeader, centerForms, buttonBar);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(16));
        center.setStyle("-fx-background-color: #020617;");

        BorderPane content = new BorderPane();
        content.setCenter(center);
        content.setStyle("-fx-background-color: #020617;");

        BorderPane root = createWindowChrome(stage, content, "VivaCar Oto Servis - Yeni Kayıt");

        Scene scene = new Scene(root, 950, 600);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        // EVENTLER

        btnGeri.setOnAction(e -> showLoginScreen(stage));

        btnKaydet.setOnAction(e -> {
            try {
                String ad = cAd.getText().trim();
                String soyad = cSoyad.getText().trim();
                String tel = cTel.getText().trim();
                String email = cEmail.getText().trim();

                String plaka = cPlaka.getText().trim();
                String marka = cMarka.getText().trim();
                String model = cModel.getText().trim();
                String uretimYiliS = cUretimYili.getText().trim();
                String kmS = cKilometre.getText().trim();
                String sonServis = cSonServis.getText().trim();
                String sasiNo = cSasiNo.getText().trim();
                String yakitTipi = cYakitTipi.getText().trim();

                if (ad.isEmpty() || soyad.isEmpty() || tel.isEmpty() || plaka.isEmpty()) {
                    showAlert("Eksik Bilgi", "Ad, Soyad, Telefon ve Plaka boş olamaz.");
                    return;
                }

                // Telefon 11 hane kontrolü
                if (!tel.matches("\\d{11}")) {
                    showAlert("Telefon Hatası",
                            "Telefon numarası 11 haneli olmalıdır.\n" +
                                    "Örnek: 05523713980");
                    return;
                }

                // Aynı telefonla ikinci kayıt yasak
                Musteri mevcut = servis.musteriTelefonlaBul(tel);
                if (mevcut != null) {
                    showAlert(
                            "Telefon Zaten Kayıtlı",
                            "Bu telefon numarası ile zaten bir müşteri kaydı var.\n" +
                                    "Lütfen müşteri kodu ile giriş yapın."
                    );
                    return;
                }

                // --- E-POSTA KONTROLÜ (BURAYI EKLE) ---
                // Regex ile @ işareti ve mail formatını kontrol ediyoruz
                if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    showAlert("E-posta Hatası",
                            "Lütfen geçerli bir e-posta adresi giriniz.\n" +
                                    "Örnek: mertdoguc@hotmail.com");
                    return; // Hata varsa kaydı durdurur, aşağıya geçmez
                }

                // Son servis tarihi kontrolü (isteğe bağlı, boş olabilir)
                if (!sonServis.isEmpty() && !isGecerliServisTarihi(sonServis)) {
                    showAlert("Tarih Hatası",
                            "Son servis tarihi gg.MM.yyyy formatında olmalı.\n" +
                                    "Ay 1–12 arasında, Şubat en fazla 28 gün,\n" +
                                    "diğer aylar en fazla 31 gün olabilir.");
                    return;
                }

                int uretimYili = 0;
                int km = 0;
                try {
                    if (!uretimYiliS.isEmpty()) uretimYili = Integer.parseInt(uretimYiliS);
                    if (!kmS.isEmpty()) km = Integer.parseInt(kmS);
                } catch (NumberFormatException ex) {
                    showAlert("Sayı Hatası", "Üretim yılı ve kilometre sayı olmalıdır.");
                    return;
                }

                // --- ŞİMDİ MANTIK KONTROLLERİNİ BURAYA YAPIŞTIR ---

                // 1. Plaka Tekrar Kontrolü (Aynı araçtan iki tane olmasın)
                if (servis.aracAra(plaka) != null) {
                    showAlert("Plaka Zaten Kayıtlı", "Bu plaka ile sisteme kayıtlı bir araç zaten var.");
                    return;
                }

                // 2. Kilometre Mantık Kontrolü (Sadece negatif girişi engelliyoruz)
                if (km < 0) {
                    showAlert("Kilometre Hatası", "Kilometre negatif bir değer olamaz!");
                    return;
                }

                // 3. Üretim Yılı Mantık Kontrolü
                int mevcutYil = java.time.Year.now().getValue();
                if (uretimYili != 0 && (uretimYili < 1900 || uretimYili > mevcutYil + 1)) {
                    showAlert("Üretim Yılı Hatası",
                            "Lütfen geçerli bir üretim yılı giriniz (1900 - " + (mevcutYil + 1) + ").");
                    return;
                }

                int yeniMusteriID = nextMusteriID++;
                String yeniKod = servis.yeniMusteriKoduUret();

                Musteri musteri = new Musteri(yeniMusteriID, ad, soyad, tel, email, yeniKod);
                servis.musteriEkle(musteri);

                Arac arac = new Arac(plaka, marka, model, uretimYili, km, sonServis, sasiNo, yakitTipi);
                servis.musteriyeAracEkle(musteri, arac);

                showAlert("Kayıt Tamamlandı",
                        "Müşteri kaydınız oluşturuldu.\n\n" +
                                "Müşteri kodunuz: " + yeniKod +
                                "\nLütfen bu kodu kimseyle paylaşmayınız.");

                showLoginScreen(stage);

            } catch (NumberFormatException ex) {
                showAlert("Sayı Hatası", "Üretim yılı ve kilometre alanları sayısal olmalıdır.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Hata", "Kayıt oluşturulurken hata oluştu: " + ex.getMessage());
            }
        });
    }

    // ===================== MÜŞTERİ PORTAL EKRANI =========================

    private void showCustomerPortalScreen(Stage stage, Musteri musteri) {
        Label lblHeader = new Label("Müşteri Paneli");
        lblHeader.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;"
        );

        Label lblWelcome = new Label("Hoş geldiniz, " + musteri.getAd() + " " + musteri.getSoyad());
        lblWelcome.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #9ca3af;" +
                        "-fx-font-size: 13;"
        );

        VBox headerBox = new VBox(4, lblHeader, lblWelcome);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(8, 0, 12, 0));

        // Sol: Araç listesi
        ListView<Arac> lstAraclar = new ListView<>();
        lstAraclar.setItems(FXCollections.observableArrayList(musteri.getSahipOlunanAraclar()));
        lstAraclar.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Arac item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String text = item.getPlaka();
                    if (item.getMarka() != null && !item.getMarka().isEmpty()) {
                        text += " - " + item.getMarka();
                    }
                    if (item.getModel() != null && !item.getModel().isEmpty()) {
                        text += " " + item.getModel();
                    }
                    setText(text);
                }
            }
        });
        lstAraclar.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-control-inner-background: #020617;" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-selection-bar: #2563eb;" +
                        "-fx-selection-bar-text: #f9fafb;"
        );

        VBox aracCard = createCard("Kayıtlı Araçlarınız", lstAraclar);
        aracCard.setPrefWidth(320);

        // Sağ: Randevu oluşturma paneli
        TextField cTarih = createTextField();
        TextField cSaat = createTextField();
        ComboBox<String> cServisTuru = new ComboBox<>();
        cServisTuru.getItems().addAll(
                "Periyodik Bakım",
                "Yağ Değişimi",
                "Fren Bakımı",
                "Lastik Değişimi",
                "Genel Kontrol"
        );
        cServisTuru.getSelectionModel().selectFirst();
        cServisTuru.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-background-color: #020617;" +
                        "-fx-border-color: #1f2937;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;"
        );

        GridPane randevuGrid = new GridPane();
        randevuGrid.setHgap(8);
        randevuGrid.setVgap(6);
        randevuGrid.setPadding(new Insets(8));
        randevuGrid.setStyle("-fx-background-color: #020617;");

        int row = 0;
        randevuGrid.add(createLabel("Tarih (gg.MM.yyyy)"), 0, row);
        randevuGrid.add(cTarih, 1, row++);

        randevuGrid.add(createLabel("Saat (HH:mm)"), 0, row);
        randevuGrid.add(cSaat, 1, row++);

        randevuGrid.add(createLabel("Servis Türü"), 0, row);
        randevuGrid.add(cServisTuru, 1, row++);

        VBox randevuCard = createCard("Randevu Oluştur", randevuGrid);

        Button btnRandevuOlustur = createColoredButton("Randevu Oluştur", "#2563eb");
        Button btnGeri = createColoredButton("Oturumu Kapat", "#ef4444");

        HBox buttonBar = new HBox(10, btnRandevuOlustur, btnGeri);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(12, 0, 0, 0));

        VBox sagPanel = new VBox(10, randevuCard, buttonBar);
        sagPanel.setPadding(new Insets(0, 0, 0, 0));

        HBox center = new HBox(20, aracCard, sagPanel);
        center.setPadding(new Insets(8, 0, 0, 0));

        VBox mainBox = new VBox(8, headerBox, center);
        mainBox.setPadding(new Insets(16));
        mainBox.setStyle("-fx-background-color: #020617;");

        BorderPane content = new BorderPane();
        content.setCenter(mainBox);
        content.setStyle("-fx-background-color: #020617;");

        BorderPane root = createWindowChrome(stage, content, "VivaCar Oto Servis - Müşteri Paneli");

        Scene scene = new Scene(root, 900, 550);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        // EVENTLER

        // Oturumu Kapat butonu için onay kutulu yeni mantık
        btnGeri.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Oturumu Kapat");
            alert.setHeaderText("Güvenli Çıkış");
            alert.setContentText("Hesabınızdan çıkış yapmak istediğinize emin misiniz?");

            // Alert kutusunu VivaCar'ın karanlık temasına uyduruyoruz
            DialogPane dp = alert.getDialogPane();
            dp.setStyle("-fx-background-color: #020617; -fx-font-family: 'Montserrat Medium';");

            // Yazıları senin kullandığın beyaz tona (#f9fafb) çeviriyoruz
            dp.lookupAll(".label").forEach(node -> {
                if (node instanceof Label) ((Label) node).setTextFill(Color.web("#f9fafb"));
            });

            // Kullanıcı "Tamam" derse giriş ekranına geri gönder
            if (alert.showAndWait().get() == ButtonType.OK) {
                showLoginScreen(stage);
            }
        });

        // Randevu oluşturma butonu (Burası aynı kalıyor)
        btnRandevuOlustur.setOnAction(e -> {
            try {
                Arac seciliArac = lstAraclar.getSelectionModel().getSelectedItem();
                if (seciliArac == null) {
                    showAlert("Seçim Yok", "Lütfen randevu oluşturmak için listeden bir araç seçin.");
                    return;
                }
                // ... (Kodun geri kalanı sende olduğu gibi devam ediyor)

                String tarihStr = cTarih.getText().trim();
                String saatStr = cSaat.getText().trim();
                String servisTuru = cServisTuru.getValue();

                if (tarihStr.isEmpty() || saatStr.isEmpty()) {
                    showAlert("Eksik Bilgi", "Tarih ve Saat alanları boş olamaz.");
                    return;
                }

                LocalDateTime tarihSaat;
                try {
                    tarihSaat = LocalDateTime.parse(tarihStr + " " + saatStr, TARIH_SAAT_FORMAT);
                } catch (DateTimeParseException ex) {
                    showAlert("Tarih/Saat Hatası", "Tarih: gg.MM.yyyy, Saat: HH:mm formatında olmalı.");
                    return;
                }

                if (tarihSaat.isBefore(LocalDateTime.now())) {
                    showAlert("Geçersiz Tarih",
                            "Geçmiş bir tarihe randevu oluşturulamaz.");
                    return;
                }

                int yeniRandevuID = nextRandevuID++;
                String durum = "Onaylandı";

                Randevu r = new Randevu(yeniRandevuID, musteri, seciliArac, tarihSaat, servisTuru, durum);

                if (!servis.randevuEkle(r)) {
                    showAlert("Çakışma", "Bu araç için aynı tarih & saatte başka bir randevu var.");
                    return;
                }

                showAlert("Başarılı", "Randevunuz oluşturuldu. Personel onay ekranına düşmüştür.");

                cTarih.clear();
                cSaat.clear();
                cServisTuru.getSelectionModel().selectFirst();

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Hata", "Randevu oluşturulurken hata oluştu: " + ex.getMessage());
            }
        });
    }

    // ===================== PERSONEL ANA EKRAN =========================

    private void showMainScreen(Stage stage) {
        randevuData = FXCollections.observableArrayList();

        // Mevcut veriden ID sayaçlarını başlat
        for (Musteri m : servis.getMusteriListesi()) {
            if (m.getMusteriID() >= nextMusteriID) {
                nextMusteriID = m.getMusteriID() + 1;
            }
        }
        for (Randevu r : servis.getRandevuListesi()) {
            if (r.getRandevuID() >= nextRandevuID) {
                nextRandevuID = r.getRandevuID() + 1;
            }
        }

        // -------- MÜŞTERİ PANELİ (GÖRÜNTÜ / EKLEME İÇİN) --------
        txtAd = createTextField();
        txtSoyad = createTextField();
        txtTelefon = createTextField();
        txtEmail = createTextField();

        GridPane musteriGrid = new GridPane();
        musteriGrid.setHgap(8);
        musteriGrid.setVgap(6);
        musteriGrid.setPadding(new Insets(8));
        musteriGrid.setStyle("-fx-background-color: #020617;");

        int row = 0;
        musteriGrid.add(createLabel("Ad"), 0, row);
        musteriGrid.add(txtAd, 1, row++);

        musteriGrid.add(createLabel("Soyad"), 0, row);
        musteriGrid.add(txtSoyad, 1, row++);

        musteriGrid.add(createLabel("Telefon"), 0, row);
        musteriGrid.add(txtTelefon, 1, row++);

        musteriGrid.add(createLabel("E-posta"), 0, row);
        musteriGrid.add(txtEmail, 1, row++);

        VBox musteriCard = createCard("Müşteri Bilgileri", musteriGrid);

        // -------- ARAÇ PANELİ --------
        txtPlaka = createTextField();
        txtMarka = createTextField();
        txtModel = createTextField();
        txtUretimYili = createTextField();
        txtKilometre = createTextField();
        txtSonServis = createTextField();
        txtSasiNo = createTextField();
        txtYakitTipi = createTextField();

        GridPane aracGrid = new GridPane();
        aracGrid.setHgap(8);
        aracGrid.setVgap(6);
        aracGrid.setPadding(new Insets(8));
        aracGrid.setStyle("-fx-background-color: #020617;");

        row = 0;
        aracGrid.add(createLabel("Plaka"), 0, row);
        aracGrid.add(txtPlaka, 1, row++);

        aracGrid.add(createLabel("Marka"), 0, row);
        aracGrid.add(txtMarka, 1, row++);

        aracGrid.add(createLabel("Model"), 0, row);
        aracGrid.add(txtModel, 1, row++);

        aracGrid.add(createLabel("Üretim Yılı"), 0, row);
        aracGrid.add(txtUretimYili, 1, row++);

        aracGrid.add(createLabel("Kilometre"), 0, row);
        aracGrid.add(txtKilometre, 1, row++);

        aracGrid.add(createLabel("Son Servis Tarihi (gg.MM.yyyy)"), 0, row);
        aracGrid.add(txtSonServis, 1, row++);

        aracGrid.add(createLabel("Şasi No"), 0, row);
        aracGrid.add(txtSasiNo, 1, row++);

        aracGrid.add(createLabel("Yakıt Tipi"), 0, row);
        aracGrid.add(txtYakitTipi, 1, row++);

        VBox aracCard = createCard("Araç Bilgileri", aracGrid);

        // -------- RANDEVU PANELİ --------
        txtTarih = createTextField();
        txtSaat = createTextField();

        cmbServisTuru = new ComboBox<>();
        cmbServisTuru.getItems().addAll(
                "Periyodik Bakım",
                "Yağ Değişimi",
                "Fren Bakımı",
                "Lastik Değişimi",
                "Genel Kontrol"
        );
        cmbServisTuru.getSelectionModel().selectFirst();
        cmbServisTuru.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-background-color: #020617;" +
                        "-fx-border-color: #1f2937;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;"
        );

        GridPane randevuGrid = new GridPane();
        randevuGrid.setHgap(8);
        randevuGrid.setVgap(6);
        randevuGrid.setPadding(new Insets(8));
        randevuGrid.setStyle("-fx-background-color: #020617;");

        row = 0;
        randevuGrid.add(createLabel("Tarih (gg.MM.yyyy)"), 0, row);
        randevuGrid.add(txtTarih, 1, row++);

        randevuGrid.add(createLabel("Saat (HH:mm)"), 0, row);
        randevuGrid.add(txtSaat, 1, row++);

        randevuGrid.add(createLabel("Servis Türü"), 0, row);
        randevuGrid.add(cmbServisTuru, 1, row++);

        VBox randevuCard = createCard("Randevu Bilgileri", randevuGrid);

        VBox solForm = new VBox(12, musteriCard, aracCard, randevuCard);
        solForm.setPadding(new Insets(16));
        solForm.setPrefWidth(360);

        // -------- TABLO --------
        table = new TableView<>();
        table.setItems(randevuData);

        TableColumn<Randevu, String> colMusteriAd = new TableColumn<>("Müşteri Ad Soyad");
        colMusteriAd.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getMusteri() != null
                                ? cellData.getValue().getMusteri().getAd() + " " +
                                cellData.getValue().getMusteri().getSoyad()
                                : ""
                )
        );

        TableColumn<Randevu, String> colPlaka = new TableColumn<>("Plaka");
        colPlaka.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getArac() != null
                                ? cellData.getValue().getArac().getPlaka()
                                : ""
                )
        );

        TableColumn<Randevu, String> colTarihSaat = new TableColumn<>("Tarih & Saat");
        colTarihSaat.setCellValueFactory(
                cellData -> {
                    LocalDateTime ts = cellData.getValue().getTarihSaat();
                    String val = (ts != null) ? ts.format(TARIH_SAAT_FORMAT) : "";
                    return new SimpleStringProperty(val);
                }
        );

        TableColumn<Randevu, String> colServis = new TableColumn<>("Servis Türü");
        colServis.setCellValueFactory(new PropertyValueFactory<>("servisTuru"));

        TableColumn<Randevu, String> colDurum = new TableColumn<>("Durum");
        colDurum.setCellValueFactory(new PropertyValueFactory<>("durum"));

        table.getColumns().addAll(colMusteriAd, colPlaka, colTarihSaat, colServis, colDurum);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle(
                "-fx-background-color: #020617;" +
                        "-fx-control-inner-background: #020617;" +
                        "-fx-table-cell-border-color: #111827;" +
                        "-fx-text-background-color: #f9fafb;" +
                        "-fx-selection-bar: #2563eb;" +
                        "-fx-selection-bar-text: #f9fafb;" +
                        "-fx-font-family: 'Montserrat Medium';"
        );

        randevuData.addAll(servis.getRandevuListesi());

        // Tablo seçildiğinde formu doldur (sadece gösterim amaçlı)
        table.getSelectionModel().selectedItemProperty().addListener((obs, eski, secili) -> {
            if (secili != null) {
                formuRandevudanDoldur(secili);
            }
        });

        VBox tableCard = createCard("Randevu Listesi", table);

        // 1. Oturumu Kapat butonu ve Onay Mekanizması
        Button btnLogout = createColoredButton("Oturumu Kapat", "#ef4444");
        btnLogout.setOnAction(e -> {
            // Onay kutusu oluşturuluyor
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Oturumu Kapat");
            alert.setHeaderText("Güvenli Çıkış");
            alert.setContentText("Oturumu kapatmak istediğinize emin misiniz?");

            // Karanlık tema ayarı (Alert'in bembeyaz patlamasını engeller)
            DialogPane dp = alert.getDialogPane();
            dp.setStyle("-fx-background-color: #020617; -fx-font-family: 'Montserrat Medium';");
            dp.lookupAll(".label").forEach(node -> ((Label) node).setTextFill(Color.web("#f9fafb")));

            // Kullanıcı OK derse işlemleri yap ve çık
            if (alert.showAndWait().get() == ButtonType.OK) {
                inputTemizle(); // Önceki personelin bilgilerini temizle
                showLoginScreen(stage); // Giriş ekranına gönder
            }
        });

        // 2. Başlık Etiketi
        Label lblHeader = new Label("Personel İşlem Ekranı");
        lblHeader.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;"
        );

        // 3. Başlığı solda, butonu sağda tutan boşluk
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // 4. HeaderBox Güncellemesi
        HBox headerBox = new HBox(15, lblHeader, headerSpacer, btnLogout);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(10, 25, 10, 25));
        // -------- BUTONLAR --------
        Button btnEkle = createColoredButton("Randevu Ekle", "#2563eb");
        Button btnGuncelle = createColoredButton("Seçili Randevunun Tarihini Güncelle", "#f97316");
        Button btnSil = createColoredButton("Seçili Randevuyu Sil", "#ef4444");
        Button btnDurumGuncelle = createColoredButton("Seçili Randevuyu Tamamlandı Yap", "#22c55e");
        Button btnIptal = createColoredButton("Seçili Randevuyu İptal Et", "#6b7280");
        Button btnFatura = createColoredButton("Fatura Kes (Manuel)", "#a855f7");

        HBox buttonBar = new HBox(10,
                btnEkle, btnGuncelle, btnSil, btnDurumGuncelle, btnIptal, btnFatura
        );
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(12));

        VBox sagTaraf = new VBox(12, tableCard, buttonBar);
        sagTaraf.setPadding(new Insets(16));
        sagTaraf.setStyle("-fx-background-color: #020617;");

        BorderPane middle = new BorderPane();
        middle.setTop(headerBox);
        middle.setLeft(solForm);
        middle.setCenter(sagTaraf);
        middle.setPadding(new Insets(8));
        middle.setStyle("-fx-background-color: #020617;");

        // -------- ALT ÇUBUK: TARİH/SAAT + FOTO --------
        lblDateTime = new Label(LocalDateTime.now().format(TARIH_SAAT_FORMAT));
        lblDateTime.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #f9fafb;" +
                        "-fx-font-size: 18;"
        );

        HBox timeBox = new HBox(lblDateTime);
        timeBox.setAlignment(Pos.CENTER_LEFT);

        ImageView mechanicView = null;
        try {
            // Fotoğrafı buraya koy: src/main/resources/ui/usta.png
            Image img = new Image(getClass().getResourceAsStream("/ui/usta.png"));
            mechanicView = new ImageView(img);
            mechanicView.setFitHeight(180);
            mechanicView.setPreserveRatio(true);
            mechanicView.setSmooth(true);
            mechanicView.setEffect(new DropShadow(18, Color.web("#00000080")));
        } catch (Exception e) {
            // Foto bulunamazsa sessiz geç
        }

        Label tagline = new Label("\"VivaCar Oto Servis\" olarak 45 yıldır hizmetinizdeyiz");
        tagline.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #f9fafb;" +
                        "-fx-font-style: italic;" +
                        "-fx-font-size: 16;"
        );

        VBox rightBottom = new VBox(10);
        if (mechanicView != null) {
            rightBottom.getChildren().addAll(mechanicView, tagline);
        } else {
            rightBottom.getChildren().add(tagline);
        }
        rightBottom.setAlignment(Pos.BOTTOM_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomBar = new HBox(16, timeBox, spacer, rightBottom);
        bottomBar.setPadding(new Insets(8, 16, 16, 16));
        bottomBar.setStyle("-fx-background-color: #020617;");

        BorderPane content = new BorderPane();
        content.setCenter(middle);
        content.setBottom(bottomBar);
        content.setStyle("-fx-background-color: #020617;");

        BorderPane root = createWindowChrome(stage, content, "VivaCar Oto Servis");

        Scene scene = new Scene(root, 1200, 650);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        // HEADER'I SİMSİYAH YAP
        applyDarkHeader(table);

        // -------- EVENTLER --------
        btnEkle.setOnAction(e -> randevuEkle());
        btnSil.setOnAction(e -> randevuSil());
        btnDurumGuncelle.setOnAction(e -> randevuDurumTamamlandi());
        btnIptal.setOnAction(e -> randevuIptalEt());
        btnGuncelle.setOnAction(e -> randevuGuncelle());
        btnFatura.setOnAction(e -> faturaKes());
    }

    // ===================== EVENT METODLARI =========================

    // Personel ekranından randevu ekleme
    private void randevuEkle() {
        try {
            String ad = txtAd.getText().trim();
            String soyad = txtSoyad.getText().trim();
            String telefon = txtTelefon.getText().trim();
            String email = txtEmail.getText().trim();

            String plaka = txtPlaka.getText().trim();
            String marka = txtMarka.getText().trim();
            String model = txtModel.getText().trim();
            String uretimYiliS = txtUretimYili.getText().trim();
            String kmS = txtKilometre.getText().trim();
            String sonServis = txtSonServis.getText().trim();
            String sasiNo = txtSasiNo.getText().trim();
            String yakitTipi = txtYakitTipi.getText().trim();

            String tarihStr = txtTarih.getText().trim();
            String saatStr = txtSaat.getText().trim();
            String servisTuru = cmbServisTuru.getValue();
            String durum = "Onaylandı"; // Yeni randevular direkt onaylı

            if (ad.isEmpty() || soyad.isEmpty()
                    || plaka.isEmpty()
                    || tarihStr.isEmpty() || saatStr.isEmpty()) {
                showAlert("Eksik Bilgi",
                        "Ad, Soyad, Plaka, Tarih ve Saat boş olamaz.");
                return;
            }

            // Son servis tarihi kontrolü
            if (!sonServis.isEmpty() && !isGecerliServisTarihi(sonServis)) {
                showAlert("Tarih Hatası",
                        "Son servis tarihi gg.MM.yyyy formatında olmalı.\n" +
                                "Ay 1–12 arasında, Şubat en fazla 28 gün,\n" +
                                "diğer aylar en fazla 31 gün olabilir.");
                return;
            }

            int uretimYili = 0;
            int km = 0;
            if (!uretimYiliS.isEmpty()) {
                uretimYili = Integer.parseInt(uretimYiliS);
            }
            if (!kmS.isEmpty()) {
                km = Integer.parseInt(kmS);
            }

            LocalDateTime tarihSaat;
            try {
                tarihSaat = LocalDateTime.parse(tarihStr + " " + saatStr, TARIH_SAAT_FORMAT);
            } catch (DateTimeParseException ex) {
                showAlert("Tarih/Saat Hatası", "Tarih: gg.MM.yyyy, Saat: HH:mm formatında olmalı.");
                return;
            }

            // Geçmiş tarihe izin verme
            if (tarihSaat.isBefore(LocalDateTime.now())) {
                showAlert("Geçersiz Tarih",
                        "Geçmiş bir tarihe randevu oluşturulamaz.");
                return;
            }

            // Müşteri: telefon numarasına göre bul, yoksa isim/telefonla yeni müşteri
            Musteri musteri = null;
            if (!telefon.isEmpty()) {
                musteri = findMusteriByTelefon(telefon);
            }
            if (musteri == null) {
                int yeniMusteriID = nextMusteriID++;
                // Personel ekranından eklenen müşteriye müşteri kodu vermiyoruz
                // (müşteri paneline bu kayıtlar girmeyecekse null bırakılabilir)
                musteri = new Musteri(yeniMusteriID, ad, soyad, telefon, email, null);
                servis.musteriEkle(musteri);
            } else {
                musteri.setAd(ad);
                musteri.setSoyad(soyad);
                musteri.setTelefon(telefon);
                musteri.setEmail(email);
                servis.musteriListesiniGuncelle();
            }

            // Araç
            Arac arac = servis.aracAra(plaka);
            if (arac == null) {
                arac = new Arac(plaka, marka, model, uretimYili, km,
                        sonServis, sasiNo, yakitTipi);
                servis.musteriyeAracEkle(musteri, arac);
            } else {
                arac.setMarka(marka);
                arac.setModel(model);
                arac.setUretimYili(uretimYili);
                arac.setKilometre(km);
                arac.setSonServisTarihi(sonServis);
                arac.setSasiNo(sasiNo);
                arac.setYakitTipi(yakitTipi);
                servis.aracListesiniGuncelle();
            }

            int yeniRandevuID = nextRandevuID++;

            Randevu r = new Randevu(yeniRandevuID, musteri, arac, tarihSaat, servisTuru, durum);

            if (!servis.randevuEkle(r)) {
                showAlert("Çakışma", "Bu araç için aynı tarih & saatte başka bir randevu var.");
                return;
            }

            randevuData.add(r);
            inputTemizle();

        } catch (NumberFormatException ex) {
            showAlert("Sayı Hatası", "Üretim yılı ve kilometre alanları sayısal olmalıdır.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Hata", "Randevu eklenirken hata oluştu: " + ex.getMessage());
        }
    }

    private void randevuSil() {
        Randevu secili = table.getSelectionModel().getSelectedItem();
        if (secili == null) {
            showAlert("Seçim Yok", "Lütfen silmek için tablodan bir randevu seçin.");
            return;
        }

        boolean basarili = servis.randevuSil(secili.getRandevuID());
        if (basarili) {
            randevuData.remove(secili);
        } else {
            showAlert("Silme Hatası", "Randevu listede bulunamadı.");
        }
    }

    private void randevuDurumTamamlandi() {
        Randevu secili = table.getSelectionModel().getSelectedItem();
        if (secili == null) {
            showAlert("Seçim Yok", "Lütfen güncellemek için tablodan bir randevu seçin.");
            return;
        }

        // Durumu Tamamlandı yap
        secili.setDurum("Tamamlandı");
        servis.randevuListesiniGuncelle();

        double ucret = servisUcreti(secili.getServisTuru());
        String dosyaAdi = olusturFaturaDosyaAdi(secili);

        try {
            // Fatura dosyası yaz
            faturaDosyasiYaz(secili, dosyaAdi, ucret);

            // Randevuyu sistemden ve ekrandan sil
            servis.randevuSil(secili.getRandevuID());
            randevuData.remove(secili);
            table.refresh();

            showAlert("İşlem Tamamlandı",
                    "Randevu tamamlandı ve fatura kesildi: " + dosyaAdi);
        } catch (IOException e) {
            showAlert("Fatura Hatası", "Fatura oluşturulurken hata oluştu: " + e.getMessage());
        }
    }

    private void randevuIptalEt() {
        Randevu secili = table.getSelectionModel().getSelectedItem();
        if (secili == null) {
            showAlert("Seçim Yok", "Lütfen iptal etmek için tablodan bir randevu seçin.");
            return;
        }

        secili.setDurum("İptal");
        servis.randevuListesiniGuncelle();
        table.refresh();
    }

    // Sadece randevu tarih & servis türünü günceller
    private void randevuGuncelle() {
        Randevu secili = table.getSelectionModel().getSelectedItem();
        if (secili == null) {
            showAlert("Seçim Yok", "Lütfen güncellemek için tablodan bir randevu seçin.");
            return;
        }

        try {
            String tarihStr = txtTarih.getText().trim();
            String saatStr = txtSaat.getText().trim();
            String servisTuru = cmbServisTuru.getValue();

            if (tarihStr.isEmpty() || saatStr.isEmpty()) {
                showAlert("Eksik Bilgi",
                        "Tarih ve Saat boş olamaz.");
                return;
            }

            LocalDateTime tarihSaat;
            try {
                tarihSaat = LocalDateTime.parse(tarihStr + " " + saatStr, TARIH_SAAT_FORMAT);
            } catch (DateTimeParseException ex) {
                showAlert("Tarih/Saat Hatası", "Tarih: gg.MM.yyyy, Saat: HH:mm formatında olmalı.");
                return;
            }

            if (tarihSaat.isBefore(LocalDateTime.now())) {
                showAlert("Geçersiz Tarih",
                        "Geçmiş bir tarihe randevu oluşturulamaz.");
                return;
            }

            // Sadece randevunun tarih ve servis türünü güncelle
            secili.setTarihSaat(tarihSaat);
            secili.setServisTuru(servisTuru);

            servis.randevuListesiniGuncelle();
            table.refresh();
            showAlert("Başarılı", "Randevu bilgileri güncellendi.");

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Hata", "Randevu güncellenirken hata oluştu: " + ex.getMessage());
        }
    }

    // Fatura oluştur (manuel buton)
    private void faturaKes() {
        Randevu secili = table.getSelectionModel().getSelectedItem();
        if (secili == null) {
            showAlert("Seçim Yok", "Lütfen fatura kesmek için tablodan bir randevu seçin.");
            return;
        }

        double ucret = servisUcreti(secili.getServisTuru());
        String dosyaAdi = olusturFaturaDosyaAdi(secili);

        try {
            faturaDosyasiYaz(secili, dosyaAdi, ucret);
            showAlert("Fatura Oluşturuldu", "Fatura dosyası oluşturuldu: " + dosyaAdi);
        } catch (IOException e) {
            showAlert("Fatura Hatası", "Fatura oluşturulurken hata oluştu: " + e.getMessage());
        }
    }

    // ===================== FATURA YARDIMCI METODLARI =========================

    // Dosya adını "Ad_Soyad.txt" formatında üretir, boşsa fallback kullanır
    private String olusturFaturaDosyaAdi(Randevu r) {
        Musteri m = r.getMusteri();
        String base;
        if (m != null) {
            String ad = m.getAd() != null ? m.getAd().trim() : "";
            String soyad = m.getSoyad() != null ? m.getSoyad().trim() : "";
            base = (ad + "_" + soyad).trim();
            if (base.isEmpty()) {
                base = "Musteri_" + r.getRandevuID();
            }
        } else {
            base = "Musteri_" + r.getRandevuID();
        }
        base = base.replaceAll("\\s+", "_");
        return base + ".txt";
    }

    // Fatura içeriğini dosyaya yazar
    private void faturaDosyasiYaz(Randevu secili, String dosyaAdi, double ucret) throws IOException {
        Musteri m = secili.getMusteri();
        Arac a = secili.getArac();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dosyaAdi))) {
            bw.write("=========== FATURA ===========");
            bw.newLine();
            bw.write("Fatura Tarihi : " + LocalDateTime.now().format(TARIH_SAAT_FORMAT));
            bw.newLine();
            bw.write("--------------------------------");
            bw.newLine();
            bw.write("MÜŞTERİ BİLGİLERİ");
            bw.newLine();
            if (m != null) {
                bw.write("Ad Soyad  : " + m.getAd() + " " + m.getSoyad());
                bw.newLine();
                bw.write("Telefon   : " + m.getTelefon());
                bw.newLine();
                bw.write("E-posta   : " + m.getEmail());
                bw.newLine();
            } else {
                bw.write("Müşteri bilgisi bulunamadı.");
                bw.newLine();
            }
            bw.write("--------------------------------");
            bw.newLine();
            bw.write("ARAÇ BİLGİLERİ");
            bw.newLine();
            if (a != null) {
                bw.write("Plaka     : " + a.getPlaka());
                bw.newLine();
                bw.write("Marka     : " + a.getMarka());
                bw.newLine();
                bw.write("Model     : " + a.getModel());
                bw.newLine();
                bw.write("Yakıt Tipi: " + a.getYakitTipi());
                bw.newLine();
            } else {
                bw.write("Araç bilgisi bulunamadı.");
                bw.newLine();
            }
            bw.write("--------------------------------");
            bw.newLine();
            bw.write("İŞLEM BİLGİLERİ");
            bw.newLine();
            bw.write("Servis Türü      : " + secili.getServisTuru());
            bw.newLine();
            bw.write("Randevu Tarihi   : " + secili.getTarihSaat().format(TARIH_SAAT_FORMAT));
            bw.newLine();
            bw.write("Durum            : " + secili.getDurum());
            bw.newLine();
            bw.write("--------------------------------");
            bw.newLine();
            bw.write(String.format("Toplam Tutar     : %.2f TL", ucret));
            bw.newLine();
            bw.write("=========== TEŞEKKÜR EDERİZ ===========");
            bw.newLine();
        }
    }

    // Servis türüne göre ücret
    private double servisUcreti(String servisTuru) {
        if (servisTuru == null) return 0;

        switch (servisTuru) {
            case "Periyodik Bakım":
                return 3000.0;
            case "Yağ Değişimi":
                return 1000.0;
            case "Fren Bakımı":
                return 2500.0;
            case "Lastik Değişimi":
                return 1500.0;
            case "Genel Kontrol":
                return 800.0;
            default:
                return 0.0;
        }
    }

    // ===================== YARDIMCI METODLAR =========================

    private Musteri findMusteriByTelefon(String tel) {
        return servis.musteriTelefonlaBul(tel);
    }

    private void formuRandevudanDoldur(Randevu r) {
        Musteri m = r.getMusteri();
        Arac a = r.getArac();

        txtTarih.setText(r.getTarihSaat().format(TARIH_FORMAT));
        txtSaat.setText(r.getTarihSaat().format(SAAT_FORMAT));
        cmbServisTuru.setValue(r.getServisTuru());

        if (m != null) {
            txtAd.setText(m.getAd());
            txtSoyad.setText(m.getSoyad());
            txtTelefon.setText(m.getTelefon());
            txtEmail.setText(m.getEmail());
        }

        if (a != null) {
            txtPlaka.setText(a.getPlaka());
            txtMarka.setText(a.getMarka());
            txtModel.setText(a.getModel());
            txtUretimYili.setText(String.valueOf(a.getUretimYili()));
            txtKilometre.setText(String.valueOf(a.getKilometre()));
            txtSonServis.setText(a.getSonServisTarihi());
            txtSasiNo.setText(a.getSasiNo());
            txtYakitTipi.setText(a.getYakitTipi());
        }
    }

    private Label createLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 12;"
        );
        return l;
    }

    private TextField createTextField() {
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

    private Button createColoredButton(String text, String bgColor) {
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

    private VBox createCard(String titleText, Node contentNode) {
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

    private void inputTemizle() {
        txtAd.clear();
        txtSoyad.clear();
        txtTelefon.clear();
        txtEmail.clear();

        txtPlaka.clear();
        txtMarka.clear();
        txtModel.clear();
        txtUretimYili.clear();
        txtKilometre.clear();
        txtSonServis.clear();
        txtSasiNo.clear();
        txtYakitTipi.clear();

        txtTarih.clear();
        txtSaat.clear();

        cmbServisTuru.getSelectionModel().selectFirst();
    }

    private void showAlert(String baslik, String mesaj) {
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
        if (contentLabel instanceof Label) {
            ((Label) contentLabel).setTextFill(Color.web("#f9fafb"));
        }

        alert.showAndWait();
    }

    // TableView kolon başlıklarını (header) komple siyaha çeken kesin çözüm
    private void applyDarkHeader(TableView<?> tableView) {
        Runnable styler = () -> {
            // 1. Background container
            for (Node n : tableView.lookupAll(".column-header-background")) {
                n.setStyle("-fx-background-color: #020617;");
            }
            // 2. Individual column headers
            for (Node n : tableView.lookupAll(".column-header")) {
                n.setStyle("-fx-background-color: #020617; -fx-border-color: #1f2937; -fx-border-width: 0 1 1 0;");
            }
            // 3. Filler area (right side)
            for (Node n : tableView.lookupAll(".filler")) {
                n.setStyle("-fx-background-color: #020617; -fx-border-color: #1f2937; -fx-border-width: 0 0 1 0;");
            }
            // 4. Text labels
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

    // Modern başlık barı
    private BorderPane createWindowChrome(Stage stage, Region content, String titleText) {
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

    private void styleTitleBarButton(Button b, boolean isClose) {
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

    // Son servis tarihi doğrulama (gg.MM.yyyy, Şubat 28, diğerleri 31, ay max 12)
    private boolean isGecerliServisTarihi(String tarih) {
        if (tarih == null) return false;
        if (!tarih.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            return false;
        }
        try {
            int gun = Integer.parseInt(tarih.substring(0, 2));
            int ay = Integer.parseInt(tarih.substring(3, 5));
            // int yil = Integer.parseInt(tarih.substring(6, 10)); // şu an yıl için özel kontrol yok

            if (ay < 1 || ay > 12) return false;
            if (gun < 1) return false;

            if (ay == 2) {
                return gun <= 28;
            } else {
                return gun <= 31;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}