module com.cs.os.cpuscheduler {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.cs.os.cpuscheduler to javafx.fxml;
    exports com.cs.os.cpuscheduler;
}