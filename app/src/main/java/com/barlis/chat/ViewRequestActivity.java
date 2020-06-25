package com.barlis.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.barlis.chat.Model.ERequestStatus;
import com.barlis.chat.Model.EResultCodes;
import com.barlis.chat.Model.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ViewRequestActivity extends AppCompatActivity {
    Button acceptBtn, backBtn, removeWorkerBtn, quitRequestBtn, closeRequest;
    TextView requestTitle, profession, requestDescription, qualifications, notes, requestStatus, workerName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*0.8), (int)(height*0.7));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        requestTitle = findViewById(R.id.request_title);
        profession = findViewById(R.id.needed_profession);
        requestDescription = findViewById(R.id.request_description);
        qualifications = findViewById(R.id.qualifications);
        notes = findViewById(R.id.notes);
        requestStatus = findViewById(R.id.request_status);
        workerName = findViewById(R.id.worker_name);
        acceptBtn = findViewById(R.id.accept_btn);
        backBtn = findViewById(R.id.back_btn);
        removeWorkerBtn = findViewById(R.id.remove_worker);
        quitRequestBtn = findViewById(R.id.quit_request);
        closeRequest = findViewById(R.id.close_request);

        Request request = (Request)getIntent().getSerializableExtra("request");
        requestTitle.setText(request.getRequestTitle());
        profession.setText(getResources().getString(R.string.required_profession) + ": " + request.getRequiredProfession());
        requestDescription.setText(getResources().getString(R.string.job_description) + ": " + request.getRequestDetails());
        qualifications.setText(getResources().getString(R.string.qualifications) + ": " + request.getQualifications());
        notes.setText(getResources().getString(R.string.notes) + ": " + request.getNotes());
        switch (request.getStatus()) {
            case REQUEST_AVAILABLE:
                requestStatus.setText(getResources().getString(R.string.status_available));
                break;
            case REQUEST_TAKEN:
                requestStatus.setText(getResources().getString(R.string.status_taken));
                break;
            case REQUEST_DONE:
                requestStatus.setText(getResources().getString(R.string.status_done));
                break;
        }

        if (request.getStatus() != ERequestStatus.REQUEST_AVAILABLE) {
            workerName.setVisibility(View.VISIBLE);
            workerName.setText(getResources().getString(R.string.taken_by) + " " + request.getWorkerName());
        }
        removeEmptyFields(request);

        if (firebaseUser.getUid().equals(request.getCreatorId()) || !request.getStatus().equals(ERequestStatus.REQUEST_AVAILABLE)) {
            acceptBtn.setEnabled(false);
        }

        if (request.getStatus().equals(ERequestStatus.REQUEST_TAKEN)) {
            if (request.getCreatorId().equals(firebaseUser.getUid())) {
                acceptBtn.setVisibility(View.GONE);
                removeWorkerBtn.setVisibility(View.VISIBLE);
                closeRequest.setVisibility(View.VISIBLE);
                removeWorkerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("request_position", getIntent().getIntExtra("request_position", 0 ));
                        intent.putExtra("workerId", request.getWorkerId());
                        setResult(EResultCodes.REMOVE_WORKER.getValue(), intent);
                        finish();
                    }
                });
                closeRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("request_position", getIntent().getIntExtra("request_position", 0 ));
                        intent.putExtra("workerId", request.getWorkerId());
                        setResult(EResultCodes.CLOSE_REQUEST.getValue(), intent);
                        finish();
                    }
                });
            }
            else if (request.getWorkerId().equals(firebaseUser.getUid())) {
                acceptBtn.setVisibility(View.GONE);
                quitRequestBtn.setVisibility(View.VISIBLE);
                quitRequestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("request_position", getIntent().getIntExtra("request_position", 0 ));
                        intent.putExtra("creatorId", request.getCreatorId());
                        setResult(EResultCodes.QUIT_REQUEST.getValue(), intent);
                        finish();
                    }
                });
            }
        }

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("request_position", getIntent().getIntExtra("request_position", 0 ));
                intent.putExtra("creatorId", request.getCreatorId());
                setResult(EResultCodes.UPDATE_REQUEST_WORKER.getValue(), intent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void removeEmptyFields(Request request) {
        if (request.getQualifications() == null) {
            qualifications.setVisibility(View.GONE);
        }
        if (request.getNotes() == null) {
            notes.setVisibility(View.GONE);
        }
    }
}
