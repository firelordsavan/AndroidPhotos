package com.example.androidphotos55;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddTag extends AppCompatActivity {

    private Spinner tagTypeChoices;
    private EditText tagValueText;
    private ArrayList<Tag> existingTags;

    public static final String NEW_TYPE = "newType";
    public static final String NEW_VALUE = "newValue";
    public static final String PHOTO = "photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle info = getIntent().getExtras();
        Photo photo = (Photo) info.getSerializable(PHOTO);
        existingTags = photo.getTags();

        tagTypeChoices = findViewById(R.id.tag_type_spinner);
        tagValueText = findViewById(R.id.tag_value_input);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tag_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagTypeChoices.setAdapter(adapter);
        tagTypeChoices.setSelection(0);
    }

    public void cancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void addTag(View view){
        String type = tagTypeChoices.getSelectedItem().toString();
        String value = tagValueText.getText().toString();

        if (value.length() == 0 || value == null) {
            Bundle toSend = new Bundle();
            toSend.putString(CreateAlbumFragment.MESSAGE_KEY, "A tag value is required.");
            DialogFragment newFragment = new CreateAlbumFragment();
            newFragment.setArguments(toSend);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return;
        }

        String newValue = value.toLowerCase().trim(); // New Album Name
        for(int i = 0; i < existingTags.size(); i++) {
            if(type.equals(existingTags.get(i).getTagType()) && newValue.equals(existingTags.get(i).getTagValue().toLowerCase())) {
                Bundle wap = new Bundle();
                wap.putString(CreateAlbumFragment.MESSAGE_KEY, "A tag with the same type and value exists already.");
                DialogFragment newFrag = new CreateAlbumFragment();
                newFrag.setArguments(wap);
                newFrag.show(getSupportFragmentManager(), "badfields");
                return;
            }
        }
        Bundle toSend = new Bundle();
        toSend.putInt(NEW_TYPE, tagTypeChoices.getSelectedItemPosition());
        toSend.putString(NEW_VALUE, value.trim());
        Intent intent = new Intent();
        intent.putExtras(toSend);
        setResult(RESULT_OK, intent);
        finish();
    }
}