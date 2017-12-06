package com.example.ssdeol.suggest.Modules;

import java.util.List;

/**
 * Created by hnguye97 on 12/5/17.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
