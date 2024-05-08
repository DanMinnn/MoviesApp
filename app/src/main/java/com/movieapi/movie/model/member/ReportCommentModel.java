package com.movieapi.movie.model.member;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportCommentModel {
    String idCmt, idUser, content, nameUser, idReport;


    public ReportCommentModel(String idCmt, String idUser, String content, String nameUser) {
        this.idCmt = idCmt;
        this.idUser = idUser;
        this.content = content;
        this.nameUser = nameUser;
    }

    public ReportCommentModel() {
    }

    public String getIdCmt() {
        return idCmt;
    }

    public void setIdCmt(String idCmt) {
        this.idCmt = idCmt;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public void reportComments(ReportCommentModel reportCommentModel){
        DatabaseReference nodeReport = FirebaseDatabase.getInstance().getReference().child("reports");
        idReport = nodeReport.push().getKey();

        nodeReport.child(idReport).setValue(reportCommentModel);
    }
}
