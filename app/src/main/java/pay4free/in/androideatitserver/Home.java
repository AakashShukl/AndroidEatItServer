package pay4free.in.androideatitserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;
import pay4free.in.androideatitserver.Common.Common;
import pay4free.in.androideatitserver.Interface.ItemClickListener;
import pay4free.in.androideatitserver.Viewholder.MenuViewHolder;
import pay4free.in.androideatitserver.model.Category;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView txtFullname;
FirebaseDatabase database;
    DatabaseReference categories;

    FirebaseRecyclerAdapter<Category,MenuViewHolder>adapter;
    RecyclerView recyclerView;
    FirebaseStorage storage;
    StorageReference storageReference;
    RecyclerView.LayoutManager layoutManager;
    Uri saveuri;
    private final int pick=71;
    MaterialEditText edtname;
    FButton btnupload,btnselect;
    DrawerLayout drawer;
    Category newcategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);

        database=FirebaseDatabase.getInstance();
        categories=database.getReference("Category");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView=navigationView.getHeaderView(0);
        txtFullname=(TextView)headerView.findViewById(R.id.name);
        txtFullname.setText(Common.currentuser.getName());

        //Load menu
       recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
       recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        loadmenu();
    }

    private void showDialog() {
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(Home.this);
        alertdialog.setTitle("Add New Category");
        alertdialog.setMessage("Please fill Full information");
        LayoutInflater inflator=this.getLayoutInflater();
        View view=inflator.inflate(R.layout.add_new_menu,null);
        edtname=view.findViewById(R.id.edtName);
        btnselect=view.findViewById(R.id.btnselect);
        btnupload=view.findViewById(R.id.btnupload);
        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        alertdialog.setView(view);
        alertdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        alertdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
                if(newcategory!=null)
                {
                    categories.push().setValue(newcategory);
                   Snackbar.make(drawer,"New Category"+newcategory.getName()+"was Added",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        alertdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             dialog.dismiss();
            }
        });
        alertdialog.show();


        }

    private void uploadImage() {
        if(saveuri!=null)
        {
         final ProgressDialog dialog=new ProgressDialog(this);
            dialog.setMessage("Uploading......");
            dialog.show();

            String imageName= UUID.randomUUID().toString();
    final StorageReference  imageFolder=storageReference.child("images/"+imageName);
            imageFolder.putFile(saveuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    Toast.makeText(Home.this,"Uploaded!!!",Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                       newcategory=new Category(edtname.getText().toString(),uri.toString()) ;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(Home.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Uploaded "+ progress +"%");
                        }
                    });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==pick && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
          saveuri=data.getData();
            btnselect.setText("Image Selected");
        }
    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Intent chooser=Intent.createChooser(intent,"Select Picture");
        startActivityForResult(chooser,pick);
    }


    private void loadmenu() {
        adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,categories) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.textMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }
                };
                adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

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
        getMenuInflater().inflate(R.menu.home, menu);
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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {



        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }

        if(item.getTitle().equals(Common.DELETE))
        {
            DeleteCategory(adapter.getRef(item.getOrder()).getKey());

        }
        return super.onContextItemSelected(item);
    }

    private void DeleteCategory(String key) {

categories.child(key).removeValue();
        Toast.makeText(this,"Item deleted",Toast.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(final String key, final Category item) {

        AlertDialog.Builder alertdialog=new AlertDialog.Builder(Home.this);
        alertdialog.setTitle("Update Category");
        alertdialog.setMessage("Please fill Full information");
        LayoutInflater inflator=this.getLayoutInflater();
        View view=inflator.inflate(R.layout.add_new_menu,null);
        edtname=view.findViewById(R.id.edtName);
        btnselect=view.findViewById(R.id.btnselect);
        btnupload=view.findViewById(R.id.btnupload);
        edtname.setText(item.getName());
        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });
        alertdialog.setView(view);
        alertdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        alertdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               item.setName(edtname.getText().toString());
                categories.child(key).setValue(item);
            }
        });
        alertdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertdialog.show();


    }
    public void changeImage(final Category item)
    {
        if(saveuri!=null)
        {
            final ProgressDialog dialog=new ProgressDialog(this);
            dialog.setMessage("Uploading......");
            dialog.show();

            String imageName= UUID.randomUUID().toString();
            final StorageReference  imageFolder=storageReference.child("images/"+imageName);
            imageFolder.putFile(saveuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(Home.this,"Uploaded!!!",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(Home.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Uploaded "+ progress +"%");
                        }
                    });

        }
    }
}
