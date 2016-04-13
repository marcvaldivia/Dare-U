package com.dare_u.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dare_u.behaviour.ListResponsive;
import com.dare_u.dare_u.R;
import com.dare_u.domain.Contact;
import com.dare_u.domain.Header;
import com.dare_u.utils.General;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class ContactAdapter extends ArrayAdapter<Object> {

    private Activity context;
    private ListResponsive contextResponsive;
    private int resource;

    Object[] contacts;

    int[] icons = new int[]{R.drawable.ic_star_border_black_48dp, R.drawable.ic_star_yellow_48dp};

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param contextResponsive The current context where the click action is activated.
     * @param resource The resource ID for a layout file containing a View to use.
     * @param contacts Array with the contacts.
     */
    public ContactAdapter(Activity context, ListResponsive contextResponsive, int resource, Object[] contacts) {
        super(context, resource, contacts);
        this.context = context;
        this.contextResponsive = contextResponsive;
        this.resource = resource;
        this.contacts = contacts;
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
        View item;

        Object object = contacts[position];

        if (object instanceof Header) {
            item = inflater.inflate(R.layout.listview_header, null);

            Header header = (Header) object;

            // Title
            TextView txtTitle = (TextView) item.findViewById(R.id.txtHeader);
            txtTitle.setText(header.getTitle().toUpperCase());
        } else {
            item = inflater.inflate(resource, null);

            Contact contact = (Contact) object;

            // Capital Letter
            //TextView txtCapital = (TextView) item.findViewById(R.id.txtCapital);
            //txtCapital.setText(contact.getContact().substring(0, 1).toUpperCase());

            // Contact Name
            TextView txtContact = (TextView) item.findViewById(R.id.txtContact);
            txtContact.setText(contact.getContact().toLowerCase());

            // Favorite
            ImageView favorite = (ImageView) item.findViewById(R.id.favorite);
            int star = General.booleanToInt(contact.isFavorite());
            favorite.setImageResource(icons[star]);

            // Add Favorite
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent();
                    final int position = listView.getPositionForView(parentRow);
                    contextResponsive.clickResponse(v, (Contact) contacts[position]);
                }
            });
        }

        return item;
    }

    @Override
     public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
