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
        
        Integer acceptCode = 1;
        Connection connection = DB.getInstance().getConnection();
        String sql = "{call spProcessCourierRequest (?, ?, ?, ?)}";
        try (PreparedStatement getUserName = connection.prepareStatement("select idUser from [User] where UserName = ?")) {
            CallableStatement acceptCourier = connection.prepareCall(sql);
            
            getUserName.setString(1, courierUserName);
            ResultSet userByUserName = getUserName.executeQuery();
            if(userByUserName.next()){
            
                acceptCourier.setInt(1, userByUserName.getInt(1));
                acceptCourier.setString(2, licencePlateNumber);
                acceptCourier.setInt(3, acceptCode);
                acceptCourier.registerOutParameter(4, Types.INTEGER);
                acceptCourier.execute();
                return acceptCourier.getInt(4) == 0 ? false : true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        return false;
    }

    @Override
    public boolean deleteCourier(String courierUserName) {
       
         Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement deleteCourierStatement = connection.prepareStatement("delete from Courier "
                    + "where idUser = (select idUser from [User] where UserName = ?) ")) {
            
            deleteCourierStatement.setString(1, courierUserName);
            return deleteCourierStatement.executeUpdate() > 0 ? true : false;
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_DistrictDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                    + "where C.Status = ? ")) {
            
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
                + " where NumOfDeliveredPackages > ? "
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
