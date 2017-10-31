package pay4free.in.androideatitserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import pay4free.in.androideatitserver.Common.Common;
import pay4free.in.androideatitserver.model.User;

public class Signin extends AppCompatActivity {
EditText phone,password;
    Button signin;
    FirebaseDatabase database;
    DatabaseReference users;
ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        phone=(MaterialEditText)findViewById(R.id.edtphone);
        password=(MaterialEditText)findViewById(R.id.edtpass);
        signin=(FButton)findViewById(R.id.signin);
        database=FirebaseDatabase.getInstance();
        users=database.getReference("User");
        progress=new ProgressDialog(this);
        progress.setMessage("Please Wait........");
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInuser(phone.getText().toString(),password.getText().toString());
            }
        });
    }

    private void signInuser(String phone, String password) {
        progress.show();
        final String localphone=phone;
        final String localpassword=password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localphone).exists()) {
                    progress.dismiss();
                    User user = dataSnapshot.child(localphone).getValue(User.class);
                    user.setPhone(localphone);
                    if (Boolean.parseBoolean(user.getIsStaff())) {
                        if (user.getPassword().equals(localpassword)) {
                            Toast.makeText(Signin.this, "Succesfully login", Toast.LENGTH_SHORT).show();
                              Common.currentuser=user;
                              startActivity(new Intent(Signin.this,Home.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        Toast.makeText(getApplicationContext(), "Please login with Staff", Toast.LENGTH_SHORT).show();
                }

else

            {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "User not exist in Database", Toast.LENGTH_SHORT).show();

            }
        }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
