package com.fasotec.retrofitexample.Views;

import android.Manifest;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.fasotec.retrofitexample.Api.StackOverflowAPI;
import com.fasotec.retrofitexample.Models.Question;
import com.fasotec.retrofitexample.Models.StackOverflowQuestions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by stephaneki on 17/02/2017 .
 *
 */
public class StackOverflowQuestionsListActivity extends ListActivity implements Callback<StackOverflowQuestions> {

    private final int INTERNET_REQUEST_CODE = 102;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // requestWindowFeature is for asking the system to include or exclude some of the
        // window feature like toolbar actionbar icons etc.
        // requestWindowFeature(Window.FEATURE_P)

        ArrayAdapter<Question> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<Question>());

        setListAdapter(arrayAdapter);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    INTERNET_REQUEST_CODE);
        }else{
            apiCall();
        }

    }

    private boolean hasInternetPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    public void apiCall(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.stackexchange.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Prepare call with Retrofit 2.0
        StackOverflowAPI stackOverflowAPI = retrofit.create(StackOverflowAPI.class);

        Call<StackOverflowQuestions> call = stackOverflowAPI.loadQuestions("android");

        //asynchronous call
        call.enqueue(this);

        //synchronous call would be with execute, in this case you would have to perform this outside
        // the main
        // call.execute()

        // to cancel a running request
        // call.cancel()
        // calls can only be used once but you can easily clone them
        // Call<StackOverflowQuestions> c = call.clone();
        // c.enqueue(this)
    }

    private void requestInternetPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        INTERNET_REQUEST_CODE);

                // INTERNET_REQUEST_CODE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case INTERNET_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // internet-related task you need to do.
                    apiCall();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"You need internet to view stack questions", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onResponse(Call<StackOverflowQuestions> call, Response<StackOverflowQuestions> response) {
        ArrayAdapter<Question> adapter = (ArrayAdapter<Question>) getListAdapter();
        adapter.clear();
        adapter.addAll(response.body().items);
    }

    @Override
    public void onFailure(Call<StackOverflowQuestions> call, Throwable t) {
        Toast.makeText(this,t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
