/*
 * Copyright 2010-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.demo.s3uploader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class Login extends AlertActivity {

	protected Button fbButton;
	protected TextView introText;

	private static final String LOG_TAG = "Login";
	
	public static AmazonClientManager clientManager = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_menu);
		Log.i(LOG_TAG, "Started login activity");

		introText = (TextView) findViewById(R.id.login_intro_text);
		introText.setText( "Login" );

		fbButton = (Button) findViewById(R.id.fb_login_button);
		wireButtons();
		
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			clientManager = new AmazonClientManager( getSharedPreferences( "com.amazon.aws.demo.AWSDemo", Context.MODE_PRIVATE ), bundle);
		} catch (NameNotFoundException e) {
			displayErrorAndExit("Unable to load application bundle: " + e.getMessage());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Login.this.finish();
		}
	}



	public void wireButtons() {
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivityForResult(new Intent(Login.this, FacebookLogin.class), 1);}
        });
        
	}
	protected void displayErrorAndExit( String msg ) {
        AlertDialog.Builder confirm = new AlertDialog.Builder( this );
        if ( msg == null ) { 
            confirm.setTitle("Error Code Unknown" );
            confirm.setMessage( "Please review the README file." );
        } 
        else {
            confirm.setTitle( "Error" );
            confirm.setMessage( msg + "\nPlease review the README file."  );
        }

        confirm.setNegativeButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int which ) {
            	Login.this.finish();
            }
        } );
        confirm.show();                
    }
}
