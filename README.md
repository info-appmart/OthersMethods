決済意外の各メッソードの実装 
======================

【Appmartアプリ内課金サービス】の決済意外の各メッソードの実装です。

###　パーミッション追加

課金システムを使うには下記パーミッションが必要となります。

```
<uses-permission android:name="jp.app_mart.permissions.APPMART_BILLING" />
<uses-permission android:name="android.permission.INTERNET" />
```



### メッソード一覧：

 *  getServiceDetails　(サービスの詳細情報を取得)
 *  getPaymentDetails　(トランザクション情報を取得)
 *  getNextPaymentDetails　(次回支払情報を取得)
 *  stopContinuePayment　(継続課金の停止)
 *  hasAlreadyBought　(管理サービスの購入履歴有・無)
 

各メッソードの【引数】・【戻り値】 は この[READMEファイル](https://github.com/info-appmart/inBillingSampleOnePage/blob/master/README.md#%E3%83%AA%E3%83%95%E3%82%A1%E3%83%AC%E3%83%B3%E3%82%B9)をご参照ください。
 
