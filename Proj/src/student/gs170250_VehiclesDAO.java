/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author User
 */
public class gs170250_VehiclesDAO implements VehicleOperations{

    @Override
    public boolean insertVehicle(String regNumber, int fuelType, BigDecimal fuelConsumption) {
        
        if(!gs170250_Constants.codeToFuelType.containsKey(Integer.valueOf(fuelType))) {
           return false;
        }
        Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement checkRegNumber = connection.prepareStatement("select * from Vehicle where regNumber = ?");
                 PreparedStatement insertVehicleStatement = connection.prepareStatement("insert into Vehicle (RegNumber, FuelType, FuelConsumption) "
                    + "values(?, ?, ?)") ) {
             
            checkRegNumber.setString(1, regNumber);
            if(checkRegNumber.executeQuery().next()) {
                return false;
            }
            
            insertVehicleStatement.setString(1, regNumber);
            insertVehicleStatement.setString(2, gs170250_Constants.codeToFuelType.get(fuelType));
            insertVehicleStatement.setBigDecimal(3, fuelConsumption);
            
            return insertVehicleStatement.executeUpdate() > 0 ? true : false;
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_DistrictDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int deleteVehicles(String... regNumbers) {
        
        int numOfVehiclesDeleted = 0;
        Connection connection = DB.getInstance().getConnection();
        try(PreparedStatement deleteVehicleByRegNumber = connection.prepareStatement("delete from Vehicle where RegNumber = ? ");
              ) {
            
            for(String name : regNumbers) {
                
              deleteVehicleByRegNumber.setString(1, name);
              numOfVehiclesDeleted += deleteVehicleByRegNumber.executeUpdate();
            }
            return numOfVehiclesDeleted;
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_DistrictDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return numOfVehiclesDeleted;
    }

    @Override
    public List<String> getAllVehichles() {
       
        Connection connection = DB.getInstance().getConnection();
        List<String> allVehicles = new LinkedList<>();
        try (PreparedStatement getAllVehicles = connection.prepareStatement("select RegNumber from Vehicle")){
           
            ResultSet allVehicleResult = getAllVehicles.executeQuery();
            
            while(allVehicleResult.next()) {
                allVehicles.add(allVehicleResult.getString(1));
            }
            return allVehicles;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_VehiclesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean changeFuelType(String regNumber, int fuelType) {
        
        if(!gs170250_Constants.codeToFuelType.containsKey(Integer.valueOf(fuelType))) {
            return false;
        }
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement updateFuelType = connection.prepareStatement("update Vehicle set FuelType = ? where "
                + "RegNumber = ? ")){
           
            updateFuelType.setString(2, regNumber);
            updateFuelType.setString(1, gs170250_Constants.codeToFuelType.get(Integer.valueOf(fuelType)));
            
            return updateFuelType.executeUpdate() > 0 ? true : false;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_VehiclesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean changeConsumption(String regNumber, BigDecimal fuelConsumption) {
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement updateFuelType = connection.prepareStatement("update Vehicle set FuelConsumption = ? where "
                + "RegNumber = ? ")){
           
            updateFuelType.setString(2, regNumber);
            updateFuelType.setBigDecimal(1, fuelConsumption);
            
            return updateFuelType.executeUpdate() > 0 ? true : false;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_VehiclesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
