package com.quickblox.q_municate.ui.fragments.search;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.q_municate.R;
import com.quickblox.q_municate.ui.adapters.search.AllUsersAdapter;
import com.quickblox.q_municate.ui.adapters.search.GlobalSearchAdapter;
import com.quickblox.q_municate.ui.fragments.base.BaseFragment;
import com.quickblox.q_municate.ui.views.recyclerview.SimpleDividerItemDecoration;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

public class AllUsersFragment extends BaseFragment {

    private static final String TAG = AllUsersFragment.class.getSimpleName();

    @Bind(R.id.all_contacts_recyclerview)
    RecyclerView contactsRecyclerView;

    private List<QBUser> usersList;
    private AllUsersAdapter allUsersAdapter;

    public static AllUsersFragment newInstance() {
        return new AllUsersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_all_users, container, false);

        ButterKnife.bind(this, view);

        initFields();
        initContactsList(usersList);
        searchUsers();
        //initCustomListeners();

        //addActions();
        //addObservers();

        return view;
    }

    private void initFields() {
        usersList = new ArrayList<>();
    }

    private void initContactsList(List<QBUser> usersList) {
        allUsersAdapter = new AllUsersAdapter(baseActivity, usersList);
        allUsersAdapter.setFriendListHelper(friendListHelper);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //contactsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));;
        contactsRecyclerView.setAdapter(allUsersAdapter);
        //allUsersAdapter.setUserOperationListener(userOperationAction);
    }

    @Override
    public void onConnectedToService(QBService service) {
        super.onConnectedToService(service);
        if (friendListHelper != null && allUsersAdapter != null) {
            allUsersAdapter.setFriendListHelper(friendListHelper);
        }
    }

    public void searchUsers() {
        Log.i("XXX", "searchUsers()");
        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPage(1);
        requestBuilder.setPerPage(ConstsCore.FL_FRIENDS_PER_PAGE);

        QMUserService.getInstance().getUsers(requestBuilder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<List<QMUser>>(){

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        Log.i("XXX", "onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError " + e.getMessage());
                        Log.i("XXX", "onError " + e.getMessage());
                    }

                    @Override
                    public void onNext(List<QMUser> qmUsers) {
                        Log.i("XXX", "onNext()");
                        if (qmUsers != null && !qmUsers.isEmpty()) {
                            checkForExcludeMe(qmUsers);

                            usersList.addAll(qmUsers);
                            Log.i("XXX", "usersList = " + usersList.size());
                            //updateContactsList(usersList);
                        }
                    }
                });
    }

    private void checkForExcludeMe(Collection<QMUser> usersCollection) {
        QBUser qbUser = AppSession.getSession().getUser();
        QMUser me = QMUser.convert(qbUser);
        if (usersCollection.contains(me)) {
            usersCollection.remove(me);
        }
    }
}

