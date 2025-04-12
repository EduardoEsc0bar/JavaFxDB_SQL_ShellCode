module org.example.javafxdb_sql_shellcode {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    exports com.example.module03_basicgui_db_interface;

    opens org.example.javafxdb_sql_shellcode to javafx.fxml;
    opens com.example.module03_basicgui_db_interface to javafx.fxml;
    exports org.example.javafxdb_sql_shellcode;
}