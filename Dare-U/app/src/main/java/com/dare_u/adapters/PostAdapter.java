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
import com.dare_u.domain.Post;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class PostAdapter extends ArrayAdapter<Post> {

    private Activity context;
    private int resource;

    Post[] posts;

    int[] icons = new int[]{R.drawable.ic_remove_red_eye_blue_48dp, R.drawable.ic_close_red_48dp, R.drawable.ic_camera_alt_black_48dp,};

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a View to use.
     * @param posts Array with the posts.
     */
    public PostAdapter(Activity context, int resource, Post[] posts) {
        super(context, resource, posts);
        this.context = context;
        this.resource = resource;
        this.posts = posts;
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

        Post post = posts[position];

        // ImageView
        ImageView ivTurn = (ImageView) item.findViewById(R.id.ivTurn);
        ivTurn.setImageResource(icons[0]);

        // Title
        TextView txtTitle = (TextView) item.findViewById(R.id.txtTitle);
        String title = post.getUsername();
        txtTitle.setText(title.toLowerCase());

        // Resume
        TextView txtResume = (TextView) item.findViewById(R.id.txtResume);
        String resume = post.getChallenge();
        txtResume.setText(Html.fromHtml(resume));

        return item;
    }
}
