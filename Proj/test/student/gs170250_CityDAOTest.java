/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */

import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.VehicleOperations;

public class gs170250_CityDAOTest {
  
  private GeneralOperations generalOperations;
  
  private CityOperations cityOperations;
  
  private VehicleOperations vehicleOperations;
  
  private CourierOperations courierOperations;
  
  private CourierRequestOperation courierRequestOperation;
  
  @Before
  public void setUp() throws Exception {
    this.cityOperations = new gs170250_CityDAO();
    this.generalOperations = new gs170250_GeneralDAO();
    this.generalOperations.eraseAll();
  }
  
  @After
  public void tearDown() throws Exception {
    this.generalOperations.eraseAll();
  }
  
  @Test
  public void insertCity_OnlyOne() throws Exception {
    String name = "Tokyo";
    String postalCode = "100";
    int rowId = this.cityOperations.insertCity(name, postalCode);
    List<Integer> list = this.cityOperations.getAllCities();
    Assert.assertEquals(1L, list.size());
    Assert.assertTrue(list.contains(Integer.valueOf(rowId)));
  }
  
  @Test
  public void insertCity_TwoCities_SameBothNameAndPostalCode() throws Exception {
    String name = "Tokyo";
    String postalCode = "100";
    int rowIdValid = this.cityOperations.insertCity(name, postalCode);
    int rowIdInvalid = this.cityOperations.insertCity(name, postalCode);
    Assert.assertEquals(-1L, rowIdInvalid);
    List<Integer> list = this.cityOperations.getAllCities();
    Assert.assertEquals(1L, list.size());
    Assert.assertTrue(list.contains(Integer.valueOf(rowIdValid)));
  }
  
  @Test
  public void insertCity_TwoCities_SameName() throws Exception {
    String name = "Tokyo";
    String postalCode1 = "100";
    String postalCode2 = "1020";
    int rowIdValid = this.cityOperations.insertCity(name, postalCode1);
    int rowIdInvalid = this.cityOperations.insertCity(name, postalCode2);
    Assert.assertEquals(-1L, rowIdInvalid);
    List<Integer> list = this.cityOperations.getAllCities();
    Assert.assertEquals(1L, list.size());
    Assert.assertTrue(list.contains(Integer.valueOf(rowIdValid)));
  }
  
  @Test
  public void insertCity_TwoCities_SamePostalCode() throws Exception {
    String name1 = "Tokyo";
    String name2 = "Beijing";
    String postalCode = "100";
    int rowIdValid = this.cityOperations.insertCity(name1, postalCode);
    int rowIdInvalid = this.cityOperations.insertCity(name2, postalCode);
    Assert.assertEquals(-1L, rowIdInvalid);
    List<Integer> list = this.cityOperations.getAllCities();
    Assert.assertEquals(1L, list.size());
    Assert.assertTrue(list.contains(Integer.valueOf(rowIdValid)));
  }
  
  @Test
  public void insertCity_MultipleCities() throws Exception {
    String name1 = "Tokyo";
    String name2 = "Beijing";
    String postalCode1 = "100";
    String postalCode2 = "065001";
    int rowId1 = this.cityOperations.insertCity(name1, postalCode1);
    int rowId2 = this.cityOperations.insertCity(name2, postalCode2);
    List<Integer> list = this.cityOperations.getAllCities();
    Assert.assertEquals(2L, list.size());
    Assert.assertTrue(list.contains(Integer.valueOf(rowId1)));
    Assert.assertTrue(list.contains(Integer.valueOf(rowId2)));
  }
  
  @Test
  public void deleteCity_WithId_OnlyOne() {
    String name = "Beijing";
    String postalCode = "065001";
    int rowId = this.cityOperations.insertCity(name, postalCode);
    Assert.assertNotEquals(-1L, rowId);
    Assert.assertTrue(this.cityOperations.deleteCity(rowId));
    Assert.assertEquals(0L, this.cityOperations.getAllCities().size());
  }
  
  @Test
  public void deleteCity_WithId_OnlyOne_NotExisting() {
    Random random = new Random();
    int rowId = random.nextInt();
    Assert.assertFalse(this.cityOperations.deleteCity(rowId));
    Assert.assertEquals(0L, this.cityOperations.getAllCities().size());
  }
  
  @Test
  public void deleteCity_WithName_One() {
    String name = "Beijing";
    String postalCode = "065001";
    int rowId = this.cityOperations.insertCity(name, postalCode);
    Assert.assertNotEquals(-1L, rowId);
    Assert.assertEquals(1L, this.cityOperations.deleteCity(new String[] { name }));
    Assert.assertEquals(0L, this.cityOperations.getAllCities().size());
  }
  
  @Test
  public void deleteCity_WithName_MultipleCities() throws Exception {
    String name1 = "Tokyo";
    String name2 = "Beijing";
    String postalCode1 = "100";
    String postalCode2 = "065001";
    int rowId1 = this.cityOperations.insertCity(name1, postalCode1);
    int rowId2 = this.cityOperations.insertCity(name2, postalCode2);
    List<Integer> list = this.cityOperations.getAllCities();
    Assert.assertEquals(2L, list.size());
    Assert.assertEquals(2L, this.cityOperations.deleteCity(new String[] { name1, name2 }));
  }
  
  @Test
  public void deleteCity_WithName_OnlyOne_NotExisting() {
    String name = "Tokyo";
    Assert.assertEquals(0L, this.cityOperations.deleteCity(new String[] { name }));
    Assert.assertEquals(0L, this.cityOperations.getAllCities().size());
  }
    
}
