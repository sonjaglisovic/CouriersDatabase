/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.util.HashMap;
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
    public static final Map<Integer, String> codeToFuelType = Map.of(0, "plin", 1, "dizel", 2, "benzin");
}