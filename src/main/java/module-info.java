module com.os {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.os to javafx.fxml;
    exports com.os;
    exports com.os.cpuscheduler.algorithms;
    opens com.os.cpuscheduler.algorithms to javafx.fxml;
    exports com.os.cpuscheduler.gui;
    opens com.os.cpuscheduler.gui to javafx.fxml;

    exports com.os.mmu.gui;
    opens com.os.mmu.gui to javafx.fxml;
}