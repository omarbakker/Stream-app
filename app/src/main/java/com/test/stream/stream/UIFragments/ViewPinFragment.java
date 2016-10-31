package com.test.stream.stream.UIFragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.stream.stream.R;
import com.test.stream.stream.UI.ToolbarActivity;

public class ViewPinFragment extends Fragment {

    public ViewPinFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_view_pin, container, false);
        TextView title = (TextView) fragmentLayout.findViewById(R.id.viewPinTitle);
        TextView subTitle = (TextView) fragmentLayout.findViewById(R.id.viewPinSubtitle);
        TextView description = (TextView) fragmentLayout.findViewById(R.id.viewPinDescription);

        Intent intent =  getActivity().getIntent();
        title.setText(intent.getExtras().getString(ToolbarActivity.PIN_TITLE_EXTRA));
        subTitle.setText(intent.getExtras().getString(ToolbarActivity.PIN_SUBTITLE_EXTRA));
        description.setText(intent.getExtras().getString(ToolbarActivity.PIN_DESCRIPTION_EXTRA));
        // Inflate the layout for this fragment
        return fragmentLayout;
    }


}
