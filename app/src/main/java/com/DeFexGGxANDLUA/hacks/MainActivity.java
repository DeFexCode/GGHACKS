package com.DeFexGGxANDLUA.hacks;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.VideoView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;

    private EditText txtUsername;
    private Button btnLogin;
    private TextView tvMessage;

    String xua;
    String zi2383 = "gplay.lastdayr";
    String zi2383s = "ulessurvival/files/";
    String zi28 = "ed/0/Android/data";
    String zi283 = "/com.herogame.";
    String zi8 = "/storage/emulat";
    String zia = (this.zi8 + this.zi28 + this.zi283 + this.zi2383 + this.zi2383s);

    private VideoView videoBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openChannelButton = findViewById(R.id.openChannelButton);

        openChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTelegramChannel();
            }
        });

        VideoView videoView = findViewById(R.id.videoBackground);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video;
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.start();

        videoView.setOnCompletionListener(mp -> {
            videoView.start();
        });


        // Initialize views
        txtUsername = findViewById(R.id.txtUsername);
        btnLogin = findViewById(R.id.btnLogin);
        tvMessage = findViewById(R.id.tvMessage);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void openTelegramChannel() {
        Uri telegramUri = Uri.parse("https://t.me/defexhacks");
        Intent telegramIntent = new Intent(Intent.ACTION_VIEW, telegramUri);
        startActivity(telegramIntent);
    }

    private void login() {
        String username = txtUsername.getText().toString();

        if (username.isEmpty()) {
            tvMessage.setText("WRITE KEY");
        } else {
            // Вызываем AsyncTask для выполнения HTTP-запроса в фоновом потоке
            new LoginTask().execute(username);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String buildId = Build.ID;  // Получаем BUILD ID
            String manufacturer = Build.MANUFACTURER; //мануфактурер фак ми
            String model = Build.MODEL;//билд модел фак ю
            String serverUrl = "https://defexggxhuligan.000webhostapp.com/login.php";

            try {
                // Формируем URL для запроса
                URL url = new URL(serverUrl);

                // Устанавливаем соединение
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    // Устанавливаем метод запроса POST
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);

                    // Формируем тело запроса
                    String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                            "&password=" + URLEncoder.encode(buildId, "UTF-8") +
                            "&uuid=" + URLEncoder.encode(android_id, "UTF-8") +
                            "&manuf=" + URLEncoder.encode(manufacturer, "UTF-8") +
                            "&mod=" + URLEncoder.encode(model, "UTF-8");


                    // Получаем поток для записи данных в тело запроса
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = postData.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Получаем ответ от сервера
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        return stringBuilder.toString();
                    }
                } finally {
                    // Закрываем соединение
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                return "ERROR CONNECTION";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Обрабатываем результат в основном потоке
            handleLoginResult(result);
        }
    }

    private void handleLoginResult(String result) {
        if (result.contains("Login success")) {
            // Если сервер вернул "Login success"
            tvMessage.setText("Вход выполнен успешно");

            // Проверяем, есть ли у нас разрешение
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Если нет, то запрашиваем разрешение у пользователя
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                // Если уже есть разрешение, вызываем метод
                saveBuildIdToFile();
            }

            SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            preferences.edit().putString("username", txtUsername.getText().toString()).apply();

            // Здесь можно перейти на другой экран или выполнить другие действия
        } else {
            // Обработка других возможных ответов от сервера
            tvMessage.setText(result);

            // Показываем Toast с сообщением об ошибке
            Toast.makeText(MainActivity.this, "Ошибка входа: " + result, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBuildIdToFile() {
        try {
            Process process = Runtime.getRuntime().exec("su -c echo '" + "0.4.3.G.D.2.5.7.FK.RKQ1.1.1.60.BBC.100.011.255.33" + Build.ID + "KK.386.HSJ038.333.FAB.010.100.1002" + "' > /storage/emulated/0/Android/data/com.herogame.gplay.lastdayrulessurvival/files/security_log");
            Process process2 = Runtime.getRuntime().exec("su -c echo > /storage/emulated/0/Android/data/com.herogame.gplay.lastdayrulessurvival/files/security");
            Process process3 = Runtime.getRuntime().exec("su -c echo '" + "2NFN838588830GJJAKVNNMLAI93YFJKLA8387FKAFGC6564" + Build.ID + "ASGI488302769020JFKA0385JFKA0385739" + "' > /storage/emulated/0/Android/data/com.herogame.gplay.lastdayrulessurvival/files/blog");
            Process process4 = Runtime.getRuntime().exec("su -c echo 'l5, �2, �2, �2, \\n\" +\n\"3, !3, ��: R3, X3, d3, o3, �3, �3, U4, ��: h4, w4, ��: �4, ��: �4, �4,  5, 45, _5, y5, �5, �5, /�: �5, �5, �5, I�: �5, e�: ��: ��: ��:  �: K�: v�: ��: ��: ��:  �: T�: m�: ��: �5, �5' > " + this.zia + this.xua + "\n");
            Process process5 = Runtime.getRuntime().exec("su -c am start -a android.intent.action.MAIN -n com.herogame.gplay.lastdayrulessurvival/.UnityPlayerActivity");
            process.waitFor();
            process2.waitFor();
            process3.waitFor();
            process4.waitFor();
            process5.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}