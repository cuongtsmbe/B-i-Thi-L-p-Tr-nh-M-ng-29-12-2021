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
//https://teamvietdev.com/ma-hoa-va-giai-ma-aes-trong-java/
public class AES {
    public static String MaHoaAES(String strToEncrypt,String keyEAS) {
          try {
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                byte[] key = keyEAS.getBytes("UTF-8");
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
        public static String GiaiMaAES(String strToDecrypt,String keyEAS) {
                try {
                      MessageDigest sha = MessageDigest.getInstance("SHA-1");
                      byte[] key = keyEAS.getBytes("UTF-8");
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
}
