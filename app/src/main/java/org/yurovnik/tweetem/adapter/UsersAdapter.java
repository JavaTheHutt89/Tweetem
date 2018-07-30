package org.yurovnik.tweetem.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.yurovnik.tweetem.R;
import org.yurovnik.tweetem.pojo.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    private onUserClickListener userClickListener;
    private List<User> userList = new ArrayList<>();

    public UsersAdapter(onUserClickListener userClickListener) {
        this.userClickListener = userClickListener;
    }

    public void setItems(Collection<User> users){
        userList.addAll(users);
        notifyDataSetChanged();
    }

    public void clearItems(){
        userList.clear();
        notifyDataSetChanged();
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item_view, parent, false);

        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, int position) {
        holder.bind(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

        private ImageView userImageView;
        private TextView nameTextView;
        private TextView nickTextView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.profile_image_view);
            nameTextView = itemView.findViewById(R.id.user_name_text_view);
            nickTextView = itemView.findViewById(R.id.user_nick_text_view);

            itemView.setOnClickListener(view -> {
                User user = userList.get(getLayoutPosition());
                userClickListener.onUserClick(user);
            });
        }

        private void bind(User user){
            nameTextView.setText(user.getName());
            nickTextView.setText(user.getNick());
            Picasso
                    .with(itemView.getContext())
                    .load(user.getImageUrl())
                    .into(userImageView);
        }
    }

    public interface onUserClickListener {
        void onUserClick(User user);
    }
}
