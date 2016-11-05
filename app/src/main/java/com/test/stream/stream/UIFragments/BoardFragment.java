package com.test.stream.stream.UIFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.test.stream.stream.Controllers.BoardManager;
import com.test.stream.stream.Controllers.ProjectManager;
import com.test.stream.stream.Objects.Board.Pin;
import com.test.stream.stream.Objects.Board.PinMessage;
import com.test.stream.stream.Objects.Projects.Project;
import com.test.stream.stream.R;
import com.test.stream.stream.UI.PinDetailActivity;
import com.test.stream.stream.UI.ToolbarActivity;
import com.test.stream.stream.Utilities.PinAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardFragment extends ListFragment {

    ArrayList<PinMessage> pinMessages = new ArrayList();
    private PinAdapter pinAdapter;
    ImageButton floatButton;

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoardManager.getInstance().InitializePins(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pin, container, false);

        return view;
    }

    // Function that updates the Adapter of the ListFragment
    public void updateUI() {
        // Get all pins from the databse
        List<Pin> allPins = BoardManager.getInstance().GetPinsInProject();
        ArrayList<PinMessage> pins = new ArrayList();
        // Go through each pin in database
        for (Pin currentPin : allPins) {
            if (currentPin.getClass() == PinMessage.class) {
                PinMessage currentMessage = (PinMessage) currentPin;
                pins.add(currentMessage);
            }
        }
        Collections.reverse(pins);
        if (pinAdapter == null) {

            pinAdapter = new PinAdapter(getActivity(), pinMessages);
            setListAdapter(pinAdapter);

        } else {
            pinAdapter.clear();
            pinAdapter.addAll(pins);
            pinAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pinAdapter = new PinAdapter(getActivity(), pinMessages);
        setListAdapter(pinAdapter);
        floatButton = (ImageButton) getView().findViewById(R.id.pinImageButton);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_pin_dialog, null);

                final EditText titleText = (EditText) view.findViewById(R.id.dialog_title);
                final EditText subtitleText = (EditText) view.findViewById(R.id.dialog_subtitle);
                final EditText descriptionText = (EditText) view.findViewById(R.id.dialog_description);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setView(view)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String title = titleText.getText().toString();
                                String subtitle = subtitleText.getText().toString();
                                String description = descriptionText.getText().toString();
                                pinMessages.add(new PinMessage(title, subtitle, description));
                                setListAdapter(pinAdapter);
                                BoardManager.getInstance().CreateMessagePin(title, subtitle, description);
                            }
                        })
                        .setNegativeButton("Cancel", null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    @Override
    public void onListItemClick(ListView i, View v, int position, long id){
        launchPinDetailAcitivty(position);
    }

    private void launchPinDetailDialog(int position){
        PinMessage pin = (PinMessage) getListAdapter().getItem(position);
    }


    private void launchPinDetailAcitivty(int position){
        PinMessage pin = (PinMessage) getListAdapter().getItem(position);
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


}