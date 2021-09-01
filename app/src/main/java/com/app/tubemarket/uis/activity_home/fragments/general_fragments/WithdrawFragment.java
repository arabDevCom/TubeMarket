package com.app.tubemarket.uis.activity_home.fragments.general_fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tubemarket.R;
import com.app.tubemarket.adapters.CoinsAdapter;
import com.app.tubemarket.adapters.WithdrawAdapter;
import com.app.tubemarket.databinding.FragmentBuyCoinBinding;
import com.app.tubemarket.databinding.FragmentWithdrawBinding;
import com.app.tubemarket.interfaces.Listeners;
import com.app.tubemarket.models.CoinDataModel;
import com.app.tubemarket.models.CoinsModel;
import com.app.tubemarket.models.UserModel;
import com.app.tubemarket.models.WithdrawDataModel;
import com.app.tubemarket.models.WithdrawModel;
import com.app.tubemarket.preferences.Preferences;
import com.app.tubemarket.remote.Api;
import com.app.tubemarket.tags.Tags;
import com.app.tubemarket.uis.activity_home.HomeActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WithdrawFragment extends Fragment implements Listeners.WithdrawListener {

    private FragmentWithdrawBinding binding;
    private HomeActivity activity;
    private UserModel userModel;
    private Preferences preferences;
    private List<WithdrawModel> list;
    private WithdrawAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdraw, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        list = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);

        binding.recView.setLayoutManager(new GridLayoutManager(activity,2));
        adapter = new WithdrawAdapter(activity, list, this);
        binding.recView.setAdapter(adapter);



        getWithdraw();
    }

    private void getWithdraw() {
        binding.loader.setVisibility(View.VISIBLE);
        list.clear();
        adapter.notifyDataSetChanged();
        Api.getService(Tags.base_url)
                .getWithdrawCoins("Bearer " + userModel.getToken(), "desc")
                .enqueue(new Callback<WithdrawDataModel>() {
                    @Override
                    public void onResponse(Call<WithdrawDataModel> call, Response<WithdrawDataModel> response) {
                        binding.loader.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                list.clear();
                                list.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();

                            }
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<WithdrawDataModel> call, Throwable t) {
                        binding.loader.setVisibility(View.GONE);
                        Log.e("fail", t.getMessage());
                    }
                });
    }

    @Override
    public void onWithdrawData(WithdrawModel withdrawModel, View view) {

    }
}