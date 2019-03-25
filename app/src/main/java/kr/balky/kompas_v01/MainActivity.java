package kr.balky.kompas_v01;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "*** 디버깅 체크포인트 ***";

    IntentIntegrator mIntegrator = new IntentIntegrator(this);
    RequestQueue mQueue;
    TextView mTextMediId;
    TextView mTextKoName;
    TextView mTextSource;
    TextView mTextShape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* 초기 메세지 플로팅 버튼 비활성화
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        //슬라이드 액션바 Loading
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //액션바 메뉴 List
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTextMediId = findViewById(R.id.content1);
        mTextKoName = findViewById(R.id.content2);
        mTextSource = findViewById(R.id.content3);
        mTextShape = findViewById(R.id.content4);

        //바코드 입력
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("카메라 테스트 시작", "* * * * Camera");
                mIntegrator.setBeepEnabled(false);
                mIntegrator.setCaptureActivity(CustomScannerActivity.class);
                mIntegrator.setRequestCode(IntentIntegrator.REQUEST_CODE);
                mIntegrator.initiateScan();
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"Scanning 결과");
        if (resultCode == 0) {
            Toast.makeText(getApplicationContext(),"결과값이 없데...", Toast.LENGTH_LONG).show();
        }else if(requestCode == IntentIntegrator.REQUEST_CODE){
            try{
                IntentResult result = IntentIntegrator.parseActivityResult(resultCode,data);
                String str = result.getContents();
                Log.d(TAG,"content 결과 " + str);
                if(str.substring(0,4).equals("http")){
                    mQueue = Volley.newRequestQueue(this);
                    httpGet(str);
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"QR코드가 올바르지 않습니다.", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),"requestCode : " + requestCode + ", resultCode : " + resultCode, Toast.LENGTH_LONG).show();
        }
    }

    public void httpGet(String url){
        Log.d(TAG,"서버로 통신 시도");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
//                    JSONArray jsonArray = response.getJSONArray("herbinfo");
//                    for(int i = 0; i < jsonArray.length() ; i++){
//                        JSONObject herb = jsonArray.getJSONObject(i);
//                        int id = herb.getInt("id");
//                        String name = herb.getString("herbname");
//                        String desc = herb.getString("herbdesc");
//                        mTextView.append("아이디 : " + id + ", 이름 : " + name + ", 상세 : " + desc + "\n\n");

                        Log.d(TAG, "jsonObject : " + response.toString());
                        String id = String.valueOf(response.getInt("mediId"));
                        String koName = response.getString("koName");
                        String source = response.getString("source");
                        String shape = response.getString("shape");


                        mTextMediId.setText(id);
                        mTextKoName.setText(koName);
                        mTextSource.setText(source);
                        mTextShape.setText(shape);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"QR코드가 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }
}
