package com.example.whywasteapp;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.whywasteapp.login.LoginActivity;
import com.example.whywasteapp.settings.ChangePassFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import es.dmoral.toasty.Toasty;

public final class Functions extends Activity {

    private static Context context;

    //Variables
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseAuth User;
    public static FirebaseUser firebaseUser;

    public static SimpleDateFormat Date = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat Time = new SimpleDateFormat("HH:mm:ss");
    public static SharedPreferences prefrence;
    public static String NAME,MOBILE,EMAIL,ADDRESS,CITY,STATE,GENDER;

    public Functions() {}

    public static String getCurrentDate() {
        String date = Date.format(new Date());
        return date;
    }

    public static String getCurrentTime() {
        String time = Time.format(new Date());
        return time;
    }

    public static Uri BitmapToUri(Bitmap imageBitmap,Activity activity) {
        try{
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            Random r = new Random();
            int randomNumber = r.nextInt(100);
            String path = MediaStore.Images.Media.insertImage(activity.getApplication().getContentResolver(), imageBitmap, String.valueOf(randomNumber), null);
            Uri imageUri = Uri.parse(path);
            return imageUri;
        }catch (Exception e){
            Toasty.error(activity,"Storage permission deny",Toasty.LENGTH_LONG).show();
            return null;
        }

        /*
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            //options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(imageBitmap, 200, 200,
                    true);
            File file = new File(activity.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = activity.openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
        */
    }

    public static SharedPreferences getPref(Activity activity){
        prefrence = activity.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        NAME = prefrence.getString("Name","");
        MOBILE = prefrence.getString("MobNo","");
        EMAIL = prefrence.getString("Email","");
        ADDRESS = prefrence.getString("Address","");
        CITY = prefrence.getString("City","");
        STATE = prefrence.getString("State","");
        GENDER = prefrence.getString("Gender","");
        return prefrence;
    }

    public static void setNotification(Activity activity,String Title, String Description) {
        NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("ID","NAME",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity,"ID")
                .setSmallIcon(R.drawable.whywaste)
                .setContentTitle(Title)
                .setContentText(Description)
                .setAutoCancel(true);

        Intent intent = new Intent(activity,MainActivity.class);

        PendingIntent pi = PendingIntent.getActivity(activity,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);

        notificationManager.notify(0,builder.build());
    }

    public static void playNotificationSound(Activity activity) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(activity, notification);
        r.play();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.setLooping(false);
        }
    }

    public static void setVibration(Activity activity) {
        Vibrator v = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Functions.context;
    }

    public static FirebaseUser getCurrentUser()
    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            return firebaseUser;
        }else {
            return null;
        }
    }

    public static String getUserID()
    {
        User = FirebaseAuth.getInstance();
        String UID = User.getUid();
        return UID;
    }

    public static String getUserEmailID()
    {
        FirebaseUser User =  firebaseAuth.getCurrentUser();
        String Email = User.getEmail();
        return Email;
    }

    public static boolean isValidUsername(String name)
    {
        // Regex to check valid username.
        String regex = "^[A-Za-z]\\w{9,12}$";

        Pattern p = Pattern.compile(regex);
        if (name == null) {
            return false;
        }
        Matcher m = p.matcher(name);
        return m.matches();
    }

    public static boolean isValid(String email)
    {
        // Regex to check valid email.
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    public static void stopBtnAnimation(CircularProgressButton btn) {
        btn.stopAnimation();
        btn.revertAnimation();
    }

    public static boolean isValidIndianMobileNumber(String s)
    {
        Pattern p = Pattern.compile("^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[6789]\\d{9}$");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    public static boolean isValidPinCode(String pinCode) {
        // Regex to check valid pin code of India.
        String regex = "^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the pin code is empty
        // return false
        if (pinCode == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given pin code
        // and regular expression.
        Matcher m = p.matcher(pinCode);

        // Return if the pin code
        // matched the ReGex
        return m.matches();
    }

    //ObjectToJSONObject : Used In getCity Function
    public static JSONObject objectToJSONObject(Object object){
        Object json = null;
        JSONObject jsonObject = null;
        try {
            json = new JSONTokener(object.toString()).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONObject) {
            jsonObject = (JSONObject) json;
        }
        return jsonObject;
    }

    public static void KeyboardDown(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void KeyboardUp(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    //GetState Code
    public static ArrayList<String> getState(String fileName,Activity activity) {
        JSONArray jsonArray = null;
        ArrayList<String> stateList = new ArrayList<String>();
        try{
            InputStream is = activity.getApplication().getAssets().open(fileName);
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data,"UTF-8");
            JSONObject obj= new JSONObject(json);
            jsonArray = obj.getJSONArray("states");
            if (jsonArray!=null)
            {
                for (int i =0; i<jsonArray.length();i++) {
                    stateList.add(jsonArray.getJSONObject(i).getString("state"));
                }
            }
        }catch (IOException e){
            Log.d("IOE",String.valueOf(e));
            e.printStackTrace();
        }catch (JSONException je){
            Log.d("JE",String.valueOf(je));
            je.printStackTrace();
        }
        return stateList;
    }

    //GetCityCode
    public static ArrayList<String> getCity(String fileName, int selectedState,Activity activity) {
        JSONArray jsonArray = null;
        ArrayList<String> cityList = new ArrayList<String>();
        try{
            InputStream is = activity.getApplication().getAssets().open(fileName);
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data,"UTF-8");
            JSONObject obj= new JSONObject(json);
            jsonArray = obj.getJSONArray("states");

            JSONObject CityObj = Functions.objectToJSONObject(jsonArray.get(selectedState));
            JSONArray jsonCityArray = CityObj.getJSONArray("districts");

            if (jsonCityArray!=null)
            {
                for (int i = 0; i < jsonCityArray.length(); i++) {
                    cityList.add(jsonCityArray.get(i).toString());
                }
            }
        }catch (IOException e){
            Log.d("IOE",String.valueOf(e));
            e.printStackTrace();
        }catch (JSONException je){
            Log.d("JE",String.valueOf(je));
            je.printStackTrace();
        }
        return cityList;
    }

    /*public boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            fragment.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                    new ChangePassFragment()).commit();
            return true;
        }
        return false;
    }*/

}
