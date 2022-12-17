package com.manuni.imagetopdf;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ImageListFragment extends Fragment {
    public static final int REQUEST_CODE_FOR_STORAGE = 100;
    public static final int REQUEST_CODE_FOR_CAMERA = 101;
    private FloatingActionButton addImageFab;

    private Context mContext;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private RecyclerView imageListRV;
    private ArrayList<ImageListModel> listImageList;
    private ImageListAdapter imageListAdapter;

    private Uri imageUri = null;

    public ImageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        addImageFab = view.findViewById(R.id.addImageFab);

        imageListRV = view.findViewById(R.id.imageListRV);

        addImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputImageDialog();
            }
        });

        loadImages();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_bar_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.delete_white){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Delete Images")
                    .setMessage("Are you sure to delete all/selected images?")
                    .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteImages(true);
                        }
                    }).setNeutralButton("Delete Selected", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteImages(false);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteImages(boolean deleteAll){
        ArrayList<ImageListModel> imageToDelete = new ArrayList<>();
        if (deleteAll){
            //jehetu delete all image kora hoyeche tai main list a ja ache sob ei list a rakha hoyeche.
            imageToDelete = listImageList;
        }else{
            for (int i=0; i<listImageList.size(); i++){
                if (listImageList.get(i).isChecked()){
                    imageToDelete.add(listImageList.get(i));
                }
            }

        }
        for (int i=0; i<imageToDelete.size(); i++){
            try {
                String pathOfImageToDelete = imageToDelete.get(i).getImageUri().getPath();
                File file = new File(pathOfImageToDelete);
                if (file.exists()){
                    boolean delete = file.delete();
                    Toast.makeText(mContext, "Image has deleted "+delete, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
        loadImages();

    }

    private void loadImages(){

        listImageList = new ArrayList<>();
        imageListAdapter= new ImageListAdapter(mContext,listImageList);
        imageListRV.setAdapter(imageListAdapter);

        File folder = new File(mContext.getExternalFilesDir(null),Constants.IMAGES_FOLDER);
        if (folder.exists()){
            File[] files = folder.listFiles();
            if (files != null){
                for (File file: files){
                    Uri imageUri = Uri.fromFile(file);
                    ImageListModel model = new ImageListModel(imageUri,false);
                    listImageList.add(model);
                    imageListAdapter.notifyItemInserted(listImageList.size());//notify adapter that a new image is inserted
                }
            }
        }else {
            Toast.makeText(mContext, "Folder doesn't exists!", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveImageToAppLevelDirectory(Uri imageToBeSaved){
        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.getContentResolver(),imageToBeSaved));
            }else {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),imageToBeSaved);
            }

            File directory = new File(mContext.getExternalFilesDir(null),Constants.IMAGES_FOLDER);
            directory.mkdirs();

            long timestamp = System.currentTimeMillis();
            String fileName = timestamp+"jpeg";

            File file = new File(mContext.getExternalFilesDir(null),""+Constants.IMAGES_FOLDER+"/"+fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.flush();
                fos.close();
                Toast.makeText(mContext, "Image saved.", Toast.LENGTH_SHORT).show();

                loadImages();
            }catch (Exception e){
                Toast.makeText(mContext, "Failed to save image due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(mContext, "Failed to prepare image due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showInputImageDialog(){
        PopupMenu popupMenu = new PopupMenu(mContext,addImageFab);

        popupMenu.getMenu().add(Menu.NONE,1,1,"CAMERA");
        popupMenu.getMenu().add(Menu.NONE,2,2,"GALLERY");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == 1){
                    if (checkCameraPermission()){
                        pickImageFromCamera();
                    }else {
                        requestCameraPermission();
                    }

                }else if (itemId == 2){
                    if (checkStoragePermission()){
                        pickImageGallery();
                    }else {
                        requestStoragePermission();
                    }

                }
                return true;
            }
        });
    }
    private void pickImageGallery(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");

        galleryActivityResultLauncher.launch(pickIntent);

    }
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                      Intent data = result.getData();
                      imageUri = data.getData();
                      //save the picked image
                        saveImageToAppLevelDirectory(imageUri);

                        ImageListModel imageListModel = new ImageListModel(imageUri,false);
                        listImageList.add(imageListModel);
                        imageListAdapter.notifyItemInserted(listImageList.size());
                    }else{
                        Toast.makeText(mContext, "Cancelled.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void pickImageFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"TEMP IMAGE TITLE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"TEMP IMAGE DESC");

        imageUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);

        cameraActivityResultLauncher.launch(intent);
    }
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                       //Intent data = result.getData();
                        saveImageToAppLevelDirectory(imageUri);

                        ImageListModel imageListModel = new ImageListModel(imageUri,false);
                        listImageList.add(imageListModel);
                        imageListAdapter.notifyItemInserted(listImageList.size());

                    }else {
                        Toast.makeText(mContext, "Cancelled.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result;
    }
    private void requestStoragePermission(){
        requestPermissions(storagePermissions,REQUEST_CODE_FOR_STORAGE);


    }

    private boolean checkCameraPermission(){

        boolean resultForCamera = ContextCompat.checkSelfPermission(mContext,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED;
        boolean resultForStorage = ContextCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;


        return  resultForCamera && resultForStorage;
    }

    private void requestCameraPermission(){

        requestPermissions(cameraPermissions, REQUEST_CODE_FOR_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CODE_FOR_CAMERA:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickImageFromCamera();
                    }else{
                        Toast.makeText(mContext, "Permissions are required!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(mContext, "Cancelled.", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case REQUEST_CODE_FOR_STORAGE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickImageGallery();
                    }else {
                        Toast.makeText(mContext, "Permission required!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(mContext, "Cancelled.", Toast.LENGTH_SHORT).show();
                }

            }
            break;
        }
    }
}