module com.lms.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.lms.demo to javafx.fxml;
    opens controller to javafx.fxml;
    opens model to javafx.base;
    exports com.lms.demo;
}