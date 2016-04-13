package com.dare_u.adapters;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dare_u.dare_u.R;
import com.dare_u.domain.Challenge;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class ChallengeAdapter extends ArrayAdapter<Challenge> {

    private Activity context;
    private int resource;

    int userId;
    Challenge[] challenges;

    int[] icons = new int[]{R.drawable.ic_remove_red_eye_blue_48dp, R.drawable.ic_close_red_48dp, R.drawable.ic_camera_alt_black_48dp,};

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a View to use.
     * @param userId The id of the user logged on the app.
     * @param challenges Array with the challenges.
     */
    public ChallengeAdapter(Activity context, int resource, int userId, Challenge[] challenges) {
        super(context, resource, challenges);
        this.context = context;
        this.resource = resource;
        this.userId = userId;
        this.challenges = challenges;
    }

    /**
     * Method to generate the custom listView
     *
     * @param position int
     * @param view View
     * @param parent ViewGroup
     * @return View
     */
    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(resource, null);

        Challenge challenge = challenges[position];

        // ImageView
        ImageView ivTurn = (ImageView) item.findViewById(R.id.ivTurn);

        // Title
        TextView txtTitle = (TextView) item.findViewById(R.id.txtTitle);

        // Resume
        TextView txtResume = (TextView) item.findViewById(R.id.txtResume);

        if (challenge.getUserId() == userId) {
            if (challenge.getTurn() == 1) {
                ivTurn.setImageResource(icons[2]);

                String title = challenge.getContact();
                txtTitle.setText(title.toLowerCase());

                String resume = challenge.getChallenge();
                txtResume.setText(Html.fromHtml(resume));
            } else {
                return new View(context);
            }
        } else {
            int turn = challenge.getTurn();
            ivTurn.setImageResource(icons[turn]);

            String title = challenge.getUsername();
            txtTitle.setText(title.toLowerCase());

            if (challenge.getTurn() == 1) {
                txtResume.setText(R.string.challenges_waiting);
            } else {
                String resume = challenge.getChallenge();
                txtResume.setText(Html.fromHtml(resume));
            }
        }

        return item;
    }
}
