package com.example.sayandpray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface LiturgyApiService {
    @GET("api/v0/en/calendars/general-en/{year}/{month}/{day}")
    Call<LiturgyResponse> getLiturgyDay(
            @Path("year") int year,
            @Path("month") int month,
            @Path("day") int day
    );
}
