package crypto;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

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
	private final static int BLOCK_SIZE = 128, DATA_SIZE = 117;
	
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
	
	public static PrivateKey publicToPrivate(RSAPublicKey pub) {
		PrivateKey pri = null;
		try {
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(pub.getModulus(), pub.getPublicExponent());
			KeyFactory fact = KeyFactory.getInstance("RSA");
			pri = fact.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error generating key. Exiting");
			e.printStackTrace();
			System.exit(0);
		} catch (InvalidKeySpecException e) {
			System.out.println("Error generating key. Exiting");
			e.printStackTrace();
			System.exit(0);
		}
		
		return pri;
	}
	
	public static PublicKey privateToPublic(RSAPrivateKey pri) {
		PublicKey pub = null;
		try {
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(pri.getModulus(), pri.getPrivateExponent());
			KeyFactory fact = KeyFactory.getInstance("RSA");
			pub = fact.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error generating key. Exiting");
			e.printStackTrace();
			System.exit(0);
		} catch (InvalidKeySpecException e) {
			System.out.println("Error generating key. Exiting");
			e.printStackTrace();
			System.exit(0);
		}
		
		return pub;
	}
	
	private static byte[] dealWithBlocks(Cipher cipher, byte[] data, int blockSize) throws IllegalBlockSizeException, BadPaddingException {
		ArrayList<Byte> list = new ArrayList<Byte>();
		
		byte[] block = new byte[blockSize];
		for (int i=0 ; i<data.length ; i+=block.length) {
			int remaining = data.length - i;
			if (block.length < remaining) {
				System.arraycopy(data, i, block, 0, block.length);
			} else {
				block = new byte[remaining];
				System.arraycopy(data, i, block, 0, remaining);
			}
			
			
			for (byte b : cipher.doFinal(block)) {
				list.add(new Byte(b));
			}
		}
		return ByteUtil.arrayListToArray(list);
	}
	
	/************** ENCRYPTION **************/
	public static Message encrypt(Message m, PublicKey k) {
		return new Message(encrypt(m.getMessageAsBytes(), k));
	}
	
	public static DiningKeySet encrypt(DiningKeySet plainKS, PublicKey k) {
		DiningKeySet cipherKS = new DiningKeySet();
		
		for (DiningKey dk : plainKS.getKeySet()) {
			cipherKS.addKey(encrypt(dk, k));
		}
		
		return cipherKS;
	}
	
	public static DiningKey encrypt(DiningKey dk, PublicKey k) {
		return new DiningKey(encrypt(dk.getKeyAsBytes(), k), dk.getKeyOp());
	}
	
	public static byte[] encrypt(byte[] plainData, PublicKey k) {
		byte[] cipherData = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, k);
			
			if (plainData.length < DATA_SIZE) {
				cipherData = cipher.doFinal(plainData);
			} else {
				cipherData = dealWithBlocks(cipher, plainData, DATA_SIZE);
			}
		} catch (NoSuchPaddingException e) {
			System.out.println("Error during encryption.");
			e.printStackTrace();
		} catch (BadPaddingException e) {
			System.out.println("Error during encryption.");
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			System.out.println("Error during encryption.");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			System.out.println("Error during encryption.");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error during encryption.");
			e.printStackTrace();
		}
		
		return cipherData;
	}
	
	/************** DECRYPTION **************/
	public static Message decrypt(Message m, PrivateKey k) {
		return new Message(decrypt(m.getMessageAsBytes(), k));
	}
	
	public static DiningKeySet decrypt(DiningKeySet cipherKS, PrivateKey k) {
		DiningKeySet plainKS = new DiningKeySet();
		
		for (DiningKey dk : cipherKS.getKeySet()) {
			plainKS.addKey(decrypt(dk, k));
		}
		
		return plainKS;
	}
	
	public static DiningKey decrypt(DiningKey dk, PrivateKey k) {
		return new DiningKey(decrypt(dk.getKeyAsBytes(), k), dk.getKeyOp());
	}
	
	public static byte[] decrypt(byte[] cipherData, PrivateKey k) {
		byte[] plainData = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, k);

			if (cipherData.length < BLOCK_SIZE) {
				plainData = cipher.doFinal(cipherData);
			} else {
				plainData = dealWithBlocks(cipher, cipherData, BLOCK_SIZE);
			}
		} catch (NoSuchPaddingException e) {
			System.out.println("Error during decryption.");
			e.printStackTrace();
		} catch (BadPaddingException e) {
			System.out.println("Error during decryption.");
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			System.out.println("Error during decryption.");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			System.out.println("Error during decryption.");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error during decryption.");
			e.printStackTrace();
		}
		
		return plainData;
	}
}
