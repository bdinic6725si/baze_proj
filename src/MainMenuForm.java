import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainMenuForm extends JFrame {

    private String currentUser;

    public MainMenuForm(String username) {
        this.currentUser = username;
        setTitle("Admin Panel — " + username);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Dobrodošli, " + username, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Astronomija — Admin Panel", SwingConstants.CENTER);
        lblSub.setForeground(Color.GRAY);
        gbc.gridy = 1;
        panel.add(lblSub, gbc);

        JSeparator sep = new JSeparator();
        gbc.gridy = 2;
        panel.add(sep, gbc);

        JButton btnSesije = createMenuButton("Pregled zakazanih sesija i eksperimenata");
        gbc.gridy = 3;
        panel.add(btnSesije, gbc);

        JButton btnPromeniSesiju = createMenuButton("Promena podataka o sesiji");
        gbc.gridy = 4;
        panel.add(btnPromeniSesiju, gbc);

        JButton btnObrisiLab = createMenuButton("Brisanje laboratorije");
        gbc.gridy = 5;
        panel.add(btnObrisiLab, gbc);

        JButton btnOdjava = new JButton("Odjavi se");
        btnOdjava.setBorderPainted(false);
        btnOdjava.setContentAreaFilled(false);
        btnOdjava.setForeground(Color.RED);
        gbc.gridy = 6;
        panel.add(btnOdjava, gbc);

        add(panel);

        btnSesije.addActionListener(e -> new SesijeForm().setVisible(true));
        btnPromeniSesiju.addActionListener(e -> new PromeniSesijuForm().setVisible(true));
        btnObrisiLab.addActionListener(e -> new ObrisiLaboratorijuForm().setVisible(true));
        btnOdjava.addActionListener(e -> {
            new AuthForms.LoginForm().setVisible(true);
            dispose();
        });
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBackground(new Color(240, 248, 255));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

class SesijeForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtFilter;

    public SesijeForm() {
        setTitle("Pregled zakazanih sesija");
        setSize(950, 550);
        setLocationRelativeTo(null);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(new JLabel("Pretraži po laboratoriji:"));
        txtFilter = new JTextField(20);
        JButton btnFilter = new JButton("Pretraži");
        JButton btnSvi = new JButton("Prikaži sve");
        toolbar.add(txtFilter);
        toolbar.add(btnFilter);
        toolbar.add(btnSvi);

        // Tabela
        String[] columns = {"ID Sesije", "Datum", "Pocetak", "Kraj", "Tip", "Eksperiment", "Laboratorija", "Status"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scroll = new JScrollPane(table);

        // Status bar
        JLabel lblStatus = new JLabel("  Učitavanje podataka...");
        lblStatus.setForeground(Color.GRAY);

        add(toolbar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        loadData("");
        lblStatus.setText("  Ukupno redova: " + model.getRowCount());

        btnFilter.addActionListener(e -> {
            loadData(txtFilter.getText().trim());
            lblStatus.setText("  Ukupno redova: " + model.getRowCount());
        });
        btnSvi.addActionListener(e -> {
            txtFilter.setText("");
            loadData("");
            lblStatus.setText("  Ukupno redova: " + model.getRowCount());
        });
    }

    private void loadData(String filter) {
        model.setRowCount(0);

        String sql = """
            SELECT s.ID_Sesije, s.Datum, s.Vreme_Pocetka, s.Vreme_Zavrsetka,
                   s.Tip_Sesije, e.Naziv AS Eksperiment,
                   l.Naziv AS Laboratorija, iz.Status
            FROM Sesija s
            JOIN Izvodjenje iz ON iz.ID_Izvodjenja = s.ID_Izvodjenja
            JOIN Eksperiment e ON e.ID_Eksperimenta = iz.ID_Eksperimenta
            JOIN Laboratorija l ON l.ID_Lab = s.ID_Lab
            WHERE l.Naziv LIKE ?
            ORDER BY s.Datum DESC, s.Vreme_Pocetka
            """;

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + filter + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ID_Sesije"),
                        rs.getDate("Datum"),
                        rs.getTime("Vreme_Pocetka"),
                        rs.getTime("Vreme_Zavrsetka"),
                        rs.getString("Tip_Sesije"),
                        rs.getString("Eksperiment"),
                        rs.getString("Laboratorija"),
                        rs.getString("Status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Greška: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
}


class PromeniSesijuForm extends JFrame {

    private JTextField txtID, txtDatum, txtPocetak, txtKraj;
    private JComboBox<String> cbTip, cbStatus;
    private JTable table;
    private DefaultTableModel model;

    public PromeniSesijuForm() {
        setTitle("Promena podataka o sesiji");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Gornji panel — tabela sa sesijama
        String[] columns = {"ID", "Datum", "Pocetak", "Kraj", "Tip", "Laboratorija", "Eksperiment"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(950, 280));

        // Donji panel — forma za izmenu
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Izmeni odabranu sesiju"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Red 0 — ID
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID Sesije:"), gbc);
        txtID = new JTextField(8); txtID.setEditable(false);
        txtID.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; formPanel.add(txtID, gbc);

        // Red 1 — Datum
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Datum (yyyy-mm-dd):"), gbc);
        txtDatum = new JTextField(12);
        gbc.gridx = 3; formPanel.add(txtDatum, gbc);

        // Red 2 — Vreme pocetka
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Vreme početka (HH:mm:ss):"), gbc);
        txtPocetak = new JTextField(10);
        gbc.gridx = 1; formPanel.add(txtPocetak, gbc);

        // Red 2 — Vreme kraja
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Vreme kraja (HH:mm:ss):"), gbc);
        txtKraj = new JTextField(10);
        gbc.gridx = 3; formPanel.add(txtKraj, gbc);

        // Red 3 — Tip sesije
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Tip sesije:"), gbc);
        cbTip = new JComboBox<>(new String[]{"nocna", "dnevna"});
        gbc.gridx = 1; formPanel.add(cbTip, gbc);

        // Red 3 — Status izvođenja
        gbc.gridx = 2; gbc.gridy = 2; formPanel.add(new JLabel("Status izvođenja:"), gbc);
        cbStatus = new JComboBox<>(new String[]{
                "planirano", "zapoceto", "otkazano",
                "zavrseno_uspesno", "zavrseno_neuspesno"
        });
        gbc.gridx = 3; formPanel.add(cbStatus, gbc);

        // Dugmad
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnSacuvaj = new JButton("Sačuvaj izmene");
        btnSacuvaj.setBackground(new Color(70, 180, 100));
        btnSacuvaj.setForeground(Color.WHITE);
        JButton btnOtkazi = new JButton("Otkaži");
        btnPanel.add(btnSacuvaj);
        btnPanel.add(btnOtkazi);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadData();

        // Klik na red popunjava formu
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtID.setText(model.getValueAt(row, 0).toString());
                txtDatum.setText(model.getValueAt(row, 1).toString());
                txtPocetak.setText(model.getValueAt(row, 2).toString());
                txtKraj.setText(model.getValueAt(row, 3).toString());
                cbTip.setSelectedItem(model.getValueAt(row, 4).toString());
            }
        });

        btnSacuvaj.addActionListener(e -> saveChanges());
        btnOtkazi.addActionListener(e -> {
            table.clearSelection();
            txtID.setText(""); txtDatum.setText("");
            txtPocetak.setText(""); txtKraj.setText("");
        });
    }

    private void loadData() {
        model.setRowCount(0);
        String sql = """
            SELECT s.ID_Sesije, s.Datum, s.Vreme_Pocetka, s.Vreme_Zavrsetka,
                   s.Tip_Sesije, l.Naziv AS Laboratorija,
                   e.Naziv AS Eksperiment
            FROM Sesija s
            JOIN Izvodjenje iz ON iz.ID_Izvodjenja = s.ID_Izvodjenja
            JOIN Eksperiment e ON e.ID_Eksperimenta = iz.ID_Eksperimenta
            JOIN Laboratorija l ON l.ID_Lab = s.ID_Lab
            ORDER BY s.Datum DESC
            """;
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ID_Sesije"),
                        rs.getDate("Datum"),
                        rs.getTime("Vreme_Pocetka"),
                        rs.getTime("Vreme_Zavrsetka"),
                        rs.getString("Tip_Sesije"),
                        rs.getString("Laboratorija"),
                        rs.getString("Eksperiment")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Greška: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveChanges() {
        if (txtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Odaberite sesiju iz tabele.", "Upozorenje", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = """
            UPDATE Sesija
            SET Datum = ?, Vreme_Pocetka = ?, Vreme_Zavrsetka = ?, Tip_Sesije = ?
            WHERE ID_Sesije = ?
            """;

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, txtDatum.getText().trim());
            ps.setString(2, txtPocetak.getText().trim());
            ps.setString(3, txtKraj.getText().trim());
            ps.setString(4, cbTip.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(txtID.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sesija uspešno izmenjena!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Greška: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
}


class ObrisiLaboratorijuForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public ObrisiLaboratorijuForm() {
        setTitle("Brisanje laboratorije");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblInfo = new JLabel(
                "  Brisanje je dozvoljeno samo ako u laboratoriji ne radi nijedan istraživač.",
                SwingConstants.LEFT
        );
        lblInfo.setForeground(new Color(180, 60, 60));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        String[] columns = {"ID Lab", "Naziv", "Tip", "Lokacija", "Broj Istraživača", "Može se obrisati"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnObrisi = new JButton("Obriši odabranu laboratoriju");
        btnObrisi.setBackground(new Color(200, 50, 50));
        btnObrisi.setForeground(Color.WHITE);
        JButton btnRefresh = new JButton("Osveži");
        btnPanel.add(btnObrisi);
        btnPanel.add(btnRefresh);

        add(lblInfo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loadData();

        btnObrisi.addActionListener(e -> deleteLaboratory());
        btnRefresh.addActionListener(e -> loadData());
    }

    private void loadData() {
        model.setRowCount(0);

        // Prikazuje laboratorije i koliko istrazivaca radi u njima
        String sql = """
            SELECT l.ID_Lab, l.Naziv, l.Tip, l.Opis_Lokacije,
                   COUNT(DISTINCT ie.ID_Istrazivaca) AS Broj_Istrazivaca
            FROM Laboratorija l
            LEFT JOIN Izvodjenje iz ON iz.ID_Lab = l.ID_Lab
            LEFT JOIN Izvodjac_Eksperimenta ie ON ie.ID_Izvodjenja = iz.ID_Izvodjenja
            GROUP BY l.ID_Lab, l.Naziv, l.Tip, l.Opis_Lokacije
            ORDER BY l.Naziv
            """;

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int brojIstrazivaca = rs.getInt("Broj_Istrazivaca");
                String mozeSe = brojIstrazivaca == 0 ? "✓ Da" : "✗ Ne";
                model.addRow(new Object[]{
                        rs.getInt("ID_Lab"),
                        rs.getString("Naziv"),
                        rs.getString("Tip"),
                        rs.getString("Opis_Lokacije"),
                        brojIstrazivaca,
                        mozeSe
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Greška: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLaboratory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Odaberite laboratoriju iz tabele.", "Upozorenje", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int brojIstrazivaca = (int) model.getValueAt(row, 4);
        String naziv = model.getValueAt(row, 1).toString();

        // Provera — ne sme biti istrazivaca
        if (brojIstrazivaca > 0) {
            JOptionPane.showMessageDialog(this,
                    "Laboratorija '" + naziv + "' se ne može obrisati!\n" +
                            "U njoj radi " + brojIstrazivaca + " istraživač(a).",
                    "Brisanje nije dozvoljeno",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Potvrda brisanja
        int confirm = JOptionPane.showConfirmDialog(this,
                "Da li ste sigurni da želite da obrišete laboratoriju:\n'" + naziv + "'?",
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) model.getValueAt(row, 0);

        String sql = "DELETE FROM Laboratorija WHERE ID_Lab = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Laboratorija '" + naziv + "' uspešno obrisana!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Greška pri brisanju: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
}