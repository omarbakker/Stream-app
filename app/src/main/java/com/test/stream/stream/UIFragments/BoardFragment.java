package com.test.stream.stream.UIFragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.stream.stream.Controllers.BoardManager;

import com.test.stream.stream.Objects.Board.Pin;
import com.test.stream.stream.R;
import com.test.stream.stream.Utilities.Listeners.DataEventListener;
import com.test.stream.stream.UI.Adapters.PinAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardFragment extends Fragment implements
        View.OnClickListener {

    ArrayList<Pin> pins = new ArrayList();
    private AlertDialog newPinDialog;
    private AlertDialog editPinDialog;
    private ListView mPinListView;
    private PinAdapter pinAdapter;
    private TextView mPinTextView;
    private Pin mCurrentPin;

    //fields for new task input
    EditText titleText;
    EditText subtitleText;
    EditText descriptionText;

    EditText titleEditText;
    EditText subtitleEditText;
    EditText descriptionEditText;
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
     * Function that updates the Adapter of the Fragment
     */
    private void updateUI() {

        //Show no pins message if there are none in the project.
        if(!BoardManager.sharedInstance().hasPins())
        {
            mPinTextView.setText(R.string.no_pins);
            mPinTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            mPinTextView.setText(R.string.empty);
            mPinTextView.setVisibility(View.GONE);
        }

        // Get all pins from the database
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
            mPinListView.setAdapter(pinAdapter);
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

        mPinListView = (ListView) getView().findViewById(R.id.list_pin);
        pinAdapter = new PinAdapter(getActivity(), pins);
        mPinListView.setAdapter(pinAdapter);
        //Set fonts
        mPinTextView = (TextView) getView().findViewById(R.id.text_board);
        Typeface Syncopate = Typeface.createFromAsset(getActivity().getAssets(), "Raleway-Regular.ttf");
        mPinTextView.setTypeface(Syncopate);
        floatButton = (ImageButton) getView().findViewById(R.id.pinImageButton);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showNewPinDialog();
            }
        });
        mPinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                final Pin pin = (Pin) adapterView.getItemAtPosition(position);
                showEditPinDialog(pin);
            }
        });

        // Call database to populate board if any PinMessages are in the database
        BoardManager.sharedInstance().InitializePins(dataListener);

    }

    public void showEditPinDialog(Pin pin) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.edit_pin_dialog, null);
        newPinDialog = new AlertDialog.Builder(getActivity()).setView(view).create();

        titleText = (EditText) view.findViewById(R.id.dialog_edit_title);
        subtitleText = (EditText) view.findViewById(R.id.dialog_edit_subtitle);
        descriptionText = (EditText) view.findViewById(R.id.dialog_edit_description);

        titleText.setText(pin.getTitle());
        subtitleText.setText(pin.getSubtitle());
        descriptionText.setText(pin.getDescription());
        mCurrentPin = pin;
        Button delete = (Button) view.findViewById(R.id.delete_pin);
        Button cancel = (Button) view.findViewById(R.id.CancelDeletePin);

        delete.setOnClickListener(this);
        cancel.setOnClickListener(this);

        newPinDialog.show();
    }

    public void showNewPinDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_pin_dialog, null);
        newPinDialog = new AlertDialog.Builder(getActivity()).setView(view).create();

        titleText = (EditText) view.findViewById(R.id.dialog_title);
        subtitleText = (EditText) view.findViewById(R.id.dialog_subtitle);
        descriptionText = (EditText) view.findViewById(R.id.dialog_description);


        Button done = (Button) view.findViewById(R.id.doneAddingPin);
        Button cancel = (Button) view.findViewById(R.id.CancelAddingPin);

        done.setOnClickListener(this);
        cancel.setOnClickListener(this);

        newPinDialog.show();
    }

    public void createPin(){
        String title = titleText.getText().toString();
        String subtitle = subtitleText.getText().toString();
        String description = descriptionText.getText().toString();
        if(title.equals("") || subtitle.equals("") || description.equals("")){
            Toast.makeText(getActivity(), "Title, subtitle or description is empty. Please fill in fields.", Toast.LENGTH_LONG).show();
        } else {
            pins.add(new Pin(title, subtitle, description));
            // Add the PinMessage details to the database
            BoardManager.sharedInstance().CreateMessagePin(title, subtitle, description);
            newPinDialog.dismiss();
        }

    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.doneAddingPin:
                createPin();
                break;
            case R.id.CancelAddingPin:
                newPinDialog.dismiss();
                break;
            case R.id.delete_pin:
                BoardManager.sharedInstance().RemovePin(mCurrentPin);
                newPinDialog.dismiss();
                break;
            case R.id.CancelDeletePin:
                newPinDialog.dismiss();
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroyView()
    {
        BoardManager.sharedInstance().Destroy();
        super.onDestroyView();
    }


}