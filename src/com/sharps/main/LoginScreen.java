package com.sharps.main;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class LoginScreen extends Activity implements LoginListener {
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	private EditText usernameField;
	private EditText passwordField;
	private CheckBox box;
	private ProgressDialog dialog;
	private AlertDialog ballarUr;
	private AccountManager accountManager;
	private SharedPreferences sp1=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mediator.setLoginListener(this);
		usernameField = (EditText) findViewById(R.id.editText1);
		passwordField = (EditText) findViewById(R.id.editText2);
		box=(CheckBox)findViewById(R.id.checkBox1);
		sp1=this.getSharedPreferences("Login",MODE_PRIVATE);
		if (sp1!=null&&sp1.getString("Unm", null)!=null&&sp1.getString("Psw", null)!=null) {
			usernameField.setText(sp1.getString("Unm", null));       
			passwordField.setText(sp1.getString("Psw", null));
			box.setChecked(true);
		}
		ballarUr=new AlertDialog.Builder(this).create();
	}

	public Context getContext() {
		return this;
	}
	public void doLogin(View view){
		if (mediator.gotInternet(getContext())) {
			dialog=ProgressDialog.show(LoginScreen.this, "", "Loggar in...");
			dialog.show();
			SharedPreferences.Editor Ed=sp1.edit();
			if (box.isChecked()) {
				Ed.putString("Unm",usernameField.getText().toString());              
				Ed.putString("Psw",passwordField.getText().toString());   
				Ed.commit();
			}else{
				Ed.clear();
				Ed.commit();
			}
			mediator.login(usernameField.getText().toString(),passwordField.getText().toString());
		}else{
			ballarUr.setMessage("Ingen internetanslutning");
			ballarUr.setButton("OK", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
			   }
			});
			ballarUr.show();
		}
	}
	@Override
	public synchronized void loginFinished(boolean status) {
		// TODO Auto-generated method stub
		if (status) {
			Intent myIntent = new Intent(LoginScreen.this,
					SpreadsheetView.class);
			finish();
			dialog.dismiss();
			LoginScreen.this.startActivity(myIntent);
		}else{
			
			dialog.dismiss();
			ballarUr.setMessage("Fel inloggningsuppgifter");
			ballarUr.setButton("OK", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
			   }
			});
			ballarUr.setIcon(R.drawable.ic_launcher);
			ballarUr.show();
		}
	}

}
