package com.sharps.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class LoginScreen extends Activity implements LoginListener {
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	private EditText usernameField;
	private EditText passwordField;
	private Button button;
	private ProgressDialog dialog;
	private AlertDialog ballarUr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mediator.setLoginListener(this);
		usernameField = (EditText) findViewById(R.id.editText1);
		passwordField = (EditText) findViewById(R.id.editText2);
		button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(myClickHandler_Login);
		ballarUr=new AlertDialog.Builder(this).create();
		mediator.context=this.getBaseContext();
	}

	private OnClickListener myClickHandler_Login = new OnClickListener() {

		public void onClick(View v) {
			if (mediator.gotInternet()) {
				dialog=ProgressDialog.show(LoginScreen.this, "Logging in", "Loading...");
				dialog.show();
				mediator.login(usernameField.getText().toString(),passwordField.getText().toString());
			}else{
				ballarUr.setTitle("Fail");
				ballarUr.setMessage("Inloggningen misslyckades");
				ballarUr.setButton("OK", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {
				      // here you can add functions
				   }
				});
				ballarUr.setIcon(R.drawable.ic_launcher);
				ballarUr.show();
			}
			
		}

	};
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
			
			ballarUr.setTitle("Fail");
			ballarUr.setMessage("Inloggningen misslyckades");
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
