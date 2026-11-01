package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import model.Arac;
import model.Musteri;
import model.Randevu;
import service.ServisYonetimSistemi;
import service.FaturaService;

import static ui.UiFactory.*;
import static util.DateFormats.*;

/** Personel ekranı: randevu listesi, güncelleme, iptal, tamamlama ve fatura işlemleri. */
class StaffScreen {

    private final AppContext ctx;
    private final ServisYonetimSistemi servis;

    StaffScreen(AppContext ctx) {
        this.ctx = ctx;
        this.servis = ctx.servis();
    }

    // Müşteri alanları (salt okunur)
    private TextField txtAd;
    private TextField txtSoyad;
    private TextField txtTelefon;
    private TextField txtEmail;

    // Araç alanları (salt okunur)
    private TextField txtPlaka;
    private TextField txtMarka;
    private TextField txtModel;
    private TextField txtUretimYili;
    private TextField txtKilometre;
    private TextField txtSonServis;
    private TextField txtSasiNo;
    private TextField txtYakitTipi;

    // Randevu alanları
    private TextField txtTarih;   // gg.MM.yyyy
    private TextField txtSaat;    // HH:mm
    private ComboBox<String> cmbServisTuru;

    // Tablo
    private TableView<Randevu> table;
    private ObservableList<Randevu> randevuData;

