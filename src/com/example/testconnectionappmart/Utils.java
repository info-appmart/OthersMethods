package com.example.testconnectionappmart;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

/**
 * @author canu
 */
public class Utils {

	public static final String APPMART_BILLING_PACKAGE = "jp.app_mart";
	public static final String APPMART_BILLING_INTERFACE = "jp.app_mart.service.AppmartInBillingService";

	final static boolean DEBUG = true;
	final static String DEBUG_LOG = "LOG";
	final static String SEP_SYMBOL = "&";
	
	final static int ACTION_SETTLEMENT 					=1;
	final static int ACTION_SETTLEMENT_CHECK 			=2;
	final static int ACTION_NEXT_PAYMENT 				=3;
	final static int ACTION_STOP_CONTINUE_SETTLEMENT 	=4;
	final static int ACTION_GET_SERVICE_INFO		 	=5;
	final static int ACTION_GET_PURCHASING_INFO		 	=6;
		
	final static String PARAMETER_DEVELOPPER_ID = "your_develper_id";
	final static String PARAMETER_APPLI_ID 		= "your_appli_id";
	final static String PARAMETER_SERVICE_ID	= "your_service_id";
	final static String PARAMETER_LICENCE_KEY 	= "your_licence_key";
	final static String PARAMETER_TRANSACTION_ID= "your_transaction_id";
	final static String PARAMETER_NEXT_TRANSACTION_ID= "your_ext_transction_id";
	final static String PARAMETER_PUBLIC_KEY 	= "your_public_key";
		
	/*
	 * debug用
	 */
	public static void debug(Context mContext, String str) {
		if (DEBUG)
		Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();		
		Log.d(DEBUG_LOG, str);
	}

	/*
	 * debug用
	 */
	public static void error(String str) {
		Log.e(DEBUG_LOG, str);
	}

		
	/* 
	 * 引数暗号化
	 */
	public static String createEncryptedData(String serviceId, String developId,
			String strLicenseKey, String strPublicKey) {

		final String SEP_SYMBOL = "&";
		StringBuilder infoDataSB = new StringBuilder();
		infoDataSB.append(serviceId).append(SEP_SYMBOL);

		// デベロッパID引数を追加
		infoDataSB.append(developId).append(SEP_SYMBOL);

		// ライセンスキー引数を追加
		infoDataSB.append(strLicenseKey);

		String strEncryInfoData = "";

		try {
			KeyFactory keyFac = KeyFactory.getInstance("RSA");
			KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(
					strPublicKey.getBytes(), Base64.DEFAULT));
			Key publicKey = keyFac.generatePublic(keySpec);

			if (publicKey != null) {
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);

				byte[] EncryInfoData = cipher.doFinal(infoDataSB.toString()
						.getBytes());
				strEncryInfoData = new String(Base64.encode(EncryInfoData,
						Base64.DEFAULT));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			strEncryInfoData = "";
		}

		return strEncryInfoData.replaceAll("(\\r|\\n)", "");

	}
	
}
