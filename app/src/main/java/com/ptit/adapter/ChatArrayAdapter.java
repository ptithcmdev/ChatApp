package com.ptit.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ptit.appchatnodejs.R;
import com.ptit.model.ChatRoom;
import com.ptit.model.Message;
import com.ptit.utils.CircleImageBitmap;
import com.ptit.utils.ConvertImage;

import java.util.List;

/**
 * Created by Phan Anh on 6/24/2016.
 */
public class ChatArrayAdapter extends ArrayAdapter<Message> {
    Activity context;
    int resource;
    List<Message> objects;
    LayoutInflater inflater;
    public ChatArrayAdapter(Activity context, int resource, List<Message> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Message getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(Message item) {
        return super.getPosition(item);
    }
    private static class ViewHolder{
        ImageView imgHinh;
        TextView txtMsg;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        super.getView(position, convertView, parent);

        ViewHolder viewHolder;

        if (convertView == null)
        {
             inflater = context.getLayoutInflater();
            viewHolder = new ViewHolder();
            viewHolder.imgHinh = (ImageView) convertView.findViewById(R.id.imgAvatar);
            viewHolder.txtMsg = (TextView) convertView.findViewById(R.id.msgr);

            convertView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }




       Message msg = objects.get(position);


        // Identifying the message owner
        if (objects.get(position).isSelf()) {
            // message belongs to you, so load the right aligned layout
            convertView = inflater.inflate(R.layout.chat_item_right,
                    null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = inflater.inflate(R.layout.chat_item_left,
                    null);
        }


        viewHolder.txtMsg.setText(msg.getMessage().toString());


        ConvertImage.String_to_Image(msg.getImg(), viewHolder.imgHinh);

        Bitmap bitmap = ((BitmapDrawable)viewHolder.imgHinh.getDrawable()).getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
        bitmap = CircleImageBitmap.getCircleBitmap(bitmap);
        viewHolder.imgHinh.setImageBitmap(bitmap);

        return convertView;



    }
}
