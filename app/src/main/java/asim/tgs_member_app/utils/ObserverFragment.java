package asim.tgs_member_app.utils;


import java.util.Observable;

/**
 * Created by PC-GetRanked on 4/10/2018.
 */

public class ObserverFragment extends Observable {

    @Override
    public void notifyObservers() {
        super.notifyObservers();
        setChanged();
    }
}
