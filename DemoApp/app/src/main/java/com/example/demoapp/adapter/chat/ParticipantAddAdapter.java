package com.example.demoapp.adapter.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.R;
import com.example.demoapp.model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ParticipantAddAdapter extends RecyclerView.Adapter<ParticipantAddAdapter.HolderParticipantAdd> {

    private Context context;
    private ArrayList<Users> usersList;
    private String groupId, myGroupRole;

    public ParticipantAddAdapter(Context context, ArrayList<Users> usersList, String groupId, String myGroupRole) {
        this.context = context;
        this.usersList = usersList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public HolderParticipantAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_participant_add, parent, false);
        return new HolderParticipantAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantAdd holder, int position) {
        //get data
        Users users = usersList.get(position);
        String name = users.getName();
        String email = users.getEmail();
        String image = users.getImage();
        String uid = users.getUid();

        //set data
        holder.tvName.setText(name);
        holder.tvEmail.setText(email);
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_face).into(holder.ivAvatar);
        } catch (Exception e) {
            holder.ivAvatar.setImageResource(R.drawable.ic_face);
        }

        checkIfAlreadyExitst(users, holder);

        //handle click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Check if user already added or not
                if added: show remove-participant/make-admin option( Admin will not able to change role of creator)
                if not added, show add participant option
                 */
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                ref.child(groupId).child("Participants").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    //user exists/ not participant
                                    String hisPreviousRole = "" + snapshot.child("role").getValue();

                                    //option to display in dialog
                                    String[] option;

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Option");
                                    if (myGroupRole.equals("creator")) {
                                        if (hisPreviousRole.equals("admin")) {
                                            //in creator, he is admin
                                            option = new String[]{"Remove Admin", "Remove User"};
                                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //handle item click
                                                    if (which == 0) {
                                                        //Remove Admin clicked
                                                        removeAdmin(users);
                                                    } else {
                                                        //Remove User clicked
                                                        removeParticipant(users);
                                                    }
                                                }
                                            }).show();
                                        } else if (hisPreviousRole.equals("participant")) {
                                            //in creator, he is participant
                                            option = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //handle item click
                                                    if (which == 0) {
                                                        //Make Admin clicked
                                                        makeAdmin(users);
                                                    } else {
                                                        //Remove User clicked
                                                        removeParticipant(users);
                                                    }
                                                }
                                            }).show();
                                        }
                                    } else if (myGroupRole.equals("admin")) {
                                        if (hisPreviousRole.equals("creator")) {
                                            //in admin, he is creator
                                            Toast.makeText(context, "Creator of Group...", Toast.LENGTH_SHORT).show();
                                        } else if (hisPreviousRole.equals("admin")) {
                                            // in admin, he is admin too
                                            option = new String[]{"Remove Admin", "Remove User"};
                                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //handle item click
                                                    if (which == 0) {
                                                        //Remove Admin clicked
                                                        removeAdmin(users);
                                                    } else {
                                                        //Remove User clicked
                                                        removeParticipant(users);
                                                    }
                                                }
                                            }).show();
                                        } else if (hisPreviousRole.equals("participant")) {
                                            option = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(option, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //handle item click
                                                    if (which == 0) {
                                                        //Make Admin clicked
                                                        makeAdmin(users);
                                                    } else {
                                                        //Remove User clicked
                                                        removeParticipant(users);
                                                    }
                                                }
                                            }).show();
                                        }
                                    }
                                } else {
                                    // user doesn't exists/ not-participant: add
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Add Participant")
                                            .setMessage("Add this user in this group?")
                                            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //add user
                                                    addParticipant(users);
                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

    }

    private void addParticipant(Users users) {
        //setup user data - add user in group
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm aa");
        String date = df.format(Calendar.getInstance().getTime());
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", users.getUid());
        hashMap.put("role", "participant");
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("timemessage", date);
        //add that user in Group>groupId>Participants
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(users.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //added successfully
                        Toast.makeText(context, "Added successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed adding user in group
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeAdmin(Users users) {
        //setup data - change role
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "admin");//role are: participant/admin/creator
        //update role in database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(users.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //make admin
                        Toast.makeText(context, "The user is now admin...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //dailed making admin
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeParticipant(Users users) {
        //remove participant from group
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(users.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //remove successfully
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //failed removing participant
                    }
                });
    }

    private void removeAdmin(Users users) {
        //setup data - remove admin - just change role
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "participant");//role are: participant/admin/creator
        //update role in database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(users.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //make admin
                        Toast.makeText(context, "The user is no longer admin...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //dailed making admin
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfAlreadyExitst(Users users, HolderParticipantAdd holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(users.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //already exists
                            String hisRole = "" + snapshot.child("role").getValue();
                            holder.tvStatus.setText(hisRole);
                        } else {
                            //doesn't exists
                            holder.tvStatus.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class HolderParticipantAdd extends RecyclerView.ViewHolder {

        private ImageView ivAvatar;
        private TextView tvName, tvEmail, tvStatus;

        public HolderParticipantAdd(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.profile_image);
            tvName = itemView.findViewById(R.id.tv_name_item);
            tvEmail = itemView.findViewById(R.id.tv_email_item);
            tvStatus = itemView.findViewById(R.id.tv_status);

        }
    }
}
