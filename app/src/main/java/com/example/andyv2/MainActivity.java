package com.example.andyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    ListView lvItems;
    List<Item> itemList;
    private static final int GALLERY_REQUEST_CODE = 100;
    EditText editTextName;
    EditText editTextURL  ;
    Button buttonAddItem;
    ImageView imageView;
    DatabaseReference databaseItems;
    FirebaseStorage storage;
    StorageReference storageRef ;
    FirebaseAuth mAuth;
    String currentURL="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        databaseItems = FirebaseDatabase.getInstance().getReference("items");

        editTextName = findViewById(R.id.editTextName);
        editTextURL = findViewById(R.id.editTextPhotoURL);
        buttonAddItem = findViewById(R.id.buttonAddItem);
        lvItems = findViewById(R.id.lvItems);
        itemList = new ArrayList<Item>();
//        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Item item = itemList.get(position);
//
//                showUpdateDialog(item.getItemId(),
//                        item.getItemName(),
//                        item.getItemPhotoURL());
//
//                return false;
//            }
//        });




        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              addItem();
         //   pickFromGallery();

            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

    }
    private void addItem() {
        String name = editTextName.getText().toString().trim();
       // String url = editTextURL.getText().toString().trim();

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String url = currentURL;

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "You must enter a Name.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "You must upload an image.", Toast.LENGTH_LONG).show();
            return;
        }

        String id = databaseItems.push().getKey();
        Item student = new Item(id, name, url);

        Task setValueTask = databaseItems.child(id).setValue(student);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,"Item added.",Toast.LENGTH_LONG).show();

                editTextName.setText("");
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        currentURL = "";
    }
    @Override
    protected void onStart() {
        super.onStart();
        databaseItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    Item item = studentSnapshot.getValue(Item.class);
                    itemList.add(item);
                }

                ItemListAdapter adapter = new ItemListAdapter(MainActivity.this, itemList);
                lvItems.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    private void updateItem(String id, String name, String url) {
        DatabaseReference dbRef = databaseItems.child(id);

        Item item = new Item(id,name,url);

        Task setValueTask = dbRef.setValue(item);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Item Updated.",Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
//    private void showUpdateDialog(final String itemID, String item, String photoURL) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//
//        LayoutInflater inflater = getLayoutInflater();
//
//        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
//        dialogBuilder.setView(dialogView);
//
//        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
//        editTextName.setText(item);
//
//
//
//
//        final Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
//
//        dialogBuilder.setTitle("Update Item " + item );
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//
//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String firstName = editTextName.getText().toString().trim();
//
//                if (TextUtils.isEmpty(firstName)) {
//                    editTextName.setError(" Name is required");
//                    return;
//                } else if (TextUtils.isEmpty(url)) {
//                    editTextURL.setError("URL is Required");
//                    return;
//                }
//
//                updateItem(itemID, firstName, lastName);
//
//                alertDialog.dismiss();
//            }
//        });
//        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteItem(itemID);
//
//                alertDialog.dismiss();
//            }
//        });
//
//    }

    private void deleteItem(String id) {
        DatabaseReference dbRef = databaseItems.child(id);

        Task setRemoveTask = dbRef.removeValue();
        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Item Deleted.",Toast.LENGTH_LONG).show();
            }
        });

        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
/////////////////////IMAGE STUFF
    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent



        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    ///////////////////////////////////////////


                    StorageReference storageRef = storage.getReference();
                    final String fileName = UUID.randomUUID().toString();
                    // Create a reference to "mountains.jpg"
                    StorageReference mountainsRef = storageRef.child( fileName
                            +".jpg");




                    ////////
                    //data.getData return the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                     imageView = new ImageView(this);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                    System.out.println("im so tired");
// Get the data from an ImageView as bytes
                    imageView.setDrawingCacheEnabled(true);
                    imageView.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] byteArray = baos.toByteArray();


                    UploadTask uploadTask = mountainsRef.putBytes(byteArray);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata();
                            //contains file metadata such as size, content-type, etc.
                           ///////////////////////
                          // String url=  taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                            //System.out.println("boom boom boom " + url);
                            ////////////////////////

                            // ...

                        }
                    });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            currentURL = uri.toString();
                                            System.out.println("boooooomLLLLLL::::::: " + currentURL) ;
                                        }
                                    });
                                }
                            }
                        }});
            }
    }

    public void uploadImage(View view) throws InterruptedException {
        pickFromGallery();
        Thread.sleep(500);
        ImageView imgOnePhoto = (ImageView) view.findViewById(R.id.thumbImage);
        //  DownloadImageTask dit = new DownloadImageTask(_context, imgOnePhoto);
        //dit.execute(toon.getPicture());
        if (currentURL.length() > 0) {
            new ImageDownloaderTask(imgOnePhoto).execute(currentURL);
        }

    }
}
