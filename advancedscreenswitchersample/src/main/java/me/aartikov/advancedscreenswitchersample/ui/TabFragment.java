package me.aartikov.advancedscreenswitchersample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.aartikov.advancedscreenswitchersample.R;
import me.aartikov.advancedscreenswitchersample.SampleApplication;
import me.aartikov.advancedscreenswitchersample.screens.InnerScreen;
import me.aartikov.advancedscreenswitchersample.screens.TabScreen;
import me.aartikov.alligator.Navigator;
import me.aartikov.alligator.annotations.RegisterScreen;


@RegisterScreen(TabScreen.class)
public class TabFragment extends Fragment implements ContainerIdProvider {
	@BindView(R.id.name_text_view)
	TextView mNameTextView;

	private Unbinder mButterKnifeUnbinder;

	private Navigator mNavigator = SampleApplication.getNavigator();

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tab, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mButterKnifeUnbinder = ButterKnife.bind(this, view);

		TabScreen screen = SampleApplication.getScreenResolver().getScreen(this);
		mNameTextView.setText(screen.getName());
	}

	@Override
	public void onResume() {
		super.onResume();
		setInitialFragmentIfRequired();
	}

	private void setInitialFragmentIfRequired() {
		boolean hasFragment = getChildFragmentManager().findFragmentById(R.id.inner_container) != null;
		if (!hasFragment && mNavigator.canExecuteCommandImmediately()) {
			mNavigator.reset(new InnerScreen(1));
		}
	}

	@Override
	public void onDestroyView() {
		mButterKnifeUnbinder.unbind();
		super.onDestroyView();
	}

	@Override
	public int getContainerId() {
		return R.id.inner_container;
	}
}
