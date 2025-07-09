package com.cse3310.farmerlens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import com.cse3310.farmerlens.PlantIdResponse;
import java.io.IOException;
import android.util.Log;
import java.lang.Throwable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import java.io.ByteArrayOutputStream;
import android.graphics.Bitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;




public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 100; // Placeholder constant for request code
    private PlantIdResponse plantIdResponse;

    FirebaseAuth auth;
    TextView textView;
    Button button;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.user_details);
        button = findViewById(R.id.logout);
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchOptions();
            }
        });
    }

    public class Constants {
        public static final String PLANT_ID_API_KEY = "Farmer Lens";
    }

    public void showSearchOptions() {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.searchButton));
        popupMenu.getMenu().add("Take Picture");
        popupMenu.getMenu().add("Pick from Gallery");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "Take Picture":
                        startCameraIntent();
                        return true;
                    case "Pick from Gallery":
                        startGalleryIntent();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    private void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void startGalleryIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
    }

    public interface PlantIdService {
        @POST("identify")
        Call<PlantIdResponse> identifyPlant(@Header("Api-Key") String apiKey, @Body RequestBody image);
    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = (Bitmap) extras.get("data");
                processImage(bitmap);
            } else {
                // Handle the case where extras is null
                Log.e("Extras Error", "Intent extras are null");
            }
        }
    }

    private void processImage(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageData = stream.toByteArray();

            PlantIdService plantIdService = RetrofitClient.getInstance().create(PlantIdService.class);
            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageData);

            Call<PlantIdResponse> call = plantIdService.identifyPlant(Constants.PLANT_ID_API_KEY, imageRequestBody);
            call.enqueue(new Callback<PlantIdResponse>() {
                @Override
                public void onResponse(Call<PlantIdResponse> call, Response<PlantIdResponse> response) {
                    // Handle identification response
                    if (response.isSuccessful()) {
                        PlantIdResponse plantIdResponse = response.body();
                        // Process the identification result
                    } else {
                        int statusCode = response.code();
                        Log.e("HTTP Error", "Unsuccessful response. Status code: " + statusCode);

                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                            Log.e("Error Body", "Error body: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (statusCode == 404) {
                            Log.e("HTTP Error", "404 Not Found");
                        } else if (statusCode == 500) {
                            Log.e("HTTP Error", "500 Internal Server Error");
                        } else {
                            Log.e("HTTP Error", "Unexpected HTTP error code: " + statusCode);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PlantIdResponse> call, Throwable t) {
                    // Handle failure
                    if (t instanceof IOException) {
                        Log.e("Network Error", "IOException: " + t.getMessage());
                    } else {
                        Log.e("Conversion Error", "Error converting response: " + t.getMessage());
                    }
                }
            });
        } else {
            // Handle the case where the captured bitmap is null
            Log.e("Bitmap Error", "Captured bitmap is null");
        }
    }
    private void showSearchResults() {
        // Assuming plantIdResponse is the result you want to pass
        Intent intent = new Intent(MainActivity.this, SearchResults.class);
        CharSequence plantIdResponseText = "Result: " + plantIdResponse.toString(); // Modify this line based on your actual result
        intent.putExtra("PLANT_ID_RESPONSE", plantIdResponseText);
        startActivity(intent);
    }

}