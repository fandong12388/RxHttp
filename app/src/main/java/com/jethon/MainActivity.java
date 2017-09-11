package com.jethon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jethon.http.RxHttp;
import com.jethon.http.core.request.HttpGsonRequest;
import com.jethon.http.core.request.RequestBuilder;
import com.jethon.http.core.response.HttpResponse;
import com.jethon.http.utils.GsonUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.jethon.RequestPool.gRequestPool;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String UA = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Mobile Safari/537.36";

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //1.初始化Volley
        RxHttp.init(getApplicationContext(), BuildConfig.DEBUG, new BaseParameterGenerator(), "");
        tv = (TextView) findViewById(R.id.tv);
        findViewById(R.id.btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //2.请求数据
        getWeather().subscribe(weather -> {
            try {
                String json = GsonUtil.toJson(weather);
                tv.setText(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, e -> {
            Log.e("Request", "发生了错误：" + e.getMessage());
        });
    }

    public Observable<Weather> getWeather() {
        //2.创建一个请求
        HttpGsonRequest<Weather> request = RequestBuilder.create(Weather.class)
                .url("http://www.weather.com.cn/data/sk/101010100.html")
                .header("User-Agent", UA)
                .build();
        //3.请求网络
        return gRequestPool.request(request)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }


}
