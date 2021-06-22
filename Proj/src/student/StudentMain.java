package student;

import java.math.BigDecimal;
import rs.etf.sab.operations.*;
import rs.etf.sab.tests.*;


public class StudentMain {

    public static void main(String[] args) {
        
        
        CityOperations cityOperations = new gs170250_CityDAO(); // Change this to your implementation. 
        DistrictOperations districtOperations = new gs170250_DistrictDAO();        
        CourierOperations courierOperations = new gs170250_CourierDAO(); // e.g. = new MyDistrictOperations();        
        CourierRequestOperation courierRequestOperation = new gs170250_CourierRequestDAO();
        GeneralOperations generalOperations = new gs170250_GeneralDAO();
        UserOperations userOperations = new gs170250_UserDAO();
        VehicleOperations vehicleOperations = new gs170250_VehiclesDAO();
        PackageOperations packageOperations = new gs170250_PackageDAO();
        
        TestHandler.createInstance(
                cityOperations,
                courierOperations,
                courierRequestOperation,
                districtOperations,
                generalOperations,
                userOperations,
                vehicleOperations,
                packageOperations);

        TestRunner.runTests();
    }
}
