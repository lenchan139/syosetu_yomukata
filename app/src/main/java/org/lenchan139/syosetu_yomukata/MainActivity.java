package org.lenchan139.syosetu_yomukata;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    FloatingActionButton fab;
    private ProgressDialog progressBar;
    String currUrl;
    private static final String TAG = "Main";
    String cookies;

    @SuppressLint("SetJavaScriptEnabled")


    String favURL = "http://syosetu.com/favnovelmain/list";
    private  void onClickDefault(View v){
        Snackbar.make(v, "ページはダンロードしています、しばらくお待ちください。", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       webView = (WebView) findViewById(R.id.webView);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onClickDefault(view);

                webView.loadUrl("javascript:window.HTMLOUT.showHTML" +
                        "('<html>'+document.getElementById('bkm').innerHTML+'</html>');");
            }
        });

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressBar = ProgressDialog.show(MainActivity.this, "ダンロード中.....", "ページはまだダンロード中、しばらくお待ちください。");


        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new LoadListener(), "HTMLOUT");
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                if( url.indexOf("ncode.") != -1){
                    url.replace("ncode.","nk.");
                }
                view.loadUrl(url);
                currUrl = url;
                //new sync
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " +url);
                progressBar.dismiss();
                cookies = CookieManager.getInstance().getCookie(url);
                if(cookies != null && currUrl != null){
                    Log.v("cookies",cookies);
                    //new sync().execute();
                }


            }


        });
        webView.loadUrl(favURL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(currUrl == null){
            currUrl = favURL;
        }
        //noinspection SimplifiableIfStatement
        if(id == R.id.bookmarks ){
            webView.loadUrl(favURL);
        }
        else if (id == R.id.open_in_browser) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(currUrl));
            startActivity(i);
            return true;
        }else if(id == R.id.copy_url){
            ClipboardManager _clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            _clipboard.setText(currUrl);
            Toast.makeText(this, "こぴーしました。", Toast.LENGTH_SHORT).show();
        }else if(id == R.id.action_settings){
            Toast.makeText(this, "なにも出ませんよー", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }



    class LoadListener{
        @JavascriptInterface
        public void showHTML(String html)
        {
            Log.e("result",html);
            if(html == null){
                Snackbar.make(fab, "ERROR: 「しおりを挿む」ボットンが存在しません。", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        }else if(html.indexOf("しおりを挿む") == -1){
                Snackbar.make(fab, "ERROR: 「しおりを挿む」ボットンが存在しません。", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }else{
               String a = Jsoup.parse(html).select(".bookmark_now").html();
                String key1 = "http://";
                a  = a.substring(a.indexOf(key1));
                a = a.substring(0,a.indexOf("\">しおりを挿む</a>"));
                Log.v("fabUrl",a);
                final String finalA = a;
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(finalA);
                    }
                });

            }
        }
    }



}


