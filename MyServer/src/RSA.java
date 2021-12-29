/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

//https://viblo.asia/p/java-ma-hoa-va-giai-ma-voi-thuat-toan-rsa-bJzKmW3Xl9N
public class RSA {
    public static String pathKeyPublic="./publicKey.rsa";
    public static  String pathKeyPrivate="./privateKey.rsa";
    public PublicKey PublicKey=null;
    public PrivateKey PrivateKey=null;
	public static void createRSA() {
            
		try {
			SecureRandom sr = new SecureRandom();
			// Thuật toán phát sinh khóa - RSA
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024, sr);//khởi tạo độ dài của khóa

			// Khởi tạo cặp khóa
			KeyPair kp = kpg.genKeyPair();
			// PublicKey
			PublicKey publicKey = kp.getPublic();
			// PrivateKey
			PrivateKey privateKey = kp.getPrivate();

			File publicKeyFile = saveKey(new File(RSA.pathKeyPublic));
			File privateKeyFile = saveKey(new File(RSA.pathKeyPrivate));

			// Lưu Public Key
			FileOutputStream fos = new FileOutputStream(publicKeyFile);
                     
                        String Base64PublicKeyString=Base64.getEncoder().encodeToString(publicKey.getEncoded());
                       
			fos.write(Base64PublicKeyString.getBytes());
			fos.close();
			// Lưu Private Key
			fos = new FileOutputStream(privateKeyFile);
                        String Base64PrivateKeyString=Base64.getEncoder().encodeToString(privateKey.getEncoded());
			fos.write(Base64PrivateKeyString.getBytes());
			fos.close();

			System.out.println("Created Key successfully");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
        
        public static String GetKeyPublicBase64() throws IOException{
            //Đọc key public trong file trả về string
              FileInputStream fis = null;
                int i = 0;
                String keyPublic="";
                try {
                   // create new file input stream
                   fis = new FileInputStream(RSA.pathKeyPublic);

                   // read till the end of the stream
                   while((i = fis.read())!=-1) {
                      // convert integer to character
                      char c = (char)i;
                      keyPublic=keyPublic+c;     
                   }
                } catch(Exception ex) {
                   ex.printStackTrace();
                } finally {
                   // releases all system resources from the streams
                   if(fis!=null) {
                      fis.close();
                   }
                }
                
                return keyPublic;
        }
         public static String GetKeyPrivateBase64() throws IOException{
            //Đọc key public trong file trả về string
              FileInputStream fis = null;
                int i = 0;
                String keyPrivate="";
                try {
                   // create new file input stream
                   fis = new FileInputStream(RSA.pathKeyPrivate);

                   // read till the end of the stream
                   while((i = fis.read())!=-1) {
                      // convert integer to character
                      char c = (char)i;
                      keyPrivate=keyPrivate+c;     
                   }
                } catch(Exception ex) {
                   ex.printStackTrace();
                } finally {
                   // releases all system resources from the streams
                   if(fis!=null) {
                      fis.close();
                   }
                }
                
                return keyPrivate;
        }

         public static String GiaiMaDuLieu(String code) {
                   try{
                        // Lấy private key RSA
			String keyBase64private=RSA.GetKeyPrivateBase64();
			// Tạo private key
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyBase64private.getBytes()));
			KeyFactory factory = KeyFactory.getInstance("RSA");
			PrivateKey priKey = factory.generatePrivate(spec);

			// Giải mã dữ liệu
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.DECRYPT_MODE, priKey);
			byte decryptOut[] = c.doFinal(Base64.getDecoder().decode(code));
		//	System.out.println("Dữ liệu sau khi giải mã: " + new String(decryptOut));
                        return new String(decryptOut);
                } catch (Exception ex) {
			System.out.println(ex);
                        return null;
		}
        }
       
        
	private static File saveKey(File file) {
            try{
		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
			file.createNewFile();
		}
            }catch(Exception e){
                System.out.println(e);
            }
		return file;
	}
//       public static void main(String[] args){
//        //   RSA.createRSA();
//       }
}
