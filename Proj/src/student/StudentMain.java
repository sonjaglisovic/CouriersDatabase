package student;

import java.math.BigDecimal;
import rs.etf.sab.operations.*;
import rs.etf.sab.tests.*;


public class StudentMain {

    public static void main(String[] args) {
        
        
        CityOperations cityOperations = new gs170250_CityDAO(); // Change this to your implementation. 
        DistrictOperations districtOperations = new gs170250_DistrictDAO();
        
        CourierOperations courierOperations = new gs170250_CourierDAO(); // e.g. = new MyDistrictOperations();
        courierOperations.insertCourier("mirko122", "CA024XT");
        if(!courierOperations.deleteCourier("mirko12")) {
            System.out.println("ne brise");
        }
        courierOperations.getAllCouriers().forEach(System.out::println);
        
        courierOperations.getCouriersWithStatus(1).forEach(System.out::println);
        
        System.out.println((courierOperations.getAverageCourierProfit(-1)));
        System.out.println((courierOperations.getAverageCourierProfit(0)));
        
        CourierRequestOperation courierRequestOperation = new gs170250_CourierRequestDAO();
        GeneralOperations generalOperations = new gs170250_GeneralDAO();
        UserOperations userOperations = new gs170250_UserDAO();
        
        if(!userOperations.insertUser("sonja12", "Sonja", "Glisovic", "1234567abc")) {
            System.out.println("passed");
        }
        System.out.println(userOperations.declareAdmin("sonja12"));
        
        Integer num = userOperations.getSentPackages("sonj123", "ivan123", "mirko123");
        if(num!=null)
        System.err.println(num);
        else System.err.println("NULL");
        System.out.println(userOperations.deleteUsers("sonja12"));
        
        VehicleOperations vehicleOperations = new gs170250_VehiclesDAO();
        
        
        vehicleOperations.insertVehicle("CA123BG", 1, BigDecimal.valueOf(15));
        if(!vehicleOperations.changeFuelType("CA123BG", 0)) {
            System.out.println("passed");
        }
        vehicleOperations.changeConsumption("CA123BG", BigDecimal.valueOf(35.4));
        
        vehicleOperations.getAllVehichles().forEach(System.out::println);
        
        PackageOperations packageOperations = new gs1702500_PackageDAO();

        courierRequestOperation.insertCourierRequest("mirko123", "CA024XT");
        courierRequestOperation.insertCourierRequest("maja123", "CA024XT");
        courierRequestOperation.insertCourierRequest("boca123", "KG050BS");
        courierRequestOperation.insertCourierRequest("boca123", "KG050BT");
        courierRequestOperation.insertCourierRequest("boca123", "KG050BT");
        
        courierRequestOperation.getAllCourierRequests().stream().forEach(System.out::println);
        courierRequestOperation.changeVehicleInCourierRequest("boca123", "CA024XT");
        courierRequestOperation.insertCourierRequest("ivan123", "KG050BT");
        courierRequestOperation.grantRequest("ivan123");
        //courierRequestOperation.deleteCourierRequest("ivan123");
        
        /*TestHandler.createInstance(
                cityOperations,
                courierOperations,
                courierRequestOperation,
                districtOperations,
                generalOperations,
                userOperations,
                vehicleOperations,
                packageOperations);

        TestRunner.runTests();*/
    }
}
