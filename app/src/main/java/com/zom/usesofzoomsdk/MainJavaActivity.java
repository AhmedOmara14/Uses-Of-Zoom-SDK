package com.zom.usesofzoomsdk;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import us.zoom.sdk.InviteOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParamsWithoutLogin;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

import static us.zoom.sdk.MeetingStatus.MEETING_STATUS_INMEETING;

public class MainJavaActivity extends AppCompatActivity {
    public String url, meetingId;

    private ZoomSDKAuthenticationListener authListener = new ZoomSDKAuthenticationListener() {
        @Override
        public void onZoomSDKLoginResult(long result) {
            if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(MainJavaActivity.this);

            }
        }

        @Override
        public void onZoomSDKLogoutResult(long l) {
        }

        @Override
        public void onZoomIdentityExpired() {
        }

        @Override
        public void onZoomAuthIdentityExpired() {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeSdk(this);
        initViews();
    }


    public void initializeSdk(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        // TODO: Do not use hard-coded values for your key/secret in your app in production!
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = "Izf7qItjKYK3m2bHtDWqmI9aOJZeBjGVMxzM"; // TODO: Retrieve your SDK key and enter it here
        params.appSecret = "xwEFwjdd04NsKEhz47hbKgb3SwuMaPokplQC"; // TODO: Retrieve your SDK secret and enter it here
        params.domain = "zoom.us";

        params.enableLog = true;


        // TODO: Add functionality to this listener (e.g. logs for debugging)
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {

            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
            }

            @Override
            public void onZoomAuthIdentityExpired() {
            }
        };
        sdk.initialize(context, listener, params);


    }

    private void initViews() {
        findViewById(R.id.join_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createJoinMeetingDialog();
            }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ZoomSDK.getInstance().isLoggedIn()) {
                    startMeeting(MainJavaActivity.this);

                } else {
                    createLoginDialog();
                }
            }
        });
    }


    public void joinMeeting(Context context, String meetingNumber) {
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = ""; // TODO: Enter your name
        params.meetingNo = meetingNumber;
        // params.password = password;


        meetingService.joinMeetingWithParams(context, params, options);

    }

    public void login(String username, String password) {
        int result = ZoomSDK.getInstance().loginWithZoom(username, password);
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
            ZoomSDK.getInstance().addAuthenticationListener(authListener);
        }
    }


    public void startMeeting(final Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        if (sdk.isLoggedIn()) {
            final MeetingService meetingService = sdk.getMeetingService();
            StartMeetingOptions options = new StartMeetingOptions();
            meetingService.startInstantMeeting(context, options);

            meetingService.addListener(new MeetingServiceListener() {
                @Override
                public void onMeetingStatusChanged(MeetingStatus meetingStatus, int i, int i1) {
                    Toast.makeText(context, "state : " + meetingStatus, Toast.LENGTH_SHORT).show();
                    switch (meetingStatus) {
                        case MEETING_STATUS_INMEETING:
                            // meetingId = String.valueOf(meetingService.getCurrentRtcMeetingNumber());
                            Toast.makeText(context, "id  : " + meetingService.getCurrentRtcMeetingNumber(), Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            });

        }
    }


    private void createJoinMeetingDialog() {
        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_join_meeting)
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog dialog = (AlertDialog) dialogInterface;
                        TextInputEditText numberInput = dialog.findViewById(R.id.meeting_no_input);
                        // TextInputEditText passwordInput = dialog.findViewById(R.id.password_input);
                        if (numberInput != null && numberInput.getText() != null) {
                            String meetingNumber = numberInput.getText().toString();
                            // String password = passwordInput.getText().toString();
                            if (meetingNumber.trim().length() > 0) {
                                joinMeeting(MainJavaActivity.this, meetingNumber);
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void createLoginDialog() {
        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_login)
                .setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog dialog = (AlertDialog) dialogInterface;
                        TextInputEditText emailInput = dialog.findViewById(R.id.email_input);
                        TextInputEditText passwordInput = dialog.findViewById(R.id.pw_input);
                        if (emailInput != null && emailInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                            String email = emailInput.getText().toString();
                            String password = passwordInput.getText().toString();
                            if (email.trim().length() > 0 && password.trim().length() > 0) {
                                login(email, password);
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
