package com.manuni.imagetopdf;

import android.net.Uri;

public class ImageListModel {
    Uri imageUri;
    boolean checked;

    public ImageListModel(Uri imageUri, boolean checked) {
        this.imageUri = imageUri;
        this.checked = checked;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
