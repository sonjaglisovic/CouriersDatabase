/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author User
 */
public class gs170250_CourierDAO implements CourierOperations {

    @Override
    public boolean insertCourier(String courierUserName, String licencePlateNumber) {
        
         Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement getUserId = connection.prepareStatement("select idUser from [User] where UserName = ? ");
                 PreparedStatement checkUser = connection.prepareStatement("select * from Courier where idUser = ? ");
                  PreparedStatement checkVehicle = connection.prepareStatement("select * from Courier where RegNumber = ? ");
                 PreparedStatement checkIfVehicleExists = connection.prepareStatement("select * from Vehicle where RegNumber = ? ");
                 PreparedStatement insertCourierStatement = connection.prepareStatement("insert into Courier (idUser, RegNumber) "
                    + "values(?, ?)") ) {
             
            getUserId.setString(1, courierUserName);
            ResultSet userIdResult = getUserId.executeQuery();
            
            if(!userIdResult.next()) {
                return false;
            }
            int userId = userIdResult.getInt(1);
            checkUser.setInt(1, userId);
            if(checkUser.executeQuery().next()) {
                return false;
            }
            checkVehicle.setString(1, licencePlateNumber);
            if(checkVehicle.executeQuery().next()) {
                return false;
            }
            
            checkIfVehicleExists.setString(1, licencePlateNumber);
            if(!checkIfVehicleExists.executeQuery().next()) {
                return false;
            }
            
            insertCourierStatement.setInt(1, userId);
            insertCourierStatement.setString(2, licencePlateNumber);
            
            return insertCourierStatement.executeUpdate() > 0 ? true : false;
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean deleteCourier(String courierUserName) {
       
         Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement checkPackages = connection.prepareStatement("select * from  Package where Courier = (select idUser from [User] where UserName = ?) ");
                 PreparedStatement deleteCourierStatement = connection.prepareStatement("delete from Courier "
                    + "where idUser = (select idUser from [User] where UserName = ?) ")) {
            
            checkPackages.setString(1, courierUserName);
            ResultSet couriersPackages = checkPackages.executeQuery();
            if(couriersPackages.next()) {
                return false;
            }
            deleteCourierStatement.setString(1, courierUserName);
            return deleteCourierStatement.executeUpdate() > 0 ? true : false;
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false; 
    }

    @Override
    public List<String> getCouriersWithStatus(int status) {
         
       List<String> allCouriersWithStatus = new ArrayList<>(); 
       if(!gs170250_Constants.codeToStatus.containsKey(Integer.valueOf(status))) {
           return allCouriersWithStatus;
       } 
       Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement findCouriersByStatus = connection.prepareStatement("select * from Courier C join [User] U "
                 + "on(C.idUser = U.idUser)"
                    + "where C.Status = ? order by Profit desc")) {
            
            findCouriersByStatus.setString(1, gs170250_Constants.codeToStatus.get(Integer.valueOf(status)));
            ResultSet allCouriers = findCouriersByStatus.executeQuery();
            while(allCouriers.next()) {
                allCouriersWithStatus.add(allCouriers.getString("UserName"));
            }
            return allCouriersWithStatus;
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;  
    }

    @Override
    public List<String> getAllCouriers() {
        
        Connection connection = DB.getInstance().getConnection();
        List<String> allCouriers = new ArrayList<>();
        try (PreparedStatement getAllCitiesStatement = connection.prepareStatement("select UserName from Courier C join [User] U "
                + "on(C.idUser = U.idUser) order by Profit desc")){

            ResultSet allCouriersResult = getAllCitiesStatement.executeQuery();

            while(allCouriersResult.next()) {
                allCouriers.add(allCouriersResult.getString("UserName"));
            }
            return allCouriers;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;  
    }

    @Override
    public BigDecimal getAverageCourierProfit(int numOfDelivered) {
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement getAverageProfitStatement = connection.prepareStatement("select coalesce(avg(Profit), 0) from Courier"
                + " where NumOfDeliveredPackages >= ? "
        )){

            getAverageProfitStatement.setInt(1, numOfDelivered);
            ResultSet averageValue = getAverageProfitStatement.executeQuery();

            if(averageValue.next()) {
                return BigDecimal.valueOf(averageValue.getInt(1));
            } 
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;   
    }
    
}
