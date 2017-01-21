package com.stairway.spotlight.screens.new_chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stairway.data.config.Logger;
import com.stairway.spotlight.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vidhun on 01/09/16.
 */
public class NewChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ContactClickListener contactClickListener;
    private List<NewChatItemModel> itemList;
    private final int CONTACT=1;

    private List<Integer> filteredList;

    public NewChatAdapter(ContactClickListener contactClickListener, List<NewChatItemModel> contacts) {
        this.contactClickListener = contactClickListener;
        this.itemList = new ArrayList<>();
        this.itemList.addAll(contacts);
    }

    public void setContacts(List<NewChatItemModel> contacts) {
        this.itemList.clear();
        this.itemList.addAll(contacts);
        this.notifyItemRangeInserted(0, itemList.size() - 1);
    }

    public void addContact(NewChatItemModel contact) {
        itemList.add(contact);
        this.notifyItemInserted(itemList.size()-1);
    }

    public void addContacts(List<NewChatItemModel> contacts) {
        int position = itemList.size()-1;
        itemList.addAll(contacts);
        this.notifyItemRangeChanged(position, itemList.size());
    }

    public void filterList(String query) {
        filteredList = new ArrayList<>();

        for (NewChatItemModel newChatItemModel : itemList)
            if(newChatItemModel.getContactName().toLowerCase().matches(query+".*") || newChatItemModel.getContactName().matches(".* "+query+".*")) {
                filteredList.add(itemList.indexOf(newChatItemModel));
                Logger.d(this, "Filtering: "+newChatItemModel.toString()+" at "+itemList.indexOf(newChatItemModel));
            }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return CONTACT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case CONTACT:
                View contactView = inflater.inflate(R.layout.item_contact, parent, false);
                viewHolder = new ContactsViewHolder(contactView);
                break;
            default:
                return null;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int vPos) {
        int position = vPos;
        if(filteredList!=null)
            position = filteredList.get(vPos);

        switch (holder.getItemViewType()) {
            case CONTACT:
                ContactsViewHolder cVH = (ContactsViewHolder) holder;
                cVH.renderItem(itemList.get(position));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(filteredList!=null)
            return filteredList.size();
        return itemList.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_chatItem_content)
        LinearLayout contactListContent;

        @Bind(R.id.iv_chatItem_profileImage)
        ImageView profileImage;

        @Bind(R.id.tv_chatItem_contactName)
        TextView contactName;

        @Bind(R.id.tv_chatItem_message)
        TextView status;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            contactListContent.setOnClickListener(view -> {
                if(contactClickListener != null)
                    contactClickListener.onContactItemClicked(contactName.getTag().toString());
            });
        }

        public void renderItem(NewChatItemModel contactItem) {
            contactName.setText(contactItem.getContactName());
            status.setText("ID:"+contactItem.getUserId());

            contactName.setTag(contactItem.getUserName());
        }
    }

    public interface ContactClickListener {
        void onContactItemClicked(String userId);
    }
}
