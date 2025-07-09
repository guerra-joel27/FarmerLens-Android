package com.cse3310.farmerlens;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    //public static androidx.test.core.app.ActivityScenario$$ExternalSyntheticLambda0 getInstance() {
    //}
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.plant.id/v2/")  // Replace with the actual Plant.id base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static <T> T create(Class<T> service) {
        return getInstance().create(service);
    }

}
