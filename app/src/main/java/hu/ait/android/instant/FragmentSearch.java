package hu.ait.android.instant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.android.instant.data.DataManager;
import hu.ait.android.instant.data.User;

/**
 * Created by mchen16 on 12/13/17.
 */

public class FragmentSearch extends Fragment {

    public static final String TAG = "FragmentSearch";

    @BindView(R.id.etSearch)
    EditText etSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, viewRoot);

        return viewRoot;
    }

    @OnClick(R.id.btnSearch)
    public void Search() {
        User user = DataManager.findUserByName(etSearch.getText().toString());

        if (user == null) {
            Toast.makeText(getActivity(),
                    "Error: User not found",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        DataManager.getInstance().setData(user.getUId());
        ((BottomNavActivity)getContext()).showFragment(FragmentProfile.TAG);
    }
}
