package com.saldobip.android.app;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import com.google.android.gms.ads.*;


public class MainActivity extends Activity {
	private final int DIALOG_SALDO = 1;
	private final byte[] KEY_B_SECTOR_0 = new byte[]{(byte)0x1F,(byte)0xC2,(byte)0x35,(byte)0xAC,(byte)0x13,(byte)0x09};
	private final byte[] KEY_B_SECTOR_8 = new byte[]{(byte)0x64,(byte)0xE3,(byte)0xC1,(byte)0x03,(byte)0x94,(byte)0xC2};
	private TapDialog tapDialog;

	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechList;

	public EditText txtUsername;
	public EditText txtPass;
	public Button  btnLogin;
	public Button  btnImageView1;
	private ProgressDialog pDialog;
	public Button button;
	public String URL = "http://m.saldobip.com";
	public Dialog dialog;

	public AdView adView;
	public AlertDialog.Builder alertDialogBuilder;
	public AlertDialog.Builder info;
	public java.util.Date date = null;

	// Your interstitial ad unit ID.
	private static final String AD_UNIT_ID = "ca-app-pub-1578973468341035/2334499206";

	private InterstitialAd interstitial;
	private boolean interstitialCanceled = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtUsername = (EditText)this.findViewById(R.id.txtUsername);
		SharedPreferences prefe=getSharedPreferences("datos",MainActivity.MODE_PRIVATE);
		txtUsername.setText(prefe.getString("tarjeta",""));
		
	


		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(AD_UNIT_ID);
		interstitial.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				// If the interstitial was canceled due to a timeout or an app being sent to the background,
				// don't show the interstitial.
				if (!interstitialCanceled) {
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							interstitial.show();
						}
					}, 15000);


				}
			}


		});
		interstitial.loadAd(new AdRequest.Builder().build());




		info = new AlertDialog.Builder(this); 
		btnLogin = (Button)this.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if( txtUsername.getText().toString().length() == 0 )
					txtUsername.setError( "Número de tarjeta es requerido!" );
				else {
				SharedPreferences preferencias=getSharedPreferences("datos",MainActivity.MODE_PRIVATE);
		        Editor editor=preferencias.edit();
		        editor.putString("tarjeta", txtUsername.getText().toString());
		        editor.commit();
				sendJson(txtUsername.getText().toString());
			}
			}
		});
		
	
	}


	
	private void showTapDialog(String message, int tag) {
		if(tapDialog == null) {
			tapDialog = new TapDialog(this);
			tapDialog.setCanceledOnTouchOutside(true);
		}
		tapDialog.setTitle(message);
		tapDialog.setTag(tag);
		tapDialog.show();
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		interstitialCanceled = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		hidePDialog();
	}



	protected void sendJson(final String email) {
		/*LayoutInflater inflater = getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.tap_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder.setView(dialoglayout);

		// set the title of the Alert Dialog
		alertDialogBuilder.setTitle("Respuesta:");

		// set dialog message
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("Cerrar",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int id) {
				// if no is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		//LinearLayout lay = (LinearLayout) dialoglayout.findViewById(R.id.info);
		//lay.setVisibility(View.VISIBLE);
		TextView tvNumeroBIP2 = (TextView) dialoglayout.findViewById(R.id.numero);
		TextView tvSaldoBIP2 = (TextView) dialoglayout.findViewById(R.id.saldobip);
		tvSaldoBIP2.setText("423432");
		tvNumeroBIP2.setText("645454343");


		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();*/
		/*
		  dialog = new Dialog(this);
	        dialog.setContentView(R.layout.tap_dialog);
	        dialog.setTitle("Title...");
	        LinearLayout lay = (LinearLayout) dialog.findViewById(R.id.info);
	    	lay.setVisibility(View.VISIBLE);
	        TextView tvNumeroUUID2 = (TextView) dialog.findViewById(R.id.tvNumeroUUID2);
	    	TextView tvNumeroBIP2 = (TextView) dialog.findViewById(R.id.tvNumeroBIP2);
	    	TextView tvSaldoBIP2 = (TextView) dialog.findViewById(R.id.tvSaldoBIP2);

	    	tvNumeroUUID2.setText("323232");
	    	tvNumeroBIP2.setText("64543");
	    	tvSaldoBIP2.setText("543");
	    	Button dialogButton = (Button) dialog.findViewById(R.id.ButtomOmitir);
	        // if button is clicked, close the custom dialog
	        dialogButton.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                dialog.dismiss();
	            }
	        });
	    	dialog.show();*/
		pDialog = new ProgressDialog(this);
		// Showing progress dialog before making http request
		pDialog.setMessage("Conectando con nuestros servidores...");

		pDialog.show();
		alertDialogBuilder = new AlertDialog.Builder(
				this);
		Thread t = new Thread() {

			public void run() {
				Looper.prepare(); //For Preparing Message Pool for the child Thread
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
				HttpResponse response;

				try {
					HttpPost post = new HttpPost(URL);


					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
					nameValuePairs.add(new BasicNameValuePair("tarjeta_a_consultar", email));
					nameValuePairs.add(new BasicNameValuePair("key_base", "qdq~YC6~X05Z&"));  
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 


					response = client.execute(post);


					if(response!=null){
						hidePDialog();
						String temp = EntityUtils.toString(response.getEntity());


						JSONObject myJson = null;
						try {
							myJson = new JSONObject(temp);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// use myJson as needed, for example 
						String sal = myJson.optString("saldo");
						String fec = "Fecha: "+myJson.optString("fecha"); 
						String tar = "Número Tarjeta: "+myJson.optString("tarjeta"); 
						String mensaje = "Ojo! En ocasiones el saldo está desactualizado, dado que el saldo de la tarjeta debe sincronizarse con los servidores de transantiago. Así que verifique la última actualización en la fecha mostrada.";
						LayoutInflater inflater = getLayoutInflater();
						View dialoglayout = inflater.inflate(R.layout.tap_dialog, null);

						alertDialogBuilder.setView(dialoglayout);

						// set the title of the Alert Dialog
						alertDialogBuilder.setTitle("Respuesta:");
						// set dialog message
						alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Cerrar",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								// if no is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});

						//LinearLayout lay = (LinearLayout) dialoglayout.findViewById(R.id.info);
						//lay.setVisibility(View.VISIBLE);
						TextView Mensaje = (TextView) dialoglayout.findViewById(R.id.mensaje);
						TextView tvNumeroBIP2 = (TextView) dialoglayout.findViewById(R.id.numero);
						TextView tvSaldoBIP2 = (TextView) dialoglayout.findViewById(R.id.saldobip);
						TextView tvFecha2 = (TextView) dialoglayout.findViewById(R.id.fecha);
						if(fec.equalsIgnoreCase("")) fec = "Vacío";
						if(sal.equalsIgnoreCase("")) {
						sal = "Vacío";
						tar = "Error al ingresar el número de tarjeta";
						tvNumeroBIP2.setTextColor(Color.RED);
						}
						Mensaje.setText(mensaje);
						tvSaldoBIP2.setText(sal);
						tvNumeroBIP2.setText(tar);
						tvFecha2.setText(fec);


						AlertDialog alertDialog = alertDialogBuilder.create();

						alertDialog.show();

						//Construimos el mensaje a mostrar


						Log.i("tag", temp);      }

				} catch(Exception e) {
					e.printStackTrace();

				}

				Looper.loop(); //Loop in the message queue
			}
		};

		t.start();   
	}

	private void hidePDialog() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

}
