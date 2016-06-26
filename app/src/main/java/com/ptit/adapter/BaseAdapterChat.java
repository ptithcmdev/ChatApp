package com.ptit.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ptit.appchatnodejs.R;
import com.ptit.model.Message;
import com.ptit.utils.CircleImageBitmap;
import com.ptit.utils.ConvertImage;

import java.util.List;


public class BaseAdapterChat extends BaseAdapter {
    Activity context;
    List<Message> objects;
    LayoutInflater inflater;


    public BaseAdapterChat(Activity context, List<Message> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private static class ViewHolder{
        ImageView imgHinh;
        TextView txtMsg;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Message msg = objects.get(position);
        // Identifying the message owner
        inflater = context.getLayoutInflater();
        if (objects.get(position).isSelf()) {
            // message belongs to you, so load the right aligned layout
            convertView = inflater.inflate(R.layout.chat_item_right,null);

        } else {
            // message belongs to other person, load the left aligned layout
            convertView = inflater.inflate(R.layout.chat_item_left,null);
        }
        viewHolder = new ViewHolder();
        viewHolder.imgHinh = (ImageView) convertView.findViewById(R.id.imgAvatar);
        viewHolder.txtMsg = (TextView) convertView.findViewById(R.id.msgr);
        viewHolder.txtMsg.setText(msg.getMessage());


        ConvertImage.String_to_Image(msg.getImg(), viewHolder.imgHinh);

        Bitmap bitmap = ((BitmapDrawable)viewHolder.imgHinh.getDrawable()).getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
        bitmap = CircleImageBitmap.getCircleBitmap(bitmap);
        viewHolder.imgHinh.setImageBitmap(bitmap);

        return convertView;
    }

}
