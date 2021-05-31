/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author User
 */
public class gs170250_CourierRequestDAO implements CourierRequestOperation {

    @Override
    public boolean insertCourierRequest(String userName, String regNumber) {
        
        Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement getUserId = connection.prepareStatement("select idUser from [User] where UserName = ? ");
                 PreparedStatement checkUser = connection.prepareStatement("select * from CourierRequest where idUser = ?");
                 PreparedStatement checkIfCourier = connection.prepareStatement("select * from Courier where idUser = ?");
                 PreparedStatement checkVehicle = connection.prepareStatement("select * from Vehicle where RegNumber = ?");
                 PreparedStatement insertCourierRequestStatement = connection.prepareStatement("insert into CourierRequest (idUser, RegNumber) "
                    + "values(?, ?)") ) {
             
            getUserId.setString(1, userName);
            ResultSet userIdResult = getUserId.executeQuery();
            
            if(!userIdResult.next()) {
                return false;
            }
            int userId = userIdResult.getInt(1);
            checkUser.setInt(1, userId);
            if(checkUser.executeQuery().next()) {
                return false;
            }
            checkIfCourier.setInt(1, userId);
            if(checkIfCourier.executeQuery().next()) {
                return false;
            }
            checkVehicle.setString(1, regNumber);
            
            if(!checkVehicle.executeQuery().next()) {
                return false;
            }
            
            insertCourierRequestStatement.setInt(1, userId);
            insertCourierRequestStatement.setString(2, regNumber);
            
            return insertCourierRequestStatement.executeUpdate() > 0 ? true : false;
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean deleteCourierRequest(String userName) {
        
        Connection connection = DB.getInstance().getConnection();
        try(PreparedStatement deleteCourierRequestByUserName = connection.prepareStatement("delete from "
                + "CourierRequest where idUser = (select idUser from [User] where UserName = ? ) ");
              ) {
            
            deleteCourierRequestByUserName.setString(1, userName);
           
            return deleteCourierRequestByUserName.executeUpdate() > 0 ? true : false;
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    @Override
    public boolean changeVehicleInCourierRequest(String userName, String regNumber) {
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement updateRegNumber = connection.prepareStatement("update CourierRequest set RegNumber = ? where "
                + "idUser = (select idUser from [User] where UserName = ? ) ")){
           
            updateRegNumber.setString(1, regNumber);
            updateRegNumber.setString(2, userName);
            
            return updateRegNumber.executeUpdate() > 0 ? true : false;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<String> getAllCourierRequests() {
        
        Connection connection = DB.getInstance().getConnection();
        List<String> allRequests = new LinkedList<>();
        try (PreparedStatement getAllCourierRequests = connection.prepareStatement("select UserName from CourierRequest R "
                + "join [User] U on(R.idUser = U.idUser)"
                )){
           
            ResultSet allCourierRequestsResult = getAllCourierRequests.executeQuery();
            
            while(allCourierRequestsResult.next()) {
                allRequests.add(allCourierRequestsResult.getString(1));
            }
            return allRequests;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierRequestDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean grantRequest(String userName) {
       
        Integer acceptCode = 1;
        Connection connection = DB.getInstance().getConnection();
        String sql = "{call spProcessCourierRequest (?, ?, ?)}";
        try (PreparedStatement getUser = connection.prepareStatement("select idUser from [User] where UserName = ?")) {
            CallableStatement acceptCourier = connection.prepareCall(sql);
            
            getUser.setString(1, userName);
            ResultSet userByUserName = getUser.executeQuery();
            if(!userByUserName.next()){
               return false;
            }
            acceptCourier.setInt(1, userByUserName.getInt(1));
            acceptCourier.setInt(2, acceptCode);
            acceptCourier.registerOutParameter(3, Types.INTEGER);
            acceptCourier.execute();
            return acceptCourier.getInt(3) == 0 ? false : true;
            
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CourierDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        return false; 
    }
    
}
