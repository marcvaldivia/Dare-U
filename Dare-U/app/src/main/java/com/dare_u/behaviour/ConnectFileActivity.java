package com.dare_u.behaviour;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public interface ConnectFileActivity extends ConnectActivity {

    /**
     * Progress update when the app send a file to the server.
     *
     * @param num
     */
    void onUpdate(int num);

}
