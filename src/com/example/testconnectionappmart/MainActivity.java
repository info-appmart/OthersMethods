package com.example.testconnectionappmart;

import jp.app_mart.service.AppmartInBillingInterface;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author canu
 */
public class MainActivity extends Activity implements OnClickListener {

	//AIDLインタフェース
	private AppmartInBillingInterface service;
	//各ボタン
	private Button payDetails, nextPayment,	stopContTrans, infoService, purchasingStatus;
	//結果VIEW
	private TextView result ;
	//コンテキスト
	private Context mContext;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext= getApplicationContext();

		init();

		//Appmartサービスとの接続
		Intent i = new Intent();
		i.setClassName(Utils.APPMART_BILLING_PACKAGE,Utils.APPMART_BILLING_INTERFACE);

		try {
			Boolean ret = bindService(i, mConnection, Context.BIND_AUTO_CREATE);
			if(!ret){
				Utils.error("appmartがインストールされていないようです。");
			}
		} catch (Exception e) {
			Utils.error("エラーが発生しました"+e.getMessage());
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		//serviceバインド
		unbindService(mConnection);
		mConnection = null;

	}

	/* widgets初期化 */
	private void init() {
		
		payDetails 	= (Button) findViewById(R.id.button2);
		payDetails.setOnClickListener(this);
		
		nextPayment = (Button) findViewById(R.id.button3);
		nextPayment.setOnClickListener(this);
		
		stopContTrans = (Button) findViewById(R.id.button4);
		stopContTrans.setOnClickListener(this);
		
		infoService	= (Button) findViewById(R.id.button5);
		infoService.setOnClickListener(this);
		
		purchasingStatus= (Button) findViewById(R.id.button6);
		purchasingStatus.setOnClickListener(this);
		
		result = (TextView) findViewById(R.id.result);
	}
	
	/* メッセージを更新 */
	private void updateMessage(String str){		
		if(result.getVisibility()== View.INVISIBLE)
			result.setVisibility(View.VISIBLE);			
		result.setText(str);		
	}


	/* ServiceConnectionクラス	 */
	private ServiceConnection mConnection = new ServiceConnection() {		
		public void onServiceConnected(ComponentName name, IBinder boundService) {
			service = AppmartInBillingInterface.Stub.asInterface((IBinder) boundService);
			Utils.debug(mContext, "appmartサービスの接続が成功しました");
		}
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
	};

	
	/* onclick処理 */
	@Override
	public void onClick(View arg0) {

		try {

			switch (arg0.getId()) {
			case R.id.button2: // 特定の決済情報を取得
				new Background().execute(Utils.ACTION_SETTLEMENT_CHECK);
				break;
			case R.id.button3: // 継続決済：次回支払日
				new Background().execute(Utils.ACTION_NEXT_PAYMENT);
				break;
			case R.id.button4: // 継続決済：停止
				new Background().execute(Utils.ACTION_STOP_CONTINUE_SETTLEMENT);
				break;				
			case R.id.button5: // アプリ情報取得
				new Background().execute(Utils.ACTION_GET_SERVICE_INFO);
				break;
			case R.id.button6: // 購入状態
				new Background().execute(Utils.ACTION_GET_PURCHASING_INFO);
				break;
			}

		} catch (Exception e) {
			Utils.error("エラーが発生しました。" + e.getMessage());
		}

	}


	/* 別threadでserviceのメッソードを呼び出す */
	class Background extends AsyncTask<Integer, Void, String> {
				
		protected String doInBackground(Integer... info) {

			String message = "";
		
			try {

				switch(info[0]){
				case Utils.ACTION_SETTLEMENT_CHECK:	//過去トランザクション情報を取得		
					message = service.getPaymentDetails(Utils.PARAMETER_TRANSACTION_ID, Utils.PARAMETER_SERVICE_ID, Utils.PARAMETER_DEVELOPPER_ID);
					break;
				case Utils.ACTION_NEXT_PAYMENT:	//次回支払情報を取得（継続決済のみ）
					message = service.getNextPaymentDetails(Utils.PARAMETER_NEXT_TRANSACTION_ID, Utils.PARAMETER_DEVELOPPER_ID, Utils.PARAMETER_SERVICE_ID);
					break;
				case Utils.ACTION_STOP_CONTINUE_SETTLEMENT:	//継続決済を停止
					message = service.stopContinuePayment(Utils.PARAMETER_TRANSACTION_ID, Utils.PARAMETER_DEVELOPPER_ID, Utils.PARAMETER_SERVICE_ID);
					break;
				case Utils.ACTION_GET_SERVICE_INFO:	//サービスの詳細情報を取得
					String dataEncrypted = Utils.createEncryptedData(
							Utils.PARAMETER_SERVICE_ID, Utils.PARAMETER_DEVELOPPER_ID,
							Utils.PARAMETER_LICENCE_KEY,Utils.PARAMETER_PUBLIC_KEY);
					message = service.getServiceDetails(Utils.PARAMETER_APPLI_ID, dataEncrypted);
					break;
				case Utils.ACTION_GET_PURCHASING_INFO: //管理サービスの購入履歴有・無
					message = String.valueOf(service.hasAlreadyBought(Utils.PARAMETER_DEVELOPPER_ID, Utils.PARAMETER_APPLI_ID, Utils.PARAMETER_SERVICE_ID));
				}
								
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Appmart", e.getMessage());
			}
			
			return message;
		}

	    @Override
	    protected void onPostExecute(String result) {
			updateMessage(result);			
		}
		
	}
}
