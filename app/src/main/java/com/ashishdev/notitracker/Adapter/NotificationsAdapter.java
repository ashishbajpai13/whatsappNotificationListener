package com.ashishdev.notitracker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishdev.notitracker.Async.NotificationDeleteAsync;
import com.ashishdev.notitracker.Database.Notification;
import com.ashishdev.notitracker.R;
import com.ashishdev.notitracker.Utils.TimeUtils;
import com.ashishdev.notitracker.ViewModel.NotificationViewModel;
import com.ashishdev.notitracker.databinding.CustomNotificationItemBinding;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {
    private Activity activity;
    private List<Notification> notificationList;
    private CustomNotificationItemBinding itemBinding;
    NotificationViewModel notificationViewModel;

    public NotificationsAdapter(Activity activity, List<Notification> notificationList, NotificationViewModel notificationViewModel) {
        this.activity = activity;
        this.notificationViewModel = notificationViewModel;
        this.notificationList = notificationList;
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding.deleteNoti.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.delete_noti:
                    new NotificationDeleteAsync(notificationViewModel, false,
                            notificationList.get(getAdapterPosition()).getConversationTitle(),
                            notificationList.get(getAdapterPosition()).getConversationText(),
                            notificationList.get(getAdapterPosition()).getTimeStamp(),
                            notificationList.get(getAdapterPosition()).getId(),
                            notificationList.get(getAdapterPosition()).getPhoneNo()).execute();
                    break;
            }
        }
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = CustomNotificationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(itemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        itemBinding.notificationTitle.setText(notificationList.get(position).getConversationTitle());
        itemBinding.notificationText.setText(String.format(activity.getString(R.string.message), notificationList.get(position).getConversationText()));
        if (notificationList.get(position).getTimeStamp() != null)
            itemBinding.timeStamp.setText(new TimeUtils().timeAgo(notificationList.get(position).getTimeStamp()));
        if (!notificationList.get(position).getPhoneNo().equals(activity.getString(R.string.unsaved)))
            itemBinding.contactPhNo.setText(notificationList.get(position).getPhoneNo());
        else itemBinding.contactPhNo.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

}
