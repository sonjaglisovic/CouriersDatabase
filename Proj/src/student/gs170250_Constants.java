/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author User
 */
public class gs170250_Constants {
    
    public static final int DATABASE_ERROR_CODE = -1;
    public static final int ALREADY_ADMIN = 1;
    public static final int USER_DOES_NOT_EXIST = 2;
    public static final int CODE_SUCCESS = 0;
    public static final Map<Integer, String> codeToStatus = Map.of(0, "ne vozi", 1, "vozi");
    public static final Map<String, Integer> fuelTypeToPrice = Map.of("plin", 15, "dizel", 32, "benzin", 36);
    public static final Map<Integer, String> codeToFuelType = Map.of(0, "plin", 1, "dizel", 2, "benzin");
    public static final Map<Integer, String> codeToPackageType = Map.of(0, "pismo", 1, "standardno", 2, "lomljivo");
    public static final Map<Integer, String> codeToPackageStatus = Map.of(0, "kreiran", 1, "zahtev prihvacen", 
    2, "pokupljen", 3, "isporucen");
    public static final List<Integer> price = Arrays.asList(10, 25, 75);
    public static final List<Integer> factor = Arrays.asList(0, 1, 2);
    public static final List<Integer> priceByKg = Arrays.asList(null, 100, 300);
}
