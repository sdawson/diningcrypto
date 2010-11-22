package crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import utility.ByteUtil;

import communication.DiningKey;
import communication.DiningKeySet;
import communication.Message;

/**
 * Wrapper for encryption methods.
 * 
 * @author joshuat
 *
 */
public class Encryption {
	public static KeyPair generateRSAKeys() {
		KeyPairGenerator kpg = null;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// If the algorithm doesn't exist something funny is going on.
			System.out.println("Failed to generate RSA keys. Exiting.");
			System.exit(1);
		}
		kpg.initialize(1024);
		return kpg.genKeyPair();
	}
	
	/************** ENCRYPTION **************/
	public static Message encrypt(Message m, Key k) {
		return new Message(encrypt(m.getMessage(), k));
	}
	
	public static DiningKeySet encrypt(DiningKeySet plainKS, Key k) {
		DiningKeySet cipherKS = new DiningKeySet();
		
		for (DiningKey dk : plainKS.getKeySet()) {
			cipherKS.addKey(encrypt(dk, k));
		}
		
		return cipherKS;
	}
	
	public static DiningKey encrypt(DiningKey dk, Key k) {
		return new DiningKey(encrypt(dk.getKey(), k), dk.getKeyOp());}
	
	public static int encrypt(int i, Key k) {
		return ByteUtil.bytesToInt(encrypt(ByteUtil.intToBytes(i), k));
	}
	
	public static String encrypt(String str, Key k) {
		return new String(encrypt(str.getBytes(), k));
	}
	
	public static byte[] encrypt(byte[] plainData, Key k) {
		byte[] cipherData = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, k);

			cipherData = cipher.doFinal(plainData);
		} catch (NoSuchPaddingException e) {
			System.out.println("Error during encryption.");
		} catch (BadPaddingException e) {
			System.out.println("Error during encryption.");
		} catch (IllegalBlockSizeException e) {
			System.out.println("Error during encryption.");
		} catch (InvalidKeyException e) {
			System.out.println("Error during encryption.");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error during encryption.");
		}
		
		return cipherData;
	}
	
	/************** DECRYPTION **************/
	public static Message decrypt(Message m, Key k) {
		return new Message(encrypt(m.getMessage(), k));
	}
	
	public static DiningKeySet decrypt(DiningKeySet cipherKS, Key k) {
		DiningKeySet plainKS = new DiningKeySet();
		
		for (DiningKey dk : cipherKS.getKeySet()) {
			plainKS.addKey(decrypt(dk, k));
		}
		
		return plainKS;
	}
	
	public static DiningKey decrypt(DiningKey dk, Key k) {
		return new DiningKey(decrypt(dk.getKey(), k), dk.getKeyOp());}
	
	public static int decrypt(int i, Key k) {
		return ByteUtil.bytesToInt(decrypt(ByteUtil.intToBytes(i), k));
	}
	
	public static String decrypt(String str, Key k) {
		return new String(decrypt(str.getBytes(), k));
	}
	
	public static byte[] decrypt(byte[] cipherData, Key k) {
		byte[] plainData = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, k);

			plainData = cipher.doFinal(cipherData);
		} catch (NoSuchPaddingException e) {
			System.out.println("Error during decryption.");
		} catch (BadPaddingException e) {
			System.out.println("Error during decryption.");
		} catch (IllegalBlockSizeException e) {
			System.out.println("Error during decryption.");
		} catch (InvalidKeyException e) {
			System.out.println("Error during decryption.");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error during decryption.");
		}
		
		return plainData;
	}
}
