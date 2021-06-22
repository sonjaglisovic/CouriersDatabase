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
import java.util.HashMap;
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



public class gs170250_PackageDAO implements PackageOperations {
    
    private class gs170250d_Pair implements PackageOperations.Pair<Integer, BigDecimal> {

        Integer offerId;
        BigDecimal percentage;

        public gs170250d_Pair(Integer offerId, BigDecimal percentage) {
            this.offerId = offerId;
            this.percentage = percentage;
        }
        
        @Override
        public Integer getFirstParam() {
            return offerId;
        }

        @Override
        public BigDecimal getSecondParam() {
            return percentage;
        }
    
    }
    
    @Override
    public int insertPackage(int districtFrom, int districtTo, String userName, int packageType, BigDecimal weight) {
          
        if(!gs170250_Constants.codeToPackageType.containsKey(packageType)) {
              return gs170250_Constants.DATABASE_ERROR_CODE;
        }
         
         Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement checkUser = connection.prepareStatement("select idUser from [User] where UserName = ?");
                 PreparedStatement insertPackage = connection.prepareStatement("insert into Package (Type, idUser, Weight, DistrictFrom, DistrictTo, Price) "
                    + "values(?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                 ) {
            
            checkUser.setString(1, userName);
            ResultSet userId = checkUser.executeQuery();
            if(!userId.next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            
            insertPackage.setString(1, gs170250_Constants.codeToPackageType.get(packageType));
            insertPackage.setInt(2, userId.getInt(1));
            insertPackage.setBigDecimal(3, weight);
            insertPackage.setInt(4, districtFrom);
            insertPackage.setInt(5, districtTo);
            BigDecimal calculatedPrice = gs170250_Util.calculatePrice(districtFrom, districtTo, packageType, weight);
            if(calculatedPrice.intValue() != -1) {
                insertPackage.setBigDecimal(6, gs170250_Util.calculatePrice(districtFrom, districtTo, packageType, weight));
            } else {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            insertPackage.executeUpdate();
            ResultSet insertedKeys = insertPackage.getGeneratedKeys();
            if(insertedKeys.next()) {
                return insertedKeys.getInt(1);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gs170250_Constants.DATABASE_ERROR_CODE;
    }

    @Override
    public int insertTransportOffer(String userName, int packageId, BigDecimal pricePercentage) {
        
         Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement checkCourier = connection.prepareStatement("select idUser from Courier where idUser = (select "
                 + "idUser from [User] where UserName = ?) and Status = 'ne vozi'");
                 PreparedStatement checkPackage = connection.prepareStatement("select * from Package where idPackage = ? and Status = ?");
                 PreparedStatement insertTransportOffer = connection.prepareStatement("insert into TransportOffer (idUser, idPackage, OfferDetails) "
                    + "values(?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement checkOffer = connection.prepareStatement("select * from TransportOffer  where idUser = ? and idPackage = ?");
                 ) {
             
            checkCourier.setString(1, userName);
            ResultSet courierId = checkCourier.executeQuery();
            if(!courierId.next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            } 
            checkPackage.setInt(1, packageId);
            checkPackage.setString(2, "kreiran");
            if(!checkPackage.executeQuery().next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
          
            checkOffer.setInt(1, courierId.getInt(1));
            checkOffer.setInt(2, packageId);
            if(checkOffer.executeQuery().next()) {
                return gs170250_Constants.DATABASE_ERROR_CODE;
            }
            
            insertTransportOffer.setInt(1, courierId.getInt(1));
            insertTransportOffer.setInt(2, packageId);
            insertTransportOffer.setBigDecimal(3, pricePercentage);
            
            insertTransportOffer.executeUpdate();
            ResultSet insertedKeys = insertTransportOffer.getGeneratedKeys();
            if(insertedKeys.next()) {
                return insertedKeys.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gs170250_Constants.DATABASE_ERROR_CODE;
    }

    @Override
    public boolean acceptAnOffer(int offerId) {
        
         Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement checkOffer = connection.prepareStatement("select idPackage, idUser, OfferDetails from TransportOffer where idOffer = ?");
                 PreparedStatement getPackage = connection.prepareStatement("select Status  from Package where idPackage = ?");
                 PreparedStatement updatePackage = connection.prepareStatement("update Package set Accepted = ?, Status = ?, Courier = ?, CouriersIncome = ? "
                         + "where idPackage = ?")) {
             
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
            
            if(!packageToAccept.getString(1).equals(gs170250_Constants.codeToPackageStatus.get(0))) {
                return false;
            } 
            updatePackage.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            updatePackage.setString(2, gs170250_Constants.codeToPackageStatus.get(1));
            updatePackage.setInt(3, courierId);
            updatePackage.setBigDecimal(4, percentage);
            updatePackage.setInt(5, packageId);
           
            return updatePackage.executeUpdate() > 0 ? true : false;
        } catch (SQLException ex) {
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int packageId) {
       List<Pair<Integer, BigDecimal>> offersForPackages = new ArrayList<>();
       
       Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement getTransportOffers = connection.prepareStatement("select OfferDetails, idOffer from TransportOffer where idPackage = ?")){
           
            getTransportOffers.setInt(1, packageId);
            ResultSet offers = getTransportOffers.executeQuery();
            
            while(offers.next()) {
              offersForPackages.add(new gs170250d_Pair(offers.getInt(2), offers.getBigDecimal(1)));
            } 
            return offersForPackages;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
       
    }
    
    @Override
    public boolean deletePackage(int packageId) {
        
      Connection connection = DB.getInstance().getConnection();
         try (PreparedStatement deletePackageStatement = connection.prepareStatement("delete from Package "
                    + "where idPackage = ? ")) {
            
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
        try (PreparedStatement getPackage = connection.prepareStatement("select Status, Price, Type, Weight, DistrictFrom, DistrictTo from Package where idPackage = ?");
                PreparedStatement updatePackage = connection.prepareStatement("update Package set Weight = ?, Price = ? where "
                        + "idPackage = ?")){
           
            getPackage.setInt(1, packageId);
            ResultSet packageDetails = getPackage.executeQuery();
            if(!packageDetails.next() || !packageDetails.getString(1).equals(gs170250_Constants.codeToPackageStatus.get(0))) {
                return false;
            }
           
            String packageType = packageDetails.getString(3);
            Map.Entry<Integer, String> entry = gs170250_Constants.codeToPackageType.entrySet().stream().filter(map -> 
            packageType.equals(map.getValue())).findFirst().orElse(null);
            
            updatePackage.setBigDecimal(1, weight);
            updatePackage.setBigDecimal(2, gs170250_Util.calculatePrice(packageDetails.getInt(5), packageDetails.getInt(6), 
                  entry.getKey(), weight));
            updatePackage.setInt(3, packageId);
                 
            return updatePackage.executeUpdate() > 0 ? true : false;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean changeType(int packageId, int packageType) {
        
        if(!gs170250_Constants.codeToPackageType.containsKey(packageType)) {
            return false;
        }
        
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement getPackage = connection.prepareStatement("select Status, Price, "
                + "Type, Weight, DistrictTo, DistrictFrom from Package where idPackage = ?");
                PreparedStatement updatePackage = connection.prepareStatement("update Package set Type = ?, Price = ? "
                        + "where idPackage = ?")){
           
            getPackage.setInt(1, packageId);
            ResultSet packageDetails = getPackage.executeQuery();
            if(!packageDetails.next() || !packageDetails.getString(1).equals(gs170250_Constants.codeToPackageStatus.get(0))) {
                return false;
            }
            updatePackage.setString(1, gs170250_Constants.codeToPackageType.get(packageType));
            updatePackage.setBigDecimal(2, gs170250_Util.calculatePrice(packageDetails.getInt(5), packageDetails.getInt(6), 
                  packageType, packageDetails.getBigDecimal(4)));
            updatePackage.setInt(3, packageId);
            
            
            return updatePackage.executeUpdate() > 0 ? true : false;
            
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
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
              String packageStatus = status.getString(1);
              Map.Entry<Integer, String> statusEntry = gs170250_Constants.codeToPackageStatus.entrySet().stream().filter(map -> map.getValue().equals(packageStatus)).findFirst().orElse(null);
              return statusEntry.getKey();
            } 
            return deliveryStatus;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getDrive(String courierUserName) {
       List<Integer> allPackages = new ArrayList<>(); 
      
       Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement courierByUserName = connection.prepareStatement("select idUser from Courier where idUser = (select idUser from [User] where UserName = ?) and Status = 'vozi'");
                PreparedStatement getDrive = connection.prepareStatement("select idPackage from Package where Courier = ? and Status = ?")){
           
            
            courierByUserName.setString(1, courierUserName);
            ResultSet courierId = courierByUserName.executeQuery();
            
            if(!courierId.next()) {
                return null;
            }
            getDrive.setInt(1, courierId.getInt(1));
            getDrive.setString(2, gs170250_Constants.codeToPackageStatus.get(2));
            ResultSet packages = getDrive.executeQuery();
            while(packages.next()) {
              allPackages.add(packages.getInt(1));
            } 
            return allPackages;
        }catch (SQLException ex) {
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int driveNextPackage(String userName) {
        
        
        int idPackage;
        
        Connection connection = DB.getInstance().getConnection();
        try(PreparedStatement checkCourier = connection.prepareStatement("select idUser, RegNumber, Status, CurrentDriveProfit, CouriersCurrentPlace from Courier  where idUser = (select idUser from [User] where UserName = ?) ");
                PreparedStatement nextPackageForCourier = connection.prepareStatement("select TOP 1 idPackage, Price, DistrictFrom, DistrictTo, CouriersIncome, idUser Status from Package where "
                + "Courier = ? and Status = ? order by Accepted");
                PreparedStatement updatePackages = connection.prepareStatement("update Package set "
                        + "Status = ? where Status = ? and Courier = ?");
                PreparedStatement updatePackage = connection.prepareStatement("update Package set "
                        + "Status = ? where idPackage = ?");
                PreparedStatement getVehicle = connection.prepareStatement("select FuelType, FuelConsumption from Vehicle  "
                        + "where RegNumber = ?");
                PreparedStatement startDrive = connection.prepareStatement("update Courier set Status = 'vozi' where idUser = ?");
                PreparedStatement finishDrive = connection.prepareStatement("update Courier set "
                        + "Status = ?, Profit = Profit + ? where idUser = ?");
                 PreparedStatement updateUser = connection.prepareStatement("update [User] set "
                        + "NumOfSentPackages = NumOfSentPackages + 1 where idUser = ?");
                PreparedStatement updateCourier = connection.prepareStatement("update Courier set "
                        + "NumOfDeliveredPackages = NumOfDeliveredPackages + 1 where idUser = ?");
                PreparedStatement updateCpuriersCurrentParameters = connection.prepareStatement("update Courier set "
                        + "CurrentDriveProfit = ?, CouriersCurrentPlace = ? where idUser = ?");
                PreparedStatement setCourierParametersToNull = connection.prepareStatement("update Courier set "
                        + "CurrentDriveProfit = NULL, CouriersCurrentPlace = NULL where idUser = ?");) {
              
            checkCourier.setString(1, userName);
            ResultSet courierId = checkCourier.executeQuery();
            if(!courierId.next()) {
                return -2;
            }
            
            int courier = courierId.getInt(1);
            String regNumber = courierId.getString(2);
            String courierStatus = courierId.getString(3);
            double cuurentProfit = courierId.getDouble(4);
            int currentPlace = courierId.getInt(5);
            
            nextPackageForCourier.setInt(1, courier);
            double distance = 0.0;

            ResultSet nextPackage;
            int districtFrom;
            int districtTo;
            double price;
            double income;
            int senderId;
            
            if(courierStatus.equals("ne vozi")) {
               
                nextPackageForCourier.setString(2, gs170250_Constants.codeToPackageStatus.get(1));
                nextPackage = nextPackageForCourier.executeQuery();
                
                if(nextPackage.next()){
                
                    idPackage = nextPackage.getInt(1);
                    price = nextPackage.getDouble(2);
                    districtFrom = nextPackage.getInt(3);
                    districtTo = nextPackage.getInt(4);
                    income = nextPackage.getDouble(5);
                    senderId = nextPackage.getInt(6);
                
                } else {
                    return -1;
                }
                
                updatePackages.setString(1, gs170250_Constants.codeToPackageStatus.get(2));
                updatePackages.setString(2, gs170250_Constants.codeToPackageStatus.get(1));
                updatePackages.setInt(3, courier);
                
                if(updatePackages.executeUpdate() < 1) {
                    return -2;
                }
                
                startDrive.setInt(1, courier);
                startDrive.executeUpdate();
                
             } else {
                
                nextPackageForCourier.setString(2, gs170250_Constants.codeToPackageStatus.get(2));
                nextPackage = nextPackageForCourier.executeQuery();
                
                if(nextPackage.next()){
                
                    idPackage = nextPackage.getInt(1);
                    price = nextPackage.getDouble(2);
                    districtFrom = nextPackage.getInt(3);
                    districtTo = nextPackage.getInt(4);
                    income = nextPackage.getDouble(5);
                    senderId = nextPackage.getInt(6);
                
                } else {
                    return -1;
                }
                distance += gs170250_Util.calculateEuclidDistance(currentPlace, districtFrom);
             }
            
            distance += gs170250_Util.calculateEuclidDistance(districtFrom, districtTo);
            updatePackage.setString(1, gs170250_Constants.codeToPackageStatus.get(3));
            updatePackage.setInt(2, idPackage);
            
            if(updatePackage.executeUpdate() < 1) {
                return -2;
            } 
            getVehicle.setString(1, regNumber);
            ResultSet vehicleDetails = getVehicle.executeQuery();
            
            if(!vehicleDetails.next()) {
                return -2;
            }
            
            String fuelType = vehicleDetails.getString(1);
            double fuelConsumption = vehicleDetails.getDouble(2);
            
            double profit = cuurentProfit + 
                    price * (1 + income / 100)  - distance * fuelConsumption * gs170250_Constants.fuelTypeToPrice.get(fuelType);
            updateCpuriersCurrentParameters.setDouble(1, profit);
            
            updateCpuriersCurrentParameters.setDouble(2, districtTo);
            updateCpuriersCurrentParameters.setInt(3, courier);
            
            updateCpuriersCurrentParameters.executeUpdate();
            
            updateUser.setInt(1, senderId);
            updateCourier.setInt(1, courier);
            
            updateUser.executeUpdate();
            updateCourier.executeUpdate();
            
            nextPackageForCourier.setInt(1, courier);
            nextPackageForCourier.setString(2, gs170250_Constants.codeToPackageStatus.get(2));
            nextPackage = nextPackageForCourier.executeQuery();
            
            if(!nextPackage.next()) {
                
                finishDrive.setString(1, "ne vozi");
                finishDrive.setDouble(2, profit);
                finishDrive.setInt(3, courier);
                if(finishDrive.executeUpdate() < 1 ) {
                    return -2;
                }
                setCourierParametersToNull.setInt(1, courier);
                setCourierParametersToNull.executeUpdate();
            }
            
            return idPackage;
    }   catch (SQLException ex) {
            Logger.getLogger(gs170250_PackageDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -2;
    }

}