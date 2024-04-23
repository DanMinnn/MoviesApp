package com.movieapi.movie.controller;

import com.movieapi.movie.model.member.Member;

public class SignUpController {
    Member member;

    public SignUpController() {
        member = new Member();
    }

    public void InsertMemberController(Member member, String uid){
        member.InsertMember(member, uid);
    }
}
