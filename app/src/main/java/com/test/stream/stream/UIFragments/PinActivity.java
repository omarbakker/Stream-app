package com.test.stream.stream.UIFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.WelcomeButtonAction;
import com.dexafree.materialList.view.MaterialListView;
import com.squareup.picasso.RequestCreator;
import com.test.stream.stream.R;
import java.util.ArrayList;
import java.util.List;

public class PinActivity extends AppCompatActivity {
    private Context mContext;
    private MaterialListView mListView;
    private int position = 0;
    ImageButton floatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        System.out.println("THIS IS PINACTIVITY");
        mListView = (MaterialListView) findViewById(R.id.material_listview);

        final ImageView emptyView = (ImageView) findViewById(R.id.imageView);
        emptyView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mListView.setEmptyView(emptyView);
        floatButton = (ImageButton) findViewById(R.id.pinImageButton);
        floatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                View view = LayoutInflater.from(PinActivity.this).inflate(R.layout.add_pin_dialog, null);

                final EditText titleText= (EditText) view.findViewById(R.id.dialog_title);
                final EditText subtitleText = (EditText) view.findViewById(R.id.dialog_subtitle);
                final EditText descriptionText = (EditText) view.findViewById(R.id.dialog_description);
                final ImageButton mPhotoButton = (ImageButton) view.findViewById(R.id.crime_camera);


                final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mPhotoButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        startActivityForResult(captureImage, 2);
                    }
                });
                final ImageView mPhotoView = (ImageView) view.findViewById(R.id.crime_photo);
                AlertDialog.Builder builder = new AlertDialog.Builder(PinActivity.this);

                builder.setTitle("Add Pin")
                        .setView(view)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                String title = titleText.getText().toString();
                                String subtitle = subtitleText.getText().toString();
                                String description = descriptionText.getText().toString();
                                fillArray(position, 1, title, subtitle, description);
                                position++;
                            }
                        })
                        .setNegativeButton("Cancel",null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void fillArray(int position, int type, String title, String subtitle, String description) {
        List<Card> cards = new ArrayList<>();
        //for (int i = 0; i < 35; i++) {
        if(type == 1) {
            cards.add(getRandomCard(position, title, subtitle, description));
        }
        if(type == 2){
            cards.add(getImageCard(position, title, description));
        }
        //}
        mListView.getAdapter().addAll(cards);
    }

    private Card getImageCard(final int position, String title, String description){
        return new Card.Builder(this)
                .setTag("BIG_IMAGE_CARD")
                .setDismissible()
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_big_image_card_layout)
                .setTitle(title)
                .setSubtitle(description)
                .setSubtitleGravity(Gravity.END)
                .setDrawable("https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png")
                .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                    @Override
                    public void onImageConfigure(@NonNull final RequestCreator requestCreator) {
                        requestCreator
                                .resize(200, 200)
                                .centerCrop();
                    }
                })
                .endConfig()
                .build();
    }
    private Card getRandomCard(final int position, String title, String subtitle, String description){
        final CardProvider provider = new Card.Builder(this)
                .setTag("WELCOME_CARD")
                .setDismissible()
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_welcome_card_layout)
                .setTitle(title)
                .setTitleColor(Color.WHITE)
                .setDescription(description)
                .setDescriptionColor(Color.WHITE)
                .setSubtitle(subtitle)
                .setSubtitleColor(Color.WHITE)
                .setBackgroundResourceColor(android.R.color.holo_blue_light)
                .addAction(R.id.ok_button, new WelcomeButtonAction(this)
                        .setText("Okay!")
                        .setTextColor(Color.WHITE)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                card.dismiss();
                            }
                        }));
        return provider.endConfig().build();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu){
        System.out.println("INFLATE MENU");
        getMenuInflater().inflate(R.menu.add_button, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_add) {
            View view = LayoutInflater.from(PinActivity.this).inflate(R.layout.add_pin_dialog, null);

            final EditText titleText= (EditText) view.findViewById(R.id.dialog_title);
            final EditText subtitleText = (EditText) view.findViewById(R.id.dialog_subtitle);
            final EditText descriptionText = (EditText) view.findViewById(R.id.dialog_description);
            final ImageButton mPhotoButton = (ImageButton) view.findViewById(R.id.crime_camera);


            final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mPhotoButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    startActivityForResult(captureImage, 2);
                }
            });
            final ImageView mPhotoView = (ImageView) view.findViewById(R.id.crime_photo);
            AlertDialog.Builder builder = new AlertDialog.Builder(PinActivity.this);

            builder.setTitle("Add Pin")
                    .setView(view)
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            String title = titleText.getText().toString();
                            String subtitle = subtitleText.getText().toString();
                            String description = descriptionText.getText().toString();
                            fillArray(position, 1, title, subtitle, description);
                            position++;
                        }
                    })
                    .setNegativeButton("Cancel",null);

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
