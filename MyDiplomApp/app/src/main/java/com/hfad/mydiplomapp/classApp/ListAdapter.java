package com.hfad.mydiplomapp.classApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hfad.mydiplomapp.R;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<User> {
    private static class ViewHolder {
        TextView fio;
        TextView room;
    }

    private FrameLayout userKick;
    private TextView userKickInfo;
    public User userSelected = null;

    private User userMain;
    public User getUserId() {
        return userMain;
    }

    public ListAdapter(Context context, ArrayList<User> userArrayList, FrameLayout userKick, TextView userKickInfo) {
        super(context, R.layout.list_item_users, userArrayList);
        this.userKick = userKick;
        this.userKickInfo = userKickInfo;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);
        userMain = getItem(position);
        ViewHolder viewHolder;

        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("User");
        DatabaseReference applicationDB = FirebaseDatabase.getInstance().getReference("Application");;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_users, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.fio = convertView.findViewById(R.id.userInfoItem);
            viewHolder.room = convertView.findViewById(R.id.userRoomItem);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.fio.setText(user.getLastName() + " " + user.getName().charAt(0) + ". " + user.getSureName().charAt(0) + ".");
        viewHolder.room.setText(user.getRoom());

        Button kickButton = convertView.findViewById(R.id.buttonKick);
        kickButton.setVisibility(View.VISIBLE);
        kickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSelected = user;
                userKickInfo.setText(user.getLastName() + " " + user.getName().charAt(0) + ". " + user.getSureName().charAt(0) + ".");
                userKick.setVisibility(View.VISIBLE);
            }
        });

        return convertView;
    }
}
