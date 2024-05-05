module com.cs.os.cpuscheduler {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.os.cpuscheduler to javafx.fxml;
    exports com.os.cpuscheduler;
    exports com.os.cpuscheduler.algorithms;
    opens com.os.cpuscheduler.algorithms to javafx.fxml;
    exports com.os.cpuscheduler.gui;
    opens com.os.cpuscheduler.gui to javafx.fxml;
}