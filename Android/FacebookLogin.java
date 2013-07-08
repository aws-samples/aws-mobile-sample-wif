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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

public class FacebookLogin extends AlertActivity {

    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private static final String LOG_TAG = "FB_LOGIN";
    private boolean loggingIn = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loggingIn = false;
        Log.d(LOG_TAG, "onCreate()");
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                Log.d(LOG_TAG, "attempting to restore session");
                loggingIn = true;
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                Log.d(LOG_TAG, "creating new session");
                session = new Session(this);
            }
            Session.setActiveSession(session);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume()");
        Session session = Session.getActiveSession();
        Log.d(LOG_TAG, "state " + session.getState());
        if (!loggingIn) {
            Log.d(LOG_TAG, "not in middle of login");
            loggingIn = true;
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
            else {
                Session.openActiveSession(this, true, statusCallback);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult()");
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState()");
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            Log.d(LOG_TAG, "callback " + state);
            if (loggingIn) { Log.d(LOG_TAG, "logging in"); }
            if (session.isOpened()) {
                Log.d(LOG_TAG, "session is open");
                try {
                    Login.clientManager.login(new FacebookIDP());
                    setResult(Activity.RESULT_OK, null);
                }
                catch (Exception e) {
                    setStackAndPost(e);
                    setResult(Activity.RESULT_CANCELED, null);
                }
                loggingIn = false;
                FacebookLogin.this.finish();
                
                //Change the following line based on your app's workflow
                startActivityForResult(new Intent(FacebookLogin.this, *YourActivity*.class), 1);
            }
            else if (state == SessionState.CLOSED_LOGIN_FAILED) {
                loggingIn = false;
                setStackAndPost(exception);
            }
        }
    }

    protected class FacebookIDP implements WIFIdentityProvider {

        @Override
        public String getToken() {
            return Session.getActiveSession().getAccessToken();
        }

        @Override
        public String getProviderID() {
            return "graph.facebook.com";
        }

        @Override
        public String getRoleARN() {
            return Login.clientManager.getFacebookRoleARN();
        }

        @Override
        public void logout() {
            Session.getActiveSession().closeAndClearTokenInformation();
        }

    }
}
