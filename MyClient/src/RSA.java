/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
/**
 *
 * @author NGOC BAU
 */
public class RSA {
    public static String KeyPublicRSAServer;
      
       public static String MaHoaDuLieu(String str) {
            try {                      
                      byte[] b=Base64.getDecoder().decode(RSA.KeyPublicRSAServer.getBytes());
                       
			X509EncodedKeySpec spec = new X509EncodedKeySpec(b);
			KeyFactory factory = KeyFactory.getInstance("RSA");
			PublicKey pubKey = factory.generatePublic(spec);

			// Mã hoá dữ liệu
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.ENCRYPT_MODE, pubKey);
			
			byte encryptOut[] = c.doFinal(str.getBytes());
			String strEncrypt = Base64.getEncoder().encodeToString(encryptOut);
			
                        return strEncrypt;
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
			System.out.println("Ma hoa du lieu (error) "+ex);
                          return null;
		}
          
        }
}
