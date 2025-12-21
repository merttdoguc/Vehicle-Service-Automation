public class AVLAracAgaci {

    // AĞACIN DÜĞÜM YAPISI (NODE)
    // Bu sınıf sadece bu dosya içinde kullanılacağı için private yapabiliriz
    private class Node {
        Arac arac;      // Verimiz (Yolcu)
        Node left;      // Sol Dal
        Node right;     // Sağ Dal
        int height;     // Yükseklik (Denge için şart)

        Node(Arac arac) {
            this.arac = arac;
            this.height = 1;
        }
    }

    private Node root; // Ağacın kökü (Başlangıç noktası)

    // DIŞARIDAN ÇAĞRILAN METOTLAR

    // Ekleme Metodu (ServisYonetimSistemi bunu çağıracak)
    public void insert(Arac arac) {
        root = insert(root, arac);
    }

    // Arama Metodu (ServisYonetimSistemi bunu çağıracak)
    public Arac search(String plaka) {
        return search(root, plaka);
    }

    // --- ARKA PLAN İŞLEMLERİ (RECURSIVE) ---

    // Ekleme İşlemi (Recursive)
    private Node insert(Node node, Arac arac) {
        // 1. Standart BST Ekleme İşlemi
        if (node == null)
            return new Node(arac);

        // Plakaya göre kıyaslama (Alfabetik)
        int compareResult = arac.getPlaka().compareToIgnoreCase(node.arac.getPlaka());

        if (compareResult < 0)
            node.left = insert(node.left, arac); // Küçüke sola
        else if (compareResult > 0)
            node.right = insert(node.right, arac); // Büyükse sağa
        else
            return node; // Eşitse ekleme yapma (Aynı plaka var demek)

        // 2. Yüksekliği Güncelle
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // 3. Dengeyi Kontrol Et ve Gerekirse Rotasyon Yap
        return balance(node);
    }

    // Arama İşlemi (Recursive)
    private Arac search(Node node, String plaka) {
        if (node == null) return null; // Ağacın sonuna geldik, yokmuş.

        int compareResult = plaka.compareToIgnoreCase(node.arac.getPlaka());

        if (compareResult < 0)
            return search(node.left, plaka); // Sola git
        else if (compareResult > 0)
            return search(node.right, plaka); // Sağa git
        else
            return node.arac; // Bulduk!
    }

    // --- DENGELEME VE ROTASYON MATEMATİĞİ ---

    // Düğümün yüksekliğini getirir
    private int height(Node N) {
        if (N == null) return 0;
        return N.height;
    }

    // Denge faktörünü hesaplar (Sol - Sağ)
    private int getBalance(Node N) {
        if (N == null) return 0;
        return height(N.left) - height(N.right);
    }

    // Sağa Döndürme (Right Rotate)
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Döndürme işlemi
        x.right = y;
        y.left = T2;

        // Yükseklikleri güncelle
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x; // Yeni kök
    }

    // Sola Döndürme (Left Rotate)
    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Döndürme işlemi
        y.left = x;
        x.right = T2;

        // Yükseklikleri güncelle
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y; // Yeni kök
    }

    // Dengeleme (Balance) Fonksiyonu
    private Node balance(Node node) {
        int balance = getBalance(node);

        // Durum 1: Sol taraf ağırsa ve ekleme solun soluna yapıldıysa
        if (balance > 1 && getBalance(node.left) >= 0)
            return rightRotate(node);

        // Durum 2: Sol taraf ağırsa ve ekleme solun sağına yapıldıysa
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Durum 3: Sağ taraf ağırsa ve ekleme sağın sağına yapıldıysa
        if (balance < -1 && getBalance(node.right) <= 0)
            return leftRotate(node);

        // Durum 4: Sağ taraf ağırsa ve ekleme sağın soluna yapıldıysa
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    // --- SİLME (DELETE) METOTLARI ---

    // Dışarıdan çağrılan metot
    public void delete(String plaka) {
        root = delete(root, plaka);
    }

    // Recursive Silme İşlemi
    private Node delete(Node root, String plaka) {
        // 1. Standart BST Silme
        if (root == null) return root;

        int compareResult = plaka.compareToIgnoreCase(root.arac.getPlaka());

        if (compareResult < 0) {
            root.left = delete(root.left, plaka);
        } else if (compareResult > 0) {
            root.right = delete(root.right, plaka);
        } else {
            // SİLİNECEK DÜĞÜM BULUNDU!

            // Durum 1: Tek çocuklu veya çocuksuz düğüm
            if ((root.left == null) || (root.right == null)) {
                Node temp = (root.left != null) ? root.left : root.right;

                // Çocuksuz durum
                if (temp == null) {
                    temp = root;
                    root = null;
                } else { // Tek çocuklu durum
                    root = temp; // Çocuğu yerine geçir
                }
            } else {
                // Durum 2: İki çocuklu düğüm
                // Sağ taraftaki en küçük düğümü (Successor) bul
                Node temp = minValueNode(root.right);

                // Onun verisini buraya kopyala
                root.arac = temp.arac;

                // O düğümü eski yerinden sil
                root.right = delete(root.right, temp.arac.getPlaka());
            }
        }

        // Eğer ağaçta tek düğüm vardıysa ve silindiyse
        if (root == null) return root;

        // 2. Yüksekliği Güncelle
        root.height = Math.max(height(root.left), height(root.right)) + 1;

        // 3. Dengeyi Kontrol Et (Balance)
        return balance(root);
    }

    // Yardımcı: En küçük değeri bulan metot (Sağın solu)
    private Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }
}