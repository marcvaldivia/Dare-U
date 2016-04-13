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
import com.dare_u.utils.General;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class ContactSpinnerAdapter extends ArrayAdapter<Contact> {

    private Activity context;
    private int resource;

    Contact[] contacts;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a View to use.
     * @param contacts Array with the contacts.
     */
    public ContactSpinnerAdapter(Activity context, int resource, Contact[] contacts) {
        super(context, resource, contacts);
        this.context = context;
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

        return item;
    }

    @Override
     public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