    void show(Stage stage) {
        randevuData = FXCollections.observableArrayList();
        randevuData.addAll(servis.getRandevuListesi());

        // müşteri paneli
        txtAd = createTextField();
        txtSoyad = createTextField();
        txtTelefon = createTextField();
        txtEmail = createTextField();

        GridPane musteriGrid = createFormGrid();

        int row = 0;
        addFormRow(musteriGrid, row++, "Ad", txtAd);
        addFormRow(musteriGrid, row++, "Soyad", txtSoyad);
        addFormRow(musteriGrid, row++, "Telefon", txtTelefon);
        addFormRow(musteriGrid, row++, "E-posta", txtEmail);

        VBox musteriCard = createCard("Müşteri Bilgileri", musteriGrid);

        // araç paneli
        txtPlaka = createTextField();
        txtMarka = createTextField();
        txtModel = createTextField();
        txtUretimYili = createTextField();
        txtKilometre = createTextField();
        txtSonServis = createTextField();
        txtSasiNo = createTextField();
        txtYakitTipi = createTextField();

        GridPane aracGrid = createFormGrid();

        row = 0;
        addFormRow(aracGrid, row++, "Plaka", txtPlaka);
        addFormRow(aracGrid, row++, "Marka", txtMarka);
        addFormRow(aracGrid, row++, "Model", txtModel);
        addFormRow(aracGrid, row++, "Üretim Yılı", txtUretimYili);
        addFormRow(aracGrid, row++, "Kilometre", txtKilometre);
        addFormRow(aracGrid, row++, "Son Servis Tarihi (gg.MM.yyyy)", txtSonServis);
        addFormRow(aracGrid, row++, "Şasi No", txtSasiNo);
        addFormRow(aracGrid, row++, "Yakıt Tipi", txtYakitTipi);

        VBox aracCard = createCard("Araç Bilgileri", aracGrid);

        // -------- RANDEVU PANELİ (GÜNCELLEME İÇİN) --------
        txtTarih = createTextField();
        txtSaat = createTextField();

        cmbServisTuru = createServisTuruCombo();

        GridPane randevuGrid = createFormGrid();

        row = 0;
        addFormRow(randevuGrid, row++, "Tarih (gg.MM.yyyy)", txtTarih);
        addFormRow(randevuGrid, row++, "Saat (HH:mm)", txtSaat);
        addFormRow(randevuGrid, row++, "Servis Türü", cmbServisTuru);

        VBox randevuCard = createCard("Randevu Bilgileri", randevuGrid);

        // SOL FORM
        VBox solForm = new VBox(12, musteriCard, aracCard, randevuCard);
        solForm.setPadding(new Insets(16));
        solForm.setPrefWidth(360);

        // soldaki müşteri + araç alanları kilitle
        lockAsReadOnly(txtAd, txtSoyad, txtTelefon, txtEmail,
                txtPlaka, txtMarka, txtModel, txtUretimYili, txtKilometre, txtSonServis, txtSasiNo, txtYakitTipi);

        // -------- TABLO --------
        table = new TableView<>();
        table.setItems(randevuData);

        Label emptyLbl = new Label("Aktif randevu bulunamadı");
        emptyLbl.setStyle("-fx-text-fill: #9ca3af; -fx-font-family: 'Montserrat Medium'; -fx-font-size: 14;");
        table.setPlaceholder(emptyLbl);

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

        table.getSelectionModel().selectedItemProperty().addListener((obs, eski, secili) -> {
            if (secili != null) formuRandevudanDoldur(secili);
        });

        VBox tableCard = createCard("Randevu Listesi", table);

        // Oturumu Kapat
        Button btnLogout = createColoredButton("Oturumu Kapat", "#ef4444");
        btnLogout.setOnAction(e -> {
            if (confirmLogout("Oturumu kapatmak istediğinize emin misiniz?")) {
                inputTemizle();
                ctx.showLoginScreen(stage);
            }
        });

        Label lblHeader = new Label("Personel İşlem Ekranı");
        lblHeader.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;"
        );

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox headerBox = new HBox(15, lblHeader, headerSpacer, btnLogout);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(10, 25, 10, 25));

        // Butonlar (Randevu ekle YOK)
        Button btnGuncelle = createColoredButton("Randevuyu Güncelle", "#f97316");
        Button btnSil = createColoredButton("Seçili Randevuyu Sil", "#ef4444");
        Button btnDurumGuncelle = createColoredButton("Seçili Randevuyu Tamamlandı Yap", "#22c55e");
        Button btnIptal = createColoredButton("Seçili Randevuyu İptal Et", "#6b7280");
        Button btnFatura = createColoredButton("Fatura Kes", "#a855f7");

        HBox buttonBar = new HBox(10, btnGuncelle, btnSil, btnDurumGuncelle, btnIptal, btnFatura);
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

        // Alt bar
        Label lblDateTime = new Label(LocalDateTime.now().format(TARIH_SAAT_FORMAT));
        lblDateTime.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #f9fafb;" +
                        "-fx-font-size: 18;"
        );

        HBox timeBox = new HBox(lblDateTime);
        timeBox.setAlignment(Pos.CENTER_LEFT);

        ImageView mechanicView = null;
        try {
            Image img = new Image(getClass().getResourceAsStream("/ui/usta.png"));
            mechanicView = new ImageView(img);
            mechanicView.setFitHeight(180);
            mechanicView.setPreserveRatio(true);
            mechanicView.setSmooth(true);
            mechanicView.setEffect(new DropShadow(18, Color.web("#00000080")));
        } catch (Exception ignored) {
        }

        Label tagline = new Label("\"VivaCar Oto Servis\" olarak 45 yıldır hizmetinizdeyiz");
        tagline.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #f9fafb;" +
                        "-fx-font-style: italic;" +
                        "-fx-font-size: 16;"
        );

        VBox rightBottom = new VBox(10);
        if (mechanicView != null) rightBottom.getChildren().addAll(mechanicView, tagline);
        else rightBottom.getChildren().add(tagline);
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

        applyDarkHeader(table);

        // Eventler
        btnSil.setOnAction(e -> randevuSil());
        btnDurumGuncelle.setOnAction(e -> randevuDurumTamamlandi());
        btnIptal.setOnAction(e -> randevuIptalEt());
        btnGuncelle.setOnAction(e -> randevuGuncelle());
        btnFatura.setOnAction(e -> faturaKes());
    }

    private void randevuSil() {
        Randevu secili = table.getSelectionModel().getSelectedItem();
        if (secili == null) {
            showAlert("Seçim Yok", "Lütfen silmek için tablodan bir randevu seçin.");
            return;
        }

        boolean basarili = servis.randevuSil(secili.getRandevuID());
        if (basarili) randevuData.remove(secili);
        else showAlert("Silme Hatası", "Randevu listede bulunamadı.");
    }

    private void randevuDurumTamamlandi() {
        Randevu secili = table.getSelectionModel().getSelectedItem();
        if (secili == null) {
            showAlert("Seçim Yok", "Lütfen güncellemek için tablodan bir randevu seçin.");
            return;
        }

        secili.setDurum("Tamamlandı");
        servis.randevuListesiniGuncelle();

        double ucret = FaturaService.servisUcreti(secili.getServisTuru());
        String dosyaAdi = FaturaService.olusturFaturaDosyaAdi(secili);

        try {
            FaturaService.faturaDosyasiYaz(secili, dosyaAdi, ucret);
            servis.randevuSil(secili.getRandevuID());
            randevuData.remove(secili);
            table.refresh();

            showAlert("İşlem Tamamlandı", "Randevu tamamlandı ve fatura kesildi: " + dosyaAdi);
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
                showAlert("Eksik Bilgi", "Tarih ve Saat boş olamaz.");
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
                showAlert("Geçersiz Tarih", "Geçmiş bir tarihe randevu oluşturulamaz.");
                return;
            }

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

    private void faturaKes() {
        Randevu secili = table.getSelectionModel().getSelectedItem();
        if (secili == null) {
            showAlert("Seçim Yok", "Lütfen fatura kesmek için tablodan bir randevu seçin.");
            return;
        }

        double ucret = FaturaService.servisUcreti(secili.getServisTuru());
        String dosyaAdi = FaturaService.olusturFaturaDosyaAdi(secili);

        try {
            FaturaService.faturaDosyasiYaz(secili, dosyaAdi, ucret);
            showAlert("Fatura Oluşturuldu", "Fatura dosyası oluşturuldu: " + dosyaAdi);
        } catch (IOException e) {
            showAlert("Fatura Hatası", "Fatura oluşturulurken hata oluştu: " + e.getMessage());
        }
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

    private void inputTemizle() {
        if (txtAd != null) txtAd.clear();
        if (txtSoyad != null) txtSoyad.clear();
        if (txtTelefon != null) txtTelefon.clear();
        if (txtEmail != null) txtEmail.clear();

        if (txtPlaka != null) txtPlaka.clear();
        if (txtMarka != null) txtMarka.clear();
        if (txtModel != null) txtModel.clear();
        if (txtUretimYili != null) txtUretimYili.clear();
        if (txtKilometre != null) txtKilometre.clear();
        if (txtSonServis != null) txtSonServis.clear();
        if (txtSasiNo != null) txtSasiNo.clear();
        if (txtYakitTipi != null) txtYakitTipi.clear();

        if (txtTarih != null) txtTarih.clear();
        if (txtSaat != null) txtSaat.clear();

        if (cmbServisTuru != null) cmbServisTuru.getSelectionModel().selectFirst();
    }
}
