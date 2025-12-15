package com.example.sayandpray;

import java.util.List;

public class LiturgyResponse {
    String date;
    String season;
    List<Celebration> celebrations;

    public class Celebration {
        String title;
        String colour;
    }
}
