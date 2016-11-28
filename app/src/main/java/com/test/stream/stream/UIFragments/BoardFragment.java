package com.test.stream.stream.UIFragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
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
    private GridView mPinGridView;
    private PinAdapter pinAdapter;
    private TextView mPinTextView;
    private Pin mCurrentPin;
    // variables for colors
    private TextView mAddPinTitleTextView;
    GradientDrawable blueUnselected;
    GradientDrawable blueSelected;
    GradientDrawable yellowUnselected;
    GradientDrawable yellowSelected;
    GradientDrawable pinkUnselected;
    GradientDrawable pinkSelected;
    private ImageButton blueButton;
    private ImageButton yellowButton;
    private ImageButton pinkButton;
    private boolean blueIsSelected = false;
    private boolean yellowIsSelected = false;
    private boolean pinkIsSelected = false;
    //fields for new task input
    EditText titleText;
    EditText descriptionText;


    EditText titleEditText;
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
            mPinGridView.setAdapter(pinAdapter);
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

        mPinGridView = (GridView) getView().findViewById(R.id.list_pin);
        pinAdapter = new PinAdapter(getActivity(), pins);
        // set blue color
        blueUnselected = new GradientDrawable();
        blueUnselected.setShape(GradientDrawable.OVAL);
        blueUnselected.setColor(Color.parseColor("#A7DEEB"));
        blueUnselected.setSize(100, 100);
        blueSelected = new GradientDrawable();
        blueSelected.setShape(GradientDrawable.OVAL);
        blueSelected.setColor(Color.parseColor("#A7DEEB"));
        blueSelected.setSize(115, 115);
        blueSelected.setStroke(15, Color.parseColor("#D2D1D2"));
        // set yellow color
        yellowUnselected = new GradientDrawable();
        yellowUnselected.setShape(GradientDrawable.OVAL);
        yellowUnselected.setColor(Color.parseColor("#FFFFA5"));
        yellowUnselected.setSize(100, 100);
        yellowSelected = new GradientDrawable();
        yellowSelected.setShape(GradientDrawable.OVAL);
        yellowSelected.setColor(Color.parseColor("#FFFFA5"));
        yellowSelected.setSize(115, 115);
        yellowSelected.setStroke(15, Color.parseColor("#D2D1D2"));
        // set pink color
        pinkUnselected = new GradientDrawable();
        pinkUnselected.setShape(GradientDrawable.OVAL);
        pinkUnselected.setColor(Color.parseColor("#F4C0CB"));
        pinkUnselected.setSize(100, 100);
        pinkSelected = new GradientDrawable();
        pinkSelected.setShape(GradientDrawable.OVAL);
        pinkSelected.setColor(Color.parseColor("#F4C0CB"));
        pinkSelected.setSize(115, 115);
        pinkSelected.setStroke(15, Color.parseColor("#D2D1D2"));

        mPinGridView.setAdapter(pinAdapter);
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
        mPinGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                final Pin pin = (Pin) adapterView.getItemAtPosition(position);
                showEditPinDialog(pin);
            }
        });

        // Call database to populate board if any PinMessages are in the database
        BoardManager.sharedInstance().InitializePins(dataListener);

    }

    // pop up dialog to edit pins
    public void showEditPinDialog(Pin pin) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.edit_pin_dialog, null);
        newPinDialog = new AlertDialog.Builder(getActivity()).setView(view).create();

        titleText = (EditText) view.findViewById(R.id.dialog_edit_title);
        descriptionText = (EditText) view.findViewById(R.id.dialog_edit_description);
        // get the title and description
        titleText.setText(pin.getTitle());
        descriptionText.setText(pin.getDescription());
        mCurrentPin = pin;
        Button delete = (Button) view.findViewById(R.id.delete_pin);
        Button cancel = (Button) view.findViewById(R.id.CancelDeletePin);
        //set listeners
        delete.setOnClickListener(this);
        cancel.setOnClickListener(this);

        newPinDialog.show();
    }

    public void showNewPinDialog() {
        blueIsSelected = false;
        yellowIsSelected = false;
        pinkIsSelected = false;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_pin_dialog, null);
        newPinDialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        mAddPinTitleTextView = (TextView) view.findViewById(R.id.dialog_add_pin_title);
        titleText = (EditText) view.findViewById(R.id.dialog_title);
        descriptionText = (EditText) view.findViewById(R.id.dialog_description);

        // Find color buttons and set drawable
        blueButton = (ImageButton) view.findViewById(R.id.blue_button);
        yellowButton = (ImageButton) view.findViewById(R.id.yellow_button);
        pinkButton = (ImageButton) view.findViewById(R.id.pink_button);
        blueButton.setImageDrawable(blueUnselected);
        yellowButton.setImageDrawable(yellowUnselected);
        pinkButton.setImageDrawable(pinkUnselected);

        Button done = (Button) view.findViewById(R.id.doneAddingPin);
        Button cancel = (Button) view.findViewById(R.id.CancelAddingPin);

        // set listeners
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        blueButton.setOnClickListener(this);
        yellowButton.setOnClickListener(this);
        pinkButton.setOnClickListener(this);

        newPinDialog.show();
    }

    public void createPin(){
        String title = titleText.getText().toString();
        String description = descriptionText.getText().toString();
        if(title.equals("") || description.equals("") || (blueIsSelected == false && yellowIsSelected == false
                && pinkIsSelected == false) ){
            Toast.makeText(getActivity(), "Title or description is empty or color is not selected. Please fill in fields.", Toast.LENGTH_LONG).show();
        } else {
            if(blueIsSelected){
                pins.add(new Pin(title, "blue", description));
                // Add the PinMessage details to the database
                BoardManager.sharedInstance().CreateMessagePin(title, "blue", description);
            }
            else if(pinkIsSelected){
                pins.add(new Pin(title, "pink", description));
                // Add the PinMessage details to the database
                BoardManager.sharedInstance().CreateMessagePin(title, "pink", description);
            }
            else{
                pins.add(new Pin(title, "yellow", description));
                // Add the PinMessage details to the database
                BoardManager.sharedInstance().CreateMessagePin(title, "yellow", description);
            }
            newPinDialog.dismiss();
        }

    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            // create new pin
            case R.id.doneAddingPin:
                createPin();
                break;
            // cancel dialog
            case R.id.CancelAddingPin:
                newPinDialog.dismiss();
                break;
            // delete pin
            case R.id.delete_pin:
                BoardManager.sharedInstance().RemovePin(mCurrentPin);
                newPinDialog.dismiss();
                break;
            // cancel dialog
            case R.id.CancelDeletePin:
                newPinDialog.dismiss();
                break;
            // if blue is selected
            case R.id.blue_button:
                blueButton.setImageDrawable(blueSelected);
                yellowButton.setImageDrawable(yellowUnselected);
                pinkButton.setImageDrawable(pinkUnselected);
                blueIsSelected = true;
                yellowIsSelected = false;
                pinkIsSelected = false;
                mAddPinTitleTextView.setBackgroundColor(Color.parseColor("#A7DEEB"));
                break;
            // if yellow is selected
            case R.id.yellow_button:
                yellowButton.setImageDrawable(yellowSelected);
                blueButton.setImageDrawable(blueUnselected);
                pinkButton.setImageDrawable(pinkUnselected);
                blueIsSelected = false;
                yellowIsSelected = true;
                pinkIsSelected = false;
                mAddPinTitleTextView.setBackgroundColor(Color.parseColor("#FFFFA5"));
                break;
            // if pink is selected
            case R.id.pink_button:
                pinkButton.setImageDrawable(pinkSelected);
                yellowButton.setImageDrawable(yellowUnselected);
                blueButton.setImageDrawable(blueUnselected);
                blueIsSelected = false;
                yellowIsSelected = false;
                pinkIsSelected = true;
                mAddPinTitleTextView.setBackgroundColor(Color.parseColor("#F4C0CB"));
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