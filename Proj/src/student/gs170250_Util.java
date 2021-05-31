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
 
    public static double calculatePrice(double districtFromX, double districtFromY,
            double districtToX, double districtToY, int packageType, BigDecimal weight) {
        
        double euclidDistance = Math.sqrt((districtFromY - districtToY) * (districtFromY - districtToY) + 
                (districtFromX - districtToX) * (districtFromX - districtToX));
        if(packageType == 0) {
            return euclidDistance * gs170250_Constants.price.get(packageType);
        }
        return euclidDistance * (gs170250_Constants.price.get(packageType) + gs170250_Constants.factor.get(packageType) * weight.doubleValue() * 
                gs170250_Constants.priceByKg.get(packageType));
    }
    
}
