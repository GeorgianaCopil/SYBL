package at.ac.tuwien.dsg.mela.control;

import at.ac.tuwien.dsg.mela.dataAccess.DataAccess;

/**
 * Author: Daniel Moldovan
 * Institution: Vienna University of Technology
 * Date: 6/22/13
 */
public class SystemControlFactory {
    private SystemControl systemControl;

    public SystemControlFactory(DataAccess dataAccess) {
        systemControl = new SystemControl(dataAccess);
    }

    public synchronized SystemControl getSystemControlInstance() {
        return systemControl;
    }
}
