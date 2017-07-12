package com.huaqie.wubingjie.aliPay;

import android.app.Activity;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AliPay {
	// 商户PID
	public static final String PARTNER = "2088611801165924";
	// 商户收款账号
	public static final String SELLER = "ainanatrip@163.com";
	// 商户私钥，pkcs8格式
//	public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAKFFKIqEPa2wU/3OLB9l3XObHBDuODyc6rb5fEXl+qv+DEWE3gSb/CfT7Cqh85W6izBW7xyw5+dDEyCtIoh4Mnp7WhxZY8Ih9/XXBsJtdp+lqiiH8AC90mrpwbLjgsn7J3H52XSDW0aFCj68/5YQ6J+j5RAdUq4E1cyOLuVfo6wBAgMBAAECgYEAhnFsMcW60ZB9BMqbF/XI3vaBOFAQC9/ZAH8OLgtipX2d51qz5+3OEbnnWUc4FSu1m6/tmOKWAcJ2MLiFYHWrxgLt9ceCswCZmaFccHS+G/RiG/OlXye/JehdO1cV1X8sqG+rMGmoRzrTjWpTEuQB0ZkyBjQBm0o4eSS8ULuX6K0CQQDUik7b9Ls1O+jpr+RuBK5HnUhummv1SQHTDfPF0oxPD1T6HtVIFbSFFE8RQApVxaE14Hb5U2IJ0AqPTrNZM5ZvAkEAwj8M8NMiJGlF25qVvV2TZjP4hr9H4wEoiqSam9DSdvjsO+PC9KC3Rz/B4iVvH/HnfSGEIBhSSJb2z6k9ccmcjwJAQv3pR1lHXMsqaeYgGTFugXIo73vLBYBii9S8/qCTyyOpBEGNtm/o+31gQq6t00U8wA+lUIcgwrkJawxCaZ5iDwJBAIcQEi5tkCLjNBjDPvUB2NjKsDb5T8HO72Kfm4VWSNFCuffWoZs5GNcoxOY7Ay6iJETEhjtOzf/9QDYaRkgVWI8CQQDHrFZIKz59nyZlkVEce6IaubQuy3E9bwcxmY/+2xWGYxicr6auGaewuzJJFVzLarZ2d2D2fXW3TXJhJPVziwNH";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdpzcVlEkbugXn2wu9iX6btjgp713oE2y3FgFWQaltKmCEmDJFAAzRzDnpEqgHwdY3jHkVtaZC5ulXxb3nYhEl2coQjnUByw764ud/MsB2xIE4BPr8Mcw781zVVCWaaV3lfsAQ1057GOVzlyXh5zwiIjHKNSqrrOZuLZjxcssQYQIDAQAB";

	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE ="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALWIWrVfzyW0kxwjQIicVIJiTT5jBDB3NBM1CZbVC4uLfJ8uAdV6Mu/9LP8pWYm3rIHLaVXOZ1+plq1+YJFnZ7UWZ3rpKPU4M3X5nEHnyo3J1gj0tpj9gDh0vAJfpmOnk7byDGvaBlhJ1KXCY2N+9IbBrJYGa04TVgkZ+rhpif/bAgMBAAECgYA7NnPoD41hDiqiFfmZ1exW7zuk6uVV7bwFu9oZ4fo3FVOcFISynn54+4su280B9/CEEdaVD5N2h7nHc56KL0GLKcO/ROWdq9RCFLSY4y+c4/tjXw5C+eIl9CDMeTCtO1WT8K4EOYEP7/CM4qCYWeN72M2xlJg6Hd/SJ3AMV3OKmQJBAOvnsB/lmxDzTii6jBG2472wPJHf2oPsnLxiX/5PFI0sbgHANWe5LOYWKJpSJxoU9o6bNg762+KXZJb5vU8xnZUCQQDE/vu/CZNa2pSdGoDAYhpPfpSWRBeCy9XMRLGmLKKhzPQZ6b1IxFsyHKFbNXlX7gVXG4yn1ynMIJ0eqKb+OmuvAkAJ/vpGj7sfX7/rF+SBh6dkzUfzwnGG74rArMZ2hzFOvhg3OMbKi10srrmvuFVx1HuSFV+YCOcyVcnsX862wVp1AkBkkUuRxmCspCP0ZCBQloSaCqPuZoqeIlNeOb97dxP7h/ch93rDWEK6PXV+a7kr0350bsMKuERFSm0Lxsuhw89PAkEAjX4RkuzCdwBiCqPSOLufXcWuMoRd8INv8jd0rSJ7Q0PgE/ULAG3sLdECYiiHDJNESbX2ImT4Oq/bxBOIDtyITA==";
	// 支付宝公钥
//	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDciojZ6mwGHWjuMlT3YQYUk8+wQTQ2v7Yy3H0t2swXL4c6IxAF9bysmNvt+smV4s3sc+zR/lhv2ZNvDrgkkjgggmgIuNUYkTV8xHUBGgghpnwxskiIR+tJqYn2XAUFgmg3FOy1Ko2cxiwRBSHpekEf6vbkeNsoJ1eO1N1vFQ1zqwIDAQAB";
//	public static final String RSA_PUBLIC ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	public static final String TAG = "AliPay";
	private Activity mActivity;

	public AliPay(Activity activity) {
		mActivity = activity;
	}

	public void pay(String name, String detail, String price, String tradeId) {
		String orderInfo = getOrderInfo(name, detail, price, tradeId);// PRICE
		Log.i(TAG, orderInfo);
		System.out.println("orderInfo :::::::" + orderInfo);
		// 对订单做RSA 签名
		// String sign1 = sign(orderInfo);
		String sign = sign1(orderInfo, RSA_PRIVATE);
		Log.i(TAG, sign + "");
		System.out.print("sign :::::::" + sign);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
		Log.i(TAG, payInfo);
		Runnable payRunnable = new Runnable() {
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(null);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo , true);

				// Message msg = new Message();
				// msg.what = SDK_PAY_FLAG;
				// msg.obj = result;
				// nHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	// break;
	// case R.id.pay_back:
	// finish();
	// break;
	// }
	//
	// }
	// };

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	// public void check(View v) {
	// Runnable checkRunnable = new Runnable() {
	// public void run() {
	// // 构造PayTask 对象
	// PayTask payTask = new PayTask(PayAlipayActivity.this);
	// // 调用查询接口，获取查询结果
	// boolean isExist = payTask.checkAccountIfExist();
	//
	// Message msg = new Message();
	// msg.what = SDK_CHECK_FLAG;
	// msg.obj = isExist;
	// nHandler.sendMessage(msg);
	// }
	// };
	// Thread checkThread = new Thread(checkRunnable);
	// checkThread.start();
	//
	// }

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		// PayTask payTask = new PayTask(this);
		// String version = payTask.getVersion();
		// Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price, String tradeId) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + tradeId + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://115.28.6.120:8080/Api/Pay/alipayNotify" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGate way\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		System.out.println(RSA_PRIVATE);
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	public String sign1(String content, String privateKey) {
		try {
			byte[] keyBytes = Base64.decode(RSA_PRIVATE);
			Log.i(TAG, keyBytes.toString());
			System.out.println(keyBytes);
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(keyBytes);
			System.out.print(priPKCS8);
			// KeyFactory keyf = KeyFactory.getInstance("RSA");
			KeyFactory keyf = KeyFactory.getInstance("RSA"); // , "BC"
			System.out.print(keyf);
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
			System.out.print(priKey);

			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

			signature.initSign(priKey);
			signature.update(content.getBytes("UTF-8"));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
