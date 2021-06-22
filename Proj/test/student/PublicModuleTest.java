package student;

import java.math.BigDecimal;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.DistrictOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.VehicleOperations;

public class PublicModuleTest {
  
    private UserOperations userOperations = new gs170250_UserDAO();
  
  private GeneralOperations generalOperations = new gs170250_GeneralDAO();
  
  private PackageOperations packageOperations = new gs170250_PackageDAO();
  
  private DistrictOperations districtOperations = new gs170250_DistrictDAO();
  
  private CityOperations cityOperations = new gs170250_CityDAO();
  
  private VehicleOperations vehicleOperations = new gs170250_VehiclesDAO();
  
  private CourierOperations courierOperations = new gs170250_CourierDAO();
  
  private CourierRequestOperation courierRequestOperation = new gs170250_CourierRequestDAO();
    
  @Before
  public void setUp() {
    generalOperations.eraseAll();
  }
  
  @After
  public void tearUp() {
   
  }
  
  @Test
  public void publicOne() {
    String courierLastName = "Ckalja";
    String courierFirstName = "Pero";
    String courierUsername = "perkan";
    String password = "sabi2018";
    userOperations
      .insertUser(courierUsername, courierFirstName, courierLastName, password);
    String licencePlate = "BG323WE";
    int fuelType = 0;
    BigDecimal fuelConsumption = new BigDecimal(8.3D);
    vehicleOperations.insertVehicle(licencePlate, fuelType, fuelConsumption);
    courierRequestOperation.insertCourierRequest(courierUsername, licencePlate);
    courierRequestOperation.grantRequest(courierUsername);
    Assert.assertTrue(courierOperations.getAllCouriers().contains(courierUsername));
    String senderUsername = "masa";
    String senderFirstName = "Masana";
    String senderLastName = "Leposava";
    password = "lepasampasta1";
    userOperations
      .insertUser(senderUsername, senderFirstName, senderLastName, password);
    int cityId = cityOperations.insertCity("Novo Milosevo", "21234");
    int cordXd1 = 10;
    int cordYd1 = 2;
    int districtIdOne = districtOperations.insertDistrict("Novo Milosevo", cityId, cordXd1, cordYd1);
    int cordXd2 = 2;
    int cordYd2 = 10;
    int districtIdTwo = districtOperations.insertDistrict("Vojinovica", cityId, cordXd2, cordYd2);
    int type1 = 0;
    BigDecimal weight1 = new BigDecimal(123);
    int packageId1 = packageOperations.insertPackage(districtIdOne, districtIdTwo, courierUsername, type1, weight1);
    BigDecimal packageOnePrice = Util.getPackagePrice(type1, weight1, 
        Util.euclidean(cordXd1, cordYd1, cordXd2, cordYd2), new BigDecimal(5));
    int offerId = packageOperations.insertTransportOffer(courierUsername, packageId1, new BigDecimal(5));
    packageOperations.acceptAnOffer(offerId);
    int type2 = 1;
    BigDecimal weight2 = new BigDecimal(321);
    int packageId2 = packageOperations.insertPackage(districtIdTwo, districtIdOne, courierUsername, type2, weight2);
    BigDecimal packageTwoPrice = Util.getPackagePrice(type2, weight2, 
        Util.euclidean(cordXd1, cordYd1, cordXd2, cordYd2), new BigDecimal(5));
    offerId = packageOperations.insertTransportOffer(courierUsername, packageId2, new BigDecimal(5));
    packageOperations.acceptAnOffer(offerId);
    int type3 = 1;
    BigDecimal weight3 = new BigDecimal(222);
    int packageId3 = packageOperations.insertPackage(districtIdTwo, districtIdOne, courierUsername, type3, weight3);
    BigDecimal packageThreePrice = Util.getPackagePrice(type3, weight3, 
        Util.euclidean(cordXd1, cordYd1, cordXd2, cordYd2), new BigDecimal(5));
    offerId = packageOperations.insertTransportOffer(courierUsername, packageId3, new BigDecimal(5));
    packageOperations.acceptAnOffer(offerId);
    Assert.assertEquals(1L, packageOperations.getDeliveryStatus(packageId1).intValue());
    Assert.assertEquals(packageId1, packageOperations.driveNextPackage(courierUsername));
    Assert.assertEquals(3L, packageOperations.getDeliveryStatus(packageId1).intValue());
    Assert.assertEquals(2L, packageOperations.getDeliveryStatus(packageId2).intValue());
    Assert.assertEquals(packageId2, packageOperations.driveNextPackage(courierUsername));
    Assert.assertEquals(3L, packageOperations.getDeliveryStatus(packageId2).intValue());
    Assert.assertEquals(2L, packageOperations.getDeliveryStatus(packageId3).intValue());
    Assert.assertEquals(packageId3, packageOperations.driveNextPackage(courierUsername));
    Assert.assertEquals(3L, packageOperations.getDeliveryStatus(packageId3).intValue());
    BigDecimal gain = packageOnePrice.add(packageTwoPrice).add(packageThreePrice);
    BigDecimal loss = (new BigDecimal(Util.euclidean(cordXd1, cordYd1, cordXd2, cordYd2) * 4.0D * 15.0D)).multiply(fuelConsumption);
    BigDecimal actual = courierOperations.getAverageCourierProfit(0);
    Assert.assertTrue((gain.subtract(loss).compareTo(actual.multiply(new BigDecimal(1.001D))) < 0));
    Assert.assertTrue((gain.subtract(loss).compareTo(actual.multiply(new BigDecimal(0.999D))) > 0));
  }
}

