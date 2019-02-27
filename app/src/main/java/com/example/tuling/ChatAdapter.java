package com.example.tuling;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.*;

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private List<PersonChat> lists;

    public ChatAdapter(Context context, List<PersonChat> lists) {
        this.context = context;
        this.lists = lists;
    }

    public int getCount() {
        return this.lists.size();
    }

    public Object getItem(int arg0) {
        return this.lists.get(arg0);
    }

    public long getItemId(int arg0) {
        return (long)arg0;
    }

    public int getItemViewType(int position) {
        PersonChat entity = (PersonChat)this.lists.get(position);
        return entity.isMeSend() ? 0 : 1;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ChatAdapter.HolderView holderView = null;
        PersonChat entity = (PersonChat)this.lists.get(arg0);
        boolean isMeSend = entity.isMeSend();
        if (holderView == null) {
            holderView = new ChatAdapter.HolderView();
            if (isMeSend) {
                arg1 = View.inflate(this.context, R.layout.chat_dialog_right_item, (ViewGroup)null);
                holderView.tv_chat_me_message = (TextView)arg1.findViewById(R.id.tv_chat_me_message);
                holderView.tv_chat_me_message.setText(entity.getChatMessage());
            } else {
                arg1 = View.inflate(this.context, R.layout.chat_dialog_left_item, (ViewGroup)null);
                holderView.tvname = (TextView)arg1.findViewById(R.id.tvname);
                holderView.tvname.setText(entity.getChatMessage());
            }

            arg1.setTag(holderView);
        } else {
            holderView = (ChatAdapter.HolderView)arg1.getTag();
        }

        return arg1;
    }

    public boolean isEnabled(int position) {
        return false;
    }

    class HolderView {
        TextView tv_chat_me_message;
        TextView tvname;

        HolderView() {
        }
    }

    public interface IMsgViewType {
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }
}