package com.example.ssdeol.suggest;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

/**
 * Created by ssdeol on 11/19/17.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mGoogleWindow;
    private Context mGoogleContext;

    public CustomInfoWindowAdapter(Context context){
        mGoogleContext = context;
        mGoogleWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view){

        String title = marker.getTitle();
        TextView textViewTitle = (TextView) view.findViewById(R.id.infoSnippetTitle);

        if(!title.equals("")){
            textViewTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView textViewSnippet = (TextView) view.findViewById(R.id.infoSnippet);

        if(!snippet.equals("")){
            textViewSnippet.setText(snippet);
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mGoogleWindow);
        return mGoogleWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mGoogleWindow);
        return mGoogleWindow;
    }
}
