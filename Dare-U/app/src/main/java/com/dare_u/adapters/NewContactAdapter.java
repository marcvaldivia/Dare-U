package com.dare_u.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.dare_u.behaviour.ListResponsive;
import com.dare_u.dare_u.R;
import com.dare_u.domain.Contact;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class NewContactAdapter extends ArrayAdapter<Contact> {

    private Activity context;
    private ListResponsive contextResponsive;
    private int resource;

    Contact[] contacts;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a View to use.
     * @param contacts Array with the contacts.
     */
    public NewContactAdapter(Activity context, int resource, Contact[] contacts) {
        super(context, resource, contacts);
        this.context = context;
        this.contextResponsive = (ListResponsive) context;
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
        View item = inflater.inflate(resource, null);

        // Capital Letter
        TextView txtCapital = (TextView) item.findViewById(R.id.txtCapital);
        txtCapital.setText(contacts[position].getContact().substring(0, 1).toUpperCase());

        // Contact Name
        TextView txtContact = (TextView) item.findViewById(R.id.txtContact);
        txtContact.setText(contacts[position].getContact().toLowerCase());

        // Add Contact
        final ImageButton btnAddContact = (ImageButton) item.findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);
                contextResponsive.clickResponse(v, contacts[position]);
            }
        });

        return item;
    }
}
