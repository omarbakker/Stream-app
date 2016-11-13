package com.test.stream.stream.UIFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.test.stream.stream.Controllers.BoardManager;

import com.test.stream.stream.Objects.Board.Pin;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.PinDetailActivity;
import com.test.stream.stream.UI.ToolbarActivity;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;
import com.test.stream.stream.Utilities.PinAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardFragment extends ListFragment {

    ArrayList<Pin> pins = new ArrayList();
    private PinAdapter pinAdapter;
    ImageButton floatButton;
    private DataEventListener dataListener = new DataEventListener() {
        @Override
        public void onDataChanged() {
            updateUI();
        }
    };


    public BoardFragment() {
        // Required empty public constructor
    }

    /**
     * When the Fragment is created
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Call database to populate board if any PinMessages are in the database
        BoardManager.sharedInstance().InitializePins(dataListener);
    }

    /**
     * When View is created inflate the elements on the PinBoard
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the View with populated data
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pin, container, false);

        return view;
    }

    /**
     * Function that updates the Adapter of the ListFragment
     */
    private void updateUI() {
        // Get all pins from the databse
        List<Pin> allPins = BoardManager.sharedInstance().GetPinsInProject();
        ArrayList<Pin> pins = new ArrayList();
        // Go through each pin in database
        for (Pin currentPin : allPins) {
            pins.add(currentPin);
        }
        // Reverse Pin order to show newly created on top
        Collections.reverse(pins);
        if (pinAdapter == null) {
            // If nothing in adapter then create a new one and set the adapter to show pins
            pinAdapter = new PinAdapter(getActivity(), this.pins);
            setListAdapter(pinAdapter);
        // Otherwise add all the pins in the current adapter and notify that adapter changed
        } else {
            pinAdapter.clear();
            pinAdapter.addAll(pins);
            pinAdapter.notifyDataSetChanged();
        }

    }

    /**
     * When the activity is created, make sure the ImageButton is loaded
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pinAdapter = new PinAdapter(getActivity(), pins);
        setListAdapter(pinAdapter);
        floatButton = (ImageButton) getView().findViewById(R.id.pinImageButton);
        // Listener to listen to when ImageButton for popup dialog is clicked
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_pin_dialog, null);
                // TextFields of the Popup Dialog
                final EditText titleText = (EditText) view.findViewById(R.id.dialog_title);
                final EditText subtitleText = (EditText) view.findViewById(R.id.dialog_subtitle);
                final EditText descriptionText = (EditText) view.findViewById(R.id.dialog_description);
                // Create a popup Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setView(view)
                        // When clicking Submit button after populating fields
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Add the title, subtitle, description fields into a PinMessage object
                                String title = titleText.getText().toString();
                                String subtitle = subtitleText.getText().toString();
                                String description = descriptionText.getText().toString();
                                if(title.equals("") || subtitle.equals("") || description.equals("")){
                                    Toast.makeText(getActivity(), "Title, subtitle or description is empty. Please fill in fields.", Toast.LENGTH_LONG).show();
                                } else {
                                    pins.add(new Pin(title, subtitle, description));
                                    setListAdapter(pinAdapter);
                                    // Add the PinMessage details to the database
                                    BoardManager.sharedInstance().CreateMessagePin(title, subtitle, description);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    /**
     * When a Pin is clicked on the application
     * @param i
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView i, View v, int position, long id){
        launchPinDetailActivty(position);
    }

    /**
     * Method to launch a Dialog popup
     * @param position
     */
    private void launchPinDetailDialog(int position){
        Pin pin = (Pin) getListAdapter().getItem(position);
    }

    /**
     * Method to launch a new Activity to see details of a pinMessage
     * @param position
     */
    private void launchPinDetailActivty(int position){
        Pin pin = (Pin) getListAdapter().getItem(position);
        // Create Intent
        Intent intent = new Intent(getActivity(), PinDetailActivity.class);
        // Pass data from PinMessage to new Activity
        intent.putExtra(ToolbarActivity.PIN_TITLE_EXTRA, pin.getTitle());
        intent.putExtra(ToolbarActivity.PIN_SUBTITLE_EXTRA, pin.getSubtitle());
        intent.putExtra(ToolbarActivity.PIN_DESCRIPTION_EXTRA, pin.getDescription());
        intent.putExtra(ToolbarActivity.PIN_ID_EXTRA, pin.getId());
        // Start Activity
        startActivity(intent);
    }


    @Override
    public void onDestroyView()
    {
        BoardManager.sharedInstance().Destroy();
        super.onDestroyView();
    }


}