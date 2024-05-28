package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;

@SuppressWarnings("CallToPrintStackTrace")
public class Controller {
    public Button photoClearButton;
    @FXML
    private TableColumn<Model, Integer> idColumn;
    @FXML
    private TextField idTextField;
    @FXML
    private ImageView imageView;
    @FXML
    private TableColumn<Model, Double> latColumn;
    @FXML
    private TextField latTextField;
    @FXML
    private TableColumn<Model, Double> longColumn;
    @FXML
    private TextField longTextField;
    @FXML
    private TableColumn<Model, String> nameColumn;
    @FXML
    private TextField nameTextField;
    @FXML
    private TableColumn<Model, String> photoColumn;
    @FXML
    private Label photoPathLabel;
    @FXML
    private TableColumn<Model, String> regionColumn;
    @FXML
    private ComboBox<String> regionComboBox;
    @FXML
    private TableView<Model> tableView;
    private String selectedPhotoPath;
    private byte[] photoBytes;

    public ObservableList<Model> getLandmarks() {
        ObservableList<Model> data = FXCollections.observableArrayList();
        String query = "SELECT id, name, latitude, longitude, region, photo FROM landmarks";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                String region = resultSet.getString("region");
                byte[] photo = resultSet.getBytes("photo");
                data.add(new Model(id, name, latitude, longitude, region, photo));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    private final ObservableList<String> regions = FXCollections.observableArrayList(
            "Вінницька область", "Волинська область", "Дніпропетровська область", "Донецька область",
            "Житомирська область", "Закарпатська область", "Запорізька область", "Івано-Франківська область",
            "Київська область", "Кіровоградська область", "Луганська область", "Львівська область",
            "Миколаївська область", "Одеська область", "Полтавська область", "Рівненська область",
            "Сумська область", "Тернопільська область", "Харківська область", "Херсонська область",
            "Хмельницька область", "Черкаська область", "Чернівецька область", "Чернігівська область",
            "Київ", "Севастополь"
    );

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        latColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));

        ObservableList<Model> data = this.getLandmarks();
        tableView.setItems(data);

        regionComboBox.setItems(regions);
    }

    @FXML
    private void photoButtonOnClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            selectedPhotoPath = selectedFile.getAbsolutePath();
            photoPathLabel.setText(selectedPhotoPath);
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    public void printButtonOnClicked() {
        Model selectedLandmark = tableView.getSelectionModel().getSelectedItem();
        if (selectedLandmark != null) {
            idTextField.setText(String.valueOf(selectedLandmark.getId()));
            nameTextField.setText(selectedLandmark.getName());
            latTextField.setText(String.valueOf(selectedLandmark.getLatitude()));
            longTextField.setText(String.valueOf(selectedLandmark.getLongitude()));
            regionComboBox.setValue(selectedLandmark.getRegion());
            photoPathLabel.setText("file path");

            photoBytes = selectedLandmark.getPhoto();
            if (photoBytes != null) {
                Image image = new Image(new ByteArrayInputStream(photoBytes));
                imageView.setImage(image);
            } else {
                imageView.setImage(null);
            }
        }
    }

    @FXML
    public void clearButtonOnClicked(){
        idTextField.setText("");
        nameTextField.setText("");
        latTextField.setText("");
        longTextField.setText("");
        regionComboBox.setValue("");
        photoPathLabel.setText("file path");
        imageView.setImage(null);
        selectedPhotoPath = null;
    }

    @FXML
    private void insertButtonOnClicked() {
        String name = nameTextField.getText();
        double latitude = Double.parseDouble(latTextField.getText());
        double longitude = Double.parseDouble(longTextField.getText());
        String region = regionComboBox.getValue();


        String sql = "INSERT INTO landmarks (name, latitude, longitude, region, photo) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            InputStream photo = new FileInputStream(selectedPhotoPath);

            pstmt.setString(1, name);
            pstmt.setDouble(2, latitude);
            pstmt.setDouble(3, longitude);
            pstmt.setString(4, region);
            pstmt.setBinaryStream(5, photo, (int) new File(selectedPhotoPath).length());

            pstmt.executeUpdate();

            tableView.setItems(this.getLandmarks());

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void deleteButtonOnClicked() {
        Model selectedLandmark = tableView.getSelectionModel().getSelectedItem();
        int id = selectedLandmark.getId();


        String sql = "DELETE FROM landmarks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            pstmt.executeUpdate();

            tableView.setItems(this.getLandmarks());

            this.clearButtonOnClicked();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void updateButtonOnClicked() {
        String id = idTextField.getText();
        String name = nameTextField.getText();
        double latitude = Double.parseDouble(latTextField.getText());
        double longitude = Double.parseDouble(longTextField.getText());
        String region = regionComboBox.getValue();

        String sql = "UPDATE landmarks SET name = ?, latitude = ?, longitude = ?, region = ?, photo = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setDouble(2, latitude);
            pstmt.setDouble(3, longitude);
            pstmt.setString(4, region);


           if(selectedPhotoPath != null){
               try (InputStream inputStream = new FileInputStream(selectedPhotoPath)) {
                   pstmt.setBinaryStream(5, inputStream, (int) new File(selectedPhotoPath).length());
                   pstmt.setInt(6, Integer.parseInt(id));
                   pstmt.executeUpdate();
               }
            }
            else {
                try(ByteArrayInputStream byteArray = new ByteArrayInputStream(photoBytes)) {
                    pstmt.setBinaryStream(5, byteArray, photoBytes.length);
                    pstmt.setInt(6, Integer.parseInt(id));
                    pstmt.executeUpdate();
                }
            }




            tableView.setItems(this.getLandmarks());

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void photoClearButtonOnClicked(){
        selectedPhotoPath = null;
        imageView.setImage(null);
        photoBytes = null;
        photoPathLabel.setText("file path");
    }
}
