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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class gs170250_Util {
 
    public static double calculateEuclidDistance(int districtFrom, int districtTo) {
        Connection connection = DB.getInstance().getConnection();
        try(PreparedStatement getCoordinates = connection.prepareStatement("select xCoordinate, yCoordinate from "
                + "District where IdDistrict = ?")) {
            
            getCoordinates.setInt(1, districtFrom);
            ResultSet districtFromRS = getCoordinates.executeQuery();
            if(!districtFromRS.next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            int districtFromX = districtFromRS.getInt(1);
            int districtFromY = districtFromRS.getInt(2);
            
            getCoordinates.setInt(1, districtTo);
            ResultSet districtToRS = getCoordinates.executeQuery();
            if(!districtToRS.next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            int districtToX = districtToRS.getInt(1);
            int districtToY = districtToRS.getInt(2);  
            
            return Math.sqrt((districtFromY - districtToY) * (districtFromY - districtToY) + 
                (districtFromX - districtToX) * (districtFromX - districtToX));
        
    }   catch (SQLException ex) {
            Logger.getLogger(gs170250_Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0.0;
    }   
    
    public static BigDecimal calculatePrice(int districtFrom, int districtTo, int packageType, BigDecimal weight) {
        
        double euclidDistance = calculateEuclidDistance(districtFrom, districtTo);
        
        if(packageType == 0) {
            return new BigDecimal(euclidDistance * gs170250_Constants.price.get(packageType));
        }
        return new BigDecimal (euclidDistance * (gs170250_Constants.price.get(packageType) + gs170250_Constants.factor.get(packageType) * weight.doubleValue() * 
                gs170250_Constants.priceByKg.get(packageType)));
    }
    
}
