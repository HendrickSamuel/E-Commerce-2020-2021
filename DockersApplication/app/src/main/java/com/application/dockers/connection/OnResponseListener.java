package com.application.dockers.connection;

import protocol.IOBREP.ReponseIOBREP;

public interface OnResponseListener {
    public void OnResponse(ReponseIOBREP reponseIOBREP);
    public void OnError(String message);
}
