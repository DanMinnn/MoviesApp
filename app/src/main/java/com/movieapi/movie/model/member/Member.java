package com.movieapi.movie.model.member;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Member {
    String name, avt, iduser, email;
    private DatabaseReference dataNodeMember;

    public Member(){
        dataNodeMember = FirebaseDatabase.getInstance().getReference().child("members");
    }

    public void InsertMember(Member member, String uid){
        dataNodeMember.child(uid).setValue(member, uid);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
