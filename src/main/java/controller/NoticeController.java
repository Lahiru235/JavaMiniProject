package controller;

import dao.NoticeDAO;
import model.Notice;
import util.Session;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class NoticeController implements Initializable {

    @FXML private ListView<String> noticeList;
    @FXML private TextField        titleField;
    @FXML private TextArea         contentArea;
    @FXML private Label            postedBy;
    @FXML private Label            postedDate;
    @FXML private Label            statusLabel;
    @FXML private Button           addBtn;
    @FXML private Button           saveBtn;
    @FXML private Button           deleteBtn;

    private final NoticeDAO noticeDAO = new NoticeDAO();
    private List<Notice>    notices;
    private Notice          selected  = null;
    private boolean         isAdmin   = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isAdmin = Session.get().getRole().equals("ADMIN");

        // Hide edit controls for non-admins
        addBtn.setVisible(isAdmin);
        saveBtn.setVisible(isAdmin);
        deleteBtn.setVisible(isAdmin);
        titleField.setEditable(isAdmin);
        contentArea.setEditable(isAdmin);

        loadNotices();

        // Show detail when notice clicked
        noticeList.getSelectionModel().selectedIndexProperty()
            .addListener((o, old, idx) -> {
                int i = idx.intValue();
                if (i >= 0 && i < notices.size()) {
                    selected = notices.get(i);
                    titleField.setText(selected.getTitle());
                    contentArea.setText(selected.getContent());
                    postedBy.setText("Posted by: " + selected.getCreatedBy());
                    postedDate.setText("Date: " + selected.getCreatedAt());
                }
            });
    }

    private void loadNotices() {
        try {
            notices = noticeDAO.getAll();
            ObservableList<String> titles = FXCollections.observableArrayList();
            for (Notice n : notices) titles.add(n.getTitle());
            noticeList.setItems(titles);
        } catch (SQLException e) {
            showStatus("Failed to load: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleAdd() {
        selected = null;
        titleField.clear();
        contentArea.clear();
        postedBy.setText("");
        postedDate.setText("");
        statusLabel.setText("");
        titleField.requestFocus();
    }

    @FXML
    public void handleSave() {
        String title   = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            showStatus("Title and content are required.", true);
            return;
        }

        try {
            if (selected == null) {
                noticeDAO.add(title, content, Session.get().getId());
                showStatus("Notice added.", false);
            } else {
                noticeDAO.update(selected.getId(), title, content);
                showStatus("Notice updated.", false);
            }
            loadNotices();
        } catch (SQLException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleDelete() {
        if (selected == null) {
            showStatus("Select a notice first.", true);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete this notice?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    noticeDAO.delete(selected.getId());
                    handleAdd();
                    loadNotices();
                    showStatus("Notice deleted.", false);
                } catch (SQLException e) {
                    showStatus("Error: " + e.getMessage(), true);
                }
            }
        });
    }

    private void showStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setStyle(error
            ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }
}
