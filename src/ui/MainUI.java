package ui;

import data.Database;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DecimalFormat;

public class MainUI {

    private JPanel rootPanel;
    private JTable transcriptTable;
    private JTextField classNameField;
    private JButton addButton;
    private JTextField classGradeField;
    private JTextField creditHoursField;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton calculateGPAButton;
    private JLabel GPAField;
    private DefaultTableModel model;

    public MainUI() {
        tableCRUD();
    }
    public JPanel getRootPanel() {
        return rootPanel;
    }
    private void tableCRUD() {

        Database db = new Database();
        db.connect();

        loadTable();
        addRecord();
        deleteRecord();
        updateRecord();
        calculateGPA();
        adjustTable();
    }

    private void addRecord() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String className = classNameField.getText();
                String classGrade = classGradeField.getText();
                int creditHours = Integer.parseInt(creditHoursField.getText());
                double qualityPoints = calculateQualityPoints(classGrade, creditHours);

                try {
                    PreparedStatement pst = Database.connection.prepareStatement("INSERT INTO transcript(className, classGrade, creditHours, qualityPoints)values(?, ?, ?, ?)");
                    pst.setString(1, className);
                    pst.setString(2, classGrade);
                    pst.setInt(3, creditHours);
                    pst.setDouble(4, qualityPoints);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record Added");
                    loadTable();
                    adjustTable();
                    classNameField.setText("");
                    classGradeField.setText("");
                    creditHoursField.setText("");
                    classNameField.requestFocus();
                } catch (SQLException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void updateRecord() {
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String className = classNameField.getText();
                String classGrade = classGradeField.getText();
                int creditHours = Integer.parseInt(creditHoursField.getText());
                double qualityPoints = calculateQualityPoints(classGrade, creditHours);

                try {
                    int rowToUpdate = transcriptTable.getSelectedRow();
                    String classNameToChange = transcriptTable.getModel().getValueAt(rowToUpdate, 0).toString();
                    PreparedStatement pst = Database.connection.prepareStatement("UPDATE transcript SET ClassName = ?, ClassGrade = ?, CreditHours = ?, QualityPoints = ? WHERE ClassName = ?");
                    pst.setString(1, className);
                    pst.setString(2, classGrade);
                    pst.setInt(3, creditHours);
                    pst.setDouble(4, qualityPoints);
                    pst.setString(5, classNameToChange);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record Updated");
                    loadTable();
                    adjustTable();
                    classNameField.setText("");
                    classGradeField.setText("");
                    creditHoursField.setText("");
                    classNameField.requestFocus();
                } catch(SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void deleteRecord() {
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int rowToDelete = transcriptTable.getSelectedRow();
                    String className = transcriptTable.getModel().getValueAt(rowToDelete, 0).toString();
                    PreparedStatement pst = Database.connection.prepareStatement("DELETE FROM transcript WHERE ClassName = ?");
                    pst.setString(1, className);
                    pst.executeUpdate();
                    model.removeRow(rowToDelete);
                    JOptionPane.showMessageDialog(null, "Record Deleted");
                    loadTable();
                    adjustTable();
                    classNameField.setText("");
                    classGradeField.setText("");
                    creditHoursField.setText("");
                    classNameField.requestFocus();
                } catch(SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void loadTable() {
        model = new DefaultTableModel(new String[] {"Class Name", "Class Grade", "Credit Hours", "Quality Points"}, 0);

        try {
            PreparedStatement pst = Database.connection.prepareStatement("SELECT * FROM transcript");
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                String classNameFromSQL = rs.getString("ClassName");
                String classGradeFromSQL = rs.getString("ClassGrade");
                String creditHoursFromSQL = rs.getString("CreditHours");
                String QualityPointsFromSQL = rs.getString("QualityPoints");
                model.addRow(new Object[]{classNameFromSQL, classGradeFromSQL, creditHoursFromSQL, QualityPointsFromSQL});
            }
            transcriptTable.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void calculateGPA() {
        double GPA = 0;

        calculateGPAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int totalCreditHours = 0;
                double totalQualityPoints = 0;
                double GPA = 0;

                try {
                    PreparedStatement pst = Database.connection.prepareStatement("SELECT SUM(CreditHours) FROM transcript");
                    ResultSet rs = pst.executeQuery();

                    PreparedStatement pst1 = Database.connection.prepareStatement("SELECT SUM(QualityPoints) FROM transcript");
                    ResultSet rs1 = pst1.executeQuery();

                    while(rs.next() && rs1.next()) {
                        totalCreditHours = rs.getInt(1);
                        totalQualityPoints = rs1.getDouble(1);
                    }
                    GPA = (totalQualityPoints / totalCreditHours) - 0.01;
                    DecimalFormat df = new DecimalFormat("#.00");
                    GPA = Double.parseDouble(df.format(GPA));
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                GPAField.setText("GPA: " + GPA);
            }
        });

    }

    private void adjustTable() {
        TableColumnModel columns = transcriptTable.getColumnModel();
        columns.getColumn(0).setMinWidth(200);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        columns.getColumn(0).setCellRenderer(centerRenderer);
        columns.getColumn(1).setCellRenderer(centerRenderer);
        columns.getColumn(2).setCellRenderer(centerRenderer);
        columns.getColumn(3).setCellRenderer(centerRenderer);
    }

    private double calculateQualityPoints(String classGrade, int creditHours) {

        double qualityPoints = 0;
        switch(classGrade) {
            case "A":
                qualityPoints = creditHours * 4;
                break;
            case "A-":
                qualityPoints = creditHours * 3.7;
                break;
            case "B+":
                qualityPoints = creditHours * 3.3;
                break;
            case "B":
                qualityPoints = creditHours * 3;
                break;
            case "B-":
                qualityPoints = creditHours * 2.7;
                break;
            case "C+":
                qualityPoints = creditHours * 2.3;
                break;
            case "C":
                qualityPoints = creditHours * 2;
                break;
            case "C-":
                qualityPoints = creditHours * 1.7;
                break;
            case "D+":
                qualityPoints = creditHours * 1.3;
                break;
            case "D":
                qualityPoints = creditHours * 1;
                break;
            case "D-":
                qualityPoints = creditHours * 0.7;
                break;
            default:
                qualityPoints = creditHours * 0;
                break;
        }
        DecimalFormat df = new DecimalFormat("#.00");
        qualityPoints = Double.parseDouble(df.format(qualityPoints));
        return qualityPoints;
    }
}
