/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author User
 */
public class gs1702500_PackageDAO implements PackageOperations {

    @Override
    public int insertPackage(int districtFrom, int districtTo, String userName, int packageType, BigDecimal weight) {
          
        if(!gs170250_Constants.codeToPackageType.containsKey(packageType)) {
              return gs170250_Constants.DATABASE_ERROR_CODE;
        }
         
         Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement checkDistrict = connection.prepareStatement("select * from District where IdDistrict = ?");
                 PreparedStatement checkUser = connection.prepareStatement("select idUser from [User] where UserName = ?");
                 PreparedStatement insertPackage = connection.prepareStatement("insert into Package (Type, idUser, Weight, DistrictFrom, DistrictTo) "
                    + "values(?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                 ) {
             
            checkDistrict.setInt(1, districtFrom);
            if(!checkDistrict.executeQuery().next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            checkDistrict.setInt(1, districtTo);
            if(!checkDistrict.executeQuery().next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            checkUser.setString(1, userName);
            ResultSet userId = checkUser.executeQuery();
            if(!userId.next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            
            insertPackage.setString(1, gs170250_Constants.codeToPackageStatus.get(packageType));
            insertPackage.setInt(2, userId.getInt(1));
            insertPackage.setBigDecimal(3, weight);
            insertPackage.setInt(4, districtFrom);
            insertPackage.setInt(5, districtTo);
            
            return insertPackage.executeUpdate() > 0 ? insertPackage.getGeneratedKeys().getInt(1) : gs170250_Constants.DATABASE_ERROR_CODE;
        } catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gs170250_Constants.DATABASE_ERROR_CODE;
    }

    @Override
    public int insertTransportOffer(String userName, int packageId, BigDecimal pricePercentage) {
        
         Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement checkCourier = connection.prepareStatement("select idUser from Courier where idUser = (select "
                 + "idUser from [User] where UserName = ? and Status = 'ne vozi')");
                 PreparedStatement checkPackage = connection.prepareStatement("select * from Package where idPackage = ?");
                 PreparedStatement insertTransportOffer = connection.prepareStatement("insert into TransportOffer (Courier, idPackage, OfferDetails) "
                    + "values(?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS) ) {
             
            checkCourier.setString(1, userName);
            ResultSet courierId = checkCourier.executeQuery();
            if(!courierId.next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            } 
            checkPackage.setInt(1, packageId);
            if(checkPackage.executeQuery().next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            
            insertTransportOffer.setInt(1, courierId.getInt(1));
            insertTransportOffer.setInt(2, packageId);
            insertTransportOffer.setBigDecimal(3, pricePercentage);
            
            return insertTransportOffer.executeUpdate() > 0 ? insertTransportOffer.getGeneratedKeys().getInt(1) : gs170250_Constants.DATABASE_ERROR_CODE;
        } catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gs170250_Constants.DATABASE_ERROR_CODE;
    }

    @Override
    public boolean acceptAnOffer(int offerId) {
        
         Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement checkDistrict = connection.prepareStatement("select xCoorfinate, yCoordinate from District where IdDistrict = ?");
                 PreparedStatement checkOffer = connection.prepareStatement("select idPackage, Courier, OfferDetails from TransportOffer where idOffer = ?");
                 PreparedStatement getPackage = connection.prepareStatement("select Weight, DistrictFrom, DistrictTo, Status  from Package where idPackage = ?");
                 ) {
             
            checkOffer.setInt(1, offerId);
            ResultSet offer = checkOffer.executeQuery();
            if(!offer.next()) {
                return false;
            }
            int packageId = offer.getInt(1);
            int courierId = offer.getInt(2);
            BigDecimal percentage = offer.getBigDecimal(3);
            getPackage.setInt(1, packageId);
            
            ResultSet packageToAccept = getPackage.executeQuery();
            if(!packageToAccept.next()) {
                return false;
            }
            BigDecimal weight = packageToAccept.getBigDecimal(1);
            int distrcitFrom = packageToAccept.getInt(2);
            int districtTo = packageToAccept.getInt(3);
            
            checkDistrict.setInt(1, distrcitFrom);
            ResultSet distrcitFromCoordinates = checkDistrict.executeQuery();
            if(!distrcitFromCoordinates.next()) {
                return false;
            }
            double districtFromX = distrcitFromCoordinates.getDouble(1);
            double districtFromY = distrcitFromCoordinates.getDouble(2);
            
            checkDistrict.setInt(1, districtTo);
            ResultSet distrcitToCoordinates = checkDistrict.executeQuery();
            if(!distrcitToCoordinates.next()) {
                return false;
            }
            double districtToX = distrcitToCoordinates.getDouble(1);
            double districtToY = distrcitToCoordinates.getDouble(2);
            
            if(packageToAccept.getString("Status") != gs170250_Constants.codeToStatus.get(0)) {
                return false;
            } 
            packageToAccept.updateTimestamp("Accepted", Timestamp.valueOf(LocalDateTime.now()));
            packageToAccept.updateString("Status", gs170250_Constants.codeToPackageStatus.get(1));
            packageToAccept.updateInt("Courier", courierId);
            packageToAccept.updateDouble("Price", gs170250_Util.calculatePrice(districtFromX, districtFromY,
                    districtToX, districtToY, packageId, weight) * percentage.doubleValue() / 100);
            
            packageToAccept.updateRow();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<Integer> getAllOffers() {
        
       Connection connection = DB.getInstance().getConnection();
        List<Integer> allOffers = new LinkedList<>();
        try (PreparedStatement getAllOffers = connection.prepareStatement("select idOffer from TransportOffer")){
           
            ResultSet allOffersResult = getAllOffers.executeQuery();
            
            while(allOffersResult.next()) {
                allOffers.add(allOffersResult.getInt(1));
            }
            return allOffers;
        }catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deletePackage(int packageId) {
        
      Connection connection = DB.getInstance().getConnection();
         try ( PreparedStatement deleteOffers = connection.prepareStatement("delete from TransportOffer where idPackage = ?");
                 PreparedStatement deletePackageStatement = connection.prepareStatement("delete from Package "
                    + "where IdDistrict = ? ")) {
            
            deleteOffers.setInt(1, packageId);
            deleteOffers.executeUpdate();
            
            deletePackageStatement.setInt(1, packageId);
            
            return deletePackageStatement.executeUpdate() > 0 ? true : false;
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_DistrictDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;   
    }

    @Override
    public boolean changeWeight(int packageId, BigDecimal weight) {
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement checkStatus = connection.prepareStatement("select Status from Package where idPackage = ?");
                PreparedStatement updateWeight = connection.prepareStatement("update Package set Weight = ? where "
                + "idPackage = ? ")){
           
            checkStatus.setInt(1, packageId);
            ResultSet packageStatus = checkStatus.executeQuery();
            if(!packageStatus.next() || !packageStatus.getString(1).equals(gs170250_Constants.codeToPackageStatus.get(0))) {
                return false;
            }
            updateWeight.setBigDecimal(1, weight);
            updateWeight.setInt(1, packageId);
            
            return updateWeight.executeUpdate() > 0 ? true : false;
        }catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean changeType(int packageId, int packageType) {
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement checkStatus = connection.prepareStatement("select Status from Package where idPackage = ?");
                PreparedStatement updateType = connection.prepareStatement("update Package set Type = ? where "
                + "idPackage = ? ")){
           
            checkStatus.setInt(1, packageId);
            ResultSet packageStatus = checkStatus.executeQuery();
            if(!packageStatus.next() || !packageStatus.getString(1).equals(gs170250_Constants.codeToPackageStatus.get(0))) {
                return false;
            }
            updateType.setString(1, gs170250_Constants.codeToPackageType.get(packageType));
            updateType.setInt(1, packageId);
            
            return updateType.executeUpdate() > 0 ? true : false;
        }catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Integer getDeliveryStatus(int packageId) {
       
        Integer deliveryStatus = null;
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement getStatus = connection.prepareStatement("select Status from Package where idPackage = ?")){
           
            getStatus.setInt(1, packageId);
            ResultSet status = getStatus.executeQuery();
            
            if(status.next()) {
              Map.Entry<Integer, String> statusEntry = gs170250_Constants.codeToPackageStatus.entrySet().stream().filter(map -> map.getValue().equals(status)).findFirst().orElse(null);
              return statusEntry.getKey();
            } 
            return deliveryStatus;
        }catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int packageId) {
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement getPrice = connection.prepareStatement("select Price from Package where idPackage = ?")){
           
            getPrice.setInt(1, packageId);
            ResultSet price = getPrice.executeQuery();
            
            if(price.next()) {
              return price.getBigDecimal(1);
            } 
        }catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Date getAcceptanceTime(int packageId) {
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement getAcceptanceTime = connection.prepareStatement("select Accepted from Package where idPackage = ?")){
           
            getAcceptanceTime.setInt(1, packageId);
            ResultSet accepted = getAcceptanceTime.executeQuery();
            
            if(accepted.next()) {
              return Date.valueOf(accepted.getTimestamp(1).toLocalDateTime().toLocalDate());
            } 
        }catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int packageType) {
        
       List<Integer> allPackages = new ArrayList<>(); 
       if(! gs170250_Constants.codeToPackageType.containsKey(packageType)) {
           return allPackages;
       } 
       Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement getPackagesWithType = connection.prepareStatement("select idPackage from Package where Type = ?")){
           
            getPackagesWithType.setString(1, gs170250_Constants.codeToPackageType.get(packageType));
            ResultSet packages = getPackagesWithType.executeQuery();
            
            while(packages.next()) {
              allPackages.add(packages.getInt(1));
            } 
            return allPackages;
        }catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getAllPackages() {
        
        Connection connection = DB.getInstance().getConnection();
        List<Integer> allPackages = new LinkedList<>();
        try (PreparedStatement getAllPackages = connection.prepareStatement("select idPackage from Package")){
           
            ResultSet allPackageResult = getAllPackages.executeQuery();
            
            while(allPackageResult.next()) {
                allPackages.add(allPackageResult.getInt(1));
            }
            return allPackages;
        }catch (SQLException ex) {
            Logger.getLogger(gs1702500_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getDrive(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int driveNextPackage(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
