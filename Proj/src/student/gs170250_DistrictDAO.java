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
import rs.etf.sab.operations.DistrictOperations;

/**
 *
 * @author User
 */
public class gs170250_DistrictDAO implements DistrictOperations {

    @Override
    public int insertDistrict(String name, int idCity, int xCoordinate, int yCoordinate) {
        
         Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement checkCity = connection.prepareStatement("select * from City where idCity = ?");
                 PreparedStatement checkName = connection.prepareStatement("select * from District where Name = ?");
                 PreparedStatement insertDistrictStatement = connection.prepareStatement("insert into District (Name, idCity, xCoordinate, yCoordinate) "
                    + "values(?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);) {
             
            insertDistrictStatement.setString(1, name);
            insertDistrictStatement.setInt(2, idCity);
            insertDistrictStatement.setInt(3, xCoordinate);
            insertDistrictStatement.setInt(4, yCoordinate);
            
            checkCity.setInt(1, idCity);
            checkName.setString(1, name);
            
            ResultSet cityById = checkCity.executeQuery();
            
            if(!cityById.next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            
            ResultSet districtsByName = checkName.executeQuery();
            
            if(districtsByName.next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }   
            
            insertDistrictStatement.executeUpdate();
            
            ResultSet insertedDistrictId = insertDistrictStatement.getGeneratedKeys();
            if(insertedDistrictId.next()) {
                return insertedDistrictId.getInt(1);
            } else {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_DistrictDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gs170250_Constants.DATABASE_ERROR_CODE;
    }

    @Override
    public int deleteDistricts(String... names) {
      
        int numOfDistrictsDeleted = 0;
        Connection connection = DB.getInstance().getConnection();
        try(PreparedStatement deleteDistrictByName = connection.prepareStatement("delete from District where Name = ?");
              ) {
            for(String name : names) {
          
              PreparedStatement checkPackage = connection.prepareStatement("select * from Package where DistrictFrom = (select IdDistrict from District where Name = ?) "
                      + "or DistrictTo = (select IdDistrict from District where Name = ?)");
              
              checkPackage.setString(1, name);
              checkPackage.setString(2, name);
              ResultSet packagesWithDistrict = checkPackage.executeQuery();
              if(packagesWithDistrict.next()) {
                  packagesWithDistrict.close();
                  continue;
              }
              deleteDistrictByName.setString(1, name);
              
        
            int codeSuccess = deleteDistrictByName.executeUpdate();

            if(codeSuccess > 0) {
                numOfDistrictsDeleted++;
            }
            checkPackage.close();
          }
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_DistrictDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return numOfDistrictsDeleted;
    }

    @Override
    public boolean deleteDistrict(int idDistrict) {
        
        Connection connection = DB.getInstance().getConnection();
         try ( PreparedStatement checkId = connection.prepareStatement("select * from District where IdDistrict = ?");
                  PreparedStatement checkPackages = connection.prepareStatement("select * from Package where DistrictFrom = ? "
                      + "or DistrictTo = ?");
                 PreparedStatement deleteDistrictStatement = connection.prepareStatement("delete from District "
                    + "where IdDistrict = ? ")) {
            
            checkPackages.setInt(1, idDistrict);
            checkPackages.setInt(2, idDistrict);
            ResultSet districtsInPackages = checkPackages.executeQuery();
            
            if(districtsInPackages.next()) {
                return false;
            }
            
            deleteDistrictStatement.setInt(1, idDistrict);
            
            checkId.setInt(1, idDistrict);
            ResultSet districtById = checkId.executeQuery();
            
            if(districtById.next()) {
            
                int successCode = deleteDistrictStatement.executeUpdate();
                return successCode > 0 ? true : false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_DistrictDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false; 
    }

    @Override
    public int deleteAllDistrictsFromCity(String cityName) {
        
        Connection connection = DB.getInstance().getConnection();
        try ( PreparedStatement checkPackages = connection.prepareStatement("select * from Package where DistrictFrom = ? "
                      + "or DistrictTo = ?");
                PreparedStatement getAllDistrictsFromCity = connection.prepareStatement("select IdDistrict from District where idCity = (select idCity from City where Name = ?)");
                PreparedStatement deleteDistrictStatement = connection.prepareStatement(" delete from District where IdDistrict = ? "
             );) {
           
           getAllDistrictsFromCity.setString(1, cityName);
           ResultSet districtsFromCity = getAllDistrictsFromCity.executeQuery();
           int numOfDeleted = 0;
           while(districtsFromCity.next()) {
               
               int idDistrict = districtsFromCity.getInt(1);
               checkPackages.setInt(1, idDistrict);
               checkPackages.setInt(2, idDistrict);
               ResultSet packagesForDistrict = checkPackages.executeQuery();
               if(packagesForDistrict.next()) {
                   continue;
               }
               deleteDistrictStatement.setInt(1, idDistrict);
               numOfDeleted += deleteDistrictStatement.executeUpdate();
           }
           

           return numOfDeleted;
       } catch (SQLException ex) {
           Logger.getLogger(gs170250_DistrictDAO.class.getName()).log(Level.SEVERE, null, ex);
       }
       return gs170250_Constants.DATABASE_ERROR_CODE; 
    }

    @Override
    public List<Integer> getAllDistrictsFromCity(int idCity) {
       
        List<Integer> allDistricts = new ArrayList<>();
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement getAllDistrictsStatement = connection.prepareStatement("select IdDistrict from District where idCity = ?");){
            
            getAllDistrictsStatement.setInt(1, idCity);
            ResultSet allDistrictsResult = getAllDistrictsStatement.executeQuery();
            
            while(allDistrictsResult.next()) {
                allDistricts.add(allDistrictsResult.getInt("IdDistrict"));
            }
            return allDistricts;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_DistrictDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getAllDistricts() {
        
        List<Integer> allDistricts = new ArrayList<>();
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement getAllDistrictsStatement = connection.prepareStatement("select IdDistrict from District");
            ResultSet allDistrictsResult = getAllDistrictsStatement.executeQuery()) {
            
            while(allDistrictsResult.next()) {
                allDistricts.add(allDistrictsResult.getInt("IdDistrict"));
            }
            return allDistricts;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_DistrictDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
