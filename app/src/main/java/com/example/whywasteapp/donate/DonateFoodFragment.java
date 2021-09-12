package com.example.whywasteapp.donate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whywasteapp.Functions;
import com.example.whywasteapp.MainActivity;
import com.example.whywasteapp.R;
import com.example.whywasteapp.addarea.AddAreaFragment;
import com.example.whywasteapp.home.FoodAdapter;
import com.example.whywasteapp.profile.ProfileFragment;
import com.example.whywasteapp.volunteer.VolunteerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DonateFoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class DonateFoodFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Bitmap CAPTURED_PHOTO2,CAPTURED_PHOTO3;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView quantity;
    TextInputLayout Lqty;
    TextInputEditText txtFoodName,txtPickUpAdd,txtPickUpTime,txtPincode,txtMoreQty;
    ImageView donateFoodImg1,donateFoodImg2,donateFoodImg3;
    SeekBar qty;
    CircularProgressButton donateFood;
    RadioButton Yes,No;

    String foodName,pickUpAdd,pickUpTime,ableToDeliver,pincode,moreQty,Random,downloadUrl1,downloadUrl2,downloadUrl3;
    Integer DonateImg=1,foodQty;
    final int[] SendAlert = {1};
    public static int Node;
    Uri imageUrl1,imageUrl2,imageUrl3;

    SharedPreferences prefrence;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    StorageReference StorageRef =  FirebaseStorage.getInstance().getReference();

    public DonateFoodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DonateFoodFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DonateFoodFragment newInstance(String param1, String param2) {
        DonateFoodFragment fragment = new DonateFoodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate_food, container, false);

        // Inflate the layout for this fragment
        donateFood = view.findViewById(R.id.donateFood);
        donateFoodImg1 = view.findViewById(R.id.donateImg1);
        donateFoodImg2 = view.findViewById(R.id.donateImg2);
        donateFoodImg3 = view.findViewById(R.id.donateImg3);
        quantity = view.findViewById(R.id.qty);
        qty = view.findViewById(R.id.seekBar);
        Lqty = view.findViewById(R.id.TLqty);
        Yes = view.findViewById(R.id.Yes);
        No = view.findViewById(R.id.No);

        txtFoodName = view.findViewById(R.id.txtFoodName);
        txtPickUpAdd = view.findViewById(R.id.txtPickUpAddress);
        txtPickUpTime = view.findViewById(R.id.txtPickUpTime);
        txtPincode = view.findViewById(R.id.txtPincode);
        txtMoreQty = view.findViewById(R.id.FoodQty);

        imageUrl1 = DonateFragment.CAPTURED_PHOTO1;

        donateFoodImg1.setImageURI(imageUrl1);
        quantity.setText("1");

        qty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                quantity.setText(String.valueOf(progress));
                if (progress==10){
                    Lqty.setVisibility(View.VISIBLE);
                    txtMoreQty.getText().clear();
                    //quantity.setPadding(10,10,10,10);
                }else {
                    Lqty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        txtMoreQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                moreQty = txtMoreQty.getText().toString();
                if (!moreQty.isEmpty()){
                    int more = Integer.parseInt(moreQty);
                    if (more>10){
                        quantity.setText(moreQty);
                    }else {
                        txtMoreQty.setError("Enter more then 10 Quantity..!!!");
                        txtMoreQty.requestFocus();
                        quantity.setText("10");
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        donateFoodImg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DonateImg==1){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                }else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 2);
                }
            }
        });

        donateFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodName = txtFoodName.getText().toString();
                pickUpAdd =  txtPickUpAdd.getText().toString();
                pickUpTime = txtPickUpTime.getText().toString();
                pincode = txtPincode.getText().toString();
                foodQty = qty.getProgress();
                ableToDeliver = null;

                donateFood.startAnimation();

                if (TextUtils.isEmpty(foodName))
                {
                    txtFoodName.setError("Food Name Is Required To Donate Foods..!!!");
                    txtFoodName.requestFocus();
                    Functions.stopBtnAnimation(donateFood);
                    return;
                }
                if (TextUtils.isEmpty(pickUpAdd))
                {
                    txtPickUpAdd.setError("Food PickUp Address is Required..!!!");
                    txtPickUpAdd.requestFocus();
                    Functions.stopBtnAnimation(donateFood);
                    return;
                }
                if (TextUtils.isEmpty(pickUpTime))
                {
                    txtPickUpTime.setError("Enter Food Pickup In Minimum Time..!!!");
                    txtPickUpTime.requestFocus();
                    Functions.stopBtnAnimation(donateFood);
                    return;
                }

                if (Lqty.getVisibility() == View.VISIBLE){
                    moreQty = txtMoreQty.getText().toString();
                    if (TextUtils.isEmpty(moreQty)){
                        txtMoreQty.setError("Food Quantity Is Required..!!!");
                        txtMoreQty.requestFocus();
                        Functions.stopBtnAnimation(donateFood);
                        return;
                    }
                }

                if (Yes.isChecked()){
                    ableToDeliver = "Yes";
                }else {
                    ableToDeliver = "No";
                }

                if (TextUtils.isEmpty(pincode)){
                    txtPincode.setError("Pincode is Reqired..!!!");
                    txtPincode.requestFocus();
                    Functions.stopBtnAnimation(donateFood);
                    return;
                }

                if (Functions.isValidPinCode(pincode)){
                    pincode = txtPincode.getText().toString();
                }else {
                    txtPincode.setError("Enter Valid PinCode..!!!");
                    txtPincode.requestFocus();
                    Functions.stopBtnAnimation(donateFood);
                    return;
                }

                if (ableToDeliver.equals("Yes")){
                    displayDonateAddress();  //Send Notification To Donator And Select Address
                    android.os.Handler handler = new Handler();
                    int delay = 5000; // 1000 milliseconds == 1 second
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (SendAlert[0] == 1){
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Functions.stopBtnAnimation(donateFood);
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                                                        new AddAreaFragment()).commit();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Functions.stopBtnAnimation(donateFood);
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                                                        new DonateFragment()).commit();
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                String Title = "No Any Poverty Area Found On This Area : <b>"+pincode+"</b>";
                                alert.setTitle(Html.fromHtml(Title));
                                String Message = "Please Add Any Nearest Poverty Area To Continue Donate.";
                                alert.setMessage(Message);
                                alert.show();
                                Toasty.info(getActivity(),"No Any Poverty Area Found On This Area : "+pincode,Toasty.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }, delay);
                }else {
                    checkAvilableVolunteer();
                }

            }
        });

        return view;
    }

    private void checkAvilableVolunteer() {
        final int[] VOL_NOTI = {1};
        dbRef = FirebaseDatabase.getInstance().getReference("Volunteer_Details");
        dbRef.orderByChild("pincode").equalTo(String.valueOf(pincode)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            if (VOL_NOTI[0] ==1){
                                Toasty.success(getActivity(),"Donation Notification Sended To The Volunteer",Toasty.LENGTH_LONG).show();
                                Functions.stopBtnAnimation(donateFood);
                                VOL_NOTI[0] =0;
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                                        new DonateFragment()).commit();
                            }
                        }
                    }
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Functions.stopBtnAnimation(donateFood);
                                    //displayDonateAddress();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                                            new VolunteerFragment()).commit();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Functions.stopBtnAnimation(donateFood);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                                            new DonateFragment()).commit();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.setTitle("Alert");
                    String Message = "No Any Volunteer Found On This Area : <b>"+pincode+ "</b> <br>" +
                            "Please Select Any Poverty Address From Above Notification";
                    alert.setMessage(Html.fromHtml(Message));
                    alert.show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayDonateAddress() {
        final int[] SendNoti = {1};
        dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate_Place");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String Key = dataSnapshot.getKey();
                    DatabaseReference db = dbRef.child(Key);

                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            for (DataSnapshot dataSnap:snap.getChildren()){
                                String NodeKey = dataSnap.getKey();
                                DatabaseReference ref = db.child(NodeKey);
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot ss) {
                                        String Pin = ss.child("pincode").getValue().toString();
                                        if (Pin.equals(pincode)){
                                            if (SendNoti[0]==1){
                                                uploadFoodData();
                                                SendNoti[0]=0;
                                                SendAlert[0]=0;
                                                return;
                                            }
                                        }

                                        return;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    /*db.orderByChild("pincode").equalTo(String.valueOf(pincode)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            if (snap.exists()){
                                if (SendNoti[0]==1){
                                    uploadFoodData();
                                    SendNoti[0] =0;
                                    return;
                                }
                                return;
                            } else {
                                if (SendAlert[0] == 1){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Functions.stopBtnAnimation(donateFood);
                                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                                                            new AddAreaFragment()).commit();
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Functions.stopBtnAnimation(donateFood);
                                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                                                            new DonateFragment()).commit();
                                                }
                                            });

                                    AlertDialog alert = builder.create();
                                    String Title = "No Any Poverty Area Found On This Area : <b>"+pincode+"</b>";
                                    alert.setTitle(Html.fromHtml(Title));
                                    String Message = "Please Add Any Nearest Poverty Area To Continue Donate.";
                                    alert.setMessage(Message);
                                    alert.show();
                                    Toasty.info(getActivity(),"No Any Poverty Area Found On This Area : "+pincode,Toasty.LENGTH_SHORT).show();
                                    SendAlert[0] =0;
                                    return;
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAreaNotification(Activity activity, String Title, String Description) {
        NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("ID","Name",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Notification");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity,"ID")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(Title)
                .setContentText(Description)
                .setAutoCancel(true);

        Intent intent = new Intent(activity, SelectAreaForDonate.class);
        intent.putExtra("Pincode",pincode);
        intent.putExtra("Node",String.valueOf(Node));

        PendingIntent pi = PendingIntent.getActivity(activity,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);

        notificationManager.notify(0,builder.build());
    }

    private void uploadFoodData() {
        String UserKey = Functions.getUserID();
        dbRef = FirebaseDatabase.getInstance().getReference("Food_Donate").child(UserKey);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Node = (int) snapshot.getChildrenCount();
                if (Node==0)
                    Node=1;
                else
                    Node++;

                DatabaseReference ref = dbRef.child(String.valueOf(Node));
                if (imageUrl1 != null) {
                    //Random = UUID.randomUUID().toString();
                    //StorageRef = FirebaseStorage.getInstance().getReference("FoodDonatedImage/"+UserKey+"/"+Node);
                    StorageRef = FirebaseStorage.getInstance().getReference("FoodDonatedImage").child(UserKey).child(String.valueOf(Node));
                    StorageRef.putFile(imageUrl1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            StorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl1 = uri;
                                    downloadUrl1 = imageUrl1.toString();

                                    if (downloadUrl1 != null) {
                                        AddDataIntoFirebase(downloadUrl1,UserKey);
                                    } else{
                                        Toast.makeText(getActivity(), "Image Crashed, Try Again..!!!", Toast.LENGTH_SHORT).show();
                                        Functions.stopBtnAnimation(donateFood);
                                    }
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AddDataIntoFirebase(String downloadUrl1, String UserKey) {
        Map<String, Object> data = new HashMap<>();
        data.put("foodName",foodName);
        data.put("pickUpAddress",pickUpAdd);
        data.put("imageName",downloadUrl1);
        data.put("pickUpInMinimumTime",pickUpTime);
        data.put("pincode",pincode);
        data.put("foodQty",quantity.getText().toString());

        prefrence = getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String Name = prefrence.getString("Name","");
        data.put("donatorName",Name);
        data.put("ableToDeliver",ableToDeliver);
        String Date = Functions.getCurrentDate();
        String Time = Functions.getCurrentTime();
        data.put("date",Date);
        data.put("time",Time);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Food_Donate").child(UserKey);
        db.child(String.valueOf(Node)).setValue(data);

        Toasty.success(getActivity(), "Food Donated Successfully", Toast.LENGTH_SHORT).show();
        Functions.stopBtnAnimation(donateFood);
        Functions.playNotificationSound(getActivity());
        Functions.setVibration(getActivity());
        setAreaNotification(getActivity(),"Select Poverty Area To Donate","Click Here To Select Area"); //(getActivity(),"Select Poverty Area To Donate","Click Here To Select Area");
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                new DonateFragment()).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode == RESULT_OK){
            CAPTURED_PHOTO2  = (Bitmap) data.getExtras().get("data");
            donateFoodImg2.setImageBitmap(CAPTURED_PHOTO2);
            donateFoodImg2.setVisibility(View.VISIBLE);
            DonateImg=2;
        }
        if (requestCode==2 && resultCode == RESULT_OK){
            CAPTURED_PHOTO3  = (Bitmap) data.getExtras().get("data");
            donateFoodImg3.setImageBitmap(null);
            donateFoodImg3.setImageBitmap(CAPTURED_PHOTO3);
            donateFoodImg3.setBackgroundResource(0);
            donateFoodImg3.setEnabled(false);
            DonateImg=3;
        }else {
            donateFoodImg3.setEnabled(true);
        }
    }
}