package com.example.demoapp.view.fragment.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.example.demoapp.R;
import com.example.demoapp.adapter.chat.GroupChatListAdapter;
import com.example.demoapp.databinding.FragmentGroupChatsBinding;
import com.example.demoapp.model.GroupChatList;
import com.example.demoapp.view.activity.MainActivity;
import com.example.demoapp.view.activity.chat.GroupCreateActivity;
import com.example.demoapp.view.activity.chat.NotificationsActivity;
import com.example.demoapp.view.activity.chat.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GroupChatsFragment extends Fragment {

    private FragmentGroupChatsBinding binding;

    private FirebaseAuth firebaseAuth;
    private ArrayList<GroupChatList> groupChatLists;
    private GroupChatListAdapter groupChatListAdapter;

    public GroupChatsFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGroupChatsBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        loadGroupChatslist();


        return binding.getRoot();
    }

    private void loadGroupChatslist() {
        groupChatLists = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatLists.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    // if current user's uid exists in participants lis of group the show that group
                    if(ds.child("Participants").child(firebaseAuth.getUid()).exists()){
                        GroupChatList groupChat = ds.getValue(GroupChatList.class);
                        groupChatLists.add(groupChat);
                    }
                }
                groupChatListAdapter = new GroupChatListAdapter(getActivity(), groupChatLists);
                binding.rvGroups.setAdapter(groupChatListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void searchGroupChatslist(String query) {
        groupChatLists = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatLists.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    // if current user's uid exists in participants lis of group the show that group
                    if(ds.child("Participants").child(firebaseAuth.getUid()).exists()){

                        //searchby group title
                        if(ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())) {
                            GroupChatList groupChat = ds.getValue(GroupChatList.class);
                            groupChatLists.add(groupChat);

                        }
                    }

                }
                groupChatListAdapter = new GroupChatListAdapter(getActivity(), groupChatLists);
                binding.rvGroups.setAdapter(groupChatListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        // hide addpost icon from this fragment
        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_add_participant).setVisible(false);
        menu.findItem(R.id.action_groupinfo).setVisible(false);
        // search view
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        // search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // if search query is not empty the search
                if (!TextUtils.isEmpty(query.trim())) {
                    // search text contains text, search it
                    searchGroupChatslist(query);
                } else {
                    // search text empty, get all users
                    loadGroupChatslist();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText.trim())) {
                    // search text contains text, search it
                    searchGroupChatslist(newText);
                } else {
                    // search text empty, get all users
                    loadGroupChatslist();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    // handle meni item click

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }else if(id == R.id.action_settings){
            // go to settings activity
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }else if(id==R.id.action_create_group){
            // go to group chat
            startActivity(new Intent(getActivity(), GroupCreateActivity.class));
        }else if(id==R.id.nav_notification){
            // go to notificationActivity
            startActivity(new Intent(getActivity(), NotificationsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null);
        // user not signed in, go to main activity
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }
}