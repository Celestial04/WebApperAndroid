package com.bouillie.web;

import static android.app.DownloadManager.Request;
import static android.app.usage.UsageEvents.Event.NONE;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.boullie.web.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.color.DynamicColors;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollView scroll;
    private AlertDialog alert2;
    private int selectedTheme;
    private Bitmap faviconBitmap;

    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scroll = findViewById(R.id.scroll);

        int savedTheme = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getInt("selected_theme", NONE);
        selectedTheme = (savedTheme != NONE) ? savedTheme : R.style.Theme_WebApper_dark;
        setTheme(selectedTheme);
        DynamicColors.applyToActivitiesIfAvailable(getApplication());

        LinearLayout linearLayout2 = findViewById(R.id.scrollvv);
        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (favicon != null) {
                    faviconBitmap = favicon;
                }
            }
        });

        EditText urlBar2 = findViewById(R.id.urlBar);
        urlBar2.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                urlBar2.getText().clear();
                urlBar2.setCursorVisible(true);
            } else {
                urlBar2.setText(webView.getUrl());
                urlBar2.setCursorVisible(false);
            }
        });
        urlBar2.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                String url = urlBar2.getText().toString();
                if (!URLUtil.isValidUrl(url)) {
                    url = "https://www.google.com/search?q=" + url;
                }
                webView.loadUrl(url);
                return true;
            }
            return false;
        });

        Button changeUrlButton = findViewById(R.id.changeUrlButton);
        changeUrlButton.setOnClickListener(v -> {
            String currentUrl = webView.getUrl();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.DialHeadChangeURL));

            final EditText input = new EditText(MainActivity.this);
            input.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_URI);
            input.setText(currentUrl);
            builder.setView(input);

            builder.setPositiveButton(getString(R.string.OkText), (dialog, which) -> {
                String newUrl = input.getText().toString();
                webView.loadUrl(newUrl);
            });

            builder.setNegativeButton((getString(R.string.CancelText)), (dialog, which) -> dialog.cancel());

            builder.show();
        });

        Button soundButton = findViewById(R.id.soundButton);
        soundButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.DialHeadTheme));

            String[] options = {getString(R.string.ThemeDefaultText), getString(R.string.ThemeNightText)};
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        Toast.makeText(getApplicationContext(), getString(R.string.ToastThemeDefaultText), Toast.LENGTH_SHORT).show();
                        selectedTheme = R.style.Theme_WebApper_dark;
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        updateTheme();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), getString(R.string.ToastThemeNightText), Toast.LENGTH_SHORT).show();
                        selectedTheme = R.style.Theme_WebApper_light;
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        updateTheme();
                        break;
                }
            });
            builder.show();
        });

        Button nex = findViewById(R.id.button);
        nex.setVisibility(webView.canGoForward() ? View.VISIBLE : View.GONE);
        nex.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
                nex.setVisibility(View.VISIBLE);
            } else {
                nex.setVisibility(View.GONE);
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            final List<AlertDialog.Builder> dialogsList = new ArrayList<>();

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                dialogsList.add(builder);
                builder.setMessage(getString(R.string.DialHeadDownloadAsk, fileName));
                builder.setCancelable(false);

                builder.setPositiveButton(
                        "Oui",
                        (dialog, id) -> {
                            Request request = new Request(Uri.parse(url));
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            long downloadId = downloadManager.enqueue(request);

                            AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                            dialogsList.add(builder2);
                            builder2.setTitle(getString(R.string.DialHeadDownload));
                            builder2.setMessage(getString(R.string.DialBodyDownload, fileName));
                            builder2.setCancelable(false);
                            LinearLayout layout = new LinearLayout(MainActivity.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setPadding(50, 50, 50, 50);

                            TextView textView = new TextView(MainActivity.this);
                            TextView speedl = new TextView(MainActivity.this);
                            TextView textViewETA = new TextView(MainActivity.this);
                            textView.setText(R.string.t_l_chargement_en_cours);
                            speedl.setText("Vitesse : 0.0 MB/s");
                            textViewETA.setText("ETA: Calculating...");
                            speedl.setTextSize(14);
                            textView.setTextSize(20);

                            layout.addView(textView);
                            layout.addView(speedl);
                            layout.addView(textViewETA);
                            Button cancelButton = new Button(MainActivity.this);
                            cancelButton.setText(getString(R.string.CancelText));

                            ProgressBar progressdl = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleHorizontal);
                            layout.addView(progressdl);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                progressdl.setMin(0);
                                progressdl.setIndeterminate(false);
                            }

                            cancelButton.setOnClickListener(v -> {
                                downloadManager.remove(downloadId);
                                Toast.makeText(getApplicationContext(), getString(R.string.ToastDownloadCancel, fileName), Toast.LENGTH_SHORT).show();
                                showNextDialog();
                            });

                            builder2.setView(layout);
                            builder2.setPositiveButton((getString(R.string.DialButtonDownloadCancel)), (dialog1, id1) -> {
                                downloadManager.remove(downloadId);
                                dialog1.cancel();
                                Toast.makeText(getApplicationContext(), getString(R.string.ToastDownloadCancel, fileName), Toast.LENGTH_SHORT).show();
                                if (alert2 != null && alert2.isShowing()) {
                                    alert2.dismiss();
                                    dialog1.dismiss();
                                }
                            });

                            alert2 = builder2.create();
                            dialog.dismiss();
                            alert2.show();

                            Handler handler = new Handler();
                            final int[] bytesDownloaded = {0};

                            long startTime = System.currentTimeMillis();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    DownloadManager.Query query = new DownloadManager.Query();
                                    query.setFilterById(downloadId);
                                    Cursor cursor = downloadManager.query(query);
                                    if (cursor.moveToFirst()) {
                                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                        int status = cursor.getInt(columnIndex);
                                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                            alert2.dismiss();
                                            Toast.makeText(getApplicationContext(), getString(R.string.ToastDownloadFinished, fileName), Toast.LENGTH_SHORT).show();
                                        } else if (status == DownloadManager.STATUS_FAILED) {
                                            alert2.dismiss();
                                            Toast.makeText(getApplicationContext(), getString(R.string.ToastDownloadFailed, fileName), Toast.LENGTH_SHORT).show();
                                        } else {
                                            int downloadedBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                            int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                            int progress = (int) ((downloadedBytes * 100L) / (double) bytesTotal);

                                            long elapsedTime = System.currentTimeMillis() - startTime;
                                            double bytesPerSecond = (downloadedBytes - bytesDownloaded[0]) * 1000.0 / elapsedTime / 1000000.0;

                                            int bytesRemaining = bytesTotal - downloadedBytes;
                                            long etaSeconds = (long) (bytesRemaining / bytesPerSecond);
                                            String etaFormatted = formatDuration(etaSeconds);
                                            textViewETA.setText("ETA: " + etaFormatted);

                                            speedl.setText("Vitesse : " + String.format("%.1f", bytesPerSecond) + " MB/s");
                                            bytesDownloaded[0] = downloadedBytes;
                                            textView.setText(getString(R.string.DialBodyDownloadText) + " " + Math.abs(progress) + "%");

                                            progressdl.setProgress(Math.abs(progress));
                                            handler.postDelayed(this, 500);
                                        }
                                    }
                                    cursor.close();
                                }
                            };
                            handler.postDelayed(runnable, 1000);
                        }).setNegativeButton(
                        (getString(R.string.CancelText)),
                        (dialog, id) -> {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), getString(R.string.ToastDownloadCancel, fileName), Toast.LENGTH_SHORT).show();
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }

            private void showNextDialog() {
                if (alert2 != null && alert2.isShowing()) {
                    alert2.dismiss();
                }
            }
        });

        ProgressBar myProgressBar = findViewById(R.id.progressBar);
        ImageView secureimage = findViewById(R.id.SecureImage);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                myProgressBar.setVisibility(View.VISIBLE);
                secureimage.setImageBitmap(webView.getFavicon());
                try {
                    URL parsedUrl = new URL(url);
                    String protocol = parsedUrl.getProtocol();
                    secureimage.setImageResource(protocol.equals("https") ? R.drawable.encrypted : R.drawable.uncrypted);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                if (favicon != null) {
                    faviconBitmap = favicon;
                }
                nex.setVisibility(view.canGoForward() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                if (webView.getFavicon() != null) {
                    faviconBitmap = webView.getFavicon();
                }
                try {
                    URL parsedUrl = new URL(url);
                    String protocol = parsedUrl.getProtocol();
                    secureimage.setImageResource(protocol.equals("https") ? R.drawable.encrypted : R.drawable.uncrypted);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                nex.setVisibility(view.canGoForward() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                urlBar2.setText(url);
                myProgressBar.setVisibility(View.GONE);
                try {
                    URL parsedUrl = new URL(url);
                    String protocol = parsedUrl.getProtocol();
                    secureimage.setImageResource(protocol.equals("https") ? R.drawable.encrypted : R.drawable.uncrypted);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                if (webView.getFavicon() != null) {
                    faviconBitmap = webView.getFavicon();
                }
                nex.setVisibility(view.canGoForward() ? View.VISIBLE : View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                myProgressBar.setProgress(newProgress);
                secureimage.setImageBitmap(webView.getFavicon());
                if (newProgress == 100) {
                    myProgressBar.setProgress(0);
                    myProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        webView.loadUrl("https://google.com/");

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        Button viewFav = findViewById(R.id.button6);
        viewFav.setOnClickListener(view -> {
            String currentUrl = webView.getUrl();
            String currentName = webView.getTitle();
            SharedPreferences sharedPreferences = getSharedPreferences("Favoris", MODE_PRIVATE);
            Map<String, ?> favoritesMap = sharedPreferences.getAll();
            List<String> favoritesList = new ArrayList<>();
            for (Map.Entry<String, ?> entry : favoritesMap.entrySet()) {
                String url = entry.getKey();
                String name = entry.getValue().toString();
                String currentNamee = name + " : " + url;
                favoritesList.add(currentNamee);
            }
            final String[] favoritesArray = favoritesList.toArray(new String[favoritesList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.ToastHeadFavoris))
                    .setItems(favoritesArray, (dialog, which) -> {
                        for (Map.Entry<String, ?> entry : favoritesMap.entrySet()) {
                            if (entry.getValue().toString().equals(favoritesArray[which].split(" : ")[0])) {
                                String url = entry.getKey();
                                webView.loadUrl(url);
                                break;
                            }
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        Button AddFav = findViewById(R.id.button5);
        AddFav.setOnClickListener(v -> {
            WebView webView1 = findViewById(R.id.webview);
            String currentUrl = webView1.getUrl();
            String currentName = webView1.getTitle();
            SharedPreferences sharedPreferences = getSharedPreferences("Favoris", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(currentUrl, currentName);
            editor.apply();

            if (sharedPreferences.contains(currentName)) {
                Toast.makeText(getApplicationContext(), currentName + getString(R.string.ToastFavorisAlreadyExist), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), currentName + getString(R.string.ToastFavorisAdded), Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart) {
            showStartDialog();
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }

        Button reloadButton = findViewById(R.id.button8);
        reloadButton.setOnClickListener(v -> webView.reload());

        Button remFav = findViewById(R.id.button7);
        remFav.setOnClickListener(view -> {
            String currentUrl = webView.getUrl();
            String currentName = webView.getTitle();
            SharedPreferences sharedPreferences = getSharedPreferences("Favoris", MODE_PRIVATE);
            Map<String, ?> favoritesMap = sharedPreferences.getAll();
            List<String> favoritesList = new ArrayList<>();
            for (Map.Entry<String, ?> entry : favoritesMap.entrySet()) {
                String url = entry.getKey();
                String name = entry.getValue().toString();
                String currentNamee = name + " : " + url;
                favoritesList.add(currentNamee);
            }
            final String[] favoritesArray = favoritesList.toArray(new String[favoritesList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.ToastHeadFavorisDelete))
                    .setItems(favoritesArray, (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        for (Map.Entry<String, ?> entry : favoritesMap.entrySet()) {
                            if (entry.getValue().toString().equals(favoritesArray[which].split(" : ")[0])) {
                                String url = entry.getKey();
                                editor.remove(url);
                                editor.apply();
                                Toast.makeText(MainActivity.this, favoritesArray[which].split(" : ")[0] + " à été supprimé.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        Button infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> showPageInfoBottomSheet(webView.getTitle(), webView.getUrl(), webView.getTitle(), faviconBitmap));
    }

    private void showPageInfoBottomSheet(String title, String url, String description, Bitmap favicon) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(view);

        TextView pageTitle = view.findViewById(R.id.pageTitle);
        TextView pageUrl = view.findViewById(R.id.pageUrl);
        TextView pageDescription = view.findViewById(R.id.pageDescription);
        ImageView faviconImage = view.findViewById(R.id.favicon);
        TextView securityStatus = view.findViewById(R.id.securityStatus);

        pageTitle.setText(title);
        pageUrl.setText(url);
        pageDescription.setText(description);

        if (favicon != null) {
            faviconImage.setImageBitmap(favicon);
        }

        try {
            URL parsedUrl = new URL(url);
            String protocol = parsedUrl.getProtocol();
            securityStatus.setText(protocol.equals("https") ? "Secure" : "Not Secure");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        bottomSheetDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putInt("selected_theme", selectedTheme).apply();
    }

    private void updateTheme() {
        AppCompatDelegate.setDefaultNightMode(selectedTheme);
        getDelegate().applyDayNight();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putInt("selected_theme", selectedTheme).apply();
    }

    private void showStartDialog() {
        Intent intent = new Intent(this, tuto_1.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void FirstStart() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        boolean firstStart = prefs.getBoolean("firstStart", true);
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    public void onDeleteDataClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.ConfirmationText));
        builder.setMessage(getString(R.string.DialHeadDeleteData));
        builder.setCancelable(false);
        builder.setNegativeButton(getString(R.string.CancelText), (dialog, which) -> Toast.makeText(getApplicationContext(), "Vos données n'ont pas été supprimées", Toast.LENGTH_SHORT).show());

        builder.setPositiveButton(getString(R.string.OkText), (dialog, which) -> {
            WebView webView = findViewById(R.id.webview);
            webView.clearCache(true);
            webView.clearSslPreferences();
            webView.clearFormData();
            webView.clearHistory();
            webView.clearMatches();
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStart", true);
            editor.apply();
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            editor.clear();
            editor.apply();
            SharedPreferences sharedPreferences = getSharedPreferences("Favoris", MODE_PRIVATE);
            SharedPreferences.Editor editorf = sharedPreferences.edit();
            editorf.clear();
            editorf.apply();
            File filesDir = getFilesDir();
            deleteRecursive(filesDir);

            File cacheDir = getCacheDir();
            deleteRecursive(cacheDir);

            File externalFilesDir = getExternalFilesDir(null);
            if (externalFilesDir != null) {
                deleteRecursive(externalFilesDir);
            }
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        });
        builder.show();
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    @Override
    public void onBackPressed() {
        WebView webView = findViewById(R.id.webview);
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public void setAlert2(AlertDialog alert2) {
        this.alert2 = alert2;
    }
}
