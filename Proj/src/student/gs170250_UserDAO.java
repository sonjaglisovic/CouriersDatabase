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
import java.util.regex.Pattern;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author User
 */
public class gs170250_UserDAO implements UserOperations{

    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password) {
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement checkName = connection.prepareStatement("select * from [User] where UserName = ?");
                PreparedStatement insertUserStatement = connection.prepareStatement("insert into [User] (FirstName, LastName, Password, UserName)"
                + "values(?, ?, ?, ?)")) {
            
            checkName.setString(1, userName);
            if(checkName.executeQuery().next()) {
                return false;
            }
            String firstUpperCaseRegex = "^[A-Z][a-z]*";
            String regexPassword = "^(?=.*[0-9])(?=.*[a-zA-Z]).*{8,}";
            
            if(!firstName.matches(firstUpperCaseRegex)) {
                return false;
            }
            if(!lastName.matches(firstUpperCaseRegex)) {
                return false;
            }
            
            if(!password.matches(regexPassword)) {
                return false;
            }
            
            insertUserStatement.setString(4, userName);
            insertUserStatement.setString(1, firstName);
            insertUserStatement.setString(2, lastName);
            insertUserStatement.setString(3, password);
            
            return insertUserStatement.executeUpdate() > 0 ? true : false;
            
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_CityDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    

    @Override
    public int declareAdmin(String username) {
        
       Connection connection = DB.getInstance().getConnection();
       try(PreparedStatement checkUser = connection.prepareStatement("select idUser from [User] where UserName = ? ");
               PreparedStatement checkIfAdmin = connection.prepareStatement("select * from [Admin] A join [User] U "
                       + "on(A.idUser = U.idUser) where UserName = ? ");
               PreparedStatement insertAdmin = connection.prepareStatement("insert into [Admin] (idUser) values(?)")) {
           
        checkUser.setString(1, username);
        ResultSet userByUserName = checkUser.executeQuery();
        if(!userByUserName.next()) {
            return gs170250_Constants.USER_DOES_NOT_EXIST;
        }
        checkIfAdmin.setString(1, username);
        if(checkIfAdmin.executeQuery().next()) {
           return gs170250_Constants.ALREADY_ADMIN; 
        }
        
        insertAdmin.setInt(1, userByUserName.getInt(1));
        return insertAdmin.executeUpdate() > 0 ? gs170250_Constants.CODE_SUCCESS : gs170250_Constants.DATABASE_ERROR_CODE;
        
    }   catch (SQLException ex) {
            Logger.getLogger(gs170250_UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
       return gs170250_Constants.DATABASE_ERROR_CODE;
    }
    @Override
    public Integer getSentPackages(String... names) {
        
      Integer numOfSentPackages = 0;
      Connection connection = DB.getInstance().getConnection();
      
      try(PreparedStatement getNumOfSentPackages = connection.prepareStatement("select NumOfSentPackages from [User] "
              + "where UserName = ? ")){
          for(String userName : names) {
              
            getNumOfSentPackages.setString(1, userName);
            ResultSet numOfSent = getNumOfSentPackages.executeQuery();
            if(numOfSent.next()) {
                numOfSentPackages += numOfSent.getInt(1);
            } else {
                return null;
            }
          }
          return numOfSentPackages;
      } catch (SQLException ex) {
            Logger.getLogger(gs170250_UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
      return null;
    }

    @Override
    public int deleteUsers(String... names) {
        
        Integer numOfDeletedUsers = 0;
        Connection connection = DB.getInstance().getConnection();
        CourierOperations courierOperation = new gs170250_CourierDAO();
      
      try(PreparedStatement getAdmin = connection.prepareStatement("select A.idUser from [Admin] A join [User] U on(A.idUser = U.idUser) "
              + "where UserName = ? ");
              PreparedStatement deleteUser = connection.prepareStatement("delete from  [User] "
              + "where UserName = ? ");
              PreparedStatement deleteAdmin = connection.prepareStatement("delete from  [Admin] "
              + "where idUser = ? ")){
          for(String userName : names) {
              
            courierOperation.deleteCourier(userName);
            getAdmin.setString(1, userName);
            ResultSet adminByName = getAdmin.executeQuery();
            
            if(adminByName.next()) {
                deleteAdmin.setInt(1, adminByName.getInt(1));
                deleteAdmin.executeUpdate();
            }
            deleteUser.setString(1, userName);
            numOfDeletedUsers += deleteUser.executeUpdate();
          }
          return numOfDeletedUsers;
      } catch (SQLException ex) {
            Logger.getLogger(gs170250_UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
      return 0;   
    }

    @Override
    public List<String> getAllUsers() {
        
        Connection connection = DB.getInstance().getConnection();
        List<String> allUsers = new ArrayList<>();
        try (PreparedStatement getAllUsers = connection.prepareStatement("select UserName from [User]")){
           
            ResultSet allUsersResult = getAllUsers.executeQuery();
            
            while(allUsersResult.next()) {
                allUsers.add(allUsersResult.getString("UserName"));
            }
            return allUsers;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
