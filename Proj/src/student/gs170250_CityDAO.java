/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author User
 */
public class gs170250_CityDAO implements CityOperations {
    
    @Override
    public int insertCity(String name, String zipCode) {
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement checkName = connection.prepareStatement("select * from City where Name = ?");
                PreparedStatement checkZipCode = connection.prepareStatement("select * from City where zip_Code = ?");
                PreparedStatement insertCityStatement = connection.prepareStatement("insert into City (Name, zip_Code)"
                + "values(?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            insertCityStatement.setString(1, name);
            insertCityStatement.setString(2, zipCode);
            checkName.setString(1, name);
            checkZipCode.setString(1, zipCode);
            
            ResultSet citiesByName = checkName.executeQuery();
            
            if(citiesByName.next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            
            ResultSet citiesByZipCode = checkZipCode.executeQuery();
            
            if(citiesByZipCode.next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            
            insertCityStatement.executeUpdate();
            ResultSet insertedCityId = insertCityStatement.getGeneratedKeys();
            if(insertedCityId.next()) {
                return insertedCityId.getInt(1);
            } else {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gs170250_Constants.DATABASE_ERROR_CODE;
    }

    @Override
    public int deleteCity(String... names) {
        
        int numOfCitiesDeleted = 0;
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement deleteCityStatement = connection.prepareStatement("delete from City "
                    + "where Name = ? ")) {
            
          for(String name : names) {
            
            PreparedStatement checkName = connection.prepareStatement("select * from City where Name = ?");
            PreparedStatement checkDistricts = connection.prepareStatement("select * from District where idCity = "
                    + "(select idCity from City where Name = ?)");
            
            checkName.setString(1, name);
            checkDistricts.setString(1, name);
            ResultSet citiesByName = checkName.executeQuery();
            ResultSet districtsByCity = checkDistricts.executeQuery();
            
            if(citiesByName.next() && !districtsByCity.next()) {
                
                deleteCityStatement.setString(1, name);

                int successCode = deleteCityStatement.executeUpdate();
                if(successCode != 0) {
                    numOfCitiesDeleted++;
                }
            }
            checkName.close();
            checkDistricts.close();
          }
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numOfCitiesDeleted;
    }

    @Override
    public boolean deleteCity(int idCity) {
        
       Connection connection = DB.getInstance().getConnection(); 
       try (PreparedStatement checkDistricts = connection.prepareStatement("select * from District where idCity = ?");
               PreparedStatement checkId = connection.prepareStatement("select * from City where Name = ?");
               PreparedStatement deleteCityStatement = connection.prepareStatement("delete from City "
                    + "where idCity = ? ")) {
           
           checkDistricts.setInt(1, idCity);
           ResultSet districtsInCity = checkDistricts.executeQuery();
           if(districtsInCity.next()) {
               return false;
           }
            deleteCityStatement.setInt(1, idCity);
            checkId.setInt(1, idCity);
  
            int successCode = deleteCityStatement.executeUpdate();
            if(successCode > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false; 
    }

    @Override
    public List<Integer> getAllCities() {
        
        Connection connection = DB.getInstance().getConnection();
        List<Integer> allCities = new ArrayList<>();
        try (PreparedStatement getAllCitiesStatement = connection.prepareStatement("select idCity from City")){
           
            ResultSet allCitiesResult = getAllCitiesStatement.executeQuery();
            
            while(allCitiesResult.next()) {
                allCities.add(allCitiesResult.getInt("idCity"));
            }
            return allCities;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
