package com.example.android.networking;


import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class ImageAdaptor extends RecyclerView.Adapter<ImageAdaptor.ImageHolder>{

    private int NUM_IMAGES = 14;
    private Bitmap[] imageBitmaps = new Bitmap[NUM_IMAGES];

    @Override
    public ImageAdaptor.ImageHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View imageView = inflater.inflate(R.layout.view_image, viewGroup, false);
        return new ImageHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ImageAdaptor.ImageHolder imageHolder, int position){
        ImageView imageView = imageHolder.myImageView;
        imageView.setImageBitmap(imageBitmaps[position]);
    }

    @Override
    public int getItemCount(){return imageBitmaps.length;}

    public void setImage(Bitmap bitmap, int pos){
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
        imageBitmaps[pos] = scaledBitmap;
    }


    public class ImageHolder extends RecyclerView.ViewHolder{
        ImageView myImageView;
        public ImageHolder(View viewImage){
            super(viewImage);
            myImageView = (ImageView) viewImage.findViewById(R.id.image_view);
        }
    }

}
