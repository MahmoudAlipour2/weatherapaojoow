package com.example.ojoow;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WeatherFragment extends Fragment {

    TextView tv_city, tv_weatherIcon, tv_temp, tv_details;
    // public static final String API_KEY = "&APPID=d8848fb2a6d94ea1309cf0b74c498fb0";
    //  public static final String URL_FORMAT = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric" + API_KEY;
    public static final String URL_FORMAT = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&APPID=d8848fb2a6d94ea1309cf0b74c498fb0";
    public static final String TAG = WeatherFragment.class.getSimpleName();

    private String city = "";

    public static WeatherFragment newInstance(String city) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString("city", city);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.city = getArguments().getString("city");
        if (city == null) {
            city = "Tehran";
        }
        requestData(city);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_card, container, false);
        tv_city = view.findViewById(R.id.city);
        tv_weatherIcon = view.findViewById(R.id.weather_icon);
        tv_temp = view.findViewById(R.id.temp);
        tv_details = view.findViewById(R.id.details);


        return view;
    }

/*    private void setData() {

        Typeface wFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/weathericons.ttf");
        weatherIcon.setTypeface(wFont);
        weatherIcon.setText(Html.fromHtml("&#xf00d;", 0));
    }*/


    private void requestData(String cityname) {
        String url = String.format(URL_FORMAT, cityname);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i(TAG, "response: \n" + response);
                try {
                    tv_city.setText(
                            response.getString("name").toUpperCase() + " , " +
                                    response.getJSONObject("sys").getString("country")
                    );


                    double temperature = response.getJSONObject("main").getDouble("temp");
                    tv_temp.setText(String.format(Locale.US, "%.0f %s", temperature, Html.fromHtml("&#8451;")));

                    JSONObject details = response.getJSONArray("weather").getJSONObject(0);
                    tv_details.setText(details.getString("description"));


                    JSONObject sys = response.getJSONObject("sys");
                    long sunrise = sys.getLong("sunrise");
                    long sunset = sys.getLong("sunset");
                    int weatherId = details.getInt("id");

                    setweatherIcon(weatherId, sunrise, sunset);

                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "error: " + error.getMessage());
            }

        });

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);


    }

    private void setweatherIcon(int id, long sunrise, long sunset) {

        String weather_icon_code = "";
        if (id == 800) { //clear

            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                weather_icon_code = getResources().getString(R.string.wi_forecast_io_clear_day_800);
            } else {
                weather_icon_code = getResources().getString(R.string.wi_night_clear);
            }
        } else {
            id = id / 100;

            switch (id) {
                case 2:
                    weather_icon_code = getResources().getString(R.string.wi_day_thunderstorm_2xx);
                    break;
                case 3:
                    weather_icon_code = getResources().getString(R.string.wi_day_rain_3xx);
                    break;
                case 5:
                    weather_icon_code = getResources().getString(R.string.wi_rain_mix_5xx);
                    break;
                case 6:
                    weather_icon_code = getResources().getString(R.string.wi_snow_6xx);
                    break;
                case 7:
                    weather_icon_code = getResources().getString(R.string.wi_fog_7xx);
                    break;
                case 8:
                    weather_icon_code = getResources().getString(R.string.wi_cloud_up_80x);
                    break;
                default:
                    break;
            }


        }
        Typeface wFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/weathericons.ttf");
        tv_weatherIcon.setTypeface(wFont);
        tv_weatherIcon.setText(Html.fromHtml(weather_icon_code,0));

    }


}
