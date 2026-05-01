module com.mycompany.teste {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.mycompany.teste to javafx.fxml;
    exports com.mycompany.teste;
}
