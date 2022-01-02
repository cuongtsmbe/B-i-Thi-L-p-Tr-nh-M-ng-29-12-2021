/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author NGOC BAU
 */
public class AES {
    static private String KeyAES;
    public static String GetKeyAES(){
        return AES.KeyAES;
    }
     public static void createKeyAES(){
         //tạo key AES 
         AES.KeyAES="CHAT"+String.valueOf((int)(Math.random()*(20)+10));
     }
        public static String MaHoaAES(String strToEncrypt) {
            try {
                    MessageDigest sha = MessageDigest.getInstance("SHA-1");
                    byte[] key = AES.KeyAES.getBytes("UTF-8");
                    key = sha.digest(key);
                    key = Arrays.copyOf(key, 16);
                    SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
              } catch (Exception e) {
                    System.out.println(e.toString());
              }
              return null;
         
        }
        public static String GiaiMaAES(String strToDecrypt) {
                  try {
                        MessageDigest sha = MessageDigest.getInstance("SHA-1");
                        byte[] key = AES.KeyAES.getBytes("UTF-8");
                        key = sha.digest(key);
                        key = Arrays.copyOf(key, 16);
                        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
                        Cipher cipher = Cipher.getInstance("AES");
                        cipher.init(Cipher.DECRYPT_MODE, secretKey);
                        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
                  } catch (Exception e) {
                        System.out.println(e.toString());
                  }
                  return null;
          }
        public static void main(String[] arg){
            System.out.println(AES.GiaiMaAES(AES.MaHoaAES("PhanHuuCuong")));
        }
}
