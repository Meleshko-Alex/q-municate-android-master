package com.quickblox.q_municate.ui.adapters.search;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.q_municate.R;
import com.quickblox.q_municate.ui.activities.base.BaseActivity;
import com.quickblox.q_municate.ui.views.roundedimageview.RoundedImageView;
import com.quickblox.q_municate.utils.DateUtils;
import com.quickblox.q_municate.utils.image.ImageLoaderUtils;
import com.quickblox.q_municate_core.models.UserCustomData;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;
import com.quickblox.q_municate_core.utils.OnlineStatusUtils;
import com.quickblox.q_municate_core.utils.Utils;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.model.QBUser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.ViewHolder>{

    private final List<QBUser> usersList;
    private final BaseActivity baseActivity;
    private QBFriendListHelper friendListHelper;

    public AllUsersAdapter(BaseActivity baseActivity, List<QBUser> usersList) {
        this.usersList = usersList;
        this.baseActivity = baseActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_one_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllUsersAdapter.ViewHolder holder, int position) {
        QBUser user = usersList.get(position);

        if (user.getFullName() != null) {
            holder.fullNameTextView.setText(user.getFullName());
        } else {
            holder.fullNameTextView.setText(user.getId());
        }

        final UserCustomData userCustomData = Utils.customDataToObject(user.getCustomData());
        String avatarUrl = userCustomData.getAvatarUrl();
        displayAvatarImage(avatarUrl, holder.avatarImageView);

        setOnlineStatus(holder, user);

        //holder.fullNameTextView.setText().getFullName());
    }

    private void displayAvatarImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void setOnlineStatus(AllUsersAdapter.ViewHolder viewHolder, QBUser user) {
        boolean online = friendListHelper != null && friendListHelper.isUserOnline(user.getId());

        if (online) {
            viewHolder.statusTextView.setText(OnlineStatusUtils.getOnlineStatus(online));
            viewHolder.statusTextView.setTextColor(baseActivity.getResources().getColor(R.color.red));
        } else {
            QMUser userFromDb = QMUserService.getInstance().getUserCache().get((long) user.getId());
            if (userFromDb != null){
                user = userFromDb;
            }

            viewHolder.statusTextView.setText(baseActivity.getResources().getString(R.string.last_seen,
                    DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt().getTime()),
                    DateUtils.formatDateSimpleTime(user.getLastRequestAt().getTime())));
            viewHolder.statusTextView.setTextColor(baseActivity.getResources().getColor(R.color.dark_gray));
        }
    }

    public void setFriendListHelper(QBFriendListHelper friendListHelper) {
        this.friendListHelper = friendListHelper;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.avatar_imageview)
        RoundedImageView avatarImageView;

        @Bind(R.id.name_textview)
        TextView fullNameTextView;

        @Bind(R.id.status_textview)
        TextView statusTextView;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
