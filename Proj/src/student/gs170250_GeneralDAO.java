/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author User
 */
public class gs170250_GeneralDAO implements GeneralOperations {

    @Override
    public void eraseAll() {
        Connection connection = DB.getInstance().getConnection();
        try(PreparedStatement eraseAllCities = connection.prepareStatement("delete from City where idCity is not null");
                PreparedStatement eraseAllDistricts = connection.prepareStatement("delete from District where idCity is not null");
                PreparedStatement eraseAllAdmins = connection.prepareStatement("delete from [Admin] where idUser is not null");
                PreparedStatement eraseAllCouriers = connection.prepareStatement("delete from Courier where idUser is not null");
                PreparedStatement eraseAllUsers = connection.prepareStatement("delete from [User] where idUser is not null");
                PreparedStatement eraseAllVehicles = connection.prepareStatement("delete from Vehicle where regNumber is not null");) {
            
            eraseAllAdmins.executeUpdate();
            eraseAllCouriers.executeUpdate();
            eraseAllUsers.executeUpdate();
            eraseAllDistricts.executeUpdate();
            eraseAllCities.executeUpdate();
            eraseAllVehicles.executeUpdate();
        
    }   catch (SQLException ex) {
            Logger.getLogger(gs170250_GeneralDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
